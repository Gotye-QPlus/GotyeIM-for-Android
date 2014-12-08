package com.open_demo.base;

import java.util.List;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeUser;
import com.gotye.api.listener.DownloadListener;
import com.gotye.api.listener.UserListener;

import android.annotation.SuppressLint;
import android.app.Fragment;

@SuppressLint("NewApi")
public class BaseFragment extends Fragment implements UserListener,
		DownloadListener {

	public GotyeAPI api=GotyeAPI.getInstance();
	@Override
	public void onDownloadMedia(int code, String path, String url) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRequestUserInfo(int code, GotyeUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onModifyUserInfo(int code, GotyeUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSearchUserList(int code, List<GotyeUser> mList, int pagerIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAddFriend(int code, GotyeUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetFriendList(int code, List<GotyeUser> mList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAddBlocked(int code, GotyeUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRemoveFriend(int code, GotyeUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRemoveBlocked(int code, GotyeUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetBlockedList(int code, List<GotyeUser> mList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetProfile(int code, GotyeUser user) {
		// TODO Auto-generated method stub
		
	}

}
