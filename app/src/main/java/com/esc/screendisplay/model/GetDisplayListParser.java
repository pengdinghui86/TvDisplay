package com.esc.screendisplay.model;

import com.esc.screendisplay.APPAplication;
import com.esc.screendisplay.entity.DisplayEntity;
import com.esc.screendisplay.utils.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 获取列表数据
 */
public class GetDisplayListParser {
	private final WeakReference<OnDataCompleteListener> wr;
	private ArrayList<DisplayEntity> displayEntities = null;

	public GetDisplayListParser(String userId, OnDataCompleteListener completeListener) {
		wr = new WeakReference<>(completeListener);
		request(userId);
	}

	public void request(final String userId) {
		String url = APPAplication.getInstance().getUrl() + "/idap/app/tv/getAppTvHtml";
		RequestParams params = new RequestParams(url);
        params.setReadTimeout(60 * 1000);
        params.addParameter("userId", userId);
        final OnDataCompleteListener onDataCompleteListener = wr.get();
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String t) {
				displayEntities = dataParser(t);
				if(onDataCompleteListener != null)
                    onDataCompleteListener.onParserComplete(displayEntities, null);

			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				String responseMsg = "";
				String errorResult = ex.toString();
				if (ex instanceof HttpException) { //网络错误
					errorResult = "网络错误";
					HttpException httpEx = (HttpException) ex;
					int responseCode = httpEx.getCode();
					if(responseCode == 408) {
						errorResult = "登录超时";
					}
					responseMsg = httpEx.getMessage();
					//					errorResult = httpEx.getResult();

				} else if(errorResult.equals("java.lang.NullPointerException")) {
					errorResult = "其他错误";
				} else { //其他错误
					errorResult = "其他错误";
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
	 * 数据解析
	 */
	public ArrayList<DisplayEntity> dataParser(String t) {
		ArrayList<DisplayEntity> items = new ArrayList<>();
		try {
			if(!"".equals(t)) {
				JSONArray jsonArray = new JSONArray(t);
				if (jsonArray.length() > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						DisplayEntity item = new DisplayEntity();
						JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
						item.setId(CommonUtil.getValidStr(jsonObject2.optString("id", "")));
						item.setUrl(CommonUtil.getValidStr(jsonObject2.optString("url", "")));
						item.setName(CommonUtil.getValidStr(jsonObject2.optString("name", "")));
						item.setImageUrl(CommonUtil.getValidStr(jsonObject2.optString("image_url", "")));
						items.add(item);
					}
				}
			}
			return items;
		} catch (JSONException e) {
			e.printStackTrace();
			return items;
		}
	}
}
