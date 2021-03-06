package com.esc.screendisplay;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esc.screendisplay.utils.SpUtil;

/**
 * 设置服务器地址
 */
public class EditIPActivity extends Activity {

    //默认选择http
    private int checkId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ip);
        ((TextView) findViewById(R.id.tv_actionbar_title)).setText("设置服务器地址");
        ImageView backImg = (ImageView) findViewById(R.id.iv_actionbar_back);
        backImg.setVisibility(View.VISIBLE);
        backImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final RelativeLayout edit_ip_rl_http = (RelativeLayout) findViewById(R.id.edit_ip_rl_http);
        final RelativeLayout edit_ip_rl_https = (RelativeLayout) findViewById(R.id.edit_ip_rl_https);
        final ImageView edit_ip_iv_http = (ImageView) findViewById(R.id.edit_ip_iv_http);
        final ImageView edit_ip_iv_https = (ImageView) findViewById(R.id.edit_ip_iv_https);
        final TextView edit_ip_tv_http = (TextView) findViewById(R.id.edit_ip_tv_http);
        final TextView edit_ip_tv_https = (TextView) findViewById(R.id.edit_ip_tv_https);

        edit_ip_rl_http.setBackgroundColor(getResources().getColor(R.color.region_selected));
        edit_ip_iv_http.setImageResource(R.drawable.http_select);
        edit_ip_tv_http.setTextColor(getResources().getColor(R.color.white));
        edit_ip_rl_http.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkId = 1;
                edit_ip_rl_http.setBackgroundColor(getResources().getColor(R.color.region_selected));
                edit_ip_iv_http.setImageResource(R.drawable.http_select);
                edit_ip_tv_http.setTextColor(getResources().getColor(R.color.white));

                edit_ip_rl_https.setBackgroundResource(R.drawable.border_gray);
                edit_ip_iv_https.setImageResource(R.drawable.https);
                edit_ip_tv_https.setTextColor(getResources().getColor(R.color.edit_ip_text_color));
            }
        });
        edit_ip_rl_https.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkId = 2;
                edit_ip_rl_https.setBackgroundColor(getResources().getColor(R.color.region_selected));
                edit_ip_iv_https.setImageResource(R.drawable.https_select);
                edit_ip_tv_https.setTextColor(getResources().getColor(R.color.white));

                edit_ip_rl_http.setBackgroundResource(R.drawable.border_gray);
                edit_ip_iv_http.setImageResource(R.drawable.http);
                edit_ip_tv_http.setTextColor(getResources().getColor(R.color.edit_ip_text_color));
            }
        });

        final DeleteEditText editText = (DeleteEditText) findViewById(R.id.edit_ip);
        String url = SpUtil.getSpUtil("address", MODE_PRIVATE).getSPValue("address", APPAplication.getInstance().getUrl());
        editText.setText(url.replace("http://", "").replace("https://", ""));
        String address = editText.getText().toString().trim();
        editText.setSelection(address.length());//将光标移至文字末尾

        findViewById(R.id.sure_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = editText.getText().toString().trim();
                SpUtil.getSpUtil("address", MODE_PRIVATE).putSPValue("address", address);
                if (checkId == 1) {
                    APPAplication.getInstance().setUrl("http://" + address);
                    SpUtil.getSpUtil("address", MODE_PRIVATE).putSPValue("url", "http://" + address);
                } else {
                    APPAplication.getInstance().setUrl("https://" + address);
                    SpUtil.getSpUtil("address", MODE_PRIVATE).putSPValue("url", "https://" + address);
                }
                setResult(RESULT_OK);
                finish();

            }
        });
    }
}
