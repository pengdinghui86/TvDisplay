package com.esc.screendisplay.model;

public interface UserService {
	/**
	 * 登录方法
	 *
	 * @param userName 用户名
	 * @param password 密码
	 * @param listener
	 */
	void login(String userName, String userInfo, String password, UserServiceImpl.ListListener listener);

	/**
	 * 展示列表获取
	 *
	 * @param listener
	 */
	void getDisplayList(String userId, UserServiceImpl.ListListener listener);
}
