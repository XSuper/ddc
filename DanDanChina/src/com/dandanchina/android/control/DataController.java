package com.dandanchina.android.control;

import android.util.Log;

import com.dandanchina.android.base.Constants;
import com.dandanchina.android.base.MApplication;
import com.dandanchina.android.control.cache.CacheManager;
import com.dandanchina.android.control.cache.CachePolicy;
import com.dandanchina.android.utils.MD5;
import com.dandanchina.android.utils.NetworkUtils;
import com.dandanchina.android.utils.StrUtil;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 用于访问控制，网络缓存控制
 * 
 */

public class DataController {
	public boolean debug = Constants.DEBUG;
	public static String tag = DataController.class.getSimpleName();
	public NetCachePolicy policy;// 缓存协议
	public HttpUtils net;
	public String url;
	public RequestParams params;
	public String netType;// WIFI , TWO_OR_THREE_G , UNKNOWN
	public CacheManager cacheManager;// 网络缓存
	public HttpMethod httpMethod = HttpMethod.GET;// 默认get 方式
	public long cacheTimeOut = 0;
	public final static long WIFITIMEOUT = 30 * 60 * 1000;// wifi 默认状态下超时时间
	public final static long TIMEOUT = 60 * 60 * 1000;// 3G/2G 状态下超时时间

	public String cacheValue;// 缓存值

	/**
	 * 
	 * @param policy
	 * @param url
	 * @param params
	 * @param timeOut
	 *            设置为0 时使用默认超时时间 <0 时 为不超时 >0 时为设置的超时时间
	 */
	public DataController(String url, RequestParams params, long timeOut,
			HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
		// 得到网络类型
		netType = NetworkUtils.getNetworkState(MApplication.getApplication())[0];
		if (debug)
			Log.i(tag, "netType : " + netType);

		cacheTimeOut = timeOut;
		// wifi 网络下超时时间短于 2G/3G 网络
		if (cacheTimeOut == 0) {
			if ("Wi-Fi".equals(netType)) {
				cacheTimeOut = WIFITIMEOUT;
			} else {
				cacheTimeOut = TIMEOUT;
			}
		}
		net = new HttpUtils();
		this.url = url;
		this.params = params;
		cacheManager = CacheManager.getInstance();
		// 获取缓存
		cacheValue = (String) cacheManager.get(buildKey(),
				new TypeToken<String>() {
				}, CachePolicy.GET_MEMORY_DATEBASE);
	}

	public void getData(NetCachePolicy policy, DataRequestCallBack callBack) {
		switch (policy) {

		case POLICY_CACHE_AND_REFRESH:

			if (StrUtil.isEmpty(cacheValue)) {
				getDateFromNet(policy, callBack);
			} else {
				Response response = new Response(cacheValue, false);
				callBack.onSuccess(response);
			}

			break;
		case POLICY_CACHE_ONLY:
			if (StrUtil.isEmpty(cacheValue)) {
				callBack.onFailure();
			} else {
				Response response = new Response(cacheValue, false);
				callBack.onSuccess(response);
			}

			break;
		case POLICY_NET_NOCACHE:
			if (StrUtil.isEmpty(cacheValue)) {
				getDateFromNet(policy, callBack);
			} else {
				Response response = new Response(cacheValue, false);
				if (debug)
					Log.i(tag, "POLICY_NET_NOCACHE---result = " + cacheValue);
				callBack.onSuccess(response);
			}

			break;
		case POLICY_NOCACHE:
			getDateFromNet(policy, callBack);
			break;
		case POLICY_ON_NET_ERROR:
			getDateFromNet(policy, callBack);
			break;
		}
	}

	public void getDateFromNet(final NetCachePolicy policy,
			final DataRequestCallBack callBack) {

		if (debug)
			Log.i(tag, "getDateFromNet---数据来自互联网");
		net.send(httpMethod, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				if (policy == NetCachePolicy.POLICY_ON_NET_ERROR) {
					if (StrUtil.isEmpty(cacheValue)) {
						callBack.onFailure();
					} else {
						Response response = new Response(cacheValue, true);
						callBack.onSuccess(response);
					}
				} else {
					callBack.onFailure();
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				Response response = new Response(responseInfo);
				// 请求正确
				if (response.success) {
					switch (policy) {
					case POLICY_CACHE_AND_REFRESH:
						// 只做缓存，不做页面更新，如果没有缓存才加载网络后更新页面
						if (StrUtil.isEmpty(cacheValue)) {
							callBack.onSuccess(response);
						} else {
							cacheManager.put(buildKey(), response.result,
									cacheTimeOut,
									CachePolicy.PUT_MEMORY_DATEBASE);
						}
						break;
					case POLICY_CACHE_ONLY:
						// 只读取缓存

						break;

					case POLICY_NOCACHE:
						// 不做缓存
						callBack.onSuccess(response);

						break;
					// 没有缓存的时候用网络
					case POLICY_NET_NOCACHE:
						// 网络连接失败
					case POLICY_ON_NET_ERROR:
						callBack.onSuccess(response);
						cacheManager.put(buildKey(), response.result,
								cacheTimeOut, CachePolicy.PUT_MEMORY_DATEBASE);
						break;

					}

					// 网络请求成功后，更新缓存
					cacheValue = (String) cacheManager.get(buildKey(),
							new TypeToken<String>() {
							}, CachePolicy.GET_MEMORY_DATEBASE);
				} else {
					callBack.onFailure();
				}

			}
		});
	};

	//  根据URL和参数生成key
	private String buildKey() {
		String key = url;
		if (params != null) {
			key += params.getQueryStringParams().toString()
					+ params.getHeaders().toString();
		}
		try {
			return MD5.encryptMD5(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return key;
	}
}
