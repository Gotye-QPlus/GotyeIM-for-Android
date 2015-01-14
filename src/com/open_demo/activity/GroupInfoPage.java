package com.open_demo.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeNotify;
import com.gotye.api.GotyeStatusCode;
import com.gotye.api.GotyeUser;
import com.open_demo.R;
import com.open_demo.adapter.GroupMemberAdapter;
import com.open_demo.base.BaseActivity;
import com.open_demo.util.BitmapUtil;
import com.open_demo.util.PreferenceUitl;
import com.open_demo.util.ProgressDialogUtil;
import com.open_demo.util.ToastUtil;
import com.open_demo.util.URIUtil;
import com.open_demo.view.ChangeGroupOwnerDialog;

public class GroupInfoPage extends BaseActivity {
	private GotyeGroup group;
	private static final int REQUEST_PIC = 1;
	private GroupMemberAdapter adapter;
	private GridView memberView;
	private ImageView ownerIcon;
	private GotyeUser groupOwner;
	private View delDialog;
	private String currentLoginName;
	private List<GotyeUser> members;
	private Button joinGroupBtn, dismissGroupBtn, leaveGroupBtn;

	private EditText groupName, infoVIew;
	private ImageView groupIcon;

	private CheckBox needValidate, isPublic;
	private boolean canModify = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		currentLoginName = api.getCurrentLoginUser().getName();
		group = (GotyeGroup) getIntent().getSerializableExtra("group");
		group = api.requestGroupInfo(group.getGroupID(), true);

