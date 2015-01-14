package com.open_demo.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeUser;
import com.open_demo.R;
import com.open_demo.util.PreferenceUitl;

public class GroupMemberAdapter extends BaseAdapter {
	private Context context;
	private List<GotyeUser> members;
	private float density;
	private GotyeGroup group;
	private boolean deleteFlag;
	private String currentLoginName;
	private GotyeAPI api;
	public GroupMemberAdapter(Context context,GotyeGroup group, List<GotyeUser> members) {
		this.context = context;
		this.group=group;
		this.members = members;
		api=GotyeAPI.getInstance();
		currentLoginName=api.getCurrentLoginUser().getName();
		density = context.getResources().getDisplayMetrics().density;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int size = members.size();
		return size;
	}
 
	public boolean isDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
		this.notifyDataSetChanged();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return members.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		RelativeLayout layout = new RelativeLayout(context);
		ImageView icon = new ImageView(context);
		GotyeUser u = (GotyeUser) getItem(arg0);
		if (u.getName().equals("")) {
			icon.setImageResource(R.drawable.add_member);
			layout.addView(icon, (int) (density * 60), (int) (60 * density));
		} else {
			icon.setImageResource(R.drawable.head_icon_user);
			icon.setId(arg0 + 1);
			layout.addView(icon, (int) (density * 60), (int) (60 * density));
			setMemberIcon(icon, u);
			if (deleteFlag) {
				if (!u.getName().equals(currentLoginName)) {
					ImageView deleteIcon = new ImageView(context);
					RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
							-2, -2);
					param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
							RelativeLayout.TRUE);
					param.addRule(RelativeLayout.ALIGN_PARENT_TOP,
							RelativeLayout.TRUE);
					deleteIcon.setImageResource(R.drawable.del_flag);
					layout.addView(deleteIcon, param);
				}
			}
			if (PreferenceUitl.getBooleanValue(context,
					"g_show_name_"+group.getGroupID())) {
				TextView name = new TextView(context);
				name.setText(u.getName());
				name.setGravity(Gravity.CENTER_HORIZONTAL);
				RelativeLayout.LayoutParams nameParam = new RelativeLayout.LayoutParams(
						-2, -2);
				nameParam.addRule(RelativeLayout.BELOW, arg0 + 1);
				nameParam.addRule(RelativeLayout.CENTER_HORIZONTAL,
						RelativeLayout.TRUE);
				layout.addView(name, nameParam);
			}
		}

		return layout;
	}

	private void setMemberIcon(ImageView iconView, GotyeUser member) {
		if (member.getIcon()!=null) {
			File f = new File(member.getIcon().path);
			if (f.exists()) {
				Bitmap bmp = BitmapFactory.decodeFile(member.getIcon().path);
				if (bmp != null) {
					iconView.setImageBitmap(bmp);
					return;
				}
			}
			api.downloadMedia(member.getIcon().getUrl());
		}
	}
}