package com.open_demo.view;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeUser;
import com.open_demo.R;
import com.open_demo.util.BitmapUtil;
import com.open_demo.util.ProgressDialogUtil;

public class ChangeGroupOwnerDialog extends Dialog implements
		android.view.View.OnClickListener {

	private MemberAdapter adapter;
	private GotyeUser currentSelectedUser;
	GotyeGroup group;

	public ChangeGroupOwnerDialog(Context context) {
		super(context);
	}

	ListView listview;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_change_group_owner);
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.ok).setOnClickListener(this);
		listview = (ListView) findViewById(R.id.listview);

	}

	public void setMembers(GotyeGroup group, List<GotyeUser> members) {
		this.group = group;
		currentSelectedUser = members.get(0);
		adapter = new MemberAdapter(members);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				currentSelectedUser = (GotyeUser) adapter.getItem(arg2);
				adapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			dismiss();
			break;
		case R.id.ok:
			ProgressDialogUtil.showProgress(getContext(), "正在转让并推出群...");
			GotyeAPI.getInstance().changeGroupowner(group,currentSelectedUser);
			dismiss();
			break;
		default:
			break;
		}

	}

	class MemberAdapter extends BaseAdapter {
		private List<GotyeUser> members;

		public MemberAdapter(List<GotyeUser> members) {
			this.members = members;
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
			ViewHolder holder;
			if (arg1 == null) {
				arg1 = getLayoutInflater().inflate(
						R.layout.layout_chage_group_owner_item, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) arg1.findViewById(R.id.icon);
				holder.name = (TextView) arg1.findViewById(R.id.name);
				holder.selected = (ImageView) arg1.findViewById(R.id.selected);
				arg1.setTag(holder);
			} else {
				holder = (ViewHolder) arg1.getTag();
			}
			GotyeUser u = (GotyeUser) getItem(arg0);
			setMemberIcon(holder.icon, u);
			holder.name.setText(u.getName());

			if (u.getName().equals(currentSelectedUser.getName())) {
				holder.selected.setVisibility(View.VISIBLE);
			} else {
				holder.selected.setVisibility(View.GONE);
			}
			return arg1;
		}

		class ViewHolder {
			ImageView icon,selected;
			TextView name;

		}

		private void setMemberIcon(ImageView iconView, GotyeUser member) {
			if (member.getIcon() != null && member.getIcon().path != null
					&& !"".equals(member.getIcon().path)) {
				Bitmap bmp = BitmapUtil.getBitmap(member.getIcon().path);
				if (bmp != null) {
					iconView.setImageBitmap(bmp);
					return;
				}
				GotyeAPI.getInstance().downloadMedia(member.getIcon().url);
			}

		}
	}
}
