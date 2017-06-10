/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2014年 mob.com. All rights reserved.
 */
package com.jld.InformationRelease.view.login_register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.view.login_register.countryCode.CountryListView;
import com.jld.InformationRelease.view.login_register.countryCode.GroupListView;
import com.jld.InformationRelease.view.login_register.countryCode.SearchEngine;


/** 国家列表界面 */
public class CountryPageActivity extends Activity implements TextWatcher,
		GroupListView.OnItemClickListener {
	private EditText editText;
	private CountryListView listView;
	private ImageView search_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		//透明导航栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		super.onCreate(savedInstanceState);

		// 初始化搜索引擎
		SearchEngine.prepare(CountryPageActivity.this, new Runnable() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// 初始化搜索引擎
						setContentView(R.layout.country_list_page);

						search_back = (ImageView) findViewById(R.id.tv_search_code_back);
						search_back.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								finish();
							}
						});
						listView = (CountryListView) findViewById(R.id.clCountry);
						editText = (EditText) findViewById(R.id.et_put_identify);
						listView.setOnItemClickListener(CountryPageActivity.this);
						editText.addTextChangedListener(CountryPageActivity.this);
					}
				});
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		listView.onSearch(s.toString().toLowerCase());
	}

	public void afterTextChanged(Editable s) {

	}

	@Override
	public void onItemClick(GroupListView parent, View view, int group,
                            int position) {
		if (position >= 0) {
			String[] country = listView.getCountry(group, position);
			Intent intent = new Intent();
			intent.putExtra("CountryName", country[0]);
			intent.putExtra("CountryCode", country[1]);
			setResult(888, intent);// 返回国家编码
			finish();
		}
	}

	// -------------------------------------隐藏输入法-----------------------------------------------------
	// 获取点击事件
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View view = getCurrentFocus();
			if (isHideInput(view, ev)) {
				HideSoftInput(view.getWindowToken());
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	// 判定是否需要隐藏
	private boolean isHideInput(View v, MotionEvent ev) {
		if (v != null && (v instanceof EditText)) {
			int[] l = { 0, 0 };
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
					+ v.getWidth();
			if (ev.getX() > left && ev.getX() < right && ev.getY() > top
					&& ev.getY() < bottom) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	// 隐藏软键盘
	private void HideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			manager.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
