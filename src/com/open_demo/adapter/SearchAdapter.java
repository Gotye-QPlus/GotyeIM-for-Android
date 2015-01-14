package com.open_demo.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeUser;
import com.open_demo.R;
import com.open_demo.util.ImageCache;

public class SearchAdapter extends BaseAdapter {
	private List<GotyeUser> mFriends;
	private List<GotyeGroup> mGroups;
	private Context mContext;
	private int searchType;

	public SearchAdapter(Context mContext, List<GotyeUser> mData) {
		this.mContext = mContext;
		this.mFriends = mData;
		this.searchType = 0;
	}

	public SearchAdapter(List<GotyeGroup> mData, Context mContext) {
		this.mContext = mContext;
		this.mGroups = mData;
		this.searchType = 1;
	}

	@Override
	public int getCount() {
		if (searchType == 0) {
			return mFriends.size();
		} else {
			return mGroups.size();
		}
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		if (searchType == 0) {
			return mFriends.get(arg0);
		} else {
			return mGroups.get(arg0);
		}
	}

	public void addFriends(List<GotyeUser> data) {
		mFriends.addAll(data);
		notifyDataSetChanged();
	}

	public void addGroups(List<GotyeGroup> data) {
		mGroups.addAll(data);
		notifyDataSetChanged();
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		ViewHolder viewHolder;
		if (arg1 == null) {
			arg1 = LayoutInflater.from(mContext).inflate(
					R.layout.layout_contacts_item, null);
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) arg1.findViewById(R.id.icon);
			viewHolder.firstChar = (TextView) arg1
					.findViewById(R.id.first_char);
			viewHolder.name = (TextView) arg1.findViewById(R.id.name);
			arg1.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) arg1.getTag();
		}
		if(searchType==0){
			GotyeUser user = (GotyeUser) getItem(position);
			viewHolder.name.setText(user.getName());
			viewHolder.firstChar.setVisibility(View.GONE);
			setIcon(viewHolder, user);
		}else{
			GotyeGroup group = (GotyeGroup) getItem(position);
			viewHolder.name.setText(group.getGroupName());
			viewHolder.firstChar.setVisibility(View.GONE);
			setIcon(viewHolder,group);
		}
		
		return arg1;
	}

	private void setIcon(ViewHolder viewHolder, GotyeUser user) {
		if (user.getIcon() != null) {
			ImageCache.getInstance().setIcom(viewHolder.icon,user);
		}  
	}
	private void setIcon(ViewHolder viewHolder, GotyeGroup group) {
		if(group.getIcon()!=null){
			ImageCache.getInstance().setIcom(viewHolder.icon,group);
		}
	}

	static class ViewHolder {
		ImageView icon;
		TextView firstChar, name;
	}

	public void clear() {
		if (searchType == 0) {
			mFriends.clear();
		} else {
			mGroups.clear();
		}
		notifyDataSetChanged();
	}
}
