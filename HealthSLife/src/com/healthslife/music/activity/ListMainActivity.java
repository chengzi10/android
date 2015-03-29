package com.healthslife.music.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.healthslife.R;
import com.healthslife.health.HealthServiceActivity;
import com.healthslife.music.adapter.ListItemAdapter;
import com.healthslife.music.adapter.SongItemAdapter;
import com.healthslife.music.custom.FlingGalleryView;
import com.healthslife.music.custom.FlingGalleryView.OnScrollToScreenListener;
import com.healthslife.music.custom.XfDialog;
import com.healthslife.music.dao.AlbumDao;
import com.healthslife.music.dao.ArtistDao;
import com.healthslife.music.dao.PlayerListDao;
import com.healthslife.music.dao.SongDao;
import com.healthslife.music.data.SystemSetting;
import com.healthslife.music.entity.PlayerList;
import com.healthslife.music.entity.Song;
import com.healthslife.music.service.MediaPlayerManager;
import com.healthslife.music.service.MediaPlayerManager.ServiceConnectionListener;
import com.healthslife.music.util.Common;

public class ListMainActivity extends BaseActivity {

	// 导航栏选项卡布局数组
	private ViewGroup[] vg_list_tab_item = new ViewGroup[1];
	private FlingGalleryView fgv_list_main;

	// 当前屏幕的下标
	private int screenIndex = 0;
	// 导航栏的内容
	private String[] list_item_items;

	// 本地列表
	private ViewGroup list_main_music;

	// 主屏幕内容布局
	private ViewGroup rl_list_main_content;
	// 切换内容布局
	private ViewGroup rl_list_content;

	// 本地音乐扫描音乐和返回按钮
	private ImageButton ibtn_scan;// 扫描图标
	private ImageButton ibtn_back;// 返回图标

	// 本地音乐二三级布局
	private ImageButton ibtn_list_content_icon;// 左边图标
	private ImageButton ibtn_list_content_do_icon;// 右边图标
	private TextView tv_list_content_title;// 标题
	private ListView lv_list_change_content;// 替换ListView
	private Button btn_list_random_music2;// 随机播放

	// 本地音乐随机播放
	private Button btn_list_random_music_local;

	// 底部工具栏
	private ImageButton ibtn_player_albumart;// 专辑封面
	private ImageButton ibtn_player_control;// 播放/暂停
	private TextView tv_player_title;// 播放歌曲 歌手-标题
	private ProgressBar pb_player_progress;// 播放进度条
	private TextView tv_player_currentPosition;// 当前播放的进度
	private TextView tv_player_duration;// 歌曲播放时长

	/**
	 * 默认页：0 1.全部歌曲 2.歌手 3.专辑 4.文件夹 5.播放列表 6.我最爱听 7.最近播放 22.歌手二级 33.专辑二级
	 * 44.文件夹二级 55.播放列表二级
	 * */
	private int pageNumber = 0;

	private SongDao songDao;

	private ArtistDao artistDao;
	private AlbumDao albumDao;
	private PlayerListDao playerListDao;
	private Toast toast;
	private LayoutParams params;
	private LayoutInflater inflater;

