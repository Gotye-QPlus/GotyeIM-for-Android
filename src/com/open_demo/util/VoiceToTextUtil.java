package com.open_demo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.gotye.api.GotyeMessage;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.open_demo.activity.ChatPage;

public class VoiceToTextUtil implements RecognizerListener{
	private ChatPage chatPage;
	private String voiceToText = "";
	private GotyeMessage message;
	public VoiceToTextUtil(ChatPage chatPage){
		this.chatPage=chatPage;
	}
	
	public void toPress(SpeechRecognizer mSpeech,GotyeMessage message){
		this.message=message;
		byte[] data = VoiceToTextUtil.readFileFromAssets(chatPage, message.getMedia().getPath_ex());
		ArrayList<byte[]> buffers = VoiceToTextUtil.splitBuffer(data,data.length, 1280);
		writeaudio(mSpeech,buffers);
	}
	public void writeaudio(final SpeechRecognizer mSpeech,final ArrayList<byte[]> buffers){
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
				mSpeech.startListening(VoiceToTextUtil.this);
			 
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
	/**
	 * 读取asset文件
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static byte[] readFileFromAssets(Context context,String fileName){
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
	public static ArrayList<byte[]> splitBuffer(byte[] buffer,int length,int spsize)
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

	@Override
	public void onBeginOfSpeech() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEndOfSpeech() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(SpeechError code) {
		if(chatPage.toSend!=null){
    		int p=chatPage.toSend.indexOf(message);
    		if(p>=0&&p<chatPage.toSend.size()){
    			 chatPage.api.sendMessage(message);
    			 message.setStatus(GotyeMessage.STATUS_SENDING);
    			 chatPage.adapter.addMsgToBottom(message);
    			 chatPage.scrollToBottom();
    		}
    	}
		 
		if(code.getErrorCode() == 10118){
			//Toast.makeText(chatPage, "好像没有说话哦", Toast.LENGTH_SHORT).show();
			return;
		}else if(code.getErrorCode() == 10111){
			//Toast.makeText(chatPage, "语音引擎初始化失败", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	@Override
	public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
		// TODO Auto-generated method stub
		
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
			 
			 byte[] bb=null;
			try {
				bb = voiceToText.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(chatPage.toSend!=null){
	    		int p=chatPage.toSend.indexOf(message);
	    		if(p>=0&&p<chatPage.toSend.size()){
	    			 message.putExtraData(bb);
	    			 chatPage.api.sendMessage(message);
	    			 message.setStatus(GotyeMessage.STATUS_SENDING);
	    		}
	    	}
			 chatPage.adapter.addMsgToBottom(message);
			 chatPage.scrollToBottom();
			  
		 }
	}

	@Override
	public void onVolumeChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}
	

}
