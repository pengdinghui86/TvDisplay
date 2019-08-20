package com.esc.screendisplay.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.esc.screendisplay.BrowserActivity;
import com.esc.screendisplay.R;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class X5WebView extends WebView {
	private ProgressBar mProgressBar;
	private int current, total;

	private WebChromeClient client = new WebChromeClient() {
		/**
		 * 防止加载网页时调起系统浏览器
		 */
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
			return true;
		}

		@Override
		public void onProgressChanged(final WebView view, int newProgress) {
			if (newProgress == 100) {
				mProgressBar.setVisibility(GONE);
			} else {
				if (mProgressBar.getVisibility() == GONE)
					mProgressBar.setVisibility(VISIBLE);
				mProgressBar.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}
	};

	@SuppressLint("SetJavaScriptEnabled")
	public X5WebView(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
		mProgressBar = new ProgressBar(arg0, null,
				android.R.attr.progressBarStyleHorizontal);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 10);
		mProgressBar.setLayoutParams(layoutParams);
		Drawable drawable = arg0.getResources().getDrawable(
				R.drawable.progress_style);
		mProgressBar.setProgressDrawable(drawable);
		addView(mProgressBar);
		this.setWebChromeClient(client);
		// this.setWebChromeClient(chromeClient);
		// WebStorage webStorage = WebStorage.getInstance();
		initWebViewSettings();
		this.getView().setClickable(true);
	}

	private void initWebViewSettings() {
		setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
		});

		WebSettings webSetting = this.getSettings();
		webSetting.setJavaScriptEnabled(true);
		webSetting.setJavaScriptCanOpenWindowsAutomatically(false);
		webSetting.setAllowFileAccess(true);
		webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webSetting.setSupportZoom(true);
		webSetting.setBuiltInZoomControls(true);
		webSetting.setUseWideViewPort(true);
		webSetting.setSupportMultipleWindows(true);
		// webSetting.setLoadWithOverviewMode(true);
		webSetting.setAppCacheEnabled(true);
		// webSetting.setDatabaseEnabled(true);
		webSetting.setDomStorageEnabled(true);
		webSetting.setGeolocationEnabled(true);
		webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
		// webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
		webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
		// webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
//		webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
//		webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
//		webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
//		webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
//				.getPath());
	}

	public void setCurrentPosition(int current, int total) {
		this.current = current;
		this.total = total;
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		boolean ret = super.drawChild(canvas, child, drawingTime);
		canvas.save();
        if(total > 1) {
            Paint paint = new Paint();
            paint.setColor(0x88909398);
            paint.setTextSize(24.f);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            int binary = total / 2;
            for (int i = 0; i < total; i++) {
                if (i == current) {
					paint.setColor(Color.BLUE);
					canvas.drawCircle(getWidth() / 2 - 40 * binary + i * 40, getHeight() - 40, 12, paint);
					if(BrowserActivity.stop) {
						float x = getWidth() / 2 - 40 * binary + i * 40;
						float y = getHeight() - 40;
						float x1 = x - 4f;
						float y1 = (float) (y - 4 * Math.sqrt(3));
						float x2 = x - 4f;
						float y2 = (float) (y + 4 * Math.sqrt(3));
						float x3 = x + 8f;
						float y3 = y;
						//绘制三角形
						Path path = new Path();
						path.moveTo(x1, y1);//此点为多边形的起点
						path.lineTo(x2, y2);
						path.lineTo(x3, y3);
						path.close(); //使这些点构成封闭的多边形
						paint.setColor(Color.WHITE);
						canvas.drawPath(path, paint);
					}
				}
                else {
					paint.setColor(0x88909398);
					canvas.drawCircle(getWidth() / 2 - 40 * binary + i * 40, getHeight() - 40, 10, paint);
				}
            }
            canvas.restore();
        }
		return ret;
	}

	public X5WebView(Context arg0) {
		super(arg0);
		setBackgroundColor(0x000000);
	}

}
