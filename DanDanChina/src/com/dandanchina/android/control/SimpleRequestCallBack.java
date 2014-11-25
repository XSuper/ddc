package com.dandanchina.android.control;

import com.dandanchina.android.base.BaseActivity;

public abstract class SimpleRequestCallBack<T>{

	BaseActivity activity;
	public SimpleRequestCallBack(BaseActivity activity) {
		this.activity = activity;
	}
	public abstract void onSuccess(T data,Response response);
	public abstract void onFailure();
	
	
	
}
