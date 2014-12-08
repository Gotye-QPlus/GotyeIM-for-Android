package com.open_demo.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.gotye.api.GotyeChatTarget;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeMessageType;
import com.gotye.api.GotyeRoom;
import com.gotye.api.GotyeStatusCode;
import com.gotye.api.GotyeUser;
import com.gotye.api.PathUtil;
import com.gotye.api.WhineMode;
import com.open_demo.R;
import com.open_demo.adapter.ChatMessageAdapter;
import com.open_demo.base.BaseActivity;
import com.open_demo.main.MainActivity;
import com.open_demo.util.CommonUtils;
import com.open_demo.util.FileUtil;
import com.open_demo.util.ProgressDialogUtil;
import com.open_demo.util.SendImageMessageTask;
import com.open_demo.util.ToastUtil;
import com.open_demo.util.GotyeVoicePlayClickListener;
import com.open_demo.view.RTPullListView;
import com.open_demo.view.RTPullListView.OnRefreshListener;

public class ChatPage extends BaseActivity implements OnClickListener {
	public static final int REALTIMEFROM_OTHER = 2;
	public static final int REALTIMEFROM_SELF = 1;
	public static final int REALTIMEFROM_NO = 0;
	private static final int REQUEST_PIC = 1;
	private static final int REQUEST_CAMERA = 2;

	public static final int VOICE_MAX_TIME = 60 * 1000;
	private RTPullListView pullListView;
	private ChatMessageAdapter adapter;
	private GotyeUser user;
	private GotyeRoom room;
	private GotyeGroup group;
	private GotyeUser currentLoginUser;

	private ImageView voice_text_chage;
	private Button pressToVoice;
	private EditText textMessage;
	private ImageView showMoreType;
	private LinearLayout moreTypeLayout;

	private PopupWindow menuWindow;
	private AnimationDrawable anim;
	public int chatType = 0;

	private View realTalkView;
	private TextView realTalkName, stopRealTalk;
	private AnimationDrawable realTimeAnim;
	private boolean moreTypeForSend = true;

	public int onRealTimeTalkFrom = -1; // -1默认状态 ,0表示我在说话,1表示别人在实时语音

	private File cameraFile;
	public static final int IMAGE_MAX_SIZE_LIMIT = 1024 * 1024;
	public static final int Voice_MAX_TIME_LIMIT = 60 * 1000;
	private long playingId;

