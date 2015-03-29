package com.healthslife.integralactivity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.healthslife.R;

public class IntegralActivity extends FragmentActivity{
	private ViewPager viewPager;

	private RadioGroup radioGroup;

	private ArrayList<Fragment> arrayFragmentlList;

	private FragmentAdapter fAdapter_main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initData();
		initView();
	}

	private void initData() {
		arrayFragmentlList = new ArrayList<Fragment>();

		Fragment activityProfile = new ActivityProfile();
		Fragment exchangeCenter = new ExchangeCenter();

		arrayFragmentlList.add(activityProfile);
		arrayFragmentlList.add(exchangeCenter);

	}

	private void initView() {

		this.viewPager = (ViewPager) findViewById(R.id.viewPager);
		this.radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		fAdapter_main = new FragmentAdapter(getSupportFragmentManager(), arrayFragmentlList);
		viewPager.setAdapter(fAdapter_main);
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					radioGroup.check(R.id.bt_map1);
					break;
				case 1:
					radioGroup.check(R.id.bt_map3);
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});


		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.bt_map1:
					viewPager.setCurrentItem(0, true);
					break;
				case R.id.bt_map3:
					viewPager.setCurrentItem(2, true);
					break;
				default:
					break;
				}
			}
		});
	}
}
