package com.open_demo.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeChatTarget;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeNotify;
import com.gotye.api.GotyeNotifyType;
import com.gotye.api.GotyeUser;
import com.open_demo.R;
import com.open_demo.activity.NotifyListPage;
import com.open_demo.util.ProgressDialogUtil;

public class NotifyListAdapter extends BaseAdapter {
	private NotifyListPage notifyListPage;
	private List<GotyeNotify> notifies;

	public NotifyListAdapter(NotifyListPage notifyListPage,
			List<GotyeNotify> notifies) {
		this.notifyListPage = notifyListPage;
		this.notifies = notifies;
	}

	static class ViewHolder {
		ImageView icon;
		TextView title, content, time, count;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		//被邀请{同意，拒绝}
		//收到申请 {已处理，未处理}
		//被批准 {已读，未读}
		return 6;
	}
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		GotyeNotify notify=getItem(position);
		if(notify.getType()==GotyeNotifyType.GroupInvite){
			if(notify.isRead()){
				return 0; //收到邀请已读
			}else{
				return 1; //收到邀请未读
			}
		}else if(notify.getType()==GotyeNotifyType.JoinGroupReply){
			if(notify.isRead()){
				return 2; //收到申请回复
			}else{
				return 3; //收到申请回复拒绝
			}
		}else{
			if(notify.isRead()){
				return 4; //处理申请
			}else{
				return 5; //处理申请
			}
		}
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return notifies.size();
	}

	@Override
	public GotyeNotify getItem(int arg0) {
		// TODO Auto-generated method stub
		return notifies.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (view == null) {
			view = LayoutInflater.from(notifyListPage).inflate(
					R.layout.item_delete, null);
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) view.findViewById(R.id.icon);
			viewHolder.title = (TextView) view.findViewById(R.id.title_tx);
			viewHolder.content = (TextView) view.findViewById(R.id.content_tx);
			viewHolder.time = (TextView) view.findViewById(R.id.time_tx);
			viewHolder.count = (TextView) view.findViewById(R.id.count);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		final GotyeNotify notify = (GotyeNotify) getItem(arg0);


		viewHolder.title.setText(notify.getText());
		viewHolder.content.setVisibility(View.GONE);
		viewHolder.icon.setImageResource(R.drawable.contact_group);

//		switch (notify.getType()) {
//		case GroupInvite:
//			
//			if (notify.isRead()) {
//				viewHolder.reject.setVisibility(View.VISIBLE);
//				viewHolder.agree.setVisibility(View.GONE);
//				viewHolder.reject.setText("删除");
//				viewHolder.reject.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View arg0) {
//						GotyeAPI.getInstance().deleteNotify(notify);
//
//						notifyListPage.refresh();
//					}
//				});
//			} else {
//				viewHolder.reject.setVisibility(View.VISIBLE);
//				viewHolder.reject.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View arg0) {
//						notify.setRead(true);
//						GotyeAPI.getInstance().markNotifyIsread(notify);
//						notifyListPage.refresh();
//					}
//				});
//				viewHolder.agree.setVisibility(View.VISIBLE);
//				viewHolder.agree.setText("加入该群");
//				viewHolder.agree.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View arg0) {
//						GotyeAPI.getInstance().joinGroup(
//								new GotyeGroup(notify.getFrom().Id));
//						notify.setRead(true);
//						GotyeAPI.getInstance().markNotifyIsread(notify);
//						ProgressDialogUtil.showProgress(notifyListPage, "正在加入..");
//						notifyListPage.refresh();
//					}
//				});
//			}
//			
//			
//			break;
//		case JoinGroupReply:
//			
//			if (notify.isRead()) {
//				viewHolder.reject.setVisibility(View.VISIBLE);
//				viewHolder.agree.setVisibility(View.GONE);
//				viewHolder.reject.setText("删除");
//				viewHolder.reject.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View arg0) {
//						GotyeAPI.getInstance().deleteNotify(notify);
//						notifyListPage.refresh();
//					}
//				});
//			} else {
//				viewHolder.reject.setVisibility(View.GONE);
//				viewHolder.agree.setVisibility(View.VISIBLE);
//				viewHolder.agree.setText("知道了");
//				viewHolder.agree.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View arg0) {
//						notify.setRead(true);
//						GotyeAPI.getInstance().markNotifyIsread(notify);
//						notifyListPage.refresh();
//					}
//				});
//			}
//			
//			break;
//		case JoinGroupRequest:
//			if (notify.isRead()) {
//				viewHolder.reject.setVisibility(View.VISIBLE);
//				viewHolder.agree.setVisibility(View.GONE);
//				viewHolder.reject.setText("删除");
//				viewHolder.reject.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View arg0) {
//						GotyeAPI.getInstance().deleteNotify(notify);
//						notifyListPage.refresh();
//					}
//				});
//			} else {
//				viewHolder.reject.setVisibility(View.VISIBLE);
//				viewHolder.agree.setVisibility(View.VISIBLE);
//				viewHolder.agree.setText("同意");
//				viewHolder.agree.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View arg0) {
//						notify.setRead(true);
//						GotyeAPI.getInstance().markNotifyIsread(notify);
//						notifyListPage.refresh();
//						GotyeAPI.getInstance().replyJoinGroup((GotyeUser)notify.getSender(),(GotyeGroup)notify.getFrom(),"欢迎加入",true);
//					}
//				});
//				viewHolder.reject.setText("拒绝");
//				viewHolder.reject.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View arg0) {
//						notify.setRead(true);
//						GotyeAPI.getInstance().markNotifyIsread(notify);
//						notifyListPage.refresh();
//						GotyeAPI.getInstance().replyJoinGroup((GotyeUser)notify.getSender(),(GotyeGroup)notify.getFrom(),"不同意",false);
//					}
//				});
//			}
//			break;
//		default:
//			break;
//		}

		
		viewHolder.count.setVisibility(notify.isRead() ? View.GONE
				: View.VISIBLE);
		return view;
	}

	public void refreshData(List<GotyeNotify> notifies) {
		this.notifies = notifies;
		notifyDataSetChanged();
	}

	public void clear() {
		// TODO Auto-generated method stub
		notifies.clear();
		notifyDataSetChanged();
	}

}
