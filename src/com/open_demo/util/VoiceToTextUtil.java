package com.open_demo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baidu.voicerecognition.android.Candidate;
import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.baidu.voicerecognition.android.VoiceRecognitionClient.VoiceClientStatusChangeListener;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.gotye.api.GotyeMessage;
import com.open_demo.activity.ChatPage;

public class VoiceToTextUtil implements OnEventListener,
		VoiceClientStatusChangeListener {
	private ChatPage chatPage;
	private GotyeMessage message;
	private AudioFileThread mAudioRecordThread;
	private VoiceRecognitionClient mASREngine;

	private boolean isRecognition = false;
	private Handler mHandler;

	private static final int POWER_UPDATE_INTERVAL = 100;

	public VoiceToTextUtil(ChatPage chatPage,VoiceRecognitionClient mASREngine) {
		this.chatPage = chatPage;
		this.mASREngine=mASREngine;
		mHandler = new Handler();
	}

	public void toPress(GotyeMessage message) {
		this.message = message;
		onStartListening();
	}

	@Override
	public boolean onStopListening() {
		stopRecordThread();
		mASREngine.speakFinish();
		return true;
	}

	@Override
	public boolean onStartListening() {
		VoiceRecognitionConfig config = new VoiceRecognitionConfig();
		config.setProp(Config.CURRENT_PROP);
		config.setLanguage(Config.getCurrentLanguage());
		config.enableVoicePower(Config.SHOW_VOL); // 音量反馈。
		config.setUseDefaultAudioSource(false);
		config.setSampleRate(VoiceRecognitionConfig.SAMPLE_RATE_8K); // 设置采样率,需要与外部音频一致
		// 下面发起识别
		int code = mASREngine.startVoiceRecognition(this, config);
		if (code != VoiceRecognitionClient.START_WORK_RESULT_WORKING) {
			Toast.makeText(chatPage, "启动失败:" + code, Toast.LENGTH_LONG).show();
			pressEnd(null);
		} else {
			mAudioRecordThread = new AudioFileThread(message.getMedia()
					.getPath_ex());
			mAudioRecordThread.start();
		}

		return code == VoiceRecognitionClient.START_WORK_RESULT_WORKING;
	}

	private void stopRecordThread() {
		if (mAudioRecordThread != null) {
			mAudioRecordThread.exit();
			mAudioRecordThread = null;
		}
	}

	@Override
	public boolean onCancel() {
		stopRecordThread();
		mASREngine.stopVoiceRecognition();
		return true;
	}

	class AudioFileThread extends Thread {
		private final static String TAG = "AudioFileThread";

		private String mFilePath = "8_8.10.39.54.pcm";

		private volatile boolean mStop = false;

		public AudioFileThread(String mFilePath) {
			this.mFilePath = mFilePath;
		}

		public void exit() {
			mStop = true;
		}

		@Override
		public void run() {
			Log.d(TAG, " audio thread start mFilePath " + mFilePath);

			InputStream in;
			try {
				in = new FileInputStream(new File(mFilePath));
				// in = chatPage.getAssets().open("8_8.10.39.54.pcm");
			} catch (IOException e) {
				Log.e(TAG, " e is " + e);
				return;
			}

			int length = 1024;
			byte[] buffer = new byte[length];
			while (!mStop) {
				try {
					int byteread = in.read(buffer);
					Log.d(TAG, " byteread: " + byteread);
					if (byteread != -1) {
						mASREngine.feedAudioBuffer(buffer, 0, byteread);
					} else {
						for (int i = 0; i < length; i++) {
							buffer[i] = 0;
						}
						mASREngine.feedAudioBuffer(buffer, 0, length);
					}
				} catch (IOException e) {
					Log.e(TAG, " e is " + e);
				}
			}

			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Log.e(TAG, " e is " + e);
				}
			}

			Log.d(TAG, " audio thread exit");
		}
	}
	@Override
	public void onClientStatusChange(int status, Object obj) {
		switch (status) {
		// 语音识别实际开始，这是真正开始识别的时间点，需在界面提示用户说话。
		case VoiceRecognitionClient.CLIENT_STATUS_START_RECORDING:
			isRecognition = true;
			mHandler.removeCallbacks(mUpdateVolume);
			mHandler.postDelayed(mUpdateVolume, POWER_UPDATE_INTERVAL);
			break;
		case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_START: // 检测到语音起点

			break;
		// 已经检测到语音终点，等待网络返回
		case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_END:
			stopRecordThread();
			break;
		// 语音识别完成，显示obj中的结果
		case VoiceRecognitionClient.CLIENT_STATUS_FINISH:
			stopRecordThread();

			isRecognition = false;
			updateRecognitionResult(obj);
			break;
		// 处理连续上屏
		case VoiceRecognitionClient.CLIENT_STATUS_UPDATE_RESULTS:
			updateRecognitionResult(obj);
			break;
		// 用户取消
		case VoiceRecognitionClient.CLIENT_STATUS_USER_CANCELED:
			isRecognition = false;
			break;
		default:
			break;
		}

	}

	private Runnable mUpdateVolume = new Runnable() {
		public void run() {
			if (isRecognition) {
				long vol = mASREngine.getCurrentDBLevelMeter();
				mHandler.removeCallbacks(mUpdateVolume);
				mHandler.postDelayed(mUpdateVolume, POWER_UPDATE_INTERVAL);
			}
		}
	};

	private void updateRecognitionResult(Object result) {
		if (result != null && result instanceof List) {
			List results = (List) result;
			if (results.size() > 0) {
				if (results.get(0) instanceof List) {
					List<List<Candidate>> sentences = (List<List<Candidate>>) result;
					StringBuffer sb = new StringBuffer();
					for (List<Candidate> candidates : sentences) {
						if (candidates != null && candidates.size() > 0) {
							sb.append(candidates.get(0).getWord());
						}
					}
					// mResult.setText(sb.toString());
					pressEnd(sb.toString());
				} else {
					// mResult.setText(results.get(0).toString());
				}
			}
		}
	}

	private void pressEnd(String text) {
				if (!TextUtils.isEmpty(text)) {
					byte[] bb=null;
					try {
						bb = text.getBytes("UTF-8");
						message.putExtraData(bb);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				chatPage.api.sendMessage(message);
				message.setStatus(GotyeMessage.STATUS_SENDING);
	}

	@Override
	public void onError(int errorType, int errorCode) {
		isRecognition = false;
		stopRecordThread();
		pressEnd(null);
	}

	@Override
	public void onNetworkStatusChange(int status, Object obj) {
		// 这里不做任何操作不影响简单识别
	}
}
