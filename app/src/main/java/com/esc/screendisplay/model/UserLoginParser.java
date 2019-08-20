package com.esc.screendisplay.model;

import android.util.Log;
import com.esc.screendisplay.APPAplication;
import com.esc.screendisplay.entity.Role;
import com.esc.screendisplay.entity.User;
import com.esc.screendisplay.entity.UserEntity;
import com.esc.screendisplay.utils.CommonUtil;
import com.esc.screendisplay.utils.MySharePreferencesService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.http.body.RequestBody;
import org.xutils.http.body.StringBody;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 解析登录数据
 */
public class UserLoginParser {

	private final WeakReference<OnDataCompleteListener> wr;
	private UserEntity userEntity = null;

	public UserLoginParser(String userName, String userInfo, String password,
                           OnDataCompleteListener completeListener) {
		wr = new WeakReference<>(completeListener);
		request(userName, userInfo, password);
	}

	/**
	 * 发送请求
	 */
	public void request(final String loginName, final String userInfo, final String password) {

		RequestParams params = new RequestParams(APPAplication.getInstance().getUrl() + "/idap/app/user/login");
		params.setReadTimeout(60 * 1000);
		Map map = new HashMap();
		map.put("loginName", loginName);
		map.put("longinInfo", userInfo);
		map.put("lh", password);
		map.put("app_user_equipment", "");
		map.put("app_client", "android");
		JSONObject jsonObject = new JSONObject(map);
		try {
			RequestBody requestBody = new StringBody(jsonObject.toString(), "UTF-8");
			requestBody.setContentType("application/json");
			params.setRequestBody(requestBody);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		final OnDataCompleteListener onDataCompleteListener = wr.get();
		x.http().post(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String t) {
				Log.i("LoginParse", t);
				userEntity = loginParser(t, loginName, password);
				if ("true".equals(userEntity.getSuccess())) {
					//保存登录成功的状态
					MySharePreferencesService.getInstance(
							APPAplication.getInstance()).saveContactName(
							"login", "true");
				}
				if(onDataCompleteListener != null)
					onDataCompleteListener.onParserComplete(
							userEntity, null);
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {

				String errorResult;
				if (ex instanceof HttpException) {
					errorResult = "网络错误";
				} else if (ex instanceof java.net.SocketTimeoutException) {
					errorResult = "连接服务器超时，请稍后再试";
				}  else if (ex instanceof java.net.ConnectException) {
					errorResult = "连接服务器失败";
				} else {
					errorResult = "其他错误：" + ex.toString();
				}
				if(onDataCompleteListener != null)
					onDataCompleteListener.onParserComplete(null, errorResult);
			}

			@Override
			public void onCancelled(CancelledException cex) {

			}

			@Override
			public void onFinished() {

			}

		});

	}

	/**
	 * 用户登录解析数据
	 * 
	 * @param t
	 * @return
	 * @throws JSONException
	 */
	public UserEntity loginParser(String t, String loginName, String password) {
		UserEntity userEntity = new UserEntity();
		try {
			JSONObject jsonObject = new JSONObject(t);
			if (t.contains("success")) {
				userEntity.setSuccess(jsonObject.getString("success"));
				userEntity.setMsg(jsonObject.optString("msg", ""));
				if (jsonObject.getString("success").equals("true")) {
					if (jsonObject.has("data")) {
						User user = new User(loginName, password);
						JSONObject jsonObject2 = jsonObject.getJSONObject("data");
						user.setId(CommonUtil.getValidStr(jsonObject2.optString("id", "")));
						APPAplication.userId = user.getId();
						user.setUsername(CommonUtil.getValidStr(jsonObject2.optString("name", "")));
						if(jsonObject2.has("orgName"))
							user.setOrgName(CommonUtil.getValidStr(jsonObject2.optString("orgName", "")));
						else
							user.setOrgName("");
						//1 是领导   0 不是领导
						if(jsonObject2.getString("APPLeader").equals("1"))
						{
							user.setRoleType("1");
							user.setPost("领导");
						}
						else {
							user.setRoleType("2");
							user.setPost("一二线工程师");
						}
						if(jsonObject2.has("orgId"))
							user.setOrgId(CommonUtil.getValidStr(jsonObject2.optString("orgId", "")));
						else
							user.setOrgId("");
						//对服务器返回的所有岗位名称去重
						String postName = jsonObject2.optString("postName", "");
						String[] strList = postName.split(",");
						List<String> temp = new ArrayList<>();
						postName = "";
						for(String str : strList) {
							if(str != null && !"".equals(str))
							{
								boolean flag = false;
								for(String str1 : temp)
								{
									if(str1.equals(str)) {
										flag = true;
										break;
									}
								}
								if(!flag)
									temp.add(str);
							}
						}
						for(String str : temp) {
							if(str != null && !"".equals(str))
							{
								postName += "," + str;
							}
						}
						if(!"".equals(postName))
							postName = postName.substring(1);
						user.setPostName(postName);
						if(jsonObject2.has("role"))
						{
							List<Role> roles = new ArrayList<Role>();
							JSONArray jsonArray = jsonObject2.getJSONArray("role");
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject3 = (JSONObject) jsonArray.opt(i);
								Role role = new Role();
								role.setRoleId(CommonUtil.getValidStr(jsonObject3.optString("roleId", "")));
								role.setRoleName(CommonUtil.getValidStr(jsonObject3.optString("roleName", "")));
								role.setRoleType(CommonUtil.getValidStr(jsonObject3.optString("roleType", "")));
								role.setCode(CommonUtil.getValidStr(jsonObject3.optString("code", "")));
								role.setSignature(CommonUtil.getValidStr(jsonObject3.optString("signature", "")));
								roles.add(role);
							}
							user.setRoleList(roles);
						}
						if(jsonObject2.has("userInfos"))
						{
							if(jsonObject2.getString("userInfos").equals("null")
									|| jsonObject2.getString("userInfos").equals(""))
							{
								user.setSex("");
								user.setTelephone("");
								user.setEmail("");
							}
							else {
								JSONObject jsonObject4 =jsonObject2.getJSONObject("userInfos");
								user.setSex(CommonUtil.getValidStr(jsonObject4.optString("sex", "")));
								user.setTelephone(CommonUtil.getValidStr(jsonObject4.optString("fixedPhone", "").replace("-", "")));
								user.setEmail(CommonUtil.getValidStr(jsonObject4.optString("email", "")));
							}
						}
						userEntity.setUser(user);
					}
				} else if (jsonObject.getString("success").equals("false")) {
					userEntity.setUser(null);
					return userEntity;
				}
				return userEntity;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return userEntity;
		}
		return userEntity;
	}
}
