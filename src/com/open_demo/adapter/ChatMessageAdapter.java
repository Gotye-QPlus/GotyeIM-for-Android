package com.open_demo.adapter;

import java.io.File;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeMessageType;
import com.gotye.api.GotyeUser;
import com.gotye.api.Media;
import com.open_demo.R;
import com.open_demo.activity.ChatPage;
import com.open_demo.activity.ShowBigImage;
import com.open_demo.activity.UserInfoPage;
import com.open_demo.util.BitmapUtil;
import com.open_demo.util.GotyeVoicePlayClickPlayListener;
import com.open_demo.util.ImageCache;
import com.open_demo.util.TimeUtil;
import com.open_demo.util.ToastUtil;

public class ChatMessageAdapter extends BaseAdapter {

	public static final int TYPE_RECEIVE_TEXT = 0;
	public static final int TYPE_RECEIVE_IMAGE = 1;
	public static final int TYPE_RECEIVE_VOICE = 2;
	public static final int TYPE_RECEIVE_USER_DATA = 3;

	public static final int TYPE_SEND_TEXT = 4;
	public static final int TYPE_SEND_IMAGE = 5;
	public static final int TYPE_SEND_VOICE = 6;
	public static final int TYPE_SEND_USER_DATA = 7;

	public static final int MESSAGE_DIRECT_RECEIVE = 1;
	public static final int MESSAGE_DIRECT_SEND = 0;

	private ChatPage chatPage;
	private List<GotyeMessage> messageList;

	private LayoutInflater inflater;
	private String currentLoginName;
	private GotyeAPI api;
	public ChatMessageAdapter(ChatPage activity, List<GotyeMessage> messageList) {
		this.chatPage = activity;
		this.messageList = messageList;
		inflater = activity.getLayoutInflater();
		api = GotyeAPI.getInstance();
		currentLoginName = api.getCurrentLoginUser().getName();
	}

	public void addMsgToBottom(GotyeMessage msg) {
		int position = messageList.indexOf(msg);
		if (position < 0) {
			messageList.add(msg);
			return;
		}
		messageList.remove(position);
		messageList.add(position, msg);
		notifyDataSetChanged();
	}

	public void updateMessage(GotyeMessage msg) {
		int position = messageList.indexOf(msg);
		if (position < 0) {
			return;
		}
		messageList.remove(position);
		messageList.add(position, msg);
		notifyDataSetChanged();
	}

	public void updateChatMessage(GotyeMessage msg) {
		if (messageList.contains(msg)) {
			int index = messageList.indexOf(msg);
			messageList.remove(index);
			messageList.add(index, msg);
			notifyDataSetChanged();
		}
	}
	public void addMessagesToTop(List<GotyeMessage> histMessages) {
		messageList.addAll(0, histMessages);
	}

	public void addMessageToTop(GotyeMessage msg) {
		messageList.add(0, msg);
	}

	@Override
	public int getCount() {
		return messageList.size();
	}

	@Override
	public GotyeMessage getItem(int position) {
		 if(position<0||position>=messageList.size()){
			 return null;
		 }else{
			return messageList.get(position);
		 }
		 
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		GotyeMessage message = getItem(position);
		if (message.getType() == GotyeMessageType.GotyeMessageTypeText) {
			return getDirect(message) == MESSAGE_DIRECT_RECEIVE ? TYPE_RECEIVE_TEXT
					: TYPE_SEND_TEXT;
		}
		if (message.getType() == GotyeMessageType.GotyeMessageTypeImage) {
			return getDirect(message) == MESSAGE_DIRECT_RECEIVE ? TYPE_RECEIVE_IMAGE
					: TYPE_SEND_IMAGE;

		}
		if (message.getType() == GotyeMessageType.GotyeMessageTypeAudio) {
			return getDirect(message) == MESSAGE_DIRECT_RECEIVE ? TYPE_RECEIVE_VOICE
					: TYPE_SEND_VOICE;
		}
		if (message.getType() == GotyeMessageType.GotyeMessageTypeUserData) {
			return getDirect(message) == MESSAGE_DIRECT_RECEIVE ? TYPE_RECEIVE_USER_DATA
					: TYPE_SEND_USER_DATA;
		}
		return -1;// invalid
	}

