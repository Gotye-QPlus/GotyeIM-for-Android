package com.open_demo.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gotye.api.GotyeUser;
import com.open_demo.R;
import com.open_demo.activity.ChatPage;
import com.open_demo.activity.CreateGroupSelectUser;
import com.open_demo.activity.GroupRoomListPage;
import com.open_demo.activity.SearchPage;
import com.open_demo.activity.UserInfoPage;
import com.open_demo.adapter.ContactsAdapter;
import com.open_demo.base.BaseFragment;
import com.open_demo.bean.GotyeUserProxy;
import com.open_demo.util.CharacterParser;
import com.open_demo.util.PinyinComparator;
import com.open_demo.util.ProgressDialogUtil;
import com.open_demo.view.SideBar;
import com.open_demo.view.SideBar.OnTouchingLetterChangedListener;

@SuppressLint("NewApi")
public class ContactsFragment extends BaseFragment implements OnClickListener {
	private ListView userListView;
	private SideBar sideBar;
	private CharacterParser characterParser;
	private PinyinComparator pinyinComparator = new PinyinComparator();
	private ArrayList<GotyeUserProxy> proxyFrinds = new ArrayList<GotyeUserProxy>();
	private List<GotyeUser> friends = new ArrayList<GotyeUser>();
	private ContactsAdapter adapter;
	public String currentLoginName;

	private EditText search;