	private MediaPlayerManager mediaPlayerManager;
	private MediaPlayerBroadcastReceiver mediaPlayerBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_main);

		SystemSetting setting = new SystemSetting(this, false);
		checkScannerTip(setting);

		songDao = new SongDao(this);
		artistDao = new ArtistDao(this);
		albumDao = new AlbumDao(this);
		playerListDao = new PlayerListDao(this);
		params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		// 导航栏选项卡数组 实例化
		vg_list_tab_item[0] = (ViewGroup) this
				.findViewById(R.id.list_tab_item_music);

		// 主屏幕内容布局选项 实例化
		list_main_music = (ViewGroup) this.findViewById(R.id.list_main_music);

		// 随机播放音乐
		btn_list_random_music_local = (Button) list_main_music
				.findViewById(R.id.btn_list_random_music);
		btn_list_random_music_local
				.setOnClickListener(btn_randomPlayerListener);

		// 主屏幕内容布局和切换内容布局 实例化
		rl_list_main_content = (ViewGroup) this
				.findViewById(R.id.rl_list_main_content);
		rl_list_content = (ViewGroup) this.findViewById(R.id.rl_list_content);

		// 本地音乐的二三级布局-公共标题和内容区域
		ibtn_list_content_icon = (ImageButton) rl_list_content
				.findViewById(R.id.ibtn_list_content_icon);
		ibtn_list_content_do_icon = (ImageButton) rl_list_content
				.findViewById(R.id.ibtn_list_content_do_icon);
		tv_list_content_title = (TextView) rl_list_content
				.findViewById(R.id.tv_list_content_title);
		lv_list_change_content = (ListView) rl_list_content
				.findViewById(R.id.lv_list_change_content);

		ibtn_list_content_icon.setOnClickListener(imageButton_listener);
		ibtn_list_content_do_icon.setOnClickListener(imageButton_listener);
		lv_list_change_content
				.setOnItemClickListener(list_change_content_listener);
		lv_list_change_content
				.setOnItemLongClickListener(list_change_content_looglistener);
		btn_list_random_music2 = (Button) rl_list_content
				.findViewById(R.id.btn_list_random_music2);
		btn_list_random_music2.setOnClickListener(btn_randomPlayerListener);

		// 底部工具栏
		ibtn_player_albumart = (ImageButton) this
				.findViewById(R.id.ibtn_player_albumart);
		ibtn_player_control = (ImageButton) this
				.findViewById(R.id.ibtn_player_control);
		tv_player_title = (TextView) this.findViewById(R.id.tv_player_title);
		pb_player_progress = (ProgressBar) this
				.findViewById(R.id.pb_player_progress);
		tv_player_currentPosition = (TextView) this
				.findViewById(R.id.tv_player_currentPosition);
		tv_player_duration = (TextView) this
				.findViewById(R.id.tv_player_duration);
		ibtn_player_albumart.setOnClickListener(imageButton_listener);
		ibtn_player_control.setOnClickListener(imageButton_listener);

		// 顶部扫描音乐和返回
		ibtn_scan = (ImageButton) this.findViewById(R.id.ibtn_scan);
		ibtn_back = (ImageButton) this.findViewById(R.id.ibtn_back);
		ibtn_scan.setOnClickListener(imageButton_listener);
		ibtn_back.setOnClickListener(imageButton_listener);

		// 切换主屏幕内容容器
		fgv_list_main = (FlingGalleryView) rl_list_main_content
				.findViewById(R.id.fgv_list_main);
		fgv_list_main.setDefaultScreen(screenIndex);
		fgv_list_main.setOnScrollToScreenListener(scrollToScreenListener);

		// 从资源文件中获取导航栏选项卡标题
		list_item_items = getResources().getStringArray(R.array.list_tab_items);
		// 初始化导航栏
		initTabItem();

		// 初始化本地音乐内容区域
		initListMusicItem();

		// 播放器管理
		mediaPlayerManager = new MediaPlayerManager(this);
		mediaPlayerManager.setConnectionListener(mConnectionListener);

	}

	/**
	 * 检查显示扫描歌曲提示(第一次进入软件界面)
	 * */
	private void checkScannerTip(SystemSetting setting) {
		if (setting.getValue(SystemSetting.KEY_ISSCANNERTIP) == null) {
			// 创建提示对话框
			new XfDialog.Builder(this)
					.setTitle("扫描提示")
					.setMessage("是否要扫描本地歌曲入库？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									dialog.dismiss();
									// 启动扫描页面并返回结果
									Intent it = new Intent(
											ListMainActivity.this,
											ScanMusicActivity.class);
									startActivityForResult(it, 1);
								}
							}).setNegativeButton("取消", null).create().show();
			setting.setValue(SystemSetting.KEY_ISSCANNERTIP, "OK");
		}
	}

	/**
	 * 随机播放按钮点击事件绑定监听器
	 * */
	private OnClickListener btn_randomPlayerListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btn_list_random_music) { // 本地音乐界面的随机播放按钮
				mediaPlayerManager.randomPlayer(
						MediaPlayerManager.PLAYERFLAG_ALL, "");
			} else if (v.getId() == R.id.btn_list_random_music2) { // 二三级页面的随机播放按钮
				if (Integer.valueOf(v.getTag().toString()) == 0) {
					return;
				}
				if (pageNumber == 1) { // 全部歌曲
					mediaPlayerManager.randomPlayer(
							MediaPlayerManager.PLAYERFLAG_ALL, "");
				} else if (pageNumber == 6) { // 我最爱听
					mediaPlayerManager.randomPlayer(
							MediaPlayerManager.PLAYERFLAG_LIKE, "");
				} else if (pageNumber == 7) { // 最近播放
					mediaPlayerManager.randomPlayer(
							MediaPlayerManager.PLAYERFLAG_LATELY, "");
				} else if (pageNumber == 22) { // 歌手
					mediaPlayerManager.randomPlayer(
							MediaPlayerManager.PLAYERFLAG_ARTIST, condition);
				} else if (pageNumber == 33) { // 专辑
					mediaPlayerManager.randomPlayer(
							MediaPlayerManager.PLAYERFLAG_ALBUM, condition);
				} else if (pageNumber == 44) { // 文件夹
					mediaPlayerManager.randomPlayer(
							MediaPlayerManager.PLAYERFLAG_FOLDER, condition);
				} else if (pageNumber == 55) { // 播放列表
					mediaPlayerManager
							.randomPlayer(
									MediaPlayerManager.PLAYERFLAG_PLAYERLIST,
									condition);
				}
			}
		}
	};

	// 在MediaPlayerManager中定义的ServiceConnectionListener接口
	private ServiceConnectionListener mConnectionListener = new ServiceConnectionListener() {
		@Override
		public void onServiceDisconnected() {
		}

		@Override
		public void onServiceConnected() {
			// 每次进入activity时都要重新拿数据
			mediaPlayerManager.initPlayerMain_SongInfo(); // 初始化歌曲信息-播放界面进入时
			updateSongItemList(); // 更新歌曲列表
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		// 注册播放器-广播接收器
		mediaPlayerBroadcastReceiver = new MediaPlayerBroadcastReceiver();
		registerReceiver(mediaPlayerBroadcastReceiver, new IntentFilter(
				MediaPlayerManager.BROADCASTRECEIVER_ACTION));
		// 开始服务并绑定服务
		mediaPlayerManager.startAndBindService();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// 取消注册播放器/下载任务广播接收器
		unregisterReceiver(mediaPlayerBroadcastReceiver);
		mediaPlayerManager.unbindService();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 播放器-广播接收器
	 * */
	private class MediaPlayerBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int flag = intent.getIntExtra("flag", -1);
			if (flag == MediaPlayerManager.FLAG_CHANGED) { // 更新底部工具栏播放控制
				int currentPosition = intent.getIntExtra("currentPosition", 0);
				int duration = intent.getIntExtra("duration", 0);
				tv_player_currentPosition.setText(Common
						.formatSecondTime(currentPosition));
				tv_player_duration.setText(Common.formatSecondTime(duration));
				pb_player_progress.setProgress(currentPosition);
				pb_player_progress.setMax(duration);

			} else if (flag == MediaPlayerManager.FLAG_PREPARE) { // 准备状态
				String albumPic = intent.getStringExtra("albumPic");
				tv_player_title.setText(intent.getStringExtra("title"));
				if (TextUtils.isEmpty(albumPic)) {
					ibtn_player_albumart
							.setImageResource(R.drawable.min_default_album);
				} else { // 设置底部工具栏专辑图片
					Bitmap bitmap = BitmapFactory.decodeFile(albumPic);
					// 判断SD图片是否存在
					if (bitmap != null) {
						ibtn_player_albumart.setImageBitmap(bitmap);
					} else {
						ibtn_player_albumart
								.setImageResource(R.drawable.min_default_album);
					}
				}
				int duration = intent.getIntExtra("duration", 0);
				int currentPosition = intent.getIntExtra("currentPosition", 0);
				tv_player_currentPosition.setText(Common
						.formatSecondTime(currentPosition));
				tv_player_duration.setText(Common.formatSecondTime(duration));
				pb_player_progress.setMax(duration);
				pb_player_progress.setProgress(currentPosition);
				pb_player_progress.setSecondaryProgress(0);

				// 更新播放列表状态
				updateSongItemList();
			} else if (flag == MediaPlayerManager.FLAG_INIT) { // 初始化播放信息
				int currentPosition = intent.getIntExtra("currentPosition", 0);
				int duration = intent.getIntExtra("duration", 0);
				pb_player_progress.setMax(duration);
				pb_player_progress.setProgress(currentPosition);
				tv_player_currentPosition.setText(Common
						.formatSecondTime(currentPosition));
				tv_player_duration.setText(Common.formatSecondTime(duration));
				tv_player_title.setText(intent.getStringExtra("title"));
				String albumPic = intent.getStringExtra("albumPic");
				if (TextUtils.isEmpty(albumPic)) {
					ibtn_player_albumart
							.setImageResource(R.drawable.min_default_album);
				} else {
					Bitmap bitmap = BitmapFactory.decodeFile(albumPic);
					// 判断SD卡图片是否存在
					if (bitmap != null) {
						ibtn_player_albumart.setImageBitmap(bitmap);
					} else {
						ibtn_player_albumart
								.setImageResource(R.drawable.min_default_album);
					}
				}
				int playerState = intent.getIntExtra("playerState", 0);
				if (playerState == MediaPlayerManager.STATE_PLAYER
						|| playerState == MediaPlayerManager.STATE_PREPARE) {// 播放或准备
					ibtn_player_control
							.setBackgroundResource(R.drawable.player_btn_mini_pause);
				} else {
					ibtn_player_control
							.setBackgroundResource(R.drawable.player_btn_mini_player);
				}

				if (mediaPlayerManager.getPlayerState() == MediaPlayerManager.STATE_OVER) { // 播放结束
					if (pageNumber == 1 || pageNumber == 6 || pageNumber == 7
							|| pageNumber == 22 || pageNumber == 33
							|| pageNumber == 44 || pageNumber == 55) {
						((SongItemAdapter) lv_list_change_content.getAdapter())
								.setPlayerState(mediaPlayerManager
										.getPlayerState());
					}
				}
			} else if (flag == MediaPlayerManager.FLAG_LIST) { // 自动切歌播放，更新前台歌曲列表
				updateSongItemList();
			}
		}
	}

	// 自动切歌播放，更新前台歌曲列表
	private void updateSongItemList() {
		int[] playerInfo = new int[] { mediaPlayerManager.getSongId(),
				mediaPlayerManager.getPlayerState() };

		if (pageNumber == 1 || pageNumber == 6 || pageNumber == 7
				|| pageNumber == 22 || pageNumber == 33 || pageNumber == 44
				|| pageNumber == 55) {
			((SongItemAdapter) lv_list_change_content.getAdapter())
					.setPlayerInfo(playerInfo);
		}

		int state = mediaPlayerManager.getPlayerState();
		if (state == MediaPlayerManager.STATE_PLAYER
				|| state == MediaPlayerManager.STATE_PREPARE) {// 播放
			ibtn_player_control
					.setBackgroundResource(R.drawable.player_btn_mini_pause);
		} else if (state == MediaPlayerManager.STATE_PAUSE) {// 暂停
			ibtn_player_control
					.setBackgroundResource(R.drawable.player_btn_mini_player);
		}
	}

	// ImageButton click
	private OnClickListener imageButton_listener = new OnClickListener() {

		public void onClick(View v) {
			if (v.getId() == R.id.ibtn_list_content_icon) { // 列表项左图像按钮
				rl_list_content.setVisibility(View.GONE);
				rl_list_main_content.setVisibility(View.VISIBLE);
				pageNumber = 0;
			} else if (v.getId() == R.id.ibtn_list_content_do_icon) { // 列表项右图像按钮
				if (pageNumber == 5) {// 播放列表时，弹出菜单
					doPlayList(0, 0, null);
				}
			} else if (v.getId() == R.id.ibtn_player_control) { // 控制播放图像按钮
				PlayerOrPause(null);
			} else if (v.getId() == R.id.ibtn_player_albumart) { // 控制专辑图像按钮
				startActivity(new Intent(ListMainActivity.this,
						PlayerMainActivity.class));
			} else if (v.getId() == R.id.ibtn_scan) { // 扫描音乐按钮
				Intent it = new Intent(ListMainActivity.this,
						ScanMusicActivity.class);
				startActivityForResult(it, 1);
			} else if (v.getId() == R.id.ibtn_back) { // 返回按钮
				Intent it = new Intent(ListMainActivity.this,
						HealthServiceActivity.class);
				startActivity(it);
				finish();
			}
		}
	};

	/**
	 * 播放或暂停歌曲
	 * */
	private void PlayerOrPause(View v) {
		if (mediaPlayerManager.getPlayerState() == MediaPlayerManager.STATE_NULL) {
			toast = Common.showMessage(toast, ListMainActivity.this,
					"请先添加歌曲...");
			return;
		}
		if (v == null) {
			// 当前列表播放结束
			if (mediaPlayerManager.getPlayerState() == MediaPlayerManager.STATE_OVER) {
				toast = Common.showMessage(toast, ListMainActivity.this,
						"当前列表已经播放完毕！");
				return;
			}
		}
		mediaPlayerManager.pauseOrPlayer();
		final int state = mediaPlayerManager.getPlayerState();
		int itemRsId = 0;
		if (state == MediaPlayerManager.STATE_PLAYER
				|| state == MediaPlayerManager.STATE_PREPARE) {// 播放
			ibtn_player_control
					.setBackgroundResource(R.drawable.player_btn_mini_pause);
			itemRsId = R.drawable.music_list_item_player;
		} else if (state == MediaPlayerManager.STATE_PAUSE) {// 暂停
			ibtn_player_control
					.setBackgroundResource(R.drawable.player_btn_mini_player);
			itemRsId = R.drawable.music_list_item_pause;
		}
		if (pageNumber == 1 || pageNumber == 6 || pageNumber == 7
				|| pageNumber == 22 || pageNumber == 33 || pageNumber == 44
				|| pageNumber == 55) {
			if (v == null) {
				((SongItemAdapter) lv_list_change_content.getAdapter())
						.setPlayerState(mediaPlayerManager.getPlayerState());
			} else {
				((SongItemAdapter.ViewHolder) v.getTag()).tv_song_list_item_number
						.setBackgroundResource(itemRsId);
			}
		}
	}

	/**
	 * 添加或更新播放列表
	 * */
	private void doPlayList(final int flag, final int id, String text) {
		String actionmsg = null;
		final EditText et_newPlayList = new EditText(ListMainActivity.this);
		et_newPlayList.setLayoutParams(params);
		et_newPlayList.setTextSize(15);
		if (flag == 0) {// 新建
			actionmsg = "创建";
			et_newPlayList.setHint("请输入播放列表名称");
		} else if (flag == 1) {// 更新
			actionmsg = "更新";
			et_newPlayList.setText(text);
			et_newPlayList.selectAll();
		}
		final String actionmsg2 = actionmsg;

		new XfDialog.Builder(ListMainActivity.this)
				.setTitle(actionmsg + "播放列表")
				.setView(et_newPlayList, 5, 10, 5, 10)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						String text = et_newPlayList.getText().toString()
								.trim();
						if (!TextUtils.isEmpty(text)) {
							if (playerListDao.isExists(text)) {
								toast = Common.showMessage(toast,
										ListMainActivity.this, "此名称已经存在！");
							} else {
								PlayerList playerList = new PlayerList();
								playerList.setName(text);

								int rowId = -1;
								if (flag == 0) {// 创建播放列表
									rowId = (int) playerListDao.add(playerList);
								} else if (flag == 1) {// 更新播放列表
									playerList.setId(id);
									rowId = playerListDao.update(playerList);
								}
								if (rowId > 0) {// 判断是否成功
									toast = Common.showMessage(toast,
											ListMainActivity.this, actionmsg2
													+ "成功！");
									lv_list_change_content
											.setAdapter(new ListItemAdapter(
													ListMainActivity.this,
													playerListDao.searchAll(),
													R.drawable.local_custom));
									dialog.cancel();
									dialog.dismiss();
								} else {
									toast = Common.showMessage(toast,
											ListMainActivity.this, actionmsg2
													+ "失败！");
								}
							}
						}
					}
				}).setNegativeButton("取消", null).create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == 1) {
			mediaPlayerManager.initScanner_SongInfo();
			updateListAdapterData();

		}
	}

	// 重写返回键事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (pageNumber == 0) {
				int state = mediaPlayerManager.getPlayerState(); // 获取音乐播放的状态
				if (state == MediaPlayerManager.STATE_NULL
						|| state == MediaPlayerManager.STATE_OVER
						|| state == MediaPlayerManager.STATE_PAUSE) {
					mediaPlayerManager.stop();
				}
				finish();
				return true;
			}
			return backPage();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 返回键事件
	 * */
	private boolean backPage() {
		if (pageNumber < 10) {
			rl_list_content.setVisibility(View.GONE);
			rl_list_main_content.setVisibility(View.VISIBLE);
			pageNumber = 0;
			return true;
		} else {
			if (pageNumber == 22) {
				jumpPage(1, 2, null);
			} else if (pageNumber == 33) {
				jumpPage(1, 3, null);
			} else if (pageNumber == 44) {
				jumpPage(1, 4, null);
			} else if (pageNumber == 55) {
				jumpPage(1, 5, null);
			}
		}
		return false;
	}

	// 初始化本地音乐
	private void initListMusicItem() {
		List<HashMap<String, Object>> data = Common.getListMusicData();
		SimpleAdapter music_adapter = new SimpleAdapter(this, data,
				R.layout.list_item, new String[] { "icon", "title", "icon2" },
				new int[] { R.id.iv_list_item_icon, R.id.tv_list_item_title,
						R.id.iv_list_item_icon2 });
		ListView lv_list_music = (ListView) list_main_music
				.findViewById(R.id.lv_list_music);
		lv_list_music.setAdapter(music_adapter);
		lv_list_music.setOnItemClickListener(list_music_listener);
	}

	// 音乐列表项的点击事件
	private OnItemClickListener list_music_listener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			jumpPage(1, position + 1, null);
		}
	};

	/**
	 * 当前歌曲列表的播放列表的查询条件
	 * */
	private String condition = null;

	/**
	 * 跳转某页面事件
	 * */
	private void jumpPage(int classIndex, int flag, Object obj) {
		int[] playerInfo = new int[] { mediaPlayerManager.getSongId(),
				mediaPlayerManager.getPlayerState() };
		if (classIndex == 1) {
			rl_list_main_content.setVisibility(View.GONE);
			rl_list_content.setVisibility(View.VISIBLE);
			ibtn_list_content_icon.setBackgroundResource(R.drawable.back);
			btn_list_random_music2.setVisibility(View.GONE);
			ibtn_list_content_do_icon.setVisibility(View.GONE);

			if (flag == 1) {// 全部歌曲
				tv_list_content_title.setText("全部歌曲");
				List<String[]> data = songDao.searchByAll();
				lv_list_change_content.setAdapter(new SongItemAdapter(
						ListMainActivity.this, data, playerInfo)
						.setItemListener(songItemListener));
				btn_list_random_music2.setVisibility(View.VISIBLE);
				btn_list_random_music2.setText("(共" + data.size() + "首)随机播放");
				btn_list_random_music2.setTag(data.size());
			} else if (flag == 2) {// 歌手
				tv_list_content_title.setText("歌手");
				lv_list_change_content.setAdapter(new ListItemAdapter(
						ListMainActivity.this, artistDao.searchAll(),
						R.drawable.default_list_singer));
			} else if (flag == 3) {// 专辑
				tv_list_content_title.setText("专辑");
				lv_list_change_content.setAdapter(new ListItemAdapter(
						ListMainActivity.this, albumDao.searchAll(),
						R.drawable.default_list_album));
			} else if (flag == 4) {// 文件夹
				tv_list_content_title.setText("文件夹");
				lv_list_change_content.setAdapter(new ListItemAdapter(
						ListMainActivity.this, songDao.searchByDirectory(),
						R.drawable.local_file));
			} else if (flag == 5) {// 播放列表
				tv_list_content_title.setText("播放列表");
				lv_list_change_content.setAdapter(new ListItemAdapter(
						ListMainActivity.this, playerListDao.searchAll(),
						R.drawable.local_custom));
				ibtn_list_content_do_icon.setVisibility(View.VISIBLE);
			} else if (flag == 6) {// 我最爱听
				tv_list_content_title.setText("我最爱听");
				btn_list_random_music2.setVisibility(View.VISIBLE);
				List<String[]> data = songDao.searchByIsLike();
				lv_list_change_content.setAdapter(new SongItemAdapter(
						ListMainActivity.this, data, playerInfo)
						.setItemListener(songItemListener));
				btn_list_random_music2.setVisibility(View.VISIBLE);
				btn_list_random_music2.setText("(共" + data.size() + "首)随机播放");
				btn_list_random_music2.setTag(data.size());
			} else if (flag == 7) {// 最近播放
				tv_list_content_title.setText("最近播放");
				btn_list_random_music2.setVisibility(View.VISIBLE);
				List<String[]> data = songDao.searchByLately(mediaPlayerManager
						.getLatelyStr());
				lv_list_change_content.setAdapter(new SongItemAdapter(
						ListMainActivity.this, data, playerInfo)
						.setItemListener(songItemListener));
				btn_list_random_music2.setVisibility(View.VISIBLE);
				btn_list_random_music2.setText("(共" + data.size() + "首)随机播放");
				btn_list_random_music2.setTag(data.size());
			}
			pageNumber = flag;
		} else if (classIndex == 2) {
			btn_list_random_music2.setVisibility(View.VISIBLE);
			TextView textView = ((ListItemAdapter.ViewHolder) obj).textView;
			condition = textView.getTag().toString().trim();
			tv_list_content_title.setText(textView.getText().toString());
			List<String[]> data = null;
			if (flag == 22) {// 某歌手歌曲
				data = songDao.searchByArtist(condition);
			} else if (flag == 33) {// 某专辑歌曲
				data = songDao.searchByAlbum(condition);
			} else if (flag == 44) {// 某文件夹歌曲
				data = songDao.searchByDirectory(condition);
			} else if (flag == 55) {// 某播放列表的歌曲
				data = songDao.searchByPlayerList("$" + condition + "$");
			}
			lv_list_change_content.setAdapter(new SongItemAdapter(
					ListMainActivity.this, data, playerInfo)
					.setItemListener(songItemListener));
			btn_list_random_music2.setText("(共" + data.size() + "首)随机播放");
			btn_list_random_music2.setTag(data.size());
			pageNumber = flag;
		}
	}

	/**
	 * 更新本地列表的数据展示（扫描后）
	 * */
	private void updateListAdapterData() {
		int[] playerInfo = new int[] { mediaPlayerManager.getSongId(),
				mediaPlayerManager.getPlayerState() };
		if (pageNumber == 1) {
			List<String[]> data = songDao.searchByAll();
			lv_list_change_content.setAdapter(new SongItemAdapter(
					ListMainActivity.this, data, playerInfo)
					.setItemListener(songItemListener));
			btn_list_random_music2.setText("(共" + data.size() + "首)随机播放");
			btn_list_random_music2.setTag(data.size());
		} else if (pageNumber == 22 || pageNumber == 33 || pageNumber == 44
				|| pageNumber == 55) {
			List<String[]> data = null;
			if (pageNumber == 22) {
				data = songDao.searchByArtist(condition);
			} else if (pageNumber == 33) {
				data = songDao.searchByAlbum(condition);
			} else if (pageNumber == 44) {
				data = songDao.searchByDirectory(condition);
			} else if (pageNumber == 55) {
				data = songDao.searchByPlayerList("$" + condition + "$");
			}
			lv_list_change_content.setAdapter(new SongItemAdapter(
					ListMainActivity.this, data, playerInfo)
					.setItemListener(songItemListener));
			btn_list_random_music2.setText("(共" + data.size() + "首)随机播放");
			btn_list_random_music2.setTag(data.size());
		}
	}

	/**
	 * 删除歌曲，重置播放列表
	 * */
	private void deleteForResetPlayerList(int songId, int flag, String parameter) {
		final int state = mediaPlayerManager.getPlayerState();
		if (state == MediaPlayerManager.STATE_NULL
				|| state == MediaPlayerManager.STATE_OVER) {
			return;
		}
		if (mediaPlayerManager.getPlayerFlag() == MediaPlayerManager.PLAYERFLAG_WEB) {
			return;
		}
		String t_parameter = mediaPlayerManager.getParameter();
		if (t_parameter == null)
			t_parameter = "";
		if (flag == MediaPlayerManager.PLAYERFLAG_ALL
				|| (mediaPlayerManager.getPlayerFlag() == flag && parameter
						.equals(t_parameter))) {
			// 删除'播放列表'，就播放全部歌曲
			if (songId == -1) {
				mediaPlayerManager.delete(-1);
				return;
			} else {
				// 如果是当前播放歌曲，就要切换下一首
				if (songId == mediaPlayerManager.getSongId()) {
					mediaPlayerManager.delete(songId);
				}
			}
			mediaPlayerManager.resetPlayerList();
		}
	}

	// 本地音乐的二三级布局-替换的ListView ItemClick事件
	private OnItemClickListener list_change_content_listener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (pageNumber == 2) {// 歌手种类列表
				jumpPage(2, 22, view.getTag());
			} else if (pageNumber == 3) {// 专辑种类列表
				jumpPage(2, 33, view.getTag());
			} else if (pageNumber == 4) {// 文件夹种类列表
				jumpPage(2, 44, view.getTag());
			} else if (pageNumber == 5) {// 播放种类列表
				ibtn_list_content_do_icon.setVisibility(View.GONE);
				jumpPage(2, 55, view.getTag());
			} else if (pageNumber == 1) {// 全部歌曲列表
				playerMusicByItem(view, MediaPlayerManager.PLAYERFLAG_ALL, null);
			} else if (pageNumber == 6) {// 我最爱听列表
				playerMusicByItem(view, MediaPlayerManager.PLAYERFLAG_LIKE,
						null);
			} else if (pageNumber == 7) {// 最近播放列表
				playerMusicByItem(view, MediaPlayerManager.PLAYERFLAG_LATELY,
						null);
			} else if (pageNumber == 22) {// 某歌手歌曲列表
				playerMusicByItem(view, MediaPlayerManager.PLAYERFLAG_ARTIST,
						condition);
			} else if (pageNumber == 33) {// 某专辑歌曲列表
				playerMusicByItem(view, MediaPlayerManager.PLAYERFLAG_ALBUM,
						condition);
			} else if (pageNumber == 44) {// 某文件夹歌曲列表
				playerMusicByItem(view, MediaPlayerManager.PLAYERFLAG_FOLDER,
						condition);
			} else if (pageNumber == 55) {// 某播放列表歌曲列表
				playerMusicByItem(view,
						MediaPlayerManager.PLAYERFLAG_PLAYERLIST, condition);
			}
		}
	};

	private void playerMusicByItem(View view, int flag, String condition) {
		int songId = Integer.valueOf(((SongItemAdapter.ViewHolder) view
				.getTag()).tv_song_list_item_bottom.getTag().toString());
		if (songId == mediaPlayerManager.getSongId()) {
			PlayerOrPause(view);
		} else {
			ibtn_player_control
					.setBackgroundResource(R.drawable.player_btn_mini_pause);
			mediaPlayerManager.player(songId, flag, condition);
			int[] playerInfo = new int[] { songId,
					mediaPlayerManager.getPlayerState() };
			((SongItemAdapter) lv_list_change_content.getAdapter())
					.setPlayerInfo(playerInfo);
		}
	}

	// 本地音乐的二三级布局-替换的ListView ItemLoogClick事件
	private OnItemLongClickListener list_change_content_looglistener = new OnItemLongClickListener() {

		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			if (pageNumber == 5) {// 播放列表时，弹出菜单
				if (position != 0) {
					doPlayListLoogItemDialog(view);
					return true;
				}
			} else {
				if (!(pageNumber == 2 || pageNumber == 3 || pageNumber == 4 || pageNumber == 5)) {
					final SongItemAdapter.ViewHolder viewHolder = (SongItemAdapter.ViewHolder) view
							.getTag();
					final String path = viewHolder.tv_song_list_item_top
							.getTag().toString();// 歌曲路径
					final int sid = Integer
							.parseInt(viewHolder.tv_song_list_item_bottom
									.getTag().toString());// 歌曲id
					final String text = viewHolder.tv_song_list_item_top
							.getText().toString();

					doListSongLoogItemDialog(sid, text, path, position);
				}
			}
			return false;
		}

	};

	// 歌曲列表项事件
	private SongItemAdapter.ItemListener songItemListener = new SongItemAdapter.ItemListener() {
		@Override
		public void onLikeClick(int id, View view, int position) {
			// 排除我最爱听歌曲列表
			if (pageNumber == 6) {
				songDao.updateByLike(id, 0);
				// 更新歌曲列表
				((SongItemAdapter) lv_list_change_content.getAdapter())
						.deleteItem(position);
				btn_list_random_music2.setText("(共"
						+ lv_list_change_content.getCount() + "首)随机播放");
				btn_list_random_music2
						.setTag(lv_list_change_content.getCount());
				deleteForResetPlayerList(id,
						MediaPlayerManager.PLAYERFLAG_LIKE, "");
				return;
			}
			if (view.getTag().equals("1")) {
				view.setTag("0");
				view.setBackgroundResource(R.drawable.dislike);
				songDao.updateByLike(id, 0);
			} else {
				view.setTag("1");
				view.setBackgroundResource(R.drawable.like);
				songDao.updateByLike(id, 1);
			}
		}

		@Override
		public void onMenuClick(int id, String text, String path, int position) {
			doListSongLoogItemDialog(id, text, path, position);
		}
	};

	/**
	 * 创建歌曲列表菜单对话框
	 * */
	private void doListSongLoogItemDialog(final int sid, String text,
			final String path, final int parentposition) {
		String delete_title = "移除歌曲";
		if (pageNumber == 9) {
			delete_title = "清除任务";
		}
		String[] menustring = new String[] { "设置节奏等级", "添加到列表", "设为铃声",
				delete_title, "查看详情" };
		ListView menulist = new ListView(ListMainActivity.this);
		menulist.setCacheColorHint(Color.TRANSPARENT);
		menulist.setDividerHeight(1);
		menulist.setAdapter(new ArrayAdapter<String>(ListMainActivity.this,
				R.layout.dialog_menu_item, R.id.text1, menustring));
		menulist.setLayoutParams(new LayoutParams(Common
				.getScreen(ListMainActivity.this)[0] / 2,
				LayoutParams.WRAP_CONTENT));

		final XfDialog xfdialog = new XfDialog.Builder(ListMainActivity.this)
				.setTitle(text).setView(menulist).create();
		xfdialog.show();

		menulist.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				xfdialog.cancel();
				xfdialog.dismiss();
				if (position == 0) {// 设置音乐节奏等级
					createMusicLevelDialog(sid);
				}
				if (position == 1) {// 添加到列表
					createPlayerListDialog(sid);
				} else if (position == 2) {// 设为铃声
					createRingDialog(path);
				} else if (position == 3) {// 移除歌曲
					createDeleteSongDialog(sid, path, parentposition, true);
				} else if (position == 4) {// 查看详情
					createSongDetailDialog(sid);
				}
			}
		});
	}

	/**
	 * 添加到列表对话框
	 * */
	private void createPlayerListDialog(final int id) {
		List<String[]> pList = playerListDao.searchAll();

		RadioGroup rg_pl = new RadioGroup(ListMainActivity.this);
		rg_pl.setLayoutParams(params);
		final List<RadioButton> rbtns = new ArrayList<RadioButton>();

		for (int i = 0; i < pList.size(); i++) {
			String[] str_temp = pList.get(i);
			RadioButton rbtn_temp = new RadioButton(ListMainActivity.this);
			rbtn_temp.setText(str_temp[1]);
			rbtn_temp.setTag(str_temp[0]);
			rg_pl.addView(rbtn_temp, params);
			rbtns.add(rbtn_temp);
		}

		new XfDialog.Builder(ListMainActivity.this).setTitle("播放列表")
				.setView(rg_pl)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						dialog.dismiss();
						int selectedIndex = -1;
						for (int i = 0; i < rbtns.size(); i++) {
							if (rbtns.get(i).isChecked()) {
								selectedIndex = i;
								break;
							}
						}
						if (selectedIndex != -1) {
							songDao.updateByPlayerList(
									id,
									Integer.valueOf(rbtns.get(selectedIndex)
											.getTag().toString()));
							toast = Common.showMessage(toast,
									ListMainActivity.this, "添加成功");
						} else {
							toast = Common.showMessage(toast,
									ListMainActivity.this, "请选择要添加到的播放列表");
						}
					}
				}).setNegativeButton("取消", null).create().show();
	}

	/**
	 * 设置音乐节奏等级对话框[1:舒缓,2:愉悦,3:动感]
	 * */
	private void createMusicLevelDialog(final int id) {
		RadioGroup rg_level = new RadioGroup(ListMainActivity.this);
		rg_level.setLayoutParams(params);

		final RadioButton rbtn_lowLevel = new RadioButton(ListMainActivity.this);
		rbtn_lowLevel.setText("舒缓");
		rg_level.addView(rbtn_lowLevel, params);

		final RadioButton rbtn_middleLevel = new RadioButton(
				ListMainActivity.this);
		rbtn_middleLevel.setText("愉悦");
		rg_level.addView(rbtn_middleLevel, params);

		final RadioButton rbtn_highLevel = new RadioButton(
				ListMainActivity.this);
		rbtn_highLevel.setText("动感");
		rg_level.addView(rbtn_highLevel, params);

		new XfDialog.Builder(ListMainActivity.this).setTitle("设置音乐节奏等级")
				.setView(rg_level)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Song song = songDao.searchById(id);
						int type = -1;
						if (rbtn_lowLevel.isChecked()) {
							type = 0;
							song.setLevel(1);
							songDao.updateByLevel(id, 1);

							toast = Common.showMessage(toast,
									ListMainActivity.this, "设置为舒缓音乐成功");
						} else if (rbtn_middleLevel.isChecked()) {
							type = 0;
							song.setLevel(2);
							songDao.updateByLevel(id, 2);
							toast = Common.showMessage(toast,
									ListMainActivity.this, "设置为愉悦音乐成功");
						} else if (rbtn_highLevel.isChecked()) {
							type = 0;
							song.setLevel(3);
							songDao.updateByLevel(id, 3);
							toast = Common.showMessage(toast,
									ListMainActivity.this, "设置为动感音乐成功");
						}
						if (type == -1) {
							toast = Common.showMessage(toast,
									ListMainActivity.this, "请设置音乐节奏等级");
						} else {
							try {
							} catch (Exception e) {
								toast = Common.showMessage(toast,
										ListMainActivity.this, "音乐等级设置失败");
							}
							dialog.cancel();
							dialog.dismiss();
						}
					}
				}).setNegativeButton("取消", null).show();

	}

	/**
	 * 移除歌曲对话框:flag是否是本地歌曲列表删除
	 * */
	private void createDeleteSongDialog(final int sid, final String filepath,
			final int position, final boolean flag) {
		String t_title = "移除歌曲";
		if (pageNumber == 9) {
			t_title = "清除任务";
		}
		final String title = t_title;
		final CheckBox cb_deletesong = new CheckBox(ListMainActivity.this);
		cb_deletesong.setLayoutParams(params);
		cb_deletesong.setText("同时删除本地文件");

		XfDialog.Builder builder = new XfDialog.Builder(ListMainActivity.this)
				.setView(cb_deletesong).setTitle(title)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (cb_deletesong.isChecked()) {
							Common.deleteFile(ListMainActivity.this, filepath);
						}
						int rs = 0;
						// 从播放列表中移除
						if (!cb_deletesong.isChecked() && pageNumber == 55) {
							rs = songDao.deleteByPlayerList(sid,
									Integer.valueOf(condition));
						} else {
							// 没有选中并且是下载完成删除
							if (!cb_deletesong.isChecked() && !flag) {
								rs = songDao.updateByDownLoadState(sid);
							} else {
								rs = songDao.delete(sid);
							}
						}
						if (rs > 0) {
							toast = Common.showMessage(toast,
									ListMainActivity.this, title + "成功");
							dialog.cancel();
							dialog.dismiss();

							// 更新歌曲列表
							if (flag) {
								((SongItemAdapter) lv_list_change_content
										.getAdapter()).deleteItem(position);
							}
							if (pageNumber == 1) {
								deleteForResetPlayerList(sid,
										MediaPlayerManager.PLAYERFLAG_ALL, "");
							} else if (pageNumber == 6) {
								deleteForResetPlayerList(sid,
										MediaPlayerManager.PLAYERFLAG_LIKE, "");
							} else if (pageNumber == 7) {
								deleteForResetPlayerList(sid,
										MediaPlayerManager.PLAYERFLAG_LATELY,
										"");
							} else if (pageNumber == 22) {
								deleteForResetPlayerList(sid,
										MediaPlayerManager.PLAYERFLAG_ARTIST,
										condition);
							} else if (pageNumber == 33) {
								deleteForResetPlayerList(sid,
										MediaPlayerManager.PLAYERFLAG_ALBUM,
										condition);
							} else if (pageNumber == 44) {
								deleteForResetPlayerList(sid,
										MediaPlayerManager.PLAYERFLAG_FOLDER,
										condition);
							} else if (pageNumber == 55) {
								deleteForResetPlayerList(
										sid,
										MediaPlayerManager.PLAYERFLAG_PLAYERLIST,
										condition);
							}
							btn_list_random_music2.setText("(共"
									+ lv_list_change_content.getCount()
									+ "首)随机播放");
							btn_list_random_music2
									.setTag(lv_list_change_content.getCount());
						} else {
							toast = Common.showMessage(toast,
									ListMainActivity.this, title + "失败");
						}
					}
				}).setNegativeButton("取消", null);

		builder.create().show();
	}

	/**
	 * 设置铃声对话框
	 * */
	private void createRingDialog(final String filepath) {
		RadioGroup rg_ring = new RadioGroup(ListMainActivity.this);
		rg_ring.setLayoutParams(params);
		final RadioButton rbtn_ringtones = new RadioButton(
				ListMainActivity.this);
		rbtn_ringtones.setText("来电铃声");
		rg_ring.addView(rbtn_ringtones, params);
		final RadioButton rbtn_alarms = new RadioButton(ListMainActivity.this);
		rbtn_alarms.setText("闹铃铃声");
		rg_ring.addView(rbtn_alarms, params);
		final RadioButton rbtn_notifications = new RadioButton(
				ListMainActivity.this);
		rbtn_notifications.setText("通知铃声");
		rg_ring.addView(rbtn_notifications, params);
		final RadioButton rbtn_all = new RadioButton(ListMainActivity.this);
		rbtn_all.setText("全部铃声");
		rg_ring.addView(rbtn_all, params);

		new XfDialog.Builder(ListMainActivity.this).setTitle("设置铃声")
				.setView(rg_ring)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ContentValues cv = new ContentValues();
						int type = -1;
						if (rbtn_ringtones.isChecked()) {
							type = RingtoneManager.TYPE_RINGTONE;
							cv.put(MediaStore.Audio.Media.IS_RINGTONE, true);
						} else if (rbtn_alarms.isChecked()) {
							type = RingtoneManager.TYPE_ALARM;
							cv.put(MediaStore.Audio.Media.IS_ALARM, true);
						} else if (rbtn_notifications.isChecked()) {
							type = RingtoneManager.TYPE_NOTIFICATION;
							cv.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
						} else if (rbtn_all.isChecked()) {
							type = RingtoneManager.TYPE_ALL;
							cv.put(MediaStore.Audio.Media.IS_RINGTONE, true);
							cv.put(MediaStore.Audio.Media.IS_ALARM, true);
							cv.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
						}
						if (type == -1) {
							toast = Common.showMessage(toast,
									ListMainActivity.this, "请选择铃声类型");
						} else {
							Uri uri = MediaStore.Audio.Media
									.getContentUriForPath(filepath);
							Uri ringtoneUri = null;
							Cursor cursor = ListMainActivity.this
									.getContentResolver()
									.query(uri,
											null,
											MediaStore.MediaColumns.DATA + "=?",
											new String[] { filepath }, null);
							// 查询媒体库中存在的
							if (cursor.getCount() > 0 && cursor.moveToFirst()) {
								String _id = cursor.getString(0);
								// 更新媒体库
								getContentResolver().update(uri, cv,
										MediaStore.MediaColumns.DATA + "=?",
										new String[] { filepath });
								ringtoneUri = Uri.withAppendedPath(uri, _id);
							} else {// 不存在就添加
								cv.put(MediaStore.MediaColumns.DATA, filepath);
								ringtoneUri = ListMainActivity.this
										.getContentResolver().insert(uri, cv);
							}
							try {
								RingtoneManager.setActualDefaultRingtoneUri(
										ListMainActivity.this, type,
										ringtoneUri);
								toast = Common.showMessage(toast,
										ListMainActivity.this, "铃声设置成功");
							} catch (Exception e) {
								toast = Common.showMessage(toast,
										ListMainActivity.this, "铃声设置失败");
							}
							dialog.cancel();
							dialog.dismiss();
						}
					}
				}).setNegativeButton("取消", null).show();
	}

	/**
	 * 歌曲详细对话框
	 * */
	private void createSongDetailDialog(int id) {
		Song song = songDao.searchById(id);
		song.setLevel(songDao.searchLevelById(id));
		File file = new File(song.getFilePath());
		// 歌曲不存在
		if (!file.exists()) {
			toast = Common.showMessage(toast, ListMainActivity.this,
					"歌曲已经不存在，请删除歌曲！");
			return;
		}
		if (song.getSize() == -1) {
			song.setSize((int) file.length());
			songDao.updateBySize(id, song.getSize());
		}
		// 表示当时扫描时，是在媒体库中不存在的歌曲
		int duration = song.getDurationTime();
		if (duration == -1) {
			// 获取播放时长
			MediaPlayer t_MediaPlayer = new MediaPlayer();
			try {
				t_MediaPlayer.setDataSource(song.getFilePath());
				t_MediaPlayer.prepare();
				duration = t_MediaPlayer.getDuration();
			} catch (IllegalArgumentException e) {
			} catch (IllegalStateException e) {
			} catch (IOException e) {
			} finally {
				t_MediaPlayer.release();
				t_MediaPlayer = null;
			}
			if (duration != -1) {
				song.setDurationTime(duration);
				// 更新数据库
				songDao.updateByDuration(id, duration);
			}
		}

		View view = inflater.inflate(R.layout.song_detail, null);
		view.setLayoutParams(new LayoutParams(Common
				.getScreen(ListMainActivity.this)[0] / 2,
				LayoutParams.WRAP_CONTENT));

		((TextView) view.findViewById(R.id.tv_song_title)).setText(song
				.getName());
		((TextView) view.findViewById(R.id.tv_song_album)).setText(song
				.getAlbum().getName());
		((TextView) view.findViewById(R.id.tv_song_artist)).setText(song
				.getArtist().getName());
		((TextView) view.findViewById(R.id.tv_song_duration)).setText(Common
				.formatSecondTime(duration));
		((TextView) view.findViewById(R.id.tv_song_filepath)).setText(song
				.getFilePath());
		((TextView) view.findViewById(R.id.tv_song_format)).setText(Common
				.getSuffix(song.getDisplayName()));
		((TextView) view.findViewById(R.id.tv_song_size)).setText(Common
				.formatByteToMB(song.getSize()) + "MB");

		new XfDialog.Builder(ListMainActivity.this).setTitle("歌曲详细信息")
				.setNeutralButton("确定", null).setView(view).create().show();
	}

	/**
	 * 创建播放列表长按事件--菜单对话框
	 * */
	private void doPlayListLoogItemDialog(View view) {
		final TextView textView = ((ListItemAdapter.ViewHolder) view.getTag()).textView;
		final String text = textView.getText().toString();// 播放列表名称
		final int plid = Integer.parseInt(textView.getTag().toString());// 播放列表id

		String[] menustring = new String[] { "重命名", "删除" };
		ListView menulist = new ListView(ListMainActivity.this);
		menulist.setCacheColorHint(Color.TRANSPARENT);
		menulist.setDividerHeight(1);
		menulist.setAdapter(new ArrayAdapter<String>(ListMainActivity.this,
				R.layout.dialog_menu_item, R.id.text1, menustring));
		menulist.setLayoutParams(new LayoutParams(Common
				.getScreen(ListMainActivity.this)[0] / 2,
				LayoutParams.WRAP_CONTENT));

		final XfDialog xfdialog = new XfDialog.Builder(ListMainActivity.this)
				.setTitle(text).setView(menulist).create();
		xfdialog.show();

		menulist.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {// 重命名
					xfdialog.cancel();
					xfdialog.dismiss();
					doPlayList(1, plid, text);
				} else if (position == 1) {// 删除
					xfdialog.cancel();
					xfdialog.dismiss();
					new XfDialog.Builder(ListMainActivity.this)
							.setTitle("删除提示")
							.setMessage("是否要删除这个播放列表？")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											if (playerListDao.delete(plid) > 0) {
												toast = Common.showMessage(
														toast,
														ListMainActivity.this,
														"删除成功！");
												lv_list_change_content
														.setAdapter(new ListItemAdapter(
																ListMainActivity.this,
																playerListDao
																		.searchAll(),
																R.drawable.local_custom));

												// 更新正在播放列表
												deleteForResetPlayerList(
														-1,
														MediaPlayerManager.PLAYERFLAG_PLAYERLIST,
														String.valueOf(plid));
											} else {
												toast = Common.showMessage(
														toast,
														ListMainActivity.this,
														"删除失败！");
											}
											dialog.cancel();
											dialog.dismiss();
										}
									}).setNegativeButton("取消", null).create()
							.show();
				}
			}

		});

	}

	/**
	 * 初始化导航栏
	 * */
	private void initTabItem() {
		for (int i = 0; i < vg_list_tab_item.length; i++) {
			vg_list_tab_item[i].setOnClickListener(tabClickListener);
			((TextView) vg_list_tab_item[i]
					.findViewById(R.id.tv_list_item_text))
					.setText(list_item_items[i]);
		}
	}

	// 主屏幕左右滑动事件
	private OnScrollToScreenListener scrollToScreenListener = new OnScrollToScreenListener() {

		public void operation(int currentScreen, int screenCount) {
			vg_list_tab_item[screenIndex].setBackgroundResource(0);
			screenIndex = currentScreen;
		}
	};

	// 导航栏选项卡切换事件
	private OnClickListener tabClickListener = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.list_tab_item_music:
				if (screenIndex == 0) {
					return;
				}
				vg_list_tab_item[screenIndex].setBackgroundResource(0);
				screenIndex = 0;
				break;
			default:
				break;
			}
			fgv_list_main.setToScreen(screenIndex, true);
		}
	};

}