	public int getViewTypeCount() {
		return 8;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final GotyeMessage message = getItem(position);
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = createViewByMessage(message, position);
			if (message.getType() == GotyeMessageType.GotyeMessageTypeImage) {
				holder.iv = ((ImageView) convertView
						.findViewById(R.id.iv_sendPicture));
				holder.head_iv = (ImageView) convertView
						.findViewById(R.id.iv_userhead);
				holder.tv = (TextView) convertView
						.findViewById(R.id.percentage);
				holder.pb = (ProgressBar) convertView
						.findViewById(R.id.progressBar);
				holder.staus_iv = (ImageView) convertView
						.findViewById(R.id.msg_status);
				holder.tv_userId = (TextView) convertView
						.findViewById(R.id.tv_userid);
				holder.tv_delivered=(TextView) convertView.findViewById(R.id.tv_delivered);
			} else if (message.getType() == GotyeMessageType.GotyeMessageTypeAudio) {
				holder.iv = ((ImageView) convertView
						.findViewById(R.id.iv_voice));
				holder.head_iv = (ImageView) convertView
						.findViewById(R.id.iv_userhead);
				holder.tv = (TextView) convertView.findViewById(R.id.tv_length);
				holder.pb = (ProgressBar) convertView
						.findViewById(R.id.pb_sending);
				holder.staus_iv = (ImageView) convertView
						.findViewById(R.id.msg_status);
				holder.tv_userId = (TextView) convertView
						.findViewById(R.id.tv_userid);
				holder.iv_read_status = (ImageView) convertView
						.findViewById(R.id.iv_unread_voice);
				holder.extra_data=(TextView) convertView.findViewById(R.id.extra_data);
				holder.tv_delivered=(TextView) convertView.findViewById(R.id.tv_delivered);
			} else {
				holder.pb = (ProgressBar) convertView
						.findViewById(R.id.pb_sending);
				holder.staus_iv = (ImageView) convertView
						.findViewById(R.id.msg_status);
				holder.head_iv = (ImageView) convertView
						.findViewById(R.id.iv_userhead);
				// 这里是文字内容
				holder.tv = (TextView) convertView
						.findViewById(R.id.tv_chatcontent);
				holder.tv_userId = (TextView) convertView
						.findViewById(R.id.tv_userid);
				holder.tv_delivered=(TextView) convertView.findViewById(R.id.tv_delivered);
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		switch (message.getType()) {
		// 根据消息type显示item
		case GotyeMessageTypeImage: // 图片
			handleImageMessage(message, holder, position, convertView);
			break;
		case GotyeMessageTypeAudio: // 语音
			handleVoiceMessage(message, holder, position, convertView);
			break;
		default:
			handleTextMessage(message, holder, position);
			break;
		}

		TextView timestamp = (TextView) convertView
				.findViewById(R.id.timestamp);

		// if (position == 0) {
		timestamp.setText(TimeUtil.dateToMessageTime(message.getDate() * 1000));
		timestamp.setVisibility(View.VISIBLE);
		// } else {
		// 两条消息时间离得如果稍长，显示时间
		// if (TimeUtil.needShowTime(message.getDate(), messageList.get(position
		// - 1).getDate())) {
		// timestamp.setText(TimeUtil.toLocalTimeString(message.getDate()*1000));
		// timestamp.setVisibility(View.VISIBLE);
		// } else {
		// timestamp.setVisibility(View.GONE);
		// }
		// }
		holder.head_iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(chatPage, UserInfoPage.class);
				i.putExtra("user", (GotyeUser)message.getSender());
				chatPage.startActivity(i);
			}
		});
		setIcon(holder.head_iv, message.getSender().name);
		return convertView;
	}

	private void handleImageMessage(final GotyeMessage message,
			final ViewHolder holder, final int position, View convertView) {
		holder.iv.setImageResource(R.drawable.ic_launcher);
		setImageMessage(holder.iv, message, holder);

		if (getDirect(message) == MESSAGE_DIRECT_SEND) {
			switch (message.getStatus()) {
			case GotyeMessage.STATUS_SENT: // 发送成功
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
				if(holder.tv_delivered!=null){
					holder.tv_delivered.setVisibility(View.VISIBLE);
				}
				break;
			case GotyeMessage.STATUS_SENDFAILED: // 发送失败
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.VISIBLE);
				if(holder.tv_delivered!=null){
					holder.tv_delivered.setVisibility(View.GONE);
				}
				break;
			case GotyeMessage.STATUS_SENDING: // 发送中
				holder.pb.setVisibility(View.VISIBLE);
				holder.staus_iv.setVisibility(View.GONE);
				if(holder.tv_delivered!=null){
					holder.tv_delivered.setVisibility(View.GONE);
				}
				break;
			default:
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
				if(holder.tv_delivered!=null){
					holder.tv_delivered.setVisibility(View.VISIBLE);
				}
			}
		}else{
				String name=message.getSender().name;
				holder.tv_userId.setText(name);
		}
	}

	private void handleTextMessage(GotyeMessage message, ViewHolder holder,
			final int position) {
		// 设置内容
		String extraData=message.getExtraData()==null?null:new String(message.getExtraData());
		if(extraData!=null){
			if(message.getType()==GotyeMessageType.GotyeMessageTypeText){
				holder.tv.setText(message.getText()+"\n额外数据："+extraData);
			}else{
				holder.tv.setText("自定义消息："+new String(message.getUserData())+"\n额外数据："+extraData);
			}
		}else{
			if(message.getType()==GotyeMessageType.GotyeMessageTypeText){
				holder.tv.setText(message.getText());
			}else{
				holder.tv.setText("自定义消息："+new String(message.getUserData()));
			}
		}
		
		// 设置长按事件监听
		if (getDirect(message) == MESSAGE_DIRECT_SEND) {
			switch (message.getStatus()) {
			case GotyeMessage.STATUS_SENT: // 发送成功
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
				if(holder.tv_delivered!=null){
					holder.tv_delivered.setVisibility(View.VISIBLE);
				}
				break;
			case GotyeMessage.STATUS_SENDFAILED: // 发送失败
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.VISIBLE);
				if(holder.tv_delivered!=null){
					holder.tv_delivered.setVisibility(View.GONE);
				}
				break;
			case GotyeMessage.STATUS_SENDING: // 发送中
				holder.pb.setVisibility(View.VISIBLE);
				holder.staus_iv.setVisibility(View.GONE);
				if(holder.tv_delivered!=null){
					holder.tv_delivered.setVisibility(View.GONE);
				}
				break;
			default:
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
				if(holder.tv_delivered!=null){
					holder.tv_delivered.setVisibility(View.VISIBLE);
				}
			}
		}else{
			String name=message.getSender().name;
			holder.tv_userId.setText(name);
		}
	}

	private void handleVoiceMessage(final GotyeMessage message,
			final ViewHolder holder, final int position, View convertView) {
		holder.tv.setText(TimeUtil.getVoiceTime(message.getMedia()
				.getDuration()));
		holder.iv.setOnClickListener(new GotyeVoicePlayClickPlayListener(message,
				holder.iv, this, chatPage));
		boolean isPlaying = isPlaying(message);
		if (isPlaying) {
			AnimationDrawable voiceAnimation;
			if (getDirect(message) == MESSAGE_DIRECT_RECEIVE) {
				holder.iv.setImageResource(R.anim.voice_from_icon);
			} else {
				holder.iv.setImageResource(R.anim.voice_to_icon);
			}
			voiceAnimation = (AnimationDrawable) holder.iv.getDrawable();
			voiceAnimation.start();
		} else {
			if (getDirect(message) == MESSAGE_DIRECT_RECEIVE) {
				holder.iv.setImageResource(R.drawable.chatfrom_voice_playing);
			} else {
				holder.iv.setImageResource(R.drawable.chatto_voice_playing);
			}
		}
		
		
		if(holder.extra_data!=null){
			if(message.getExtraData()!=null){
				holder.extra_data.setVisibility(View.VISIBLE);
				String extra=new String(message.getExtraData());
				holder.extra_data.setText("语音内容:"+extra);
			}else{
				holder.extra_data.setVisibility(View.GONE);
			}
		} 
		
		
		if (getDirect(message) == MESSAGE_DIRECT_RECEIVE) {
			if (message.getStatus() == GotyeMessage.ACK_UNREAD) {// if
				// holder.iv_read_status.setVisibility(View.INVISIBLE);
				holder.iv_read_status.setVisibility(View.VISIBLE);
			} else {
				holder.iv_read_status.setVisibility(View.INVISIBLE);
			}
			
			String name=message.getSender().name;
			holder.tv_userId.setText(name);
			return;
		}
		
		
		// until here, deal with send voice msg
		switch (message.getStatus()) {
		case GotyeMessage.STATUS_SENT:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			if(holder.tv_delivered!=null){
				holder.tv_delivered.setVisibility(View.VISIBLE);
			}
			break;
		case GotyeMessage.STATUS_SENDFAILED:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			if(holder.tv_delivered!=null){
				holder.tv_delivered.setVisibility(View.GONE);
			}
			break;
		case GotyeMessage.STATUS_SENDING:
			holder.pb.setVisibility(View.VISIBLE);
			holder.staus_iv.setVisibility(View.GONE);
			if(holder.tv_delivered!=null){
				holder.tv_delivered.setVisibility(View.GONE);
			}
			break;
		default:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			if(holder.tv_delivered!=null){
				holder.tv_delivered.setVisibility(View.VISIBLE);
			}
		}
		switch (message.getMedia().getStatus()) {
		case Media.MEDIA_STATUS_DOWNLOADING:
			holder.pb.setVisibility(View.VISIBLE);
			break;
		default:
			holder.pb.setVisibility(View.GONE);
			break;
		}
	}
	

	private boolean isPlaying(GotyeMessage msg) {
		long id = msg.getDbId();
		long pid = chatPage.getPlayingId();
		if (id == pid) {
			return true;
		} else {
			return false;
		}

	}

	private View createViewByMessage(GotyeMessage message, int position) {
		switch (message.getType()) {
		case GotyeMessageTypeImage:
			return getDirect(message) == MESSAGE_DIRECT_RECEIVE ? inflater
					.inflate(R.layout.layout_row_received_picture, null) : inflater
					.inflate(R.layout.layout_row_sent_picture, null);

		case GotyeMessageTypeAudio:
			return getDirect(message) == MESSAGE_DIRECT_RECEIVE ? inflater
					.inflate(R.layout.layout_row_received_voice, null) : inflater
					.inflate(R.layout.layout_row_sent_voice, null);
		case GotyeMessageTypeUserData:
			return getDirect(message) == MESSAGE_DIRECT_RECEIVE ? inflater
					.inflate(R.layout.layout_row_received_message, null) : inflater
					.inflate(R.layout.layout_row_sent_message, null);
		default:
			return getDirect(message) == MESSAGE_DIRECT_RECEIVE ? inflater
					.inflate(R.layout.layout_row_received_message, null) : inflater
					.inflate(R.layout.layout_row_sent_message, null);
		}
	}

	private ImageCache cache = ImageCache.getInstance();

	private void setIcon(ImageView iconView, String name) {
		Bitmap bmp = cache.get(name);
		if (bmp != null) {
			iconView.setImageBitmap(bmp);
		} else {
			GotyeUser user = api.requestUserInfo(name, false);
			if (user != null && user.getIcon() != null) {
				bmp = cache.get(user.getIcon().path);
				if (bmp != null) {
					iconView.setImageBitmap(bmp);
					cache.put(name, bmp);
				} else {
					bmp = BitmapUtil.getBitmap(user.getIcon().getPath());
					if (bmp != null) {
						iconView.setImageBitmap(bmp);
						cache.put(name, bmp);
					} else {
						iconView.setImageResource(R.drawable.mini_avatar_shadow);
					}
				}
			} else {
				iconView.setImageResource(R.drawable.mini_avatar_shadow);
			}
		}
	}

	private void setImageMessage(ImageView msgImageView,
			final GotyeMessage msg, ViewHolder holder) {
		Bitmap cacheBm = cache.get(msg.getMedia().getPath());
		if (cacheBm != null) {
			msgImageView.setImageBitmap(cacheBm);
			holder.pb.setVisibility(View.GONE);
		} else if (msg.getMedia().getPath() != null) {
			Bitmap bm = BitmapUtil.getBitmap(msg.getMedia().getPath());
			if (bm != null) {
				msgImageView.setImageBitmap(bm);
				cache.put(msg.getMedia().getPath(), bm);
			}
			holder.pb.setVisibility(View.GONE);
		}
		msgImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(chatPage, ShowBigImage.class);
				String path = msg.getMedia().getPath_ex();
				if (!TextUtils.isEmpty(path) && new File(path).exists()) {
					Uri uri = Uri.fromFile(new File(path));
					intent.putExtra("uri", uri);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					chatPage.startActivity(intent);
				} else {
					ToastUtil.show(chatPage, "正在下载...");
					api.downloadMessage(msg);
					return;
				}

			}
		});
		// holder.pb.setVisibility(View.VISIBLE);

	}

	private int getDirect(GotyeMessage message) {
		if (message.getSender().name.equals(currentLoginName)) {
			return MESSAGE_DIRECT_SEND;
		} else {
			return MESSAGE_DIRECT_RECEIVE;
		}
	}

	public void downloadDone(GotyeMessage msg) {
		if (msg.getType() == GotyeMessageType.GotyeMessageTypeImage) {
			// if (TextUtils.isEmpty(msg.getMedia().getPath_ex())) {
			// ToastUtil.show(chatPage, "图片下载失败");
			// return;
			// }
		}
		if (messageList.contains(msg)) {
			int index = messageList.indexOf(msg);
			messageList.remove(index);
			messageList.add(index, msg);
			notifyDataSetChanged();
		}
	}

	public static class ViewHolder {
		ImageView iv;
		TextView tv;
		ProgressBar pb;
		ImageView staus_iv;
		ImageView head_iv;
		TextView tv_userId;
		ImageView playBtn;
		TextView timeLength;
		TextView size;
		LinearLayout container_status_btn;
		LinearLayout ll_container;
		ImageView iv_read_status;
		// 显示已读回执状态
		TextView tv_ack;
		// 显示送达回执状态
		TextView tv_delivered;
		TextView extra_data;

		TextView tv_file_name;
		TextView tv_file_size;
		TextView tv_file_download_state;
	}

	public void refreshData(List<GotyeMessage> list) {
		// TODO Auto-generated method stub
		this.messageList = list;
		notifyDataSetChanged();
	}
}
