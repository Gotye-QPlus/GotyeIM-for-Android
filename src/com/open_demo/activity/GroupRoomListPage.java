package com.open_demo.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gotye.api.GotyeChatTarget;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeRoom;
import com.gotye.api.GotyeUser;
import com.open_demo.R;
import com.open_demo.base.BaseActivity;
import com.open_demo.util.BitmapUtil;
import com.open_demo.util.ImageCache;
import com.open_demo.util.ToastUtil;

public class GroupRoomListPage extends BaseActivity {
	private int pageIndex = 0;
	public static final int pageSize = 10;
	private ProgressBar progress;
	private List<GotyeChatTarget> roomsGroups;
	private int Type;
	private RoomGroupAdapter adapter;
	private ListView listView;
	private TextView title, noDataTip;
	private String currentLoginName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_room_group_page);
		title = (TextView) findViewById(R.id.title_tx);
		noDataTip = (TextView) findViewById(R.id.no_data);
		currentLoginName = api.getCurrentLoginUser().getName();
		api.addListener(this);
		Type = getIntent().getIntExtra("type", 0);
		if (Type == 0) {
			api.requestRoomList(pageIndex);
			title.setText("聊天室");
		} else {
			api.requestGroupList();
			title.setText("群列表");
			findViewById(R.id.serach_layout).setVisibility(View.VISIBLE);
			findViewById(R.id.contact_search_input).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent toSreach = new Intent(
									GroupRoomListPage.this, SearchPage.class);
							toSreach.putExtra("search_type", 1);
							startActivity(toSreach);
						}
					});
		}
		progress = (ProgressBar) findViewById(R.id.progress);
		listView = (ListView) findViewById(R.id.listview);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i;
				if (Type == 0) {
					i = new Intent(GroupRoomListPage.this, ChatPage.class);
					GotyeRoom room = (GotyeRoom) roomsGroups.get(arg2);
					i.putExtra("room_id", room.getRoomID());
					i.putExtra("room", room);

					// boolean a =
					// GotyeAPI.getInstance().supportRealtime((int)room.getRoomID());
					// ConversationDBManager.getInstance(getBaseContext())
					// .clearUnReadTip(Conversation.ROOM_MSG, null, null,
					// room.getRoomID());
				} else {
					GotyeGroup group = (GotyeGroup) roomsGroups.get(arg2);
					i = new Intent(GroupRoomListPage.this, ChatPage.class);
					i.putExtra("group_id", group.getGroupID());
					i.putExtra("group", group);
					// ConversationDBManager.getInstance(getBaseContext())
					// .clearUnReadTip(Conversation.GROUP_MSG, null, null,
					// group.getGroupID());
				}
				startActivityForResult(i, 0);
			}
		});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		GotyeChatTarget forRemove = null;
		if (intent != null) {
			long id = intent.getLongExtra("group_id", -1);
			if (id < 0) {
				return;
			}
			if (Type == 1 && roomsGroups != null) {
				for (GotyeChatTarget group : roomsGroups) {
					GotyeGroup g = (GotyeGroup) group;
					if (g.getGroupID() == id) {
						forRemove = group;
						break;
					}
				}
				if (forRemove != null) {
					roomsGroups.remove(forRemove);
					adapter.notifyDataSetChanged();
				}
			}
		}
		super.onNewIntent(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			long group_id = data.getLongExtra("group_id", -1);
			if (group_id > 0) {
				if (Type == 1) {
					for (GotyeChatTarget group : roomsGroups) {
						GotyeGroup p = (GotyeGroup) group;
						if (p.getGroupID() == group_id) {
							roomsGroups.remove(group);
							adapter.notifyDataSetChanged();
							return;
						}
					}

				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void back(View v) {
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		api.removeListener(this);
		super.onDestroy();
	}

	@Override
	public void onGetRoomList(int code, List<GotyeRoom> gotyeroom) {

		if (Type == 1) {
			return;
		}
		if (gotyeroom != null && gotyeroom.size() > 0) {
			if (roomsGroups == null) {
				roomsGroups = new ArrayList<GotyeChatTarget>();
			}
			for (GotyeRoom room : gotyeroom) {
				roomsGroups.add(room);
			}
			if (adapter == null) {
				adapter = new RoomGroupAdapter();
				listView.setAdapter(adapter);
				progress.setVisibility(View.GONE);
			} else {
				adapter.notifyDataSetChanged();
			}
		} else {
			progress.setVisibility(View.GONE);
			noDataTip.setVisibility(View.VISIBLE);
			if (Type == 0) {
				noDataTip.setText("没有聊天室");
			}
		}

	}

	@Override
	public void onGetGroupList(int code, List<GotyeGroup> grouplist) {
		if (Type == 0) {
			return;
		}
		if (grouplist != null && grouplist.size() > 0) {
			if (roomsGroups == null) {
				roomsGroups = new ArrayList<GotyeChatTarget>();
			}
			for (GotyeGroup group : grouplist) {
				roomsGroups.add(group);
			}
			if (adapter == null) {
				adapter = new RoomGroupAdapter();
				listView.setAdapter(adapter);
				progress.setVisibility(View.GONE);
			}
		} else {
			progress.setVisibility(View.GONE);
			noDataTip.setVisibility(View.VISIBLE);
			noDataTip.setText("您还没加入任何群");
		}

	}

	@Override
	public void onDownloadMedia(int code, String path, String url) {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onUserDismissGroup(GotyeGroup group, GotyeUser user) {
		// TODO Auto-generated method stub
		GotyeChatTarget toRemove = null;
		if (Type == 1 && roomsGroups != null) {
			for (GotyeChatTarget target : roomsGroups) {
				if (target.Id == group.getGroupID()) {
					toRemove = target;
					break;
				}
			}
			if (toRemove != null) {
				roomsGroups.remove(toRemove);
			}
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onUserKickdFromGroup(GotyeGroup group, GotyeUser kicked,
			GotyeUser actor) {
		// TODO Auto-generated method stub
		GotyeChatTarget toRemove = null;
		if (Type == 1 && roomsGroups != null) {
			if (kicked.getName().equals(currentLoginName)) {
				for (GotyeChatTarget target : roomsGroups) {
					if (target.Id == group.getGroupID()) {
						toRemove = target;
						break;
					}
				}
				if (toRemove != null) {
					roomsGroups.remove(toRemove);
					ToastUtil.show(getBaseContext(), "您被" + actor.getName()
							+ "请出了" + group.getGroupName() + "群");
					adapter.notifyDataSetChanged();
				}
			}
		}
	}

	class RoomGroupAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return roomsGroups.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return roomsGroups.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			View layout = getLayoutInflater().inflate(
					R.layout.layout_group_room_item, null);
			ImageView icon = (ImageView) layout.findViewById(R.id.icon);
			TextView tx = (TextView) layout.findViewById(R.id.name);
			GotyeChatTarget target = (GotyeChatTarget) getItem(arg0);
			if (Type == 0) {
				// room
				tx.setText(target.name);
			} else {
				// group
				tx.setText(target.name);
			}
			setIcon(icon, target);
			return layout;
		}

		private void setIcon(ImageView iconView, GotyeChatTarget target) {
			if (Type == 0) {
				GotyeRoom room = (GotyeRoom) target;
				if (room.getIcon() != null) {
					Bitmap bmp = ImageCache.getInstance().get(target.Id + "");
					if (bmp != null) {
						iconView.setImageBitmap(bmp);
						return;
					} else {
						bmp = BitmapUtil.getBitmap(room.getIcon().getPath());
						if (bmp != null) {
							iconView.setImageBitmap(bmp);
							ImageCache.getInstance().put(room.Id + "", bmp);
							return;
						}
					}
					api.downloadMedia(room.getIcon().getUrl());
				}

			} else {
				GotyeGroup group = (GotyeGroup) target;
				if (group.getIcon() != null) {
					Bitmap bmp = ImageCache.getInstance().get(group.Id + "");
					if (bmp != null) {
						iconView.setImageBitmap(bmp);
						return;
					} else {
						bmp = BitmapUtil.getBitmap(group.getIcon().getPath());
						if (bmp != null) {
							iconView.setImageBitmap(bmp);
							ImageCache.getInstance().put(group.Id + "", bmp);
							return;
						}
					}
					api.downloadMedia(group.getIcon().getUrl());
				}
			}
		}

	}
}
