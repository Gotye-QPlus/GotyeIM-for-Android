package com.open_demo.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeUser;
import com.gotye.api.PathUtil;
import com.open_demo.R;
import com.open_demo.adapter.SelectUserAdapter;
import com.open_demo.base.BaseActivity;
import com.open_demo.bean.GotyeUserProxy;
import com.open_demo.util.BitmapUtil;
import com.open_demo.util.CharacterParser;
import com.open_demo.util.PinyinComparator;
import com.open_demo.util.URIUtil;
import com.open_demo.view.SideBar;
import com.open_demo.view.SideBar.OnTouchingLetterChangedListener;

@SuppressLint("DefaultLocale")
public class CreateGroupSelectUser extends BaseActivity implements OnClickListener {
	private ListView userListView;
	private SideBar sideBar;
	private CharacterParser characterParser;
	private PinyinComparator pinyinComparator = new PinyinComparator();
	private ArrayList<GotyeUserProxy> contacts = new ArrayList<GotyeUserProxy>();
	private SelectUserAdapter adapter;
	private Button sure;
	private View createGroupPanel;

	private EditText inputGroupName,inputGroupInfo;

	private ProgressDialog dialog;

	private int from;
	
	private CheckBox needValidate,isPublic;
    private ArrayList<String> hasInGroupMembers;
    public String selectedGroupHeadPath=null;
    private ImageView groupIcon;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		api.addListener(this);
		from = getIntent().getIntExtra("from", 0);
		hasInGroupMembers=getIntent().getStringArrayListExtra("member");
		setContentView(R.layout.layout_select_user);
		characterParser = CharacterParser.getInstance();
		initView();
		convert();
	}

	private void initView() {
		sure = (Button) findViewById(R.id.ok);
		sure.setOnClickListener(this);
		userListView = (ListView) findViewById(R.id.listview);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		findViewById(R.id.back).setOnClickListener(this);
		createGroupPanel = findViewById(R.id.group_name_layout);
		inputGroupName = (EditText) createGroupPanel
				.findViewById(R.id.input_group_name);
		createGroupPanel.findViewById(R.id.create_group).setOnClickListener(
				this);
		groupIcon=(ImageView) createGroupPanel.findViewById(R.id.set_group_head);
		groupIcon.setOnClickListener(this);
		if (from == 1) {
			((TextView) findViewById(R.id.title_tx)).setText("添加群成员");
		}
		inputGroupInfo = (EditText) createGroupPanel
				.findViewById(R.id.input_group_info);
		needValidate=(CheckBox) findViewById(R.id.need_validate);
		isPublic=(CheckBox) findViewById(R.id.is_public);
		

	}
	public void hideKeyboard(){
		// 隐藏输入法 
		InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
		// 显示或者隐藏输入法 
		imm.hideSoftInputFromWindow(inputGroupName.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	@Override
	protected void onDestroy() {
		api.removeListener(this);
		super.onDestroy();
	}

	private void convert() {
		contacts.clear();
		List<GotyeUser> userList =api.getLocalFriendList();
		if(hasInGroupMembers!=null){
			 for(String name:hasInGroupMembers){
				 GotyeUser u=new GotyeUser();
				 u.setName(name);
				 if(userList!=null&&userList.contains(u)){
					 userList.remove(u);
				 }
			 }
		}
		if(userList==null){
			return;
		}
		for (GotyeUser user : userList) {
			String pinyin = characterParser.getSelling(user.getName());
			String sortString = pinyin.substring(0, 1).toUpperCase();
            GotyeUserProxy userProxy=new GotyeUserProxy(user);
			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				userProxy.firstChar = sortString.toUpperCase();
			} else {
				userProxy.firstChar = "#";
			}
			contacts.add(userProxy);
		}
		Collections.sort(contacts, pinyinComparator);
		adapter = new SelectUserAdapter(this, contacts);
		userListView.setAdapter(adapter);
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					userListView.setSelection(position);
				}

			}
		});
	}

	String members;

	public void getCount(Map<String, Boolean> selected) {
		int size = selected.keySet().size();
		sure.setText("确认(" + size + ")");
		if (size > 0) {
			String[] names = new String[size];
			// members=
			selected.keySet().toArray(names);
			String str = "";
			for (int i = 0; i < size; i++) {
				str += names[i] + ",";
			}
			str = str.substring(0, str.length() - 1);
			members = str;
		}

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.back:
			if (createGroupPanel.getVisibility() == View.VISIBLE) {
				createGroupPanel.setVisibility(View.GONE);
				sure.setVisibility(View.VISIBLE);
			} else {
				finish();
			}
			break;
		case R.id.ok:
			if (from == 1) {
				if (members != null && !"".equals(members)) {
					Intent data = new Intent();
					data.putExtra("member", members);
					setResult(0, data);
					finish();
				} else {
					Toast.makeText(this, "请选择成员", Toast.LENGTH_SHORT).show();
				}
			} else {
				createGroupPanel.setVisibility(View.VISIBLE);
				sure.setVisibility(View.GONE);
			}
			break;
		case R.id.create_group:
			create();
			break;
		case R.id.set_group_head:
			Intent intent;
			intent = new Intent(Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.setType("image/jpeg");
			startActivityForResult(intent, 1);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (data != null) {
				Uri selectedImage = data.getData();
				if (selectedImage != null) {
					String path=URIUtil.uriToPath(this, selectedImage);
					setPicture(path);
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	private void setPicture(String path) {
		File f = new File(PathUtil.getAppFIlePath());
		if (!f.isDirectory()) {
			f.mkdirs();
		}
		File file = new File(PathUtil.getAppFIlePath()
				+ System.currentTimeMillis() + "jpg");
		if (file.exists()) {
			file.delete();
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Bitmap smaillBit = BitmapUtil.getSmallBitmap(path, 50, 50);
		String smallPath = BitmapUtil.saveBitmapFile(smaillBit);
		selectedGroupHeadPath=smallPath;
		groupIcon.setImageBitmap(smaillBit);
	}
	private void create() {
		String groupName = inputGroupName.getText().toString();
		if (groupName != null && !"".equals(groupName)) {
			dialog = new ProgressDialog(this);
			dialog.setMessage("正在创建群....");
			dialog.show();
			boolean type=isPublic.isChecked();
			boolean needAuth=needValidate.isChecked();
			String info=inputGroupInfo.getText().toString().trim();
			
			GotyeGroup toCreate=new GotyeGroup();
			toCreate.setGroupName(groupName);
			toCreate.setOwnerType(type?0:1);
			toCreate.setNeedAuthentication(needAuth);
			toCreate.setGroupInfo(info);
			
			api.createGroup(toCreate,selectedGroupHeadPath);
			
			hideKeyboard();
		} else {
			Toast.makeText(this, "请输入群名字!", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onCreateGroup(int code, GotyeGroup group) {
		if (members != null && !"".equals(members)) {
			String[] membr=members.split(",");
			if(membr!=null&&membr.length>0){
				for(String m:membr){
					api.inviteUserToGroup(new GotyeUser(m), group,
							"过来聊聊");
				}
			}
		}
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		Toast.makeText(this, "群创建成功!", Toast.LENGTH_SHORT).show();
		finish();

	}

}
