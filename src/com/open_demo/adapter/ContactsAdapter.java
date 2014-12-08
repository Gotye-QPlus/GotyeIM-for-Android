package com.open_demo.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.open_demo.R;
import com.open_demo.activity.GroupRoomListPage;
import com.open_demo.bean.GotyeUserProxy;
import com.open_demo.util.ImageCache;

@SuppressLint("DefaultLocale")
public class ContactsAdapter extends BaseAdapter {
	private ArrayList<GotyeUserProxy> mData;
	private Context mContext;

	public ContactsAdapter(Context mContext, ArrayList<GotyeUserProxy> mData) {
		this.mContext = mContext;
		this.mData = mData;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		ViewHolder viewHolder;
		GotyeUserProxy user = (GotyeUserProxy) getItem(position);
		if (user.gotyeUser.Id == -1) {
			View roomGroupLayout = LayoutInflater.from(mContext).inflate(
					R.layout.item_contact_room_group, null);
			roomGroupLayout.findViewById(R.id.item_room).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							Intent room = new Intent(mContext, GroupRoomListPage.class);
							room.putExtra("type", 0);
							mContext.startActivity(room);
						}
					});
			roomGroupLayout.findViewById(R.id.item_group).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							Intent group = new Intent(mContext, GroupRoomListPage.class);
							group.putExtra("type", 1);
							mContext.startActivity(group);
						}
					});
			return roomGroupLayout;
		}

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
		if(viewHolder==null){
			arg1 = LayoutInflater.from(mContext).inflate(
					R.layout.layout_contacts_item, null);
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) arg1.findViewById(R.id.icon);
			viewHolder.firstChar = (TextView) arg1
					.findViewById(R.id.first_char);
			viewHolder.name = (TextView) arg1.findViewById(R.id.name);
			arg1.setTag(viewHolder);
		}
		viewHolder.name.setText("");
		String name = user.gotyeUser.getName();
//		if(user.gotyeUser.isBlocked()){
//			viewHolder.name.setText(name+" [黑名单好友]");
//		}else{
			viewHolder.name.setText(name);
//		}
		
		int section = getSectionForPosition(position);
		if (position == getPositionForSection(section)) {
			viewHolder.firstChar.setText(user.firstChar);
			viewHolder.firstChar.setVisibility(View.VISIBLE);
		} else {
			viewHolder.firstChar.setVisibility(View.GONE);
		}
		setIcon(viewHolder, user);
		return arg1;
	}

	private void setIcon(ViewHolder viewHolder, GotyeUserProxy user) {
		if(user.gotyeUser.getIcon()!=null){
               ImageCache.getInstance().setIcom(viewHolder.icon, user.gotyeUser.getIcon().getPath(), user.gotyeUser.getIcon().getUrl());			
		}else{
			viewHolder.icon.setImageResource(R.drawable.head_icon_user);
		}
	}

	public int getSectionForPosition(int position) {
		return mData.get(position).firstChar.charAt(0);
	}

	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = mData.get(i).firstChar;
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	static class ViewHolder {
		ImageView icon;
		TextView firstChar, name;
	}
}
