package com.dandanchina.android;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dandanchina.android.base.BaseActivity;
import com.dandanchina.android.bean.Category;
import com.dandanchina.android.bean.VerifyInfo;
import com.dandanchina.android.control.MobileApi;
import com.dandanchina.android.control.Response;
import com.dandanchina.android.control.SimpleRequestCallBack;

public class MainActivity extends BaseActivity implements OnClickListener{

	String accessToken = "6718e05e4188840c86e50919fbbe7b65";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button btn1 = (Button) findViewById(R.id.btn1);
		Button btn2 = (Button) findViewById(R.id.btn2);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		
		

	}
	@Override
	public void onClick(View view) {
		MobileApi api = MobileApi.getInstance();
		switch (view.getId()) {

		case R.id.btn1:
			api.categoryList(0, accessToken, this, new SimpleRequestCallBack<List<Category>>(this) {
				
				@Override
				public void onSuccess(List<Category> data, Response response) {
					for (Category category : data) {
						Log.v("category", category.getName());
					}
				}
				
				@Override
				public void onFailure() {
					
				}
			});
			break;
		case R.id.btn2:
			
			break;
		}
		
		
	}
	
	

	
	
	
	
}
