package com.esc.screendisplay;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import com.esc.screendisplay.entity.DisplayEntity;
import com.esc.screendisplay.utils.X5WebView;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.utils.TbsLog;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class BrowserActivity extends BaseActivity {
	private X5WebView mWebView1;
	private X5WebView mWebView2;
	private X5WebView mWebView3;
	private ViewGroup mViewParent;
	private static final String TAG = "BrowserActivity";
	private ValueCallback<Uri> uploadFile;
	private URL mIntentUrl;
    private ArrayList<DisplayEntity> list = new ArrayList<>();
	private String time;
	private static DisplayThread displayThread;
	public static boolean flag = false;
	public static boolean stop = false;
	private int curIndex = 0;
	private static int totalSize = 0;
	private Handler handler = new Handler();
	private Animation translate_in;
	private Animation translate_out;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		try {
			if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
				getWindow()
						.setFlags(
								android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
								android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
			}
		} catch (Exception e) {

		}
		setContentView(R.layout.activity_web_browser);
		mViewParent = (ViewGroup) findViewById(R.id.webView1);
		flag = false;
        stop = false;
        Intent intent = getIntent();
        if (intent != null) {
            list = (ArrayList<DisplayEntity>) getIntent().getExtras().getSerializable("list");
            totalSize = list.size();
            time = getIntent().getStringExtra("time");
            time = time.replace("s","");
        }
        mHandler.sendEmptyMessageDelayed(MSG_INIT_UI, 10);
    }

	private void init() {
		//页面进入效果的动画
		translate_in = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
		translate_out = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
//		translate_in.setFillAfter(true);
//		translate_in.setDetachWallpaper(true);
//		translate_out.setFillAfter(true);
//		translate_out.setDetachWallpaper(true);
		mWebView1 = new X5WebView(this, null);
		mWebView2 = new X5WebView(this, null);
		mWebView3 = new X5WebView(this, null);
		mViewParent.addView(mWebView1, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT));
		mViewParent.addView(mWebView2, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT));
		mWebView2.setVisibility(View.GONE);
		mViewParent.addView(mWebView3, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT));
		mWebView3.setVisibility(View.GONE);
		if(totalSize > 0) {
			try {
				mIntentUrl = new URL(APPAplication.getInstance().getUrl() + list.get(0).getUrl() + "?userId=" + APPAplication.userId + "&ran=" + System.currentTimeMillis());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		long time = System.currentTimeMillis();
		if (mIntentUrl != null) {
			mWebView1.setCurrentPosition(curIndex, list.size());
			mWebView1.loadUrl(mIntentUrl.toString());
		}
		TbsLog.d("time-cost", "cost time: "
				+ (System.currentTimeMillis() - time));
		CookieSyncManager.createInstance(this);
		CookieSyncManager.getInstance().sync();
		if(totalSize > 1) {
			flag = true;
			mWebView2.setCurrentPosition(1, list.size());
			mWebView2.loadUrl(APPAplication.getInstance().getUrl() + list.get(1).getUrl() + "?userId=" + APPAplication.userId + "&ran=" + System.currentTimeMillis());
			if(totalSize > 2) {
                mWebView3.setCurrentPosition(totalSize - 1, list.size());
                mWebView3.loadUrl(APPAplication.getInstance().getUrl() + list.get(totalSize - 1).getUrl() + "?userId=" + APPAplication.userId + "&ran=" + System.currentTimeMillis());
            }
			if (displayThread == null) {
				displayThread = new DisplayThread();
				displayThread.start();
			}
		}
	}

	class DisplayThread extends Thread
	{
	    public DisplayThread () {

        }

		@Override
		public void run() {
			while (flag) {
				if (!stop) {
					try {
						sleep(Integer.parseInt(time) * 1000);
						handler.post(new Runnable() {
							@Override
							public void run() {
								if (!stop) {
									curIndex++;
									if (curIndex >= list.size())
										curIndex = 0;
									switchToNext();
								}
							}
						});
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void switchToNext() {
        if(mWebView1.getVisibility() == View.VISIBLE) {
            mWebView1.startAnimation(translate_out);
            translate_out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mWebView1.setVisibility(View.GONE);
                    mWebView2.setVisibility(View.VISIBLE);
                    mWebView2.startAnimation(translate_in);
					int index = curIndex + 1;
                    if(index >= list.size())
                        index = 0;
                    if(totalSize > 3) {
                        mWebView3.setCurrentPosition(index, list.size());
                        mWebView3.loadUrl(APPAplication.getInstance().getUrl() + list.get(index).getUrl() + "?userId=" + APPAplication.userId + "&ran=" + System.currentTimeMillis());
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
			translate_in.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					updateWebView();
				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}
			});
        }
		else if(mWebView2.getVisibility() == View.VISIBLE) {
            mWebView2.startAnimation(translate_out);
            translate_out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mWebView2.setVisibility(View.GONE);
                    if(totalSize > 2) {
                        mWebView3.setVisibility(View.VISIBLE);
                        mWebView3.startAnimation(translate_in);
                        if (totalSize > 3) {
                            int index = curIndex + 1;
                            if (index >= list.size())
                                index = 0;
                            mWebView1.setCurrentPosition(index, list.size());
                            mWebView1.loadUrl(APPAplication.getInstance().getUrl() + list.get(index).getUrl() + "?userId=" + APPAplication.userId + "&ran=" + System.currentTimeMillis());
                        }
                    }
                    else {
                        mWebView1.setVisibility(View.VISIBLE);
                        mWebView1.startAnimation(translate_in);
                    }
				}

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
			translate_in.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					updateWebView();
				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}
			});
        }
        else if(mWebView3.getVisibility() == View.VISIBLE) {
            mWebView3.startAnimation(translate_out);
            translate_out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mWebView3.setVisibility(View.GONE);
                    mWebView1.setVisibility(View.VISIBLE);
                    mWebView1.startAnimation(translate_in);
                    if(totalSize > 3) {
                        int index = curIndex + 1;
                        if (index >= list.size())
                            index = 0;
                        mWebView2.setCurrentPosition(index, list.size());
                        mWebView2.loadUrl(APPAplication.getInstance().getUrl() + list.get(index).getUrl() + "?userId=" + APPAplication.userId + "&ran=" + System.currentTimeMillis());
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
			translate_in.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					updateWebView();
				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}
			});
        }
	}

    private void switchToLast() {
        if(mWebView1.getVisibility() == View.VISIBLE) {
            mWebView1.startAnimation(translate_out);
            translate_out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mWebView1.setVisibility(View.GONE);
                    if (totalSize > 2) {
                        mWebView3.setVisibility(View.VISIBLE);
                        mWebView3.startAnimation(translate_in);
                        if (totalSize > 3) {
                            int index = curIndex - 1;
                            if (index < 0)
                                index = totalSize - 1;
                            mWebView2.setCurrentPosition(index, list.size());
                            mWebView2.loadUrl(APPAplication.getInstance().getUrl() + list.get(index).getUrl() + "?userId=" + APPAplication.userId + "&ran=" + System.currentTimeMillis());
                        }
                    }
                    else {
                        mWebView2.setVisibility(View.VISIBLE);
                        mWebView2.startAnimation(translate_in);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
			translate_in.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					updateWebView();
				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}
			});
        }
        else if(mWebView2.getVisibility() == View.VISIBLE) {
            mWebView2.startAnimation(translate_out);
            translate_out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mWebView2.setVisibility(View.GONE);
                    mWebView1.setVisibility(View.VISIBLE);
                    mWebView1.startAnimation(translate_in);
                    if (totalSize > 3) {
                        int index = curIndex - 1;
                        if (index < 0)
                            index = totalSize - 1;
                        mWebView3.setCurrentPosition(index, list.size());
                        mWebView3.loadUrl(APPAplication.getInstance().getUrl() + list.get(index).getUrl() + "?userId=" + APPAplication.userId + "&ran=" + System.currentTimeMillis());
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
			translate_in.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					updateWebView();
				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}
			});
        }
        else if(mWebView3.getVisibility() == View.VISIBLE) {
            mWebView3.startAnimation(translate_out);
            translate_out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mWebView3.setVisibility(View.GONE);
                    mWebView2.setVisibility(View.VISIBLE);
                    mWebView2.startAnimation(translate_in);
                    if (totalSize > 3) {
                        int index = curIndex - 1;
                        if (index < 0)
                            index = totalSize - 1;
                        mWebView1.setCurrentPosition(index, list.size());
                        mWebView1.loadUrl(APPAplication.getInstance().getUrl() + list.get(index).getUrl() + "?userId=" + APPAplication.userId + "&ran=" + System.currentTimeMillis());
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
			translate_in.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					updateWebView();
				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}
			});
        }
    }

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		//up
		if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP && event.getAction() == KeyEvent.ACTION_UP)
		{
			if(list.size() <= 1)
				return false;
			handler.post(new Runnable() {
				@Override
				public void run() {
					curIndex--;
					if(curIndex < 0)
						curIndex = list.size() - 1;
                    switchToLast();
				}
			});
			return true;
		}
		//down
		else if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_UP) {
			if(list.size() <= 1)
				return false;
			handler.post(new Runnable() {
				@Override
				public void run() {
					curIndex++;
					if(curIndex >= list.size())
						curIndex = 0;
                    switchToNext();
				}
			});
			return true;
		}
		//left
		else if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_UP) {
			if(list.size() <= 1)
				return false;
			handler.post(new Runnable() {
				@Override
				public void run() {
					curIndex--;
					if(curIndex < 0)
						curIndex = list.size() - 1;
                    switchToLast();
				}
			});
			return true;
		}
		//right
		else if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_UP) {
			if(list.size() <= 1)
				return false;
			handler.post(new Runnable() {
				@Override
				public void run() {
					curIndex++;
					if(curIndex + 1 > list.size())
						curIndex = 0;
                    switchToNext();
				}
			});
			return true;
		}
		//enter
		else if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER && event.getAction() == KeyEvent.ACTION_UP) {
			if (stop)
				stop = false;
			else
				stop = true;
			updateWebView();
			return true;
		}
		//back
		else if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					finish();
				}
			});
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	private void updateWebView() {
        if(mWebView1.getVisibility() == View.VISIBLE)
            mWebView1.invalidate();
        else if(mWebView2.getVisibility() == View.VISIBLE)
            mWebView2.invalidate();
        else if(mWebView3.getVisibility() == View.VISIBLE)
            mWebView3.invalidate();
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		TbsLog.d(TAG, "onActivityResult, requestCode:" + requestCode
				+ ",resultCode:" + resultCode);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0:
				if (null != uploadFile) {
					Uri result = data == null || resultCode != RESULT_OK ? null
							: data.getData();
					uploadFile.onReceiveValue(result);
					uploadFile = null;
				}
				break;
			default:
				break;
			}
		} else if (resultCode == RESULT_CANCELED) {
			if (null != uploadFile) {
				uploadFile.onReceiveValue(null);
				uploadFile = null;
			}

		}

	}

	@Override
	public void onDestroy() {
		if (mHandler != null)
			mHandler.removeCallbacksAndMessages(null);
		if (mWebView1 != null) {
            ViewParent parent = mWebView1.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mWebView1);
            }
            mWebView1.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            mWebView1.getSettings().setJavaScriptEnabled(false);
            mWebView1.clearHistory();
            mWebView1.clearView();
            mWebView1.removeAllViews();
            mWebView1.destroy();
        }
		if (mWebView2 != null) {
			ViewParent parent = mWebView2.getParent();
			if (parent != null) {
				((ViewGroup) parent).removeView(mWebView2);
			}
			mWebView2.stopLoading();
			// 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
			mWebView2.getSettings().setJavaScriptEnabled(false);
			mWebView2.clearHistory();
			mWebView2.clearView();
			mWebView2.removeAllViews();
			mWebView2.destroy();
		}
        if (mWebView3 != null) {
            ViewParent parent = mWebView3.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mWebView3);
            }
            mWebView3.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            mWebView3.getSettings().setJavaScriptEnabled(false);
            mWebView3.clearHistory();
            mWebView3.clearView();
            mWebView3.removeAllViews();
            mWebView3.destroy();
        }
		flag = false;
		displayThread = null;
		super.onDestroy();
	}
	public static final int MSG_INIT_UI = 1;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_INIT_UI:
					init();
					break;
			}
			super.handleMessage(msg);
		}
	};

}
