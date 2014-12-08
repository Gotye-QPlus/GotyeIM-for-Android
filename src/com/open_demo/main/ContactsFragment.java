package com.open_demo.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
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
import android.widget.TextView.OnEditorActionListener;

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
import com.open_demo.util.ToastUtil;
import com.open_demo.view.SideBar;
import com.open_demo.view.SideBar.OnTouchingLetterChangedListener;

@SuppressLint("NewApi")
public class ContactsFragment extends BaseFragment implements OnClickListener {
	private ListView userListView;
	private SideBar sideBar;
	private CharacterParser characterParser;
	private PinyinComparator pinyinComparator = new PinyinComparator();
	private ArrayList<GotyeUserProxy> contacts = new ArrayList<GotyeUserProxy>();
	private List<GotyeUser> sampleUserList = new ArrayList<GotyeUser>();
	private ContactsAdapter adapter;
	public String currentLoginName;

	private EditText search;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.layout_contacts, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		api.addListerer(this);
		characterParser = CharacterParser.getInstance();
		GotyeUser user = api.getCurrentLoginUser();
		currentLoginName = user.getName();
		initData();
		initView();
		setAdapter();
	}

	private void initData() {
		loadLocalUser();
		if (sampleUserList == null) {
			api.requestFriendList();
		}
	}

	private void loadLocalUser() {
		sampleUserList = api.getLocalFriendList();
		handleUser(sampleUserList);
	}

	public void initView() {
		sideBar = (SideBar) getView().findViewById(R.id.sidrbar);
		userListView = (ListView) getView().findViewById(R.id.listview);
		getView().findViewById(R.id.add).setOnClickListener(this);
		search = (EditText) getView().findViewById(R.id.contact_search_input);
		search.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		search.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				Intent i = new Intent(getActivity(), SearchPage.class);
				i.putExtra("keyword", search.getText().toString().trim());
				i.putExtra("search_type", 1);
				startActivity(i);
				return true;
			}
		});
		search.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
					Intent i = new Intent(getActivity(), SearchPage.class);
					i.putExtra("keyword", search.getText().toString().trim());
					i.putExtra("search_type", 0);
					startActivity(i);
					return true;
				}
				return false;
			}
		});
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
		contacts.clear();
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
				contacts.add(userProxy);
			}
			Collections.sort(contacts, pinyinComparator);
		}
		GotyeUserProxy room_group = new GotyeUserProxy(new GotyeUser());
		room_group.gotyeUser.Id = -1;
		room_group.firstChar = "↑";
		contacts.add(0, room_group);
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}

	}

	private void selectUserByKeyword(String keyWord) {
		contacts.clear();
		if (sampleUserList != null) {
			for (GotyeUser user : sampleUserList) {
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
				contacts.add(userProxy);
			}
			Collections.sort(contacts, pinyinComparator);
		}
		GotyeUserProxy room_group = new GotyeUserProxy(new GotyeUser());
		room_group.gotyeUser.Id = -1;
		room_group.firstChar = "↑";
		contacts.add(0, room_group);
	}

	private void setAdapter() {
		adapter = new ContactsAdapter(getActivity(), contacts);
		userListView.setAdapter(adapter);
		userListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				GotyeUserProxy userProxy = ((GotyeUserProxy) adapter
						.getItem(arg2));
				if (userProxy.gotyeUser.Id == -1) {
					Intent group = new Intent(getActivity(),
							GroupRoomListPage.class);
					group.putExtra("type", 1);
					startActivity(group);
					return;
				}
				// GotyeUser user=userProxy.gotyeUser;
				// GotyeUserManager.getInstance().requestAddblocked(user.name);
				Intent i = new Intent(getActivity(), ChatPage.class);
				i.putExtra("user", userProxy.gotyeUser);
				i.putExtra("from", 200);
				startActivity(i);
				// ConversationDBManager.getInstance(getActivity())
				// .clearUnReadTip(Conversation.USER_MSG,
				// currentLoginName, user.gotyeUser.getName(), 0);
			}
		});
		userListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				GotyeUserProxy userProxy = ((GotyeUserProxy) adapter
						.getItem(arg2));
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
		loadLocalUser();
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
		default:
			break;
		}
	}

	private void addUser() {
		if (tools.isShowing()) {
			tools.dismiss();
			final EditText inputUserName = new EditText(getActivity());
			new AlertDialog.Builder(getActivity())
					.setTitle("请输入")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setView(inputUserName)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									String name = inputUserName.getText()
											.toString();
									if (!TextUtils.isEmpty(name)
											&& !currentLoginName.equals(name)) {
										api.requestAddFriend(new GotyeUser(name));
										ProgressDialogUtil.showProgress(
												getActivity(), "正在添加好友");
										dialog.dismiss();
									} else {
										if (currentLoginName.equals(name)) {
											ToastUtil.show(getActivity(),
													"不能添加自己為好友");
										} else {
											ToastUtil.show(getActivity(),
													"請輸入好友名字");
										}
										dialog.dismiss();
										inputUserName.setText("");
									}
									hideKeyboard(inputUserName);
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									hideKeyboard(inputUserName);
								}
							}).show();
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
			ToastUtil.show(getActivity(), "添加好友成功");
			loadLocalUser();
		} else {
			ToastUtil.show(getActivity(), "添加好友失败");
		}
	}
	@Override
	public void onDownloadMedia(int code, String path, String url) {
		// TODO Auto-generated method stub
     adapter.notifyDataSetChanged();
	}
}
