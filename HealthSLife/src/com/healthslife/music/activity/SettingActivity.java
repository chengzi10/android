package com.healthslife.music.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.healthslife.R;

/**
 * 扫描音乐时界面中ActionBar设置，包括一个返回按钮和标题
 *
 */
public class SettingActivity extends BaseActivity {
	public int resultCode=-1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	//为返回按钮点击事件绑定监听器
	public void setBackButton() {
		((ImageButton) (this.findViewById(R.id.bar_setting_top))
				.findViewById(R.id.ibtn_player_back_return))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (v.getId() == R.id.ibtn_player_back_return) {
							if(resultCode!=-1){
								setResult(resultCode);
							}
							finish();
						}
					}
				});
		
	}

	//为ActionBar设置标题
	public void setTopTitle(String title) {
		((TextView) (this.findViewById(R.id.bar_setting_top))
				.findViewById(R.id.tv_setting_top_title)).setText(title);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(resultCode!=-1){
				setResult(resultCode);
			}
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
