package com.healthslife.system;

import java.io.IOException;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ExpandableListActivity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.healthslife.R;

public class Music_Setting_Activity_system extends ExpandableListActivity {
	
	private MediaPlayer player = new MediaPlayer();
	private static int playingGroup = -1;//正在播放音乐的组号，-1表示没有正在播放音乐的组
	private ArrayList<Group> groups;
	private boolean isShowCheckBox = false;
	private int setting_level;
	
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		// TODO Auto-generated method stub
		if(0 == childPosition){
			if(playingGroup != groupPosition){//如果没有正在播放某组音乐，点击了该组播放按钮
				v.invalidate();//fixed
				try {
					player.reset();
					player.setDataSource(MusicMessage_system.path[groupPosition]);
					player.prepare();
					player.start();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				playingGroup = groupPosition;
				v.invalidate();//fixed
			}else{
				v.invalidate();//fixed
				player.stop();
				playingGroup = -1;
			}
		}
		else{
			if(playingGroup != -1){
				player.stop();
				playingGroup = -1;
			}
			String l = "舒缓";
			switch(childPosition)
			{
			case 1:
				l = "舒缓";
				break;
			case 2:
				l = "愉悦";
				break;
			case 3:
				l = "动感";
				break;
			default:
				break;
			}
			MusicMessage_system.setLevel(this, groupPosition,MusicMessage_system.id[groupPosition],childPosition);
			Toast toast = Toast.makeText(this,
					"设置 " + MusicMessage_system.titles[groupPosition] +" 为 " + l, 
					Toast.LENGTH_LONG);
			toast.show();
			parent.collapseGroup(groupPosition);
		}
		return false;
	}
	
