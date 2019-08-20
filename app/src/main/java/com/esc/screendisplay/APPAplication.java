package com.esc.screendisplay;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.esc.screendisplay.utils.SpUtil;
import com.lwy.smartupdate.Config;
import com.lwy.smartupdate.UpdateManager;
import com.tencent.smtt.sdk.QbSdk;

import org.xutils.x;
import me.jessyan.autosize.AutoSize;
import me.jessyan.autosize.AutoSizeConfig;

public class APPAplication extends Application {

	public String url;
	private static APPAplication instance;
	public static String userId = "";//当前登录用户id
	public LoginActivity loginActivity = null;
	public static String appUpdateUrl = "http://192.168.1.188:8088/appUpdate/UpdateManifest.json";

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		//搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
		QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
			
			@Override
			public void onViewInitFinished(boolean arg0) {
				// TODO Auto-generated method stub
				//x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
				Log.d("app", " onViewInitFinished is " + arg0);
			}
			
			@Override
			public void onCoreInitFinished() {
				// TODO Auto-generated method stub
			}
		};
		//x5内核初始化接口
		QbSdk.initX5Environment(getApplicationContext(),  cb);

		/**
		 * 以下是 AndroidAutoSize 可以自定义的参数, {@link AutoSizeConfig} 的每个方法的注释都写的很详细
		 * 使用前请一定记得跳进源码，查看方法的注释, 下面的注释只是简单描述!!!
		 */
		AutoSizeConfig.getInstance()

				//是否让框架支持自定义 Fragment 的适配参数, 由于这个需求是比较少见的, 所以须要使用者手动开启
				//如果没有这个需求建议不开启
				.setCustomFragment(true)

				//是否打印 AutoSize 的内部日志, 默认为 true, 如果您不想 AutoSize 打印日志, 则请设置为 false
//                .setLog(false)

				//是否使用设备的实际尺寸做适配, 默认为 false, 如果设置为 false, 在以屏幕高度为基准进行适配时
				//AutoSize 会将屏幕总高度减去状态栏高度来做适配, 如果设备上有导航栏还会减去导航栏的高度
				//设置为 true 则使用设备的实际屏幕高度, 不会减去状态栏以及导航栏高度
//                .setUseDeviceSize(true)

				//是否全局按照宽度进行等比例适配, 默认为 true, 如果设置为 false, AutoSize 会全局按照高度进行适配
				.setBaseOnWidth(false)

		//设置屏幕适配逻辑策略类, 一般不用设置, 使用框架默认的就好
//                .setAutoAdaptStrategy(new AutoAdaptStrategy())
		;
		//当 App 中出现多进程, 并且您需要适配所有的进程, 就需要在 App 初始化时调用 initCompatMultiProcess()
		//在 Demo 中跳转的三方库中的 DefaultErrorActivity 就是在另外一个进程中, 所以要想适配这个 Activity 就需要调用 initCompatMultiProcess()
		AutoSize.initCompatMultiProcess(this);
		//xUtils初始化
		x.Ext.init(this);
		x.Ext.setDebug(false); //输出debug日志，开启会影响性能
		//初始化自动更新组件
		Config config = new Config.Builder()
				.isDebug(true)
				.isShowInternalDialog(false)
				.build(this);
		UpdateManager.getInstance().init(config);
	}

	public static APPAplication getInstance() {
		return instance;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		if (TextUtils.isEmpty(url)) {
			return SpUtil.getSpUtil("address", MODE_PRIVATE).getSPValue("url",
					"http://121.201.78.36:9080");
		} else {
			return url;
		}

	}
}
