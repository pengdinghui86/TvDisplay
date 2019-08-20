package com.esc.screendisplay;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esc.screendisplay.entity.DisplayEntity;
import com.esc.screendisplay.model.UserServiceImpl;
import com.esc.screendisplay.utils.CommonUtil;
import com.esc.screendisplay.utils.SharedPreferencesHelper;
import com.esc.screendisplay.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private final static String[] time = {"10s", "30s", "60s", "90s"};
    private String selectTime = "10s";
    private List<DisplayEntity> displayEntityList = new ArrayList<>();
    private ArrayList<DisplayEntity> selectedList = new ArrayList<>();
    private Spinner spinner;
    private GridView gridView;
    private ArrayAdapter<String> adapter;
    private GridViewAdapter gridViewAdapter;
    private Button mStartDisplay;
    private SharedPreferencesHelper sharedPreferencesHelper;
    String userName = "";
    String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        spinner = (Spinner)findViewById(R.id.main_spinner_time_space);
        gridView = (GridView) findViewById(R.id.main_gridView);
        mStartDisplay = (Button) findViewById(R.id.main_bt_start_display);
        sharedPreferencesHelper = new SharedPreferencesHelper(
                getApplicationContext(), "setting");
        initData();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, time);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(R.layout.spinner_drop_down_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        spinner.setVisibility(View.VISIBLE);
        spinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    spinner.setBackgroundResource(R.drawable.b_01_s_bg);
                else
                    spinner.setBackgroundResource(R.drawable.b_01_bg);
            }
        });
        mStartDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedList.size() > 0)
                {
                    String urls = "";
                    int i = 0;
                    for(DisplayEntity item : selectedList)
                    {
                        if(item.getUrl() != null && !"".equals(item.getUrl())) {
                            if(i == 0)
                                urls = item.getUrl();
                            else
                                urls += "," + item.getUrl();
                            i++;
                        }
                    }
                    if(!"".equals(urls)) {
                        sharedPreferencesHelper.remove(userName + "_displayOrder");
                        sharedPreferencesHelper.remove(userName + "_time");
                        sharedPreferencesHelper.put(userName + "_displayOrder", urls);
                        sharedPreferencesHelper.put(userName + "_time", selectTime);
                    }
                    Intent intent = new Intent(MainActivity.this, BrowserActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", selectedList);
                    intent.putExtras(bundle);
                    intent.putExtra("time", selectTime);
                    startActivity(intent);
                }
                else
                    Toast.makeText(MainActivity.this, "请选择要播放的列表", Toast.LENGTH_SHORT).show();
            }
        });
        mStartDisplay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    mStartDisplay.setBackgroundResource(R.drawable.b_02_s_bg);
                else
                    mStartDisplay.setBackgroundResource(R.drawable.b_02_bg);
            }
        });
    }

    public void updateSelectList(DisplayEntity displayEntity) {
        boolean flag = false;
        for (DisplayEntity item : selectedList)
        {
            if(item.getId().equals(displayEntity.getId()))
            {
                if(!displayEntity.isSelected())
                    selectedList.remove(displayEntity);
                flag = true;
                break;
            }
        }
        if(!flag) {
            selectedList.add(displayEntity);
        }
    }

    private void checkSavedSetting()
    {
        userName = sharedPreferencesHelper.getSharedPreference("userName", "").toString().trim();
        password = sharedPreferencesHelper.getSharedPreference("password", "").toString().trim();
        if(!"".equals(userName))
        {
            String urlStr = sharedPreferencesHelper.getSharedPreference(userName + "_displayOrder", "").toString().trim();
            String time = sharedPreferencesHelper.getSharedPreference(userName + "_time", "").toString().trim();
            if(!"".equals(urlStr))
            {
                String[] urlList = urlStr.split(",");
                ArrayList<DisplayEntity> list = new ArrayList<>();
                for(String url : urlList)
                {
                    DisplayEntity displayEntity = new DisplayEntity();
                    displayEntity.setId("");
                    displayEntity.setName("");
                    displayEntity.setUrl(url);
                    for(DisplayEntity item : displayEntityList) {
                        if(url.equals(item.getUrl())) {
                            displayEntity.setImageUrl(item.getImageUrl());
                            break;
                        }
                    }
                    list.add(displayEntity);
                }
                Intent intent = new Intent(MainActivity.this, BrowserActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", list);
                intent.putExtras(bundle);
                intent.putExtra("time", time);
                startActivity(intent);
            }
        }
        else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void initData()
    {
        CommonUtil.getInstance().showProgressDialog(this, "",
                "请求数据中");
        UserServiceImpl.getUserServiceImpl().getDisplayList(APPAplication.userId, getListListener);
    }

    private UserServiceImpl.ListListener getListListener = new UserServiceImpl.ListListener() {
        @Override
        public void setListListener(
                Object object, String strError,
                String exceptionError) {
            String str = null;
            if (object != null) {
                displayEntityList = (ArrayList<DisplayEntity>) object;
            } else if (exceptionError != null) {
                str = exceptionError;
                ToastUtil.showLongToast(MainActivity.this, str);
            }
            else {
                str = strError;
                ToastUtil.showLongToast(MainActivity.this, str);
            }
            CommonUtil.getInstance().hideProgressDialog();
            if(gridViewAdapter == null) {
                gridViewAdapter = new GridViewAdapter(MainActivity.this, displayEntityList);
                gridView.setAdapter(gridViewAdapter);
            }
            else
                gridViewAdapter.notifyDataSetChanged();

            checkSavedSetting();
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("退出提示");
            builder.setMessage("确定退出当前账号吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    sharedPreferencesHelper.remove("userName");
                    sharedPreferencesHelper.remove("password");
                    sharedPreferencesHelper.remove("userInfo");
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
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

            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
            //利用数组中的对应位置取得需要的值
            selectTime = time[position];
            TextView tv = (TextView)arg1;
            if(tv != null) {
                tv.setTextColor(getResources().getColor(R.color.white));//设置颜色
                tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);//设置居中
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}
