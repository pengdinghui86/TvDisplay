package com.esc.screendisplay.model;

/**
 * 数据加载完毕之后回调此接口
 * 
 */
public interface OnDataCompleteListener {
	void onParserComplete(Object object, String error);
}
