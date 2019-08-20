package com.esc.screendisplay;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.esc.screendisplay.entity.User;
import com.esc.screendisplay.entity.UserEntity;
import com.esc.screendisplay.model.UserServiceImpl;
import com.esc.screendisplay.utils.ActivityCollector;
import com.esc.screendisplay.utils.CommonUtil;
import com.esc.screendisplay.utils.SharedPreferencesHelper;
import com.esc.screendisplay.utils.ToastUtil;
import com.lwy.smartupdate.UpdateManager;
import com.lwy.smartupdate.api.IUpdateCallback;
import com.lwy.smartupdate.data.AppUpdateModel;

public class LoginActivity extends Activity{
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView title;
    private SharedPreferencesHelper sharedPreferencesHelper = null;
    private WebView mWebView = null;
    private String loginName = "";
    private String userInfo = "";
    private String password = "";
    private ProgressDialog mProgressDialog;
    private AlertDialog mDialog;
    private IUpdateCallback mCallback;

    private void loginMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        View findViewById = findViewById(R.id.login);
        findViewById.setFitsSystemWindows(true);
        APPAplication.getInstance().loginActivity = this;
        sharedPreferencesHelper = new SharedPreferencesHelper(getApplicationContext(), "setting");
        title = (TextView) findViewById(R.id.tv_actionbar_title);
        title.setText("登录");
        usernameEditText = (EditText) findViewById(R.id.login_name_et);
        passwordEditText = (EditText) findViewById(R.id.login_psw_et);
        // 如果用户名改变，清空密码
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                passwordEditText.setText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //防止输入框将布局顶上去
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (mWebView == null) {
            initWebView();
        }
        ActivityCollector.finishAll();
        registerUpdateCallbak();
        UpdateManager.getInstance().update(this, APPAplication.appUpdateUrl, null);
    }

    public void registerUpdateCallbak() {
        mCallback = new IUpdateCallback() {
            @Override
            public void noNewApp() {
                checkSavedSetting();
            }

            @Override
            public void hasNewApp(AppUpdateModel appUpdateModel, UpdateManager updateManager, final int updateMethod) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                mDialog = builder.setTitle("新版本更新内容")
                        .setMessage(appUpdateModel.getTip())
                        .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UpdateManager.getInstance().startUpdate(updateMethod);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkSavedSetting();
                            }
                        }).create();
                mDialog.show();
            }

            @Override
            public void beforeUpdate() {
                // 更新开始
                mProgressDialog = new ProgressDialog(LoginActivity.this);
                mProgressDialog.setTitle("更新中...");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setMessage("正在更新中...");
                mProgressDialog.setMax(100);
                mProgressDialog.setProgress(0);
                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // 退到后台自动更新，进度由通知栏显示
                        if (UpdateManager.getInstance().isRunning()) {
                            UpdateManager.getInstance().onBackgroundTrigger();
                        }
                    }
                });
                mProgressDialog.show();
            }

            @Override
            public void onProgress(int percent, long totalLength, int patchIndex, int patchCount) {
                String tip;
                if (patchCount > 0) {
                    tip = String.format("正在下载补丁%d/%d", patchIndex, patchCount);
                } else {
                    tip = "正在下载更新中...";
                }
                mProgressDialog.setProgress(percent);
                mProgressDialog.setMessage(tip);
            }

            @Override
            public void onCompleted() {
                mProgressDialog.dismiss();
            }

            @Override
            public void onError(String error) {
//                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                if(mProgressDialog != null)
                    mProgressDialog.dismiss();
                checkSavedSetting();
            }

            @Override
            public void onCancelUpdate() {
                checkSavedSetting();
            }

            @Override
            public void onBackgroundTrigger() {
                Toast.makeText(LoginActivity.this, "转为后台更新，进度由通知栏提示!", Toast.LENGTH_LONG).show();
            }
        };
        UpdateManager.getInstance().register(mCallback);
    }

    public void checkSavedSetting()
    {
        loginName = sharedPreferencesHelper.getSharedPreference("userName", "").toString().trim();
        userInfo = sharedPreferencesHelper.getSharedPreference("userInfo", "").toString().trim();
        password = sharedPreferencesHelper.getSharedPreference("password", "").toString().trim();
        if(!"".equals(loginName) || !"".equals(userInfo) && !"".equals(password))
        {
            CommonUtil.getInstance().showProgressDialog(this, "",
                    "正在登录服务器");
            UserServiceImpl.getUserServiceImpl().login(loginName, userInfo, password, loginListener);
        }
        else {
           showSetOrNotSetDialog();
        }
    }

    private void showSetOrNotSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("服务器设置提示");
        builder.setMessage("是否需要设置服务器？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(LoginActivity.this, EditIPActivity.class);
                startActivityForResult(intent, 1);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 初始化webview
     */
    protected void initWebView() {
        mWebView = new WebView(this);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JsToJava(), "stub");  //JsToJava是内部类，代码在后面。stub是接口名字。
        mWebView.loadUrl("file:///android_asset/desHelper.html"); //js文件路径
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
//                String urlinit = "javascript:footballHelper.Es.jclq.setContent()";
//                mWebView.loadUrl(urlinit);
            }
        });

    }

    /**
     * 调用js方法
     */
    public void getPrxResult(String editTextUsernameValue, String editTextPasswordValue){
        loginName = editTextUsernameValue;
        password = CommonUtil.md5(editTextUsernameValue);
        String loginInfo = editTextUsernameValue + "," + editTextPasswordValue + "," + "undefined";
        String url2 = "javascript:javaCallJsMethod('" + loginInfo + "','" + password + "')"; //调用js方法
        mWebView.loadUrl(url2);
    }

    /**
     * js方法回调
     */
    private class JsToJava {
        @JavascriptInterface
        public void jsCallbackMethod(String result) {
            userInfo = result;
            UserServiceImpl.getUserServiceImpl().login(loginName, result, password, loginListener);
        }
    }

    private UserServiceImpl.ListListener loginListener = new UserServiceImpl.ListListener() {
        @Override
        public void setListListener(
                Object object, String strError,
                String exceptionError) {
            CommonUtil.getInstance().hideProgressDialog();
            User user = null;
            if (object != null) {
                UserEntity userEntity = (UserEntity) object;
                if(userEntity.getSuccess().equals("true"))
                {
                    user = userEntity.getUser();
                    if(user.getId() != null && !"".equals(user.getId())) {
                        strError = "登录成功";
                    }
                    else {
                        ToastUtil.showLongToast(LoginActivity.this, "登录失败");
                        showSetOrNotSetDialog();
                    }
                }
                else
                {
                    ToastUtil.showLongToast(LoginActivity.this, userEntity.getMsg());
                    showSetOrNotSetDialog();
                }
            } else if (strError != null) {
                ToastUtil.showLongToast(LoginActivity.this, strError);
                showSetOrNotSetDialog();
            }
            if(user != null) {
                if(user.getId() != null && !"".equals(user.getId())) {
                    //保存用户登录信息
                    sharedPreferencesHelper.put("userName", loginName);
                    sharedPreferencesHelper.put("userInfo", userInfo);
                    sharedPreferencesHelper.put("password", password);
                    loginMain();
                }
            }
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if(CommonUtil.getInstance().progressDialog != null)
                if(CommonUtil.getInstance().progressDialog.isShowing()) {
                    CommonUtil.getInstance().hideProgressDialog();
                    return true;
                }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void login(View view) {
        String currentUsername = usernameEditText.getText().toString().trim();
        String currentPassword = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(currentUsername)) {
            Toast.makeText(this, "请输入用户名",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        CommonUtil.getInstance().showProgressDialog(this, "",
                "正在登录服务器");
        getPrxResult(currentUsername, currentPassword);
    }

    public void editIp(View view) {
        Intent intent = new Intent(LoginActivity.this, EditIPActivity.class);
        startActivityForResult(intent, 1);
    }
}
