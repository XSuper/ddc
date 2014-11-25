package com.dandanchina.android.control;



import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.dandanchina.android.utils.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.http.ResponseInfo;
/**
 * 
 * 响应类
 *
 */
public class Response {
	
	public boolean isCache; //是否是读取缓存
	public String result; //响应字符串
	public String message; 
	public int status; //响应状态
	public ResponseInfo<String> responseInfo; //xutil 网络响应的类
	
	public JSONObject data;  //data 的json对象
	public JSONObject jo;    //根据result 生成的json 对象
	
	public boolean success = false;
	
	public static final String STATUS ="status";
	public static final String MESSAGE = "message";
	public static final String DATA = "data";
	public Response(String result,boolean isCache){
		initResponse(result, isCache);
		
	}
	private void initResponse(String result, boolean isCache) {
		this.isCache = isCache;
		// 有返回success code 登按 返回结果
		if (!TextUtils.isEmpty(result)) {
			// json对象
			if (result.trim().startsWith("" +
					"{")) {
				try {
					jo = new JSONObject(result);
					if (jo.has(STATUS)) {
						status = JSONUtil.getInt(jo,
								STATUS,500);
						if(status==200||status ==201){
							success = true;
						}
					}
					if (jo.has(MESSAGE)) {
						message = jo.getString(MESSAGE);
					}
					if (jo.has(DATA)) {
						data = JSONUtil.getJSONObject(jo, DATA);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (result.trim().startsWith("[")) {
				// 不处理
			}
		}
	}
	public Response(ResponseInfo<String> responseInfo){
		this.responseInfo = responseInfo;
		result = responseInfo.result;
		initResponse(result, isCache);
	}

	
	/**
	 * 解析json结果 为bean
	 * 
	 * @return
	 */
	public <T> T model(Class<T> clazz) {
		Gson gson = new Gson();
		T obj = gson.fromJson(result, clazz);
		return obj;
	}

	/**
	 * 解析json结果 为 bean list
	 * 
	 * @return
	 */
	public <T> List<T> list(final Class<T> clazz) {
		Gson gson = new Gson();
		Type type = new ParameterizedType() {
			public Type getRawType() {
				return ArrayList.class;
			}

			public Type getOwnerType() {
				return null;
			}

			public Type[] getActualTypeArguments() {
				Type[] type = new Type[1];
				type[0] = clazz;
				return type;
			}
		};
		List<T> list = gson.fromJson(result, type);
		return list;
	}

	/**
	 * 获取json结果 其中的某个属性 为 jsonarray
	 * 
	 * @param prefix
	 * @return
	 */
	public JSONArray jSONArrayFrom(String prefix) {
		if (jo != null) {
			return JSONUtil.getJSONArray(jo, prefix);
		} else {
			try {
				return new JSONArray(this.result);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * 获取json结果 其中的data 为 jsonarray
	 * 
	 * @param prefix
	 * @return
	 */
	public JSONArray jSONArrayFromData() {
		return jSONArrayFrom(DATA);
	}

	/**
	 * 获取json结果 其中的某个属性 为 jsonobject
	 * 
	 * @param prefix
	 * @return
	 */
	public JSONObject jSONFrom(String prefix) {
		if (jo != null) {
			return JSONUtil.getJSONObject(jo, prefix);
		}
		return null;
	}

	/**
	 * 获取json结果 其中data为 jsonobject
	 * 
	 * @param prefix
	 * @return
	 */
	public JSONObject jSONFromData() {
		return jSONFrom(DATA);
	}

	/**
	 * 获取json结果 对象中的某个属性 为对象 prefix data
	 * 
	 * @param prefix
	 * @return
	 */
	public <T> T modelFrom(String prefix) {
		if (jo != null) {
			String str = JSONUtil.getString(jo, prefix);
			Gson gson = new Gson();
			Type type = new TypeToken<T>() {
			}.getType();
			T obj = gson.fromJson(str, type);
			return obj;
		}
		return null;
	}

	/**
	 * 解析json结果 为bean
	 * 
	 * @return
	 */
	public <T> T modelFrom(Class<T> clazz, String prefix) {
		String str = JSONUtil.getString(jo, prefix);
		Gson gson = new Gson();
		T obj = gson.fromJson(str, clazz);
		return obj;
	}
	public <T> T modelFromData(){
		return modelFrom(DATA);
	}
	public <T> T modelFromData(Class<T> clazz){
		return modelFrom(clazz,DATA);
	}

	public <T> T modelFromDataPrefix(String prefix) {
		String str = JSONUtil.getString(data, prefix);
		Gson gson = new Gson();
		Type type = new TypeToken<T>() {
		}.getType();
		T obj = gson.fromJson(str, type);
		return obj;
	}

	public <T> T modelFromDataPrefix(Class<T> clazz,String prefix) {
		String str = JSONUtil.getString(data, prefix);
		Gson gson = new Gson();
		T obj = gson.fromJson(str, clazz);
		return obj;
	}

	/**
	 * 获取json结果 对象中的某个属性 为对象 list
	 * 
	 * @param prefix
	 * @return
	 */
	public <T> List<T> listFrom(final Class<T> clazz, String prefix) {
		if (jo != null) {
			String str = JSONUtil.getString(jo, prefix);
			Gson gson = new Gson();
			Type type = new ParameterizedType() {
				public Type getRawType() {
					return ArrayList.class;
				}

				public Type getOwnerType() {
					return null;
				}

				public Type[] getActualTypeArguments() {
					Type[] type = new Type[1];
					type[0] = clazz;
					return type;
				}
			};
			List<T> list = gson.fromJson(str, type);
			return list;
		}
		return null;
	}

	public <T> List<T> listFromData(Class<T> clazz) {
		return listFrom(clazz, DATA);
	}

	public <T> List<T> listFromDatePrefix(final Class<T> clazz, String prefix) {
		if (jo != null) {
			String str = JSONUtil.getString(data, prefix);
			Gson gson = new Gson();
			Type type = new ParameterizedType() {
				public Type getRawType() {
					return ArrayList.class;
				}

				public Type getOwnerType() {
					return null;
				}

				public Type[] getActualTypeArguments() {
					Type[] type = new Type[1];
					type[0] = clazz;
					return type;
				}
			};
			List<T> list = gson.fromJson(str, type);
			return list;
		}
		return null;
	}

}
