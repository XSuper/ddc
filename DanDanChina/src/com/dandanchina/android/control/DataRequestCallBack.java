package com.dandanchina.android.control;

import android.content.Context;

import com.dandanchina.android.base.BaseActivity;

public abstract class DataRequestCallBack{

	Context context;
	public DataRequestCallBack(Context context) {
		this.context = context;
	}
	public abstract void onSuccess(Response response);
	public abstract void onFailure();
	
	
	
}