	@Override
	public void onGroupCollapse(int groupPosition) {
		// TODO Auto-generated method stub
			if(playingGroup == groupPosition){//如果正在播放音乐且该组收缩起来
				player.stop();
				playingGroup = -1;
		}
		super.onGroupCollapse(groupPosition);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		getExpandableListView().setBackgroundColor(Color.rgb(240, 240, 240));
		getExpandableListView().setGroupIndicator(null);
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setHomeButtonEnabled(true);
		MusicMessage_system.setValues(this);
		
		Resources r = getResources();
		Drawable myDrawable = r.getDrawable(R.drawable.title_top_bg);
		actionbar.setBackgroundDrawable(myDrawable);
		groups = new ArrayList<Group>();
		for(int i = 0;i < MusicMessage_system.count;i++){
			groups.add(i,new Group(i));
		}
		
		//设置一个监听器，当音乐播放完以后释放音乐文件，更改正在播放标志
		player.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				player.stop();
				getExpandableListView().collapseGroup(playingGroup);
				playingGroup = -1;
			}
		});
		
		ExpandableListAdapter elAdapter = new BaseExpandableListAdapter() {

			String[] childText = new String[]{
				"播    放","舒    缓","愉    悦","动    感"	
			};
			

			@Override
			public boolean isChildSelectable(int groupPosition, int childPosition) {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public boolean hasStableIds() {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public View getGroupView(int groupPosition, boolean isExpanded,
					View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				ViewHolder holder = new ViewHolder();
				Group group = (Group)getGroup(groupPosition);
				LayoutInflater inflate = 
						(LayoutInflater)Music_Setting_Activity_system.this.getSystemService(
								LAYOUT_INFLATER_SERVICE);
				convertView = inflate.inflate(R.layout.music_setting_parent,null);
				holder.music_nameView = (TextView)convertView.findViewById(R.id.music_name);
//				holder.artist_nameView = (TextView)convertView.findViewById(R.id.artist_name);
				holder.music_durationView = (TextView)convertView.findViewById(R.id.music_duration);
				holder.checkBox = (CheckBox)convertView.findViewById(R.id.music_level_check);
				holder.music_nameView.setText(MusicMessage_system.titles[groupPosition]);
//				holder.artist_nameView.setText("艺术家：" + MusicMessage_system.artists[groupPosition]);
				holder.music_durationView.setText("时间：" 
						+ MusicMessage_system.duration[groupPosition]
						+ "      等级：" + 
						(MusicMessage_system.level[groupPosition] == 1?"舒缓":
							((MusicMessage_system.level[groupPosition] == 2)?"愉悦":"动感")));
				holder.checkBox.setChecked(group.isChecked());
				holder.checkBox.setOnClickListener(new Group_CheckBox_Click(groupPosition));
				if(isShowCheckBox){
					holder.checkBox.setVisibility(View.VISIBLE);
				}else{
					holder.checkBox.setVisibility(View.INVISIBLE);
				}
				return convertView;
			}
			
			
			@Override
			public long getGroupId(int groupPosition) {
				// TODO Auto-generated method stub
				return groupPosition;
			}
			
			@Override
			public int getGroupCount() {
				// TODO Auto-generated method stub
				return MusicMessage_system.titles.length;
			}
			
			@Override
			public Object getGroup(int groupPosition) {
				// TODO Auto-generated method stub
				return groups.get(groupPosition);
			}
			
			@Override
			public int getChildrenCount(int groupPosition) {
				// TODO Auto-generated method stub
				return 4;
			}
			
			@Override
			public View getChildView(int groupPosition, int childPosition,
					boolean isLastChild, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				LayoutInflater inflate = 
						(LayoutInflater)Music_Setting_Activity_system.this.getSystemService(
								LAYOUT_INFLATER_SERVICE);
				convertView = inflate.inflate(R.layout.music_setting_child,null);
				TextView textview = (TextView)convertView.findViewById(R.id.child_textview);
				textview.setText(getChild(groupPosition, childPosition).toString());
				if(childPosition == 0){
					if(playingGroup == -1){
						textview.setText("试    听");
						}else{
							textview.setText("停    止");
						}
					}
				return convertView;
				
			}
			
			@Override
			public long getChildId(int groupPosition, int childPosition) {
				// TODO Auto-generated method stub
				return childPosition;
			}
			
			@Override
			public Object getChild(int groupPosition, int childPosition) {
				// TODO Auto-generated method stub
				return childText[childPosition];
			}
		};
		
		setListAdapter(elAdapter);
	}
	
	public class Group_CheckBox_Click implements OnClickListener{
		
		private int groupPos;
		public Group_CheckBox_Click(int pos){
			this.groupPos = pos;
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			groups.get(groupPos).toggle();
		}
	}
	
	public class ViewHolder{
		public TextView music_nameView;
//		public TextView artist_nameView;
		public TextView music_durationView;
		public CheckBox checkBox;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(playingGroup != -1){//如果退出设置音乐界面，释放MadiaPlayer实例，把playingGroup设置为-1
			player.reset();
			player.release();
			playingGroup = -1;
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflator = new MenuInflater(this);
		inflator.inflate(R.menu.music_setting_actionbar_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		for(int i = 0;i < getExpandableListAdapter().getGroupCount();i++){
			getExpandableListView().collapseGroup(i);
		}
		switch(item.getItemId()){
		
		case R.id.menu_level1:
			setting_level = 1;
			isShowCheckBox = true;
			getExpandableListView().invalidateViews();
			break;
		
		case R.id.menu_level2:
			setting_level = 2;
			isShowCheckBox = true;
			getExpandableListView().invalidateViews();
			break;
		
		case R.id.menu_level3:
			setting_level = 3;
			isShowCheckBox = true;
			getExpandableListView().invalidateViews();
			break;
		
		case R.id.confirm_seting:
			int count = 0;
			for(int i = 0;i < getExpandableListAdapter().getGroupCount();i++){
				if(groups.get(i).isChecked()){
					MusicMessage_system.setLevel(Music_Setting_Activity_system.this, i, MusicMessage_system.id[i], setting_level);
					groups.get(i).toggle();
					count++;
				}
			}
			
			isShowCheckBox = false;
			getExpandableListView().invalidateViews();
			
			if(count != 0){
				Toast.makeText(this, "已设定", Toast.LENGTH_LONG).show();
			}
			break;
		case android.R.id.home:
			Music_Setting_Activity_system.this.finish();
			break;
		default:
			break;
		}
		return true;
	}

	//fixed 使列表只展开一个
	@Override
	public void onGroupExpand(int groupPosition) {
		// TODO Auto-generated method stub
		for(int i = 0;i < getExpandableListAdapter().getGroupCount();i++){
			if(i != groupPosition)
			{
				getExpandableListView().collapseGroup(i);
			}
		}
		
		super.onGroupExpand(groupPosition);
	}
//	//fixed新建的一个类 记录每组信息
	public class Group{
		private int position;
		private boolean isChecked;
		public Group(int pos){
			this.position = pos;
			this.isChecked = false;
		}
		public int getPosition() {
			return position;
		}
		
		public void setPosition(int position) {
			this.position = position;
		}

		public boolean isChecked() {
			return isChecked;
		}
	
		public void setChecked(boolean isChecked) {
			this.isChecked = isChecked;
		}
		
		public void toggle(){
			this.isChecked = (!this.isChecked);
		}
	}
	
}
