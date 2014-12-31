package com.open_demo.adapter;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gotye.api.GotyeUser;
import com.open_demo.R;
import com.open_demo.util.ImageCache;

public class MemberAdapter extends BaseAdapter {
	private Context context;
	private List<GotyeUser> members;
	private float density;

	public boolean showName=false;
	public MemberAdapter(Context context, List<GotyeUser> members) {
		this.context = context;
		this.members = members;
		density = context.getResources().getDisplayMetrics().density;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return members.size();
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
		RelativeLayout layout = new RelativeLayout(context);
		ImageView icon = new ImageView(context);
		GotyeUser u = (GotyeUser) getItem(arg0);
		icon.setId(arg0 + 1);
		layout.addView(icon, (int) (density * 60), (int) (60 * density));
		icon.setImageResource(R.drawable.head_icon_user);
		setMemberIcon(icon, u);
		if(showName){
			TextView name = new TextView(context);
			name.setText(u.getName());
			name.setGravity(Gravity.CENTER_HORIZONTAL);
			RelativeLayout.LayoutParams nameParam = new RelativeLayout.LayoutParams(
					-2, -2);
			nameParam.addRule(RelativeLayout.BELOW, arg0 + 1);
			nameParam
					.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			layout.addView(name, nameParam);
		}
		return layout;
	}

	private void setMemberIcon(ImageView iconView, GotyeUser member) {
		if(member.getIcon()!=null){
			ImageCache.getInstance().setIcom(iconView,member);
		}
		
	}
}