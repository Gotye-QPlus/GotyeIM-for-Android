package com.open_demo.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.gotye.api.GotyeGender;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeUser;
import com.open_demo.R;
import com.open_demo.adapter.SearchAdapter;
import com.open_demo.base.BaseActivity;

public class SearchPage extends BaseActivity {
	private ListView listview;
	private EditText input;
	private int pageIndex = 0;
	private String keyword;
	private SearchAdapter adapter;
	private int searchType = 0;
	private TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_search);
		listview = (ListView) findViewById(R.id.listview);
		input = (EditText) findViewById(R.id.key_word_input);
		searchType = getIntent().getIntExtra("search_type", 0);
		keyword = getIntent().getStringExtra("keyword");
		title = (TextView) findViewById(R.id.title);
		if (!TextUtils.isEmpty(keyword)) {
			input.setText(keyword);
			input.setSelection(keyword.length());
		}
		api.addListener(this);
		if (searchType == 0) {
			title.setText("搜索-好友");
		} else {
			title.setText("搜索-群");
		}

		input.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		input.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if (arg1 == EditorInfo.IME_ACTION_SEARCH||arg1==0) {
					if (adapter != null) {
						adapter.clear();
					}
					keyword = input.getText().toString();
					if (searchType == 0) {
						api.requestSearchUserList(pageIndex, keyword, "",GotyeGender.Femal.ordinal());
					} else {
						api.requestSearchGroup(keyword, pageIndex);
					}
					return true;
				}
				return false;
			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (searchType == 0) {
					GotyeUser user = (GotyeUser) adapter.getItem(arg2);
					Intent i = new Intent(SearchPage.this, UserInfoPage.class);
					i.putExtra("user", user);
					i.putExtra("from", 1);
					startActivity(i);
				} else {
					GotyeGroup group = (GotyeGroup) adapter.getItem(arg2);
					Intent i = new Intent(SearchPage.this, GroupInfoPage.class);
					i.putExtra("group", group);
					startActivity(i);
				}

			}
		});
	}

	public void back(View v) {
		finish();

	}

	@Override
	protected void onDestroy() {
		api.removeListener(this);
		super.onDestroy();
	}

	@Override
	public void onSearchUserList(int code, List<GotyeUser> mList, int pagerIndex) {
		if (mList != null) {
			List<GotyeUser> tempList = new ArrayList<GotyeUser>();
				for(GotyeUser user: mList){
					if(tempList.contains(user)){
						continue;
					}else{
						tempList.add(user);
					}
				}
			if (adapter == null) {
				adapter = new SearchAdapter(getBaseContext(), mList);
				listview.setAdapter(adapter);
			} else {
				adapter.clear();
				adapter.addFriends(mList);
			}
		}
	}

	@Override
	public void onGetGroupList(int code, List<GotyeGroup> grouplist) {
		if (grouplist != null) {
			if (adapter == null) {
				adapter = new SearchAdapter(grouplist, this);
				listview.setAdapter(adapter);
			} else {
				adapter.clear();
				adapter.addGroups(grouplist);
			}
		}
	}

	@Override
	public void onSearchGroupList(int code, List<GotyeGroup> mList,
			List<GotyeGroup> curList, int pageIndex) {
		if (curList != null) {
			if (adapter == null) {
				adapter = new SearchAdapter(curList, this);
				listview.setAdapter(adapter);
			} else {
				adapter.clear();
				adapter.addGroups(curList);
			}
		}
	}

	@Override
	public void onDownloadMedia(int code, String path, String url) {
		// TODO Auto-generated method stub
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

}