	private TextView title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gotye_activity_chat);
		currentLoginUser = api.getCurrentLoginUser();
		api.addListerer(this);
		user = (GotyeUser) getIntent().getSerializableExtra("user");
		room = (GotyeRoom) getIntent().getSerializableExtra("room");
		group = (GotyeGroup) getIntent().getSerializableExtra("group");
		initView();
		if (chatType==0) {
			api.activeSession(user);
			loadData();
		} else if (chatType==1) {
			int code=api.enterRoom(room);
			if(code==GotyeStatusCode.CODE_OK){
				api.activeSession(room);
				loadData();
				api.getLocalMessages(room, true);
				GotyeRoom temp=api.requestRoomInfo(room.Id, true);
				if(temp!=null&&!TextUtils.isEmpty(temp.getRoomName())){
					title.setText("聊天室："+temp.getRoomName());
				}
			}else{
				ProgressDialogUtil.showProgress(this, "正在进入房间...");
			}
		} else if (chatType==2) {
			api.activeSession(group);
			loadData();
		}
	}

	private void initView() {
		pullListView = (RTPullListView) findViewById(R.id.gotye_msg_listview);
		findViewById(R.id.back).setOnClickListener(this);
		title = ((TextView) findViewById(R.id.title));
		realTalkView = findViewById(R.id.real_time_talk_layout);
		realTalkName = (TextView) realTalkView
				.findViewById(R.id.real_talk_name);
		Drawable[] anim = realTalkName.getCompoundDrawables();
		realTimeAnim = (AnimationDrawable) anim[2];
		stopRealTalk = (TextView) realTalkView
				.findViewById(R.id.stop_real_talk);
		stopRealTalk.setOnClickListener(this);
		
		if (user != null) {
			chatType = 0;
			title.setText("和 " + user.name + " 聊天");
		} else if (room != null) {
			chatType = 1;
			title.setText("聊天室："+room.getRoomID());
		} else if (group != null) {
			chatType = 2;
			String titleText=null;
			if(!TextUtils.isEmpty(group.getGroupName())){
				titleText=group.getGroupName();
			}else{
				GotyeGroup temp=api.requestGroupInfo(group.getGroupID(), true);
				if(temp!=null&&!TextUtils.isEmpty(temp.getGroupName())){
					titleText=temp.getGroupName();
				}else{
					titleText=String.valueOf(group.getGroupID());
				}
			}
			title.setText("群："+titleText);
		}
		 
		voice_text_chage = (ImageView) findViewById(R.id.send_voice);
		pressToVoice = (Button) findViewById(R.id.press_to_voice_chat);
		textMessage = (EditText) findViewById(R.id.text_msg_input);
		showMoreType = (ImageView) findViewById(R.id.more_type);
		moreTypeLayout = (LinearLayout) findViewById(R.id.more_type_layout);

		moreTypeLayout.findViewById(R.id.to_gallery).setOnClickListener(this);
		moreTypeLayout.findViewById(R.id.to_camera).setOnClickListener(this);
		moreTypeLayout.findViewById(R.id.real_time_voice_chat)
				.setOnClickListener(this);

		voice_text_chage.setOnClickListener(this);
		showMoreType.setOnClickListener(this);
		textMessage.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				String text = arg0.getText().toString();
				// GotyeMessage message =new GotyeMessage();
				// GotyeChatManager.getInstance().sendMessage(message);
				sendTextMessage(text);
				textMessage.setText("");
				return true;
			}
		});
		pressToVoice.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (onRealTimeTalkFrom == 0) {
						ToastUtil.show(ChatPage.this, "正在实时通话中...");
						return false;
					}

					if (GotyeVoicePlayClickListener.isPlaying) {
						GotyeVoicePlayClickListener.currentPlayListener
								.stopPlayVoice();
					}

					if (chatType == 0) {
						api.startTalk(user, WhineMode.DEFAULT, false,
								60 * 1000);
					} else if (chatType == 1) {
						api.startTalk(room, WhineMode.DEFAULT, false,
								60 * 1000);
					} else if (chatType == 2) {
						api.startTalk(group, WhineMode.DEFAULT, false,
								60 * 1000);
					}
					pressToVoice.setText("松开 发送");
					break;
				case MotionEvent.ACTION_UP:
					if (onRealTimeTalkFrom == 0) {
						return false;
					}
					Log.d("chat_page",
							"onTouch action=ACTION_UP" + event.getAction());
					// if (onRealTimeTalkFrom > 0) {
					// return false;
					// }
					api.stopTalk();
					Log.d("chat_page",
							"after stopTalk action=" + event.getAction());
					pressToVoice.setText("按住 说话");
					break;
				case MotionEvent.ACTION_CANCEL:
					if (onRealTimeTalkFrom == 0) {
						return false;
					}
					Log.d("chat_page",
							"onTouch action=ACTION_CANCEL" + event.getAction());
					// if (onRealTimeTalkFrom > 0) {
					// return false;
					// }
					api.stopTalk();
					pressToVoice.setText("按住 说话");
					break;
				default:
					Log.d("chat_page",
							"onTouch action=default" + event.getAction());
					break;
				}
				return false;
			}
		});
		adapter = new ChatMessageAdapter(this, new ArrayList<GotyeMessage>());
		pullListView.setAdapter(adapter);
		pullListView.setSelection(adapter.getCount());
		setListViewInfo();
	}

	private void sendTextMessage(String text) {
		if (!TextUtils.isEmpty(text)) {
			GotyeMessage toSend;
			if (chatType == 0) {
				toSend = GotyeMessage.createTextMessage(currentLoginUser, user,
						text);
			} else if (chatType == 1) {
				toSend = GotyeMessage.createTextMessage(currentLoginUser, room,
						text);
			} else {
				toSend = GotyeMessage.createTextMessage(currentLoginUser,
						group, text);
			}
			String extraStr=null;
			if(text.contains("#")){
				String[] temp=text.split("#");
				if(temp.length>1){
					extraStr=temp[1];
				}
			
			}else if(text.contains("#")){
				String[] temp=text.split("#");
				if(temp.length>1){
					extraStr=temp[1];
				}
			}
			if(extraStr!=null){
				toSend.putExtraData(extraStr.getBytes());
			}
			
			int code = api.sendMessage(toSend);
			adapter.addMsgToBottom(toSend);
			scrollToBottom();
			sendUserDataMessage("userdata message".getBytes(),"text#text");
		}  
	}
	public void sendUserDataMessage(byte[] userData,String text){
		if (userData!=null) {
			GotyeMessage toSend;
			if (chatType == 0) {
				toSend = GotyeMessage.createUserDataMessage(currentLoginUser, user,
						userData,userData.length);
			} else if (chatType == 1) {
				toSend = GotyeMessage.createUserDataMessage(currentLoginUser, room,
						userData,userData.length);
			} else {
				toSend = GotyeMessage.createUserDataMessage(currentLoginUser,
						group, userData,userData.length);
			}
			String extraStr=null;
			if(text.contains("#")){
				String[] temp=text.split("#");
				if(temp.length>1){
					extraStr=temp[1];
				}
			
			}else if(text.contains("#")){
				String[] temp=text.split("#");
				if(temp.length>1){
					extraStr=temp[1];
				}
			}
			if(extraStr!=null){
				toSend.putExtraData(extraStr.getBytes());
			}
			
			int code = api.sendMessage(toSend);
			adapter.addMsgToBottom(toSend);
			scrollToBottom();
		}  
	}
	
	public void callBackSendImageMessage(GotyeMessage msg) {
		adapter.addMsgToBottom(msg);
		scrollToBottom();
	}

	public void info(View v) {
		if (chatType==0) {
			Intent intent = getIntent();
			intent.setClass(getApplication(), UserInfoPage.class);
			intent.putExtra("user", user);
			startActivity(intent);
		} else if (chatType==1) {
			Intent info = new Intent(getApplication(), RoomInfoPage.class);
			info.putExtra("room", room);
			startActivity(info);
		} else {
			Intent info = new Intent(getApplication(), GroupInfoPage.class);
			info.putExtra("group", group);
			startActivity(info);
		}
	}

	private void loadData() {
		List<GotyeMessage> messages = null;
		if (user != null) {
			messages = api.getLocalMessages(user, true);
		} else if (room != null) {
			messages = api.getLocalMessages(room, true);
		} else if (group != null) {
			messages = api.getLocalMessages(group, true);
		}
		if (messages == null) {
			messages = new ArrayList<GotyeMessage>();
		}
		for (GotyeMessage msg : messages) {
			api.downloadMessage(msg);
		}
		adapter.refreshData(messages);
	}

	private void setListViewInfo() {
		// 下拉刷新监听器
		pullListView.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (chatType == 1) {
					api.getLocalMessages(room, true);
				} else {
					List<GotyeMessage> list = null;

					if (chatType == 0) {
						list = api.getLocalMessages(user, true);
					} else if (chatType == 2) {
						list = api.getLocalMessages(group, true);
					}
					if (list != null) {
						for(GotyeMessage msg:list){
							api.downloadMessage(msg);
						}
						adapter.refreshData(list);
					} else {
						ToastUtil.show(ChatPage.this, "没有更多历史消息");
					}
				}
				adapter.notifyDataSetChanged();
				pullListView.onRefreshComplete();
			}
		});
		pullListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				final GotyeMessage message = adapter.getItem(arg2);
				pullListView.setTag(message);
				if (message.getSender().name.equals(currentLoginUser.getName())) {
					return false;
				}
				pullListView.showContextMenu();
				return true;
			}
		});
		pullListView
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

					@Override
					public void onCreateContextMenu(ContextMenu conMenu,
							View arg1, ContextMenuInfo arg2) {
						final GotyeMessage message = (GotyeMessage) pullListView
  								.getTag();
						if (message.getSender().name
								.equals(currentLoginUser.name)) {
							return;
						}
						MenuItem m = conMenu.add(0, 0, 0, "举报");
						m.setOnMenuItemClickListener(new OnMenuItemClickListener() {

							@Override
							public boolean onMenuItemClick(MenuItem item) {
								api.report(0, "举报的说明", message);
								return true;
							}
						});
					}
				});

	}

	private void scrollToBottom(){
		pullListView.setSelection(adapter.getCount()-1);
	}
	// private void showTalkView() {
	// dismissTalkView();
	// View view = LayoutInflater.from(this).inflate(
	// R.layout.gotye_audio_recorder_ring, null);
	//
	// anim = initRecordingView(view);
	// anim.start();
	// menuWindow = new PopupWindow(this);
	// menuWindow.setContentView(view);
	// menuWindow.setAnimationStyle(android.R.style.Animation_Dialog);
	// // int width = (int) (view.getMeasuredWidth() * 3 * 1.0 / 2);
	// Drawable dd = getResources().getDrawable(R.drawable.gotye_pls_talk);
	// menuWindow.setWidth(dd.getIntrinsicWidth());
	//
	// menuWindow.setHeight(dd.getIntrinsicHeight());
	// menuWindow.setBackgroundDrawable(null);
	// menuWindow.showAtLocation(findViewById(R.id.gotye_chat_content),
	// Gravity.CENTER, 0, 0);
	// }

	// private void dismissTalkView() {
	// if (menuWindow != null && menuWindow.isShowing()) {
	// menuWindow.dismiss();
	// }
	// if (anim != null && anim.isRunning()) {
	// anim.stop();
	// }
	// }
	//
	// private AnimationDrawable initRecordingView(View layout) {
	//
	// ImageView speakingBg = (ImageView) layout
	// .findViewById(R.id.background_image);
	// speakingBg.setImageDrawable(getResources().getDrawable(
	// R.drawable.gotye_pop_voice));
	// layout.setBackgroundResource(R.drawable.gotye_pls_talk);
	//
	// AnimationDrawable anim = AnimUtil.getSpeakBgAnim(getResources());
	// anim.selectDrawable(0);
	//
	// ImageView dot = (ImageView) layout.findViewById(R.id.speak_tip);
	// dot.setBackgroundDrawable(anim);
	// return anim;
	// }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		api.removeListener(this);
		if (chatType == 0) {
			api.deactiveSession(user);
		} else if (chatType == 1) {
			api.deactiveSession(room);
			api.leaveRoom(room);
		} else {
			api.deactiveSession(group);
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if (GotyeVoicePlayClickListener.isPlaying
				&& GotyeVoicePlayClickListener.currentPlayListener != null) {
			// 停止语音播放
			GotyeVoicePlayClickListener.currentPlayListener.stopPlayVoice();
		}
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// if (onRealTimeTalkFrom == REALTIMEFROM_SELF) {
		api.stopTalk();
		// return;
		// } else if (onRealTimeTalkFrom == REALTIMEFROM_OTHER) {
		api.stopPlay();
		// }
		super.onBackPressed();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.back:
			onBackPressed();
			break;
		case R.id.send_voice:
			if (pressToVoice.getVisibility() == View.VISIBLE) {
				pressToVoice.setVisibility(View.GONE);
				textMessage.setVisibility(View.VISIBLE);
				voice_text_chage
						.setImageResource(R.drawable.voice_btn_selector);
				showMoreType.setImageResource(R.drawable.send_selector);
				moreTypeForSend = true;
				moreTypeLayout.setVisibility(View.GONE);
			} else {
				pressToVoice.setVisibility(View.VISIBLE);
				textMessage.setVisibility(View.GONE);

				voice_text_chage
						.setImageResource(R.drawable.change_to_text_press);

				showMoreType.setImageResource(R.drawable.more_type_selector);
				moreTypeForSend = false;
				hideKeyboard();
			}

			break;
		case R.id.more_type:
			if (moreTypeForSend) {
				hideKeyboard();
				String str = textMessage.getText().toString();
				sendTextMessage(str);
				textMessage.setText("");
			} else {
				if (moreTypeLayout.getVisibility() == View.VISIBLE) {
					moreTypeLayout.setVisibility(View.GONE);
				} else {
					moreTypeLayout.setVisibility(View.VISIBLE);
					if (chatType == 1 && api.supportRealtime(room) == true) {
						moreTypeLayout.findViewById(R.id.real_time_voice_chat)
								.setVisibility(View.VISIBLE);
					}

				}
			}
			break;
		case R.id.to_gallery:
			takePic();
			break;
		case R.id.to_camera:
			takePhoto();
			break;
		case R.id.real_time_voice_chat:
			realTimeTalk();
			break;
		case R.id.stop_real_talk:
			int i = api.stopTalk();
			break;
		default:
			break;
		}
	}

	public void showImagePrev(GotyeMessage message) {
		hideKeyboard();
	}

	public void realTimeTalk() {
		if (onRealTimeTalkFrom > 0) {
			Toast.makeText(this, "请稍后...", Toast.LENGTH_SHORT).show();
			return;
		}
		api.startTalk(room, WhineMode.DEFAULT, true, Voice_MAX_TIME_LIMIT);
		moreTypeLayout.setVisibility(View.GONE);
	}

	public void hideKeyboard() {
		// 隐藏输入法
		InputMethodManager imm = (InputMethodManager) getApplicationContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 显示或者隐藏输入法
		imm.hideSoftInputFromWindow(textMessage.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private void takePic() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		startActivityForResult(intent, REQUEST_PIC);
	}

	private void takePhoto() {
		selectPicFromCamera();
	}

	public void selectPicFromCamera() {
		if (!CommonUtils.isExitsSdcard()) {
			Toast.makeText(getApplicationContext(), "SD卡不存在，不能拍照",
					Toast.LENGTH_SHORT).show();
			return;
		}

		cameraFile = new File(PathUtil.getAppFIlePath()
				+ +System.currentTimeMillis() + ".jpg");
		cameraFile.getParentFile().mkdirs();
		startActivityForResult(
				new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
						MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				REQUEST_CAMERA);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 选取图片的返回值
		if (requestCode == REQUEST_PIC) {
			if (data != null) {
				Uri selectedImage = data.getData();
				if (selectedImage != null) {
					String path=FileUtil.uriToPath(this, selectedImage);
					sendPicture(path);
				}
			}

		} else if (requestCode == REQUEST_CAMERA) {
			if (resultCode == RESULT_OK) {

				if (cameraFile != null && cameraFile.exists())
					sendPicture(cameraFile.getAbsolutePath());
			}
		}
		// TODO 获取图片失败
		super.onActivityResult(requestCode, resultCode, data);
	}

	 
	private void sendPicture(String path) {
		SendImageMessageTask task;
		if (chatType == 0) {
			task = new SendImageMessageTask(this, user);
		} else if (chatType == 1) {
			task = new SendImageMessageTask(this, room);
		} else {
			task = new SendImageMessageTask(this, group);
		}
		task.execute(path);
	}

	public void setPlayingId(long playingId) {
		this.playingId = playingId;
		adapter.notifyDataSetChanged();
	}

	public long getPlayingId() {
		return playingId;
	}

	@Override
	public void onSendMessage(int code, GotyeMessage message) {
		Log.d("OnSend", "code= " + code + "message = " + message);
		// GotyeChatManager.getInstance().insertChatMessage(message);
		adapter.updateMessage(message);
		if(message.getType()==GotyeMessageType.GotyeMessageTypeAudio){
			api.decodeMessage(message);
		}
		// message.senderUser =
		// DBManager.getInstance().getUser(currentLoginName);
		pullListView.setSelection(adapter.getCount());
	}

	@Override
	public void onReceiveMessage(int code, GotyeMessage message) {
		// GotyeChatManager.getInstance().insertChatMessage(message);
 		if (chatType == 0) {
			if (isMyMessage(message)) {
				// msg.senderUser = user;
				adapter.addMsgToBottom(message);
				pullListView.setSelection(adapter.getCount());
			}
		} else if (chatType == 1) {
			if (message.getReceiver().Id == room.getRoomID()) {
				// message.senderUser = user;
				adapter.addMsgToBottom(message);
				pullListView.setSelection(adapter.getCount());
			}
		} else if (chatType == 2) {
			if (message.getReceiver().Id == group.getGroupID()) {
				adapter.addMsgToBottom(message);
				pullListView.setSelection(adapter.getCount());
			}
		}
		//scrollToBottom();
	}

	private boolean isMyMessage(GotyeMessage message) {
		if (message.getSender() != null
				&& user.getName().equals(message.getSender().name)
				&& currentLoginUser.name.equals(message.getReceiver().name)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onDownloadMessage(int code, GotyeMessage message) {
		adapter.downloadDone(message);
	}

	@Override
	public void onEnterRoom(int code, long lastMsgID, GotyeRoom room) {
		ProgressDialogUtil.dismiss();
		if (code == 0) {
			api.activeSession(room);
			loadData();
			GotyeRoom temp=api.requestRoomInfo(room.Id, true);
			if(temp!=null&&!TextUtils.isEmpty(temp.getRoomName())){
				title.setText("聊天室："+temp.getRoomName());
			}
		} else {
			ToastUtil.show(this, "房间不存在...");
			finish();
		}
	}

	@Override
	public void onGetHistoryMessageList(int code, List<GotyeMessage> list) {
		if (chatType == 1) {
			List<GotyeMessage> listmessages = api.getLocalMessages(room,
					false);
			if (listmessages != null) {
				for (GotyeMessage temp : listmessages) {
					api.downloadMessage(temp);
				}
				adapter.refreshData(listmessages);
			} else {
				ToastUtil.show(this, "没有历史记录");
			}
		}
		adapter.notifyDataSetInvalidated();
		pullListView.onRefreshComplete();
	}

	@Override
	public void onStartTalk(int code, boolean isRealTime, int targetType,
			GotyeChatTarget target) {
		if (isRealTime) {
			if (code != 0) {
				ToastUtil.show(this, "抢麦失败，先听听别人说什么。");
				return;
			}
			if (GotyeVoicePlayClickListener.isPlaying) {
				GotyeVoicePlayClickListener.currentPlayListener.stopPlayVoice();
			}
			onRealTimeTalkFrom = 0;
			realTimeAnim.start();
			realTalkView.setVisibility(View.VISIBLE);
			realTalkName.setText("您正在说话..");
			stopRealTalk.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onStopTalk(int code, GotyeMessage message, boolean isVoiceReal) {
		if (isVoiceReal) {
			onRealTimeTalkFrom = -1;
			realTimeAnim.stop();
			realTalkView.setVisibility(View.GONE);
		} else {
			if (code != 0) {
				ToastUtil.show(this, "时间太短...");
				return;
			} else if (message == null) {
				ToastUtil.show(this, "时间太短...");
				return;
			}
			api.sendMessage(message);
			message.setStatus(GotyeMessage.STATUS_SENDING);
			adapter.addMsgToBottom(message);
			scrollToBottom();
			api.decodeMessage(message);
		}

	}

	@Override
	public void onPlayStop(int code) {
		onRealTimeTalkFrom = -1;
		realTimeAnim.stop();
		realTalkView.setVisibility(View.GONE);
		setPlayingId(0);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onPlayStartReal(int code, long roomId, String who) {
		if (code == 0 && roomId == this.room.getRoomID()) {
			onRealTimeTalkFrom = 1;
			realTalkView.setVisibility(View.VISIBLE);
			realTalkName.setText(who + "正在说话..");
			realTimeAnim.start();
			stopRealTalk.setVisibility(View.GONE);
			if (GotyeVoicePlayClickListener.isPlaying) {
				GotyeVoicePlayClickListener.currentPlayListener.stopPlayVoice();
			}
		}
	}

	@Override
	public void onRequestUserInfo(int code, GotyeUser user) {
		// TODO Auto-generated method stub
		this.user = user;
	}

	@Override
	public void onDownloadMedia(int code, String path, String url) {
		// TODO Auto-generated method stub
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onUserDismissGroup(GotyeGroup group, GotyeUser user) {
		// TODO Auto-generated method stub
		if (this.group!=null&&group.getGroupID() == this.group.getGroupID()) {
			Intent i = new Intent(this, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			Toast.makeText(getBaseContext(), "群主解散了该群,会话结束", Toast.LENGTH_SHORT)
					.show();
			finish();
			startActivity(i);
		}
	}

	@Override
	public void onUserKickdFromGroup(GotyeGroup group, GotyeUser kicked,
			GotyeUser actor) {
		// TODO Auto-generated method stub
		if (this.group!=null&&group.getGroupID() == this.group.getGroupID()) {
			if (kicked.getName().equals(currentLoginUser.getName())) {
				Intent i = new Intent(this, MainActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Toast.makeText(getBaseContext(), "您被踢出了群,会话结束",
						Toast.LENGTH_SHORT).show();
				finish();
				startActivity(i);
			}

		}
	}
	@Override
	public void onReport(int code, GotyeMessage message) {
		// TODO Auto-generated method stub
		if (code == GotyeStatusCode.CODE_OK) {
			ToastUtil.show(this, "举报成功");
		} else {
			ToastUtil.show(this, "举报失败");
		}
		super.onReport(code, message);
	}
	@Override
	public void onRequestRoomInfo(int code, GotyeRoom room) {
		// TODO Auto-generated method stub
		if(this.room!=null&&this.room.getRoomID()==room.getRoomID()){
			title.setText("聊天室："+room.getRoomName());	
		}
		super.onRequestRoomInfo(code, room);
	}
	@Override
	public void onRequestGroupInfo(int code, GotyeGroup group) {
		// TODO Auto-generated method stub
		if(this.group!=null&&this.group.getGroupID()==group.getGroupID()){
			title.setText("聊天室："+group.getGroupName());	
		}
	}
	@Override
	public void onDecodeMessage(int code, GotyeMessage message) {
		// TODO Auto-generated method stub
		Log.d("", "");
		super.onDecodeMessage(code, message);
	}
	
}
