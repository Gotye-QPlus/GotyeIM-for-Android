/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.open_demo.util;

import java.io.File;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeMessage;
import com.open_demo.R;
import com.open_demo.activity.ChatPage;
import com.open_demo.adapter.ChatMessageAdapter;

public class GotyeVoicePlayClickListener implements View.OnClickListener {

	GotyeMessage message;
	ImageView voiceIconView;

	private AnimationDrawable voiceAnimation = null;
	MediaPlayer mediaPlayer = null;
	ImageView iv_read_status;
	Activity activity;
	private BaseAdapter adapter;

	public static boolean isPlaying = false;
	public static GotyeVoicePlayClickListener currentPlayListener = null;

	/**
	 * 
	 * @param message
	 * @param v
	 * @param iv_read_status
	 * @param context
	 * @param activity
	 * @param user
	 * @param chatType
	 */
	public GotyeVoicePlayClickListener(GotyeMessage message, ImageView v,
			BaseAdapter adapter, Activity activity) {
		this.message = message;
		this.adapter = adapter;
		voiceIconView = v;
		this.activity = activity;
	}

	public void stopPlayVoice() {
		voiceAnimation.stop();
		if (getDirect(message) == ChatMessageAdapter.MESSAGE_DIRECT_RECEIVE) {
			voiceIconView.setImageResource(R.drawable.chatfrom_voice_playing);
		} else {
			voiceIconView.setImageResource(R.drawable.chatto_voice_playing);
		}
		// stop play voice
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		isPlaying = false;
		((ChatPage) activity).setPlayingId(0);
		adapter.notifyDataSetChanged();
	}

	private int getDirect(GotyeMessage message) {
		if (message.getSender().name.equals(GotyeAPI.getInstance()
				.getCurrentLoginUser().getName())) {
			return ChatMessageAdapter.MESSAGE_DIRECT_SEND;
		} else {
			return ChatMessageAdapter.MESSAGE_DIRECT_RECEIVE;
		}
	}

	public void playVoice(String filePath) {
		if (!(new File(filePath).exists())) {
			return;
		}
		((ChatPage) activity).setPlayingId(message.getDbId());
		// AudioManager audioManager = (AudioManager) activity
		// .getSystemService(Context.AUDIO_SERVICE);

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
		try {
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepare();
			mediaPlayer
					.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							// TODO Auto-generated method stub
							mediaPlayer.release();
							mediaPlayer = null;
							stopPlayVoice(); // stop animation
						}

					});
			isPlaying = true;
			currentPlayListener = this;
			mediaPlayer.start();
			showAnimation();

		} catch (Exception e) {
		}
	}

	// show the voice playing animation
	private void showAnimation() {
		// play voice, and start animation
		if (getDirect(message) == ChatMessageAdapter.MESSAGE_DIRECT_RECEIVE) {
			voiceIconView.setImageResource(R.anim.voice_from_icon);
		} else {
			voiceIconView.setImageResource(R.anim.voice_to_icon);
		}
		voiceAnimation = (AnimationDrawable) voiceIconView.getDrawable();
		voiceAnimation.start();
	}

	@Override
	public void onClick(View v) {
		if (((ChatPage) activity).onRealTimeTalkFrom >= 0) {
			ToastUtil.show(activity, "正在实时通话中");
			return;
		}
		if (isPlaying) {
			if (((ChatPage) activity).getPlayingId() == message.getDbId()) {
				currentPlayListener.stopPlayVoice();
				return;
			}
			currentPlayListener.stopPlayVoice();
		}
		String path = message.getMedia().getPath();
		File file = new File(path);
		if (file.exists() && file.isFile()) {
			playVoice(path);
		} else {
			Toast.makeText(activity, "正在下载语音，稍后点击", Toast.LENGTH_SHORT).show();
			GotyeAPI.getInstance().downloadMessage(message);
		}
	}
}