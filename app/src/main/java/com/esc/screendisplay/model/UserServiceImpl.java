package com.esc.screendisplay.model;

import android.util.Log;

import com.esc.screendisplay.entity.DisplayEntity;
import com.esc.screendisplay.entity.UserEntity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

	private static UserServiceImpl userServiceImpl = null;

	/**
	 * 获取UserServiceImpl对象
	 * 
	 * @return
	 */
	public synchronized static UserServiceImpl getUserServiceImpl() {
		if (userServiceImpl == null) {
			userServiceImpl = new UserServiceImpl();
		}
		return userServiceImpl;
	}

	/**
	 * 不允许直接访问构造方法
	 */
	private UserServiceImpl() {

	}

	/**
	 * 设置Boolean值返回数据
	 * 
	 * @param listener
	 * @param error
	 */
	private void setBooleanListener(BooleanListener listener, boolean flag, String error) {
		listener.setServiceImplListener(flag, error, error);
	}

	/**
	 * 设置集合返回数据
	 * 
	 * @param listener
	 * @param object
	 * @param error
	 */
	private void setListListener(final ListListener listener, Object object, String error) {
		List<Object> list = null;
		String exceptionError = null;
		if (object != null) {
			list = (List<Object>) object;

		} else {
			exceptionError = error;
		}
		Log.i("UserServiceImpl", "setListListener" + list);
		listener.setListListener(list, null, exceptionError);
	}

	public interface BackValueListener {
		/**
		 * 当backValue!=null时strError，exceptionError都为null；
		 * 当strError!=null时backValue，exceptionError都为null；
		 * 当exceptionError!=null时backValue，strError都为null；backValue回调得到值
		 * strError回调得到接口返回错误信息exceptionError回调得到接口返回异常信息
		 * 
		 * @param strError
		 * @param exceptionError
		 */
		void setListener(String backValue, String strError, String exceptionError);
	}

	public interface BooleanListener {
		/**
		 * 当backFlag=true,false时strError，exceptionError都为null；
		 * 当strError！=null时backFlag=false，exceptionError都为null；
		 * 当exceptionError！=null时backFlag=false，strError都为null；
		 * backFlag回调得到状态true，false，strError回调得到接口返回错误信息
		 * exceptionError回调得到接口返回异常信息
		 *
		 * @param strError
		 * @param exceptionError
		 */
		void setServiceImplListener(boolean backFlag, String strError, String exceptionError);
	}

	public interface ListListener {
		/**
		 * 当object=!null时strError，exceptionError都为null；
		 * 当strError！=null时object，exceptionError都为null；
		 * 当exceptionError!=null时object，strError都为null；
		 *
		 * strError回调得到接口返回错误信息exceptionError回调得到接口返回异常信息
		 *
		 * @param object
		 * @param strError
		 * @param exceptionError
		 */
		void setListListener(Object object, String strError, String exceptionError);
	}

	/**
	 * 登录方法
	 */
	@Override
	public void login(final String userName, final String userInfo, String password, ListListener listener) {
		final WeakReference<ListListener> wr = new WeakReference<>(listener);
		if (userName == null) {
			if(wr.get() != null)
				wr.get().setListListener(null, "未输入用户名", null);
			return;
		}
		new UserLoginParser(userName, userInfo, password, new OnDataCompleteListener() {

			@Override
			public void onParserComplete(Object object, String error) {
				UserEntity userEntity = (UserEntity) object;
				if(wr.get() != null)
					wr.get().setListListener(userEntity,
							error, null);
			}
		});
	}

	/**
	 * 展示列表获取
	 */
	@Override
	public void getDisplayList(String userId, ListListener listener) {
		final WeakReference<ListListener> wr = new WeakReference<>(listener);
		new GetDisplayListParser(userId, new OnDataCompleteListener() {
			@Override
			public void onParserComplete(Object object, String error) {
				List<DisplayEntity> displayEntities = new ArrayList<>();
				String stRerror = null;
				String Exceptionerror = null;
				if (object != null) {
					displayEntities = (ArrayList<DisplayEntity>) object;
				} else if (error != null) {
					Exceptionerror = error;
				} else {
					stRerror = "其他错误";
				}
				if(wr.get() != null)
					wr.get().setListListener(displayEntities,
							stRerror, Exceptionerror);
			}
		});
	}
}
