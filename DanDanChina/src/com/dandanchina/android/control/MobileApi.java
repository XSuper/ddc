package com.dandanchina.android.control;

import java.util.List;

import android.content.Context;

import com.dandanchina.android.bean.Category;
import com.dandanchina.android.bean.Product;
import com.dandanchina.android.bean.VerifyInfo;
import com.dandanchina.android.utils.AppUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class MobileApi {

	private static MobileApi mobileApi;

	private MobileApi() {

	}

	public static MobileApi getInstance() {
		if (mobileApi == null) {
			mobileApi = new MobileApi();
		}
		return mobileApi;
	}

	private static final String BASEURL = "http://api.dandanchina.com/rest/v1/";

	// 2.1
	public void Verify(Context context,
			final SimpleRequestCallBack<VerifyInfo> simpleCallback) {
		HttpUtils net = new HttpUtils();
		String v = AppUtil.getPackageInfo(context).versionName;
		String url = BASEURL + "verify/Android/" + v;
		DataController controller = new DataController(url, null, 0l,
				HttpMethod.GET);
		controller.getDateFromNet(NetCachePolicy.POLICY_NOCACHE,
				new DataRequestCallBack(context) {

					@Override
					public void onSuccess(Response response) {
						VerifyInfo info = response
								.modelFromData(VerifyInfo.class);
						simpleCallback.onSuccess(info, response);
					}

					@Override
					public void onFailure() {
						simpleCallback.onFailure();
					}
				});

	}

	public void register() {

	}

	/**
	 * 4.1
	 * 
	 * @param pid
	 *            父级id 0 代表获取一级分类
	 * @param accessToken
	 *            授权 Access Token
	 * @param context
	 * @param callback
	 */
	public DataController categoryList(int pid, String accessToken,
			Context context,
			final SimpleRequestCallBack<List<Category>> simpleCallback) {
		String url = BASEURL + "categorys/p_id/" + pid + "?access_token="
				+ accessToken;
		DataController controller = new DataController(url, null, 0,
				HttpMethod.GET);
		controller.getData(NetCachePolicy.POLICY_NET_NOCACHE,
				new DataRequestCallBack(context) {
					@Override
					public void onSuccess(Response response) {
						List<Category> categorys = response.listFromDatePrefix(
								Category.class, "categorys");
						simpleCallback.onSuccess(categorys, response);
					}

					@Override
					public void onFailure() {
						simpleCallback.onFailure();
					}
				});
		return controller;
	}

	/**
	 * 4.2获取分类下面商品信息 category Product list
	 * 
	 * @param accessToken
	 * @param categoryId
	 * @param pagecount
	 * @param page
	 * @param context
	 * @param callback
	 */
	public DataController categoryProductList(String accessToken,
			int categoryId, int pagecount, int page, Context context,
			final SimpleRequestCallBack<List<Product>> simpleCallback) {

		String url = BASEURL + "categorys/" + categoryId
				+ "/products?access_token=" + accessToken + "&pagecount="
				+ pagecount + "&page=" + page;
		DataController controller = new DataController(url, null, 0,
				HttpMethod.GET);
		controller.getData(NetCachePolicy.POLICY_NET_NOCACHE,
				new DataRequestCallBack(context) {
					@Override
					public void onSuccess(Response response) {
						List<Product> products = response
								.listFromData(Product.class);
						simpleCallback.onSuccess(products, response);
					}

					@Override
					public void onFailure() {
						simpleCallback.onFailure();
					}
				});
		return controller;
	}
}
