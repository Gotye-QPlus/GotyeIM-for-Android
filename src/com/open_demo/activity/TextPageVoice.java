package com.open_demo.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.open_demo.R;
import com.open_demo.util.JsonParser;
import com.open_demo.util.ProgressDialogUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class TextPageVoice extends Activity {

	private TextView mTxtContet = null;
	private String mFilePath = null;
	private String voiceToText;
	private SpeechRecognizer mSpeech;
	private int mRet = -1;
	public static boolean hasInit=false;
	public  InitListener mInitListener = new InitListener() {
		
		@Override
		public void onInit(int code) {
			if(code != ErrorCode.SUCCESS){
				Toast.makeText(TextPageVoice.this, "初始化失败，错误码："+code, Toast.LENGTH_SHORT).show();
				return;
			}
			hasInit=true;
		    VoiceChangeText();
		}
	};
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_textpage);
		mSpeech = SpeechRecognizer.createRecognizer(this, mInitListener);
		mFilePath = getIntent().getStringExtra("text_voice");
		mTxtContet = (TextView)findViewById(R.id.showText);
		if(hasInit){
			VoiceChangeText();
		}
	}
	
	private void VoiceChangeText(){
		 //检查语音+是否安装,如未安装，获取语音+下载地址进行下载。安装完成后即可使用服务。  
	    if(!SpeechUtility.getUtility().checkServiceInstalled ()){
			  Toast.makeText(TextPageVoice.this, "请先安装讯飞语音...", Toast.LENGTH_SHORT).show();
		        String url = SpeechUtility.getUtility().getComponentUrl();  
		        Uri uri = Uri.parse (url);  
		        Intent it = new Intent(Intent.ACTION_VIEW , uri);  
		        startActivity(it);
		        finish();
		  }
		  ProgressDialogUtil.showProgress(TextPageVoice.this, "正在转换···");
		  byte[] data = readFileFromAssets(TextPageVoice.this,mFilePath);
		  ArrayList<byte[]> buffers = splitBuffer(data,data.length, 1280);
		  writeaudio(buffers);
	}
	
	/**
	 * 读取asset文件
	 * @param context
	 * @param fileName
	 * @return
	 */
	public byte[] readFileFromAssets(Context context,String fileName){
		byte[] buffer = null;
		InputStream in = null;
		try {
			in = new FileInputStream(new File(fileName));
			buffer = new byte[in.available()];
			in.read(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}finally
		{
			try {
				if(in != null)
				{
					in.close();
					in = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return buffer;
	}
	
	/**
	 * 将字节缓冲区按照固定大小进行分割成数组
	 * @param buffer 缓冲区
	 * @param length 缓冲区大小
	 * @param spsize 切割块大小
	 * @return
	 */
	public ArrayList<byte[]> splitBuffer(byte[] buffer,int length,int spsize)
	{
		ArrayList<byte[]> array = new ArrayList<byte[]>();
		if(spsize <= 0 || length <= 0 || buffer == null || buffer.length < length)
			return array;
		int size = 0;
		while(size < length)
		{
			int left = length - size;
			if(spsize < left)
			{
				byte[] sdata = new byte[spsize];
				System.arraycopy(buffer,size,sdata,0,spsize);
				array.add(sdata);
				size += spsize;
			}else
			{
				byte[] sdata = new byte[left];
				System.arraycopy(buffer,size,sdata,0,left);
				array.add(sdata);
				size += left;
			}
		}
		return array;
	}
	
	public void writeaudio(final ArrayList<byte[]> buffers){
		new Thread(new Runnable()
		{
			@Override
			public void run() {
				
				//byte[] mybyte = new byte[1280];
				//int count=-1;
				mSpeech.setParameter(SpeechConstant.DOMAIN, "iat");
				mSpeech.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
				mSpeech.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
				mSpeech.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
				mRet = mSpeech.startListening(mRecognizerListener);
				Log.d("ListenerBack", "ret = "+mRet);
				for(int i = 0; i < buffers.size(); i++)
        		{
        			try {
        				mSpeech.writeAudio(buffers.get(i),0,buffers.get(i).length);
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        		}
				mSpeech.stopListening();
			}
			
		}).start();
	}
	
	public RecognizerListener mRecognizerListener = new RecognizerListener(){

		@Override
		public void onBeginOfSpeech() {
			Log.d("ListenerBack", "result = ");
		}

		@Override
		public void onEndOfSpeech() {
			Log.d("ListenerBack", "---onEndOfSpeech---");
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			Log.d("ListenerBack", "---onEvent---");
		}

		@Override
		public void onVolumeChanged(int arg0) {
			Log.d("ListenerBack", "---onVolumeChanged---"+ arg0);
		}

		@Override
		public void onError(SpeechError code) {
			ProgressDialogUtil.dismiss();
			Log.d("ListenerBack", "---onError---error = "+ code);
			if(code.getErrorCode() == 10118){
				Toast.makeText(TextPageVoice.this, "您好像没有说话哦", Toast.LENGTH_SHORT).show();
				finish();
			}else if(code.getErrorCode() == 10111){
				Toast.makeText(TextPageVoice.this, "引擎初始化失败", Toast.LENGTH_SHORT).show();
				finish();
			}
		}

		@Override
		public void onResult(RecognizerResult result, boolean arg1) {
			ProgressDialogUtil.dismiss();
			Log.d("ListenerBack", "---onResult---result = "+ result);
			String resultVoice = JsonParser.parseIatResult(result.getResultString());
			 if(!arg1){
				 voiceToText="";
				 voiceToText=resultVoice;
			 }else{
				 voiceToText+=resultVoice;
			 } 
			 if(arg1){
				 Log.d("ListenerBack", "---voiceToText---result = "+ voiceToText);
					mTxtContet.setText(voiceToText);
			 }
		}
	};
	
	
	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}
}
