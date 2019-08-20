package com.esc.screendisplay.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("InflateParams")
@SuppressWarnings("serial")
public class CommonUtil implements Serializable {
    private Map<String, String> map;
    private static class SingletonHolder {
        /**
         * 单例对象实例
         */
        static CommonUtil INSTANCE = null;

    }

    public static CommonUtil getInstance() {
        if(SingletonHolder.INSTANCE == null)
            SingletonHolder.INSTANCE = new CommonUtil();
        return SingletonHolder.INSTANCE;
    }

    /**
     * private的构造函数用于避免外界直接使用new来实例化对象
     */
    private CommonUtil() {
    }

    /**
     * readResolve方法应对单例对象被序列化时候
     */
    private Object readResolve() {
        return getInstance();
    }

    private String reg;
    private Toast toast = null;
    private Handler mHandler = new Handler();
    private Runnable r = new Runnable() {
        public void run() {
            toast.cancel();
        }
    };

    /**
     * 获取系统当前时间
     */
    public String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    public String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
    }

    /**
     * 电话号码验证
     */
    public boolean isMobileNo(String mobiles) {
        Pattern p = Pattern
                .compile("^((1[3-5&7-8][0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        Log.i("dianhua", "dianhua" + m.matches());
        return m.matches();
    }

    /**
     * 邮箱验证
     */
    public boolean isEmail(String email) {
        if (null == email || "".equals(email))
            return false;
        // Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p = Pattern
                .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");// 复杂匹配
        Matcher m = p.matcher(email);
        Log.i("youxiang", "youxiang" + m.matches());
        return m.matches();
    }

    public boolean startCheck(String reg, String string) {
        boolean tem = false;

        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(string);

        tem = matcher.matches();
        return tem;
    }

    /**
     * 身份证号码验证
     */
    public boolean checkIdCard(String idNr) {
        // String reg="^[1-9](\\d{14}|\\d{17})";
        if (idNr.length() == 15) {
            reg = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
        } else if (idNr.length() == 18) {
            // reg =
            // "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{4}$";这里不包括判断最后一位是x的情况
            reg = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])((\\d{4})|\\d{3}[Xx])$";

        } else {
            reg = "^[1-9](\\d{14}|\\d{17})";
        }

        return startCheck(reg, idNr);
    }

    /**
     * 判断sdcard是否被挂载
     */
    public boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 读取文件中用户保存的内容
     */
    public String read(Context context, String fileName) {
        try {
            FileInputStream inputStream = context.openFileInput(fileName);
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while (inputStream.read(bytes) != -1) {
                arrayOutputStream.write(bytes, 0, bytes.length);
            }
            inputStream.close();
            arrayOutputStream.close();
            String content = new String(arrayOutputStream.toByteArray());
            // showTextView.setText(content);
            return content;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String setTtimeline(String dateNow, String dateStar) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d1 = df.parse(dateNow);
            Date d2 = df.parse(dateStar);
            long diff = d1.getTime() - d2.getTime();
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24))
                    / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours
                    * (1000 * 60 * 60))
                    / (1000 * 60);
            long miao = (diff - days * (1000 * 60 * 60 * 24) - hours
                    * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000;
            System.out.println("" + days + "天" + hours + "小时" + minutes + "分"
                    + miao + "秒");
            if (days == 0 && hours > 0 && minutes > 0 && miao > 0) {
                return hours + "小时" + minutes + "分" + miao + "秒";
            } else if (days == 0 && hours == 0 && minutes > 0 && miao > 0) {

                return minutes + "分" + miao + "秒";
            } else if (days == 0 && hours == 0 && minutes == 0 && miao >= 0) {

                return miao + "秒";
            } else {

//                return "" + days + "天" + hours + "小时" + minutes + "分" + miao
//                        + "秒";
                //2018.4.28修改
                return "" + (days * 24 + hours) + "小时" + minutes + "分" + miao
                        + "秒";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0秒";
    }

    //传入参数为秒
    public String convert2TimeStyle(String actualAfterDuration) {
        try {
            long diff = Long.parseLong(actualAfterDuration);
            long days = diff / (60 * 60 * 24);
            long hours = (diff - days * (60 * 60 * 24))
                    / (60 * 60);
            long minutes = (diff - days * (60 * 60 * 24) - hours
                    * (60 * 60))
                    / (60);
            long miao = diff - days * (60 * 60 * 24) - hours
                    * (60 * 60) - minutes * 60;
            System.out.println("" + days + "天" + hours + "小时" + minutes + "分"
                    + miao + "秒");
            if (days == 0 && hours > 0 && minutes > 0 && miao > 0) {
                return hours + "小时" + minutes + "分" + miao + "秒";
            } else if (days == 0 && hours == 0 && minutes > 0 && miao > 0) {

                return minutes + "分" + miao + "秒";
            } else if (days == 0 && hours == 0 && minutes == 0 && miao >= 0) {

                return miao + "秒";
            } else {

                return "" + days + "天" + hours + "小时" + minutes + "分" + miao
                        + "秒";
                //2018.4.28修改
//                return "" + (days * 24 + hours) + "小时" + minutes + "分" + miao
//                        + "秒";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0秒";
    }

    /**
     * 比较时间大小
     */
    public long compareTime(String strDate, String strDate2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long diff = 0;
        try {
            Date d1 = df.parse(strDate);
            Date d2 = df.parse(strDate2);
            diff = d1.getTime() - d2.getTime();
        }
        catch (Exception ex) {
            return diff;
        }
        return diff;
    }

    /**
     * 获取系统当前时间
     *
     * @return
     */
    public String getSystemTime() {
        // SimpleDateFormat sDateFormat = new SimpleDateFormat(
        // "yyyy-MM-dd    hh:mm:ss");//12小时
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd    HH:mm:ss");// 24小时
        return sDateFormat.format(new Date());

    }

    public ProgressDialog progressDialog;

    /*
     * 提示加载
     */
    public void showProgressDialog(Context context, String title, String message) {
        if (progressDialog == null) {

            progressDialog = ProgressDialog.show(context, title, message, true,
                    false);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }
        else
            progressDialog.show();
    }

    /*
     * 隐藏提示加载
     */
    public void hideProgressDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }

    }

    /**
     * list去重复
     */
    public List<String> removeDuplicate(List<String> list) {
        Set set = new LinkedHashSet<String>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    /**
     * 将字符串用","切割成数组
     */
    private String[] convertStrToArray(String str) {
        String[] strArray = null;
        strArray = str.split(","); // 拆分字符为"," ,然后把结果交给数组strArray
        return strArray;
    }

    /**
     * 把数组转换为一个用逗号分隔的字符串 ，以便于用in+String 查询
     */
    public static String converToString(String[] ig) {
        String str = "";
        if (ig != null && ig.length > 0) {
            for (int i = 0; i < ig.length; i++) {
                str += ig[i] + ",";
            }
        }
        str = str.substring(0, str.length() - 1);
        return str;
    }

    /**
     * 把list转换为一个用逗号分隔的字符串
     */
    public static String listToString(List list) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (i < list.size() - 1) {
                    sb.append(list.get(i) + ",");
                } else {
                    sb.append(list.get(i));
                }
            }
        }
        return sb.toString();
    }

    /**
     * 随机生成四位数字加字母
     */
    public String code2() {
        // 生成验证码
        char[] bytes = new char[4];
        Random ran = new Random();
        for (int i = 0; i < bytes.length; i++) {
            byte bit = (byte) ran.nextInt(62);
            if (bit < 10)
                bytes[i] = (char) (bit + 48);
            else if (bit < 36)
                bytes[i] = (char) (bit + 55);
            else
                bytes[i] = (char) (61 + bit);
        }
        // 验证码
        return new String(bytes);
    }

    /**
     * 随机生成四位数字
     */
    public String code() {
        Random random = new Random();
        int num = 0;
        for (int i = 0; i <= 3; i++) {
            if(i == 0)
                num = num * 10 + (random.nextInt(9) + 1);
            else
                num = num * 10 + random.nextInt(9);
        }
        return String.valueOf(num);
    }

    public static int dp2px(Context context, float dp)
    {
        return (int ) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * 获得屏幕宽度
     */
    public static int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE );
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics( outMetrics);
        return outMetrics .widthPixels ;
    }

    /**
     *  将字符串转为时间戳
     */
    public static long getStringToDate(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try{
            date = dateFormat.parse(dateString);
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }
    /**
     *  时间戳格式转换
     */
    static String dayNames[] = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    public static String getNewChatTime(long timesamp) {
        String result = "";
        Calendar todayCalendar = Calendar.getInstance();
        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.setTimeInMillis(timesamp);

        String timeFormat="M月d日 HH:mm";
        String yearTimeFormat="yyyy年M月d日 HH:mm";
        String am_pm="";
        int hour=otherCalendar.get(Calendar.HOUR_OF_DAY);
        if(hour>=0&&hour<6){
            am_pm="凌晨";
        }else if(hour>=6&&hour<12){
            am_pm="早上";
        }else if(hour==12){
            am_pm="中午";
        }else if(hour>12&&hour<18){
            am_pm="下午";
        }else if(hour>=18){
            am_pm="晚上";
        }
        timeFormat="M月d日 "+ am_pm +"HH:mm";
        yearTimeFormat="yyyy年M月d日 "+ am_pm +"HH:mm";

        boolean yearTemp = todayCalendar.get(Calendar.YEAR)==otherCalendar.get(Calendar.YEAR);
        if(yearTemp){
            int todayMonth=todayCalendar.get(Calendar.MONTH);
            int otherMonth=otherCalendar.get(Calendar.MONTH);
            if(todayMonth==otherMonth){//表示是同一个月
                int temp=todayCalendar.get(Calendar.DAY_OF_MONTH)-otherCalendar.get(Calendar.DAY_OF_MONTH);
                switch (temp) {
                    case 0:
                        result = getHourAndMin(timesamp);
                        break;
                    case 1:
                        result = "昨天 " + getHourAndMin(timesamp);
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        int dayOfMonth = otherCalendar.get(Calendar.WEEK_OF_MONTH);
                        int todayOfMonth=todayCalendar.get(Calendar.WEEK_OF_MONTH);
                        if(dayOfMonth==todayOfMonth){//表示是同一周
                            int dayOfWeek=otherCalendar.get(Calendar.DAY_OF_WEEK);
                            if(dayOfWeek!=1){//判断当前是不是星期日     如想显示为：周日 12:09 可去掉此判断
                                result = dayNames[otherCalendar.get(Calendar.DAY_OF_WEEK)-1] + getHourAndMin(timesamp);
                            }else{
                                result = getTime(timesamp,timeFormat);
                            }
                        }else{
                            result = getTime(timesamp,timeFormat);
                        }
                        break;
                    default:
                        result = getTime(timesamp,timeFormat);
                        break;
                }
            }else{
                result = getTime(timesamp,timeFormat);
            }
        }else{
            result=getYearTime(timesamp,yearTimeFormat);
        }
        return result;
    }

    /**
     * 当天的显示时间格式
     */
    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    /**
     * 不同一周的显示时间格式
     */
    public static String getTime(long time, String timeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        return format.format(new Date(time));
    }

    /**
     * 不同年的显示时间格式
     */
    public static String getYearTime(long time, String yearTimeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(yearTimeFormat);
        return format.format(new Date(time));
    }

    public static String getValidStr(String str) {
        String result = "";
        if(str != null && !"null".equals(str))
            result = str;
        return result;
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
