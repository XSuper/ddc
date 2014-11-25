package com.dandanchina.android.control;

public enum NetCachePolicy {
	/**
	 * 不使用缓存
	 */
	POLICY_NOCACHE,

	/**
	 * 只要有缓存就使用缓存 ,不请求网络，不更新缓存
	 */
	POLICY_CACHE_ONLY,
	/**
	 * 先访问缓存，没有再请求网络  *默认
	 */
	POLICY_NET_NOCACHE,

	/**
	 * 只要有缓存就使用缓存 ,同时更新缓存 ,适用于数据后台处理时间长的操作 <br/>
	 * doInBackground 可能会被调用两次一次是缓存中读数据时,一次是网络数据处理时<br/>
	 * 但inInUI 只会调用一次
	 */
	POLICY_CACHE_AND_REFRESH,
	
	/**
	 * 先网络访问失败时使用缓存
	 */
	POLICY_ON_NET_ERROR,

}