	private boolean selectedFriendTab = true;
	private TextView friendTab, blockTab;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.layout_contacts, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		api.addListener(this);
		characterParser = CharacterParser.getInstance();
		GotyeUser user = api.getCurrentLoginUser();
		currentLoginName = user.getName();
		loadLocalFriends();
		api.requestFriendList();
		initView();
		setAdapter();
		int state = api.getOnLineState();
		if (state != 1) {
			setErrorTip(0);
		} else {
			setErrorTip(1);
		}
	}

	private void loadLocalFriends() {
		friends = api.getLocalFriendList();
		handleUser(friends);
	}

	private void loadLocalBlocks() {
		friends = api.getLocalBlockedList();
		handleUser(friends);
	}

	public void initView() {
		friendTab = (TextView) getView().findViewById(R.id.friend_tab);
		blockTab = (TextView) getView().findViewById(R.id.block_tab);

		friendTab.setOnClickListener(this);
		blockTab.setOnClickListener(this);

		sideBar = (SideBar) getView().findViewById(R.id.sidrbar);
		userListView = (ListView) getView().findViewById(R.id.listview);
		getView().findViewById(R.id.add).setOnClickListener(this);
		search = (EditText) getView().findViewById(R.id.contact_search_input);
		search.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String keyword = s.toString();
				search(keyword);
			}
		});
	}

	private void search(String keyWord) {
		if (TextUtils.isEmpty(keyWord)) {
			selectUserByKeyword(null);
		} else {
			selectUserByKeyword(keyWord);
		}
		adapter.notifyDataSetChanged();

	}

	private void handleUser(List<GotyeUser> userList) {
		proxyFrinds.clear();
		if (userList != null) {
			for (GotyeUser user : userList) {
				String pinyin = characterParser.getSelling(user.getName());
				String sortString = pinyin.substring(0, 1).toUpperCase();
				GotyeUserProxy userProxy = new GotyeUserProxy(user);
				if (sortString.matches("[A-Z]")) {
					userProxy.firstChar = sortString.toUpperCase();
				} else {
					userProxy.firstChar = "#";
				}
				proxyFrinds.add(userProxy);
			}
			Collections.sort(proxyFrinds, pinyinComparator);
		}
		GotyeUserProxy room = new GotyeUserProxy(new GotyeUser());
		room.gotyeUser.Id = -2;
		room.firstChar = "↑";
		proxyFrinds.add(0, room);
		GotyeUserProxy group = new GotyeUserProxy(new GotyeUser());
		group.gotyeUser.Id = -1;
		group.firstChar = "↑";
		proxyFrinds.add(1, group);
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}

	}

	private void selectUserByKeyword(String keyWord) {
		proxyFrinds.clear();
		if (friends != null) {
			for (GotyeUser user : friends) {
				if(user.Id<0){
					continue;
				}
				String pinyin = characterParser.getSelling(user.getName());
				if (keyWord != null) {
					if (!pinyin.startsWith(keyWord.toLowerCase())) {
						continue;
					}
				}
				String sortString = pinyin.substring(0, 1).toUpperCase();
				GotyeUserProxy userProxy = new GotyeUserProxy(user);
				if (sortString.matches("[A-Z]")) {
					userProxy.firstChar = sortString.toUpperCase();
				} else {
					userProxy.firstChar = "#";
				}
				proxyFrinds.add(userProxy);
			}
			Collections.sort(proxyFrinds, pinyinComparator);
			
			
		}
		GotyeUserProxy room = new GotyeUserProxy(new GotyeUser());
		room.gotyeUser.Id = -2;
		room.firstChar = "↑";
		proxyFrinds.add(0, room);
		GotyeUserProxy group = new GotyeUserProxy(new GotyeUser());
		group.gotyeUser.Id = -1;
		group.firstChar = "↑";
		proxyFrinds.add(1, group);
	}

	private void setAdapter() {
		adapter = new ContactsAdapter(getActivity(), proxyFrinds);
		userListView.setAdapter(adapter);
		userListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				GotyeUserProxy userProxy = ((GotyeUserProxy) adapter
						.getItem(arg2));
				if (selectedFriendTab) {
					if (userProxy.gotyeUser.Id == -2) {
						Intent room = new Intent(getActivity(),
								GroupRoomListPage.class);
						room.putExtra("type", 0);
						startActivity(room);
						return;
					}else if(userProxy.gotyeUser.Id == -1){
						 Intent group = new Intent(getActivity(), GroupRoomListPage.class);
						 group.putExtra("type", 1);
						 startActivity(group);
						 return;
					}
					if (userProxy.gotyeUser.Id == -1) {
						Intent group = new Intent(getActivity(),
								GroupRoomListPage.class);
						group.putExtra("type", 1);
						startActivity(group);
						return;
					}
					Intent i = new Intent(getActivity(), ChatPage.class);
					i.putExtra("user", userProxy.gotyeUser);
					i.putExtra("from", 200);
					startActivity(i);
				} else {
					if (userProxy.gotyeUser.Id == -2) {
						Intent room = new Intent(getActivity(),
								GroupRoomListPage.class);
						room.putExtra("type", 0);
						startActivity(room);
						return;
					}else if(userProxy.gotyeUser.Id == -1){
						 Intent group = new Intent(getActivity(), GroupRoomListPage.class);
						 group.putExtra("type", 1);
						 startActivity(group);
						 return;
					}
					Intent i = new Intent(getActivity(), UserInfoPage.class);
					i.putExtra("user", userProxy.gotyeUser);
					startActivity(i);
				}
			}
		});
		userListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				GotyeUserProxy userProxy = ((GotyeUserProxy) adapter
						.getItem(arg2));
				if (userProxy.gotyeUser.Id < 0) {
					return true;
				}
				Intent i = new Intent(getActivity(), UserInfoPage.class);
				i.putExtra("user", userProxy.gotyeUser);
				startActivity(i);
				return true;
			}
		});
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					userListView.setSelection(position);
				}

			}
		});

	}

	public void refresh() {
		if (selectedFriendTab) {
			loadLocalFriends();
		} else {
			loadLocalBlocks();
		}
	}

	@Override
	public void onGetFriendList(int code, List<GotyeUser> mList) {
		refresh();
	}

	@Override
	public void onGetBlockedList(int code, List<GotyeUser> mList) {
		refresh();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.add:
			showTools(arg0);
			break;
		case R.id.tools_add:
			addUser();
			break;
		case R.id.tools_group_chat:
			if (tools.isShowing()) {
				tools.dismiss();
				Intent toCreateGroup = new Intent(getActivity(),
						CreateGroupSelectUser.class);
				startActivity(toCreateGroup);
			}
			break;
		case R.id.friend_tab:
			selectedFriendTab = true;
			loadLocalFriends();
			api.requestFriendList();
			friendTab.setTextColor(getResources().getColor(R.color.app_color));
			blockTab.setTextColor(getResources().getColor(R.color.black));
			break;
		case R.id.block_tab:
			selectedFriendTab = false;
			loadLocalBlocks();
			api.requestBlockedList();
			blockTab.setTextColor(getResources().getColor(R.color.app_color));
			friendTab.setTextColor(getResources().getColor(R.color.black));
			break;
		default:
			break;
		}
	}

	private void addUser() {
		if (tools.isShowing()) {
			tools.dismiss();
			Intent toSreach = new Intent(getActivity(), SearchPage.class);
			toSreach.putExtra("search_type", 0);
			startActivity(toSreach);
		}
	}

	public void hideKeyboard(View view) {
		// 隐藏输入法
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getApplicationContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
		// 显示或者隐藏输入法
		imm.hideSoftInputFromWindow(view.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private PopupWindow tools;

	private void showTools(View v) {
		View toolsLayout = LayoutInflater.from(getActivity()).inflate(
				R.layout.layout_tools, null);
		toolsLayout.findViewById(R.id.tools_add).setOnClickListener(this);
		toolsLayout.findViewById(R.id.tools_group_chat)
				.setOnClickListener(this);
		tools = new PopupWindow(toolsLayout, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		tools.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		tools.setOutsideTouchable(false);
		tools.showAsDropDown(v, 0, 20);
		tools.update();
	}

	@Override
	public void onDestroy() {
		api.removeListener(this);
		super.onDestroy();
	}

	@Override
	public void onAddFriend(int code, GotyeUser user) {
		ProgressDialogUtil.dismiss();
		if (code == 0) {
			// ToastUtil.show(getActivity(), "添加好友成功");
			loadLocalFriends();
		} else {
			// ToastUtil.show(getActivity(), "添加好友失败");
		}
	}

	@Override
	public void onDownloadMedia(int code, String path, String url) {
		if(getActivity().isTaskRoot()){
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onLogin(int code, GotyeUser currentLoginUser) {
		// TODO Auto-generated method stub
		setErrorTip(1);
	}

	@Override
	public void onLogout(int code) {
		// TODO Auto-generated method stub
		setErrorTip(0);
	}

	@Override
	public void onReconnecting(int code, GotyeUser currentLoginUser) {
		// TODO Auto-generated method stub
		setErrorTip(-1);
	}

	private void setErrorTip(int code) {
		if (code == 1) {
			getView().findViewById(R.id.error_tip).setVisibility(View.GONE);
		} else {
			getView().findViewById(R.id.error_tip).setVisibility(View.VISIBLE);
			if (code == -1) {
				getView().findViewById(R.id.loading)
						.setVisibility(View.VISIBLE);
				((TextView) getView().findViewById(R.id.showText))
						.setText("正在连接登陆...");
				getView().findViewById(R.id.error_tip_icon).setVisibility(
						View.GONE);
			} else {
				getView().findViewById(R.id.loading).setVisibility(View.GONE);
				((TextView) getView().findViewById(R.id.showText))
						.setText("当前未登陆或网络异常");
				getView().findViewById(R.id.error_tip_icon).setVisibility(
						View.VISIBLE);
			}
		}
	}
}
