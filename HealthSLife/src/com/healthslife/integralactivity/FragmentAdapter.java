package com.healthslife.integralactivity;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter {

	private ArrayList<Fragment> arrayFragmentlList;

	public FragmentAdapter(FragmentManager fragmentManager,
			ArrayList<Fragment> arrayFragmentlList) {
		super(fragmentManager);
		this.arrayFragmentlList = arrayFragmentlList;
	}

	@Override
	public Fragment getItem(int arg0) {
		if (arrayFragmentlList != null) {
			return arrayFragmentlList.get(arg0);
		}
		return null;
	}

	@Override
	public int getCount() {
		if (arrayFragmentlList != null) {
			return arrayFragmentlList.size();
		}
		return 0;
	}
}