		if (currentLoginName.equals(group.getOwnerAccount())) {
			canModify = true;
		}
		setContentView(R.layout.layout_group_info);
		api.addListener(this);
		groupOwner = api.requestUserInfo(group.getOwnerAccount(), true);
		initView();

	}

	private void initView() {
		memberView = (GridView) findViewById(R.id.members);
		((TextView) findViewById(R.id.owner_name)).setText(group
				.getOwnerAccount());
		((TextView) findViewById(R.id.group_name))
				.setText(group.getGroupName());

		groupName = (EditText) findViewById(R.id.for_modify_group_name);
		groupName.setText(group.getGroupName());
		groupName.setEnabled(canModify);
		groupName.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		groupName.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {

				if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
					modify("");
					return true;
				}
				return false;
			}
		});

		infoVIew = (EditText) findViewById(R.id.info);
		infoVIew.setText(group.getGroupInfo());
		infoVIew.setEnabled(canModify);
		infoVIew.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		infoVIew.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {

				if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
					modify("");
					return true;
				}
				return false;
			}
		});

		isPublic = (CheckBox) findViewById(R.id.is_public);
		isPublic.setChecked(group.getOwnerType() == 0);
		isPublic.setEnabled(canModify);
		isPublic.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				modify("");
			}
		});

		needValidate = (CheckBox) findViewById(R.id.need_validate);
		needValidate.setChecked(group.isNeedAuthentication());
		needValidate.setEnabled(canModify);
		needValidate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				modify("");
			}
		});
		groupIcon = (ImageView) findViewById(R.id.group_ioon);
		groupIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (canModify) {
					Intent intent;
					intent = new Intent(Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/jpeg");
					startActivityForResult(intent, REQUEST_PIC);
				}

			}
		});
		if (group.getIcon() != null) {
			Bitmap icon = BitmapUtil.getBitmap(group.getIcon().getPath());
			if (icon != null) {
				groupIcon.setImageBitmap(icon);
			}
			api.downloadMedia(group.getIcon().getUrl());
		}

		CheckBox set_to_top = ((CheckBox) findViewById(R.id.set_to_top));
		boolean setTop = PreferenceUitl.getBooleanValue(this, "set_top_"
				+ group.getGroupID());
		set_to_top.setChecked(setTop);
		set_to_top.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				PreferenceUitl.setBooleanValue(GroupInfoPage.this, "set_top_"
						+ group.getGroupID(), arg1);
				api.markSessionTop(group, arg1);
			}
		});

		ownerIcon = (ImageView) findViewById(R.id.group_owner_icon);
		delDialog = findViewById(R.id.del_dialog);

		joinGroupBtn = (Button) findViewById(R.id.join_group);
		joinGroupBtn.setVisibility(View.GONE);
		dismissGroupBtn = (Button) findViewById(R.id.dismiss_group);
		leaveGroupBtn = (Button) findViewById(R.id.leave_group);
		if (members == null) {
			members = new ArrayList<GotyeUser>();
		}

		if (group.getOwnerAccount().equals(currentLoginName)) {
			dismissGroupBtn.setVisibility(View.VISIBLE);
			GotyeUser add = new GotyeUser();
			add.setName("");
			members.add(add);
		} else {
			dismissGroupBtn.setVisibility(View.GONE);
		}

		if (adapter == null) {
			adapter = new GroupMemberAdapter(this, group, members);
		}
		memberView.setAdapter(adapter);
		setListener();
		api.requestGroupMemberList(group, 0);
		api.requestGroupInfo(group.getGroupID(), true);
	}

	private void refreshValue() {
		groupName.setText(group.getGroupName());
		infoVIew.setText(group.getGroupInfo());
		needValidate.setChecked(group.isNeedAuthentication());
		isPublic.setChecked(group.getOwnerType() == 0);
		if (group.getIcon() != null) {
			Bitmap icon = BitmapUtil.getBitmap(group.getIcon().getPath());
			if (icon != null) {
				groupIcon.setImageBitmap(icon);
			}
			api.downloadMedia(group.getIcon().getUrl());
		}
	}

	private void setListener() {
		memberView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (group.getOwnerAccount().equals(currentLoginName)) {
					adapter.setDeleteFlag(true);
					return true;
				}
				return false;
			}
		});
		memberView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				GotyeUser user = (GotyeUser) adapter.getItem(arg2);
				if (user.getName().equals("")) {
					ArrayList<String> names = new ArrayList<String>();
					for (GotyeUser userInGroup : members) {
						if (!TextUtils.isEmpty(userInGroup.getName())) {
							names.add(userInGroup.getName());
						}
					}
					Intent intent = new Intent(GroupInfoPage.this,
							CreateGroupSelectUser.class);
					intent.putExtra("from", 1);
					intent.putStringArrayListExtra("members", names);
					startActivityForResult(intent, 0);
					adapter.setDeleteFlag(false);
					return;
				}
				if (adapter.isDeleteFlag()) {
					if (user.getName().equals(currentLoginName)) {
						return;
					}
					dialogToDeleteMember(user);
					return;
				}
			}
		});
		CheckBox showMemberName = ((CheckBox) findViewById(R.id.show_member_name));
		showMemberName.setChecked(PreferenceUitl.getBooleanValue(
				GroupInfoPage.this, "g_show_name_" + group.getGroupID()));
		showMemberName
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {
						PreferenceUitl.setBooleanValue(GroupInfoPage.this,
								"g_show_name_" + group.getGroupID(), arg1);
						if (adapter != null) {
							adapter.notifyDataSetChanged();
						}
					}
				});

		CheckBox disturb = ((CheckBox) findViewById(R.id.no_disturb));
		disturb.setChecked(api.isGroupDontdisturb(group.getGroupID()));
		disturb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					api.setGroupDontdisturb(group.getGroupID());
				} else {
					api.removeGroupDontdisturb(group.getGroupID());
				}
			}
		});
	}

	private void modify(String path) {
		String grounName = groupName.getText().toString().trim();
		String groupInfo = infoVIew.getText().toString().trim();
		boolean neewValidate = needValidate.isChecked();
		boolean publicState = isPublic.isChecked();

		if (TextUtils.isEmpty(grounName)) {
			ToastUtil.show(this, "群名不能改为空");
			return;
		}
		GotyeGroup forModify=new GotyeGroup(group.getGroupID());
		forModify.setGroupName(grounName);
		forModify.setGroupInfo(groupInfo);
		forModify.setOwnerType(publicState ? 0 : 1);
		forModify.setNeedAuthentication(neewValidate);
		api.requestModifyGroupInfo(forModify, path);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK) {
			if (data != null) {
				Uri selectedImage = data.getData();
				if (selectedImage != null) {
					String path = URIUtil.toPath(this, selectedImage);
					if (!TextUtils.isEmpty(path)) {
						setPicture(path);
					} else {
						ToastUtil.show(this, "文件不存在");
					}
				}
			}
		} else {
			if (data != null) {
				String members = data.getStringExtra("member");
				if (members != null && !"".equals(members)) {
					String[] memberlist = members.split(",");
					for (String member : memberlist) {
						api.inviteUserToGroup(new GotyeUser(member), group,
								"进来聊聊");
					}
				}
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void setPicture(String path) {
		File f = new File(path);
		String smallImagePath = path;
		if (f.exists()) {
			if (f.length() > 400) {
				smallImagePath = BitmapUtil.compressImage(path);
			}
		}
		smallImagePath = BitmapUtil.check(smallImagePath);
		if (!TextUtils.isEmpty(smallImagePath)) {
			modify(smallImagePath);
		}
	}

	public void back(View view) {
		if (delDialog.getVisibility() == View.VISIBLE) {
			delDialog.setVisibility(View.GONE);
			return;
		}
		finish();
	}

	private void setGroupMember(List<GotyeUser> members) {
		if(members==null){
			return;
		}
		this.members.addAll(0, members);
		if (!members.contains(api.getCurrentLoginUser())) {
			joinGroupBtn.setVisibility(View.VISIBLE);
		} else {
			leaveGroupBtn.setVisibility(View.VISIBLE);
		}
		
		if(members.contains(api.getCurrentLoginUser())){
			joinGroupBtn.setVisibility(View.GONE);
		}else{
			joinGroupBtn.setVisibility(View.VISIBLE);
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		back(null);
	}

	@Override
	protected void onDestroy() {
		api.removeListener(this);
		super.onDestroy();
	}

	public void joinGroup(View view) {
		if(group.isNeedAuthentication()){
			new AlertDialog.Builder(this).setMessage("是否申请加入该群？").setPositiveButton("申请", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					ProgressDialogUtil.showProgress(GroupInfoPage.this, "正在发送申请信息...");
					api.requestJoinGroup(group, "群主好人，求加入...");
				}
			}).setNegativeButton("取消", null).create().show();
		}else{
			ProgressDialogUtil.showProgress(this, "正在加入群...");
			api.joinGroup(group);
		}
		
	}

	public void dismissGroup(View view) {

		Dialog d = new AlertDialog.Builder(this).setMessage("确定解散该群?")
				.setPositiveButton("解散", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						api.dismissGroup(group);
					}
				}).setNegativeButton("取消", null).create();
		d.show();
	}

	public void leaveGroup(View view) {
		if(!currentLoginName.equals(group.getOwnerAccount())){
			ProgressDialogUtil.showProgress(this, "正在离开群...");
			api.leaveGroup(group);
			return;
		}
		if (members.size() ==1) {
			ProgressDialogUtil.showProgress(this, "正在离开群...");
			api.leaveGroup(group);
			return;
		}
		
		if(members.size()==2&&members.contains(new GotyeUser(""))){
			ProgressDialogUtil.showProgress(this, "正在离开群...");
			api.leaveGroup(group);
			return;
		}
		
		List<GotyeUser> toSelected = new ArrayList<GotyeUser>();
		for (GotyeUser user : members) {
			if(user.getName().equals(group.getOwnerAccount())||"".equals(user.getName())){
				continue;
			}
			toSelected.add(user);
		}

		if (toSelected.size() == 0) {
			ProgressDialogUtil.showProgress(this, "正在离开群...");
			api.leaveGroup(group);
			return;
		}
		ChangeGroupOwnerDialog change = new ChangeGroupOwnerDialog(this);
		change.show();
		change.setMembers(group, toSelected);
	}

	public void dialogToDeleteMember(final GotyeUser member) {
		delDialog.setVisibility(View.VISIBLE);
		((TextView) delDialog.findViewById(R.id.content)).setText("您确定要将"
				+ member.getName() + "请出本群?");
		delDialog.findViewById(R.id.dialog_sure).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						api.kickOutUser(group, member);
						delDialog.setVisibility(View.GONE);
						ProgressDialogUtil.showProgress(GroupInfoPage.this,
								"正在踢出成员：" + member.getName());
					}
				});
		delDialog.findViewById(R.id.dialog_cancel).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						delDialog.setVisibility(View.GONE);
					}
				});
	}

	@Override
	public void onJoinGroup(int code, GotyeGroup group) {
		ProgressDialogUtil.dismiss();
		if (code == 0) {
			ToastUtil.show(this, "成功加入该群");
			joinGroupBtn.setVisibility(View.GONE);
			leaveGroupBtn.setVisibility(View.VISIBLE);
			if (members == null) {
				members = new ArrayList<GotyeUser>();
			}
			if(!members.contains(api.getCurrentLoginUser())){
				members.add(api.getCurrentLoginUser());
			}
			adapter.notifyDataSetChanged();
		} else {
			ToastUtil.show(this, "加群失败");
		}

	}

	@Override
	public void onLeaveGroup(int code, GotyeGroup group) {
		if (code == GotyeStatusCode.CODE_OK) {
			ToastUtil.show(this, "您成功离开了该群");
			finish();
			
			Intent i = new Intent(this, GroupRoomListPage.class);
			i.putExtra("group_id",group.getGroupID() );
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			
		} else {
			Toast.makeText(getBaseContext(), "离开群失败 code=" + code,
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onDismissGroup(int code, GotyeGroup group) {
		if (code == GotyeStatusCode.CODE_OK) {
			Intent i = new Intent(this, GroupRoomListPage.class);
			i.putExtra("group_id", group.getGroupID());
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			Toast.makeText(getBaseContext(), "您成功解散了该群", Toast.LENGTH_SHORT)
					.show();
			finish();
			startActivity(i);
		} else {
			Toast.makeText(getBaseContext(), "解散群失败 code=" + code,
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onKickOutUser(int code, GotyeGroup group) {
		if (code == 0) {
			members.clear();
			api.requestGroupMemberList(group, 0);
			adapter.setDeleteFlag(false);
			adapter.notifyDataSetChanged();
			ProgressDialogUtil.dismiss();
			ToastUtil.show(GroupInfoPage.this, "踢出成功!");
			GotyeUser add = new GotyeUser();
			add.setName("");
			members.add(add);
		}

	}

	@Override
	public void onDownloadMedia(int code, String path, String url) {
		if (group.getIcon() != null && url.equals(group.getIcon().getUrl())) {
			Bitmap icon = BitmapUtil.getBitmap(group.getIcon().getPath());
			if (icon != null) {
				groupIcon.setImageBitmap(icon);
			}
			return;
		}
		if (this.groupOwner != null && groupOwner.getIcon().url.equals(url)) {
			Bitmap bmp = BitmapUtil.getBitmap(path);
			if (bmp != null) {
				ownerIcon.setImageBitmap(bmp);
			}
			return;
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onRequestUserInfo(int code, GotyeUser user) {
		adapter.notifyDataSetChanged();
		if (user.getName().equals(group.getOwnerAccount())) {
			groupOwner = user;
			if (user.getIcon() != null) {
				Bitmap userIcon = BitmapUtil
						.getBitmap(user.getIcon().getPath());
				if (userIcon != null) {
					ownerIcon.setImageBitmap(userIcon);
				} else {
					api.downloadMedia(user.getIcon().getUrl());
				}
			}
		}
	}

	@Override
	public void onGetGroupMemberList(int code, List<GotyeUser> allList,
			List<GotyeUser> curList, GotyeGroup group, int pagerIndex) {
		setGroupMember(allList);
	}

	@Override
	public void onModifyGroupInfo(int code, GotyeGroup gotyeGroup) {
		// TODO Auto-generated method stub
		if (code == 0) {
			ToastUtil.show(this, "修改成功");
			this.group = gotyeGroup;
		} else {
			ToastUtil.show(this, "修改失败");
		}
		refreshValue();
	}

	@Override
	public void onChangeGroupOwner(int code, GotyeGroup group) {
		if (group.getGroupID() == this.group.getGroupID()) {
			ProgressDialogUtil.dismiss();
			ToastUtil.show(this, "您成功转让该群");
			ProgressDialogUtil.showProgress(this, "正在退出群...");
			api.leaveGroup(group);
		}
	}

	@Override
	public void onUserJoinGroup(GotyeGroup group, GotyeUser user) {
		// TODO Auto-generated method stub
		if (group.getGroupID() == this.group.getGroupID()) {
			if (adapter.isDeleteFlag()) {
				members.add(user);
			} else {
				members.add(members.size() - 1, user);
			}
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onUserLeaveGroup(GotyeGroup group, GotyeUser user) {
		// TODO Auto-generated method stub
		if (group.getGroupID() == this.group.getGroupID()) {
			members.remove(user);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onSendNotify(int code, GotyeNotify notify) {
		// TODO Auto-generated method stub
		if(code==GotyeStatusCode.CODE_OK){
			ProgressDialogUtil.dismiss();ToastUtil.show(this, "成功发送申请，等待群主回应");
		}
	}
	@Override
	public void onUserDismissGroup(GotyeGroup group, GotyeUser user) {
		// TODO Auto-generated method stub
		if (group.getGroupID() == this.group.getGroupID()) {
			Intent i = new Intent(this, GroupRoomListPage.class);
			i.putExtra("group_id", group.getGroupID());
			i.putExtra("type", 1);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			Toast.makeText(getBaseContext(), "群主解散了该群", Toast.LENGTH_SHORT)
					.show();
			finish();
			startActivity(i);
		}
	}
     
	@Override
	public void onReceiveReplayJoinGroup(int code, GotyeGroup group,
			GotyeUser sender, String message,boolean isAgree) {
		if(isAgree){
			//ProgressDialogUtil.showProgress(this, "群主同意了您的请求，您正在加入群...");
			//api.joinGroup(group);
//			if(members!=null){
//				members.add(api.getCurrentLoginUser());
//				adapter.notifyDataSetChanged();
//				ToastUtil.show(this, "您成功加入了该群");
//			}
		}
	}
	@Override
	public void onUserKickdFromGroup(GotyeGroup group, GotyeUser kicked,
			GotyeUser actor) {
		if (group.getGroupID() == this.group.getGroupID()) {
			if (kicked.getName().equals(currentLoginName)) {
				Intent i = new Intent(this, GroupRoomListPage.class);
				i.putExtra("group_id", group.getGroupID());
				i.putExtra("type", 1);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Toast.makeText(getBaseContext(),
						"您被" + actor.getName() + "踢出该群了", Toast.LENGTH_SHORT)
						.show();
				finish();
				startActivity(i);
			}
		}

	}
}
