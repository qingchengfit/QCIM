package com.tencent.qcloud.timchat.chatutils;

import com.tencent.qcloud.timchat.MyApplication;
import com.tencent.qcloud.timchat.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * 时间转换工具
 */
public class TimeUtil {


    public static final Long MONTH_TIME = 2678400000L;
    public static final Long DAY_TIME = 86400000L;
    public static final Long HOUR_TIME = 3600000L;
    public static final Long MINITE_TIME = 60000L;
    public static final Long SECOND_TIME = Long.valueOf(1000L);

    private TimeUtil(){}

    /**
     * 时间转化为显示字符串
     *
     * @param timeStamp 单位为秒
     */
    public static String getTimeStr(long timeStamp){
        if (timeStamp==0) return "";
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(timeStamp*1000);
        Date currenTimeZone = inputTime.getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        if (calendar.before(inputTime)){
            //今天23:59在输入时间之前，解决一些时间误差，把当天时间显示到这里
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + MyApplication.getContext().getResources().getString(R.string.time_year)+"MM"+MyApplication.getContext().getResources().getString(R.string.time_month)+"dd"+MyApplication.getContext().getResources().getString(R.string.time_day));
            return sdf.format(currenTimeZone);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(inputTime)){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(currenTimeZone);
        }
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        if (calendar.before(inputTime)){
            return MyApplication.getContext().getResources().getString(R.string.time_yesterday);
        }else{
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            if (calendar.before(inputTime)){
                SimpleDateFormat sdf = new SimpleDateFormat("M"+MyApplication.getContext().getResources().getString(R.string.time_month)+"d"+MyApplication.getContext().getResources().getString(R.string.time_day));
                return sdf.format(currenTimeZone);
            }else{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + MyApplication.getContext().getResources().getString(R.string.time_year)+"MM"+MyApplication.getContext().getResources().getString(R.string.time_month)+"dd"+MyApplication.getContext().getResources().getString(R.string.time_day));
                return sdf.format(currenTimeZone);

            }

        }

    }

    /**
     * 时间转化为聊天界面显示字符串
     *
     * @param timeStamp 单位为秒
     */
    public static String getChatTimeStr(long timeStamp){
        if (timeStamp==0) return "";
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(timeStamp*1000);
        Date currenTimeZone = inputTime.getTime();
        Calendar calendar = Calendar.getInstance();
        if (!calendar.after(inputTime)){
            //当前时间在输入时间之前
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + MyApplication.getContext().getResources().getString(R.string.time_year)+"MM"+MyApplication.getContext().getResources().getString(R.string.time_month)+"dd"+MyApplication.getContext().getResources().getString(R.string.time_day));
            return sdf.format(currenTimeZone);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(inputTime)){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(currenTimeZone);
        }
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        if (calendar.before(inputTime)){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return MyApplication.getContext().getResources().getString(R.string.time_yesterday)+" "+sdf.format(currenTimeZone);
        }else{
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            if (calendar.before(inputTime)){
                SimpleDateFormat sdf = new SimpleDateFormat("M"+MyApplication.getContext().getResources().getString(R.string.time_month)+"d"+MyApplication.getContext().getResources().getString(R.string.time_day)+" HH:mm");
                return sdf.format(currenTimeZone);
            }else{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy"+MyApplication.getContext().getResources().getString(R.string.time_year)+"MM"+MyApplication.getContext().getResources().getString(R.string.time_month)+"dd"+MyApplication.getContext().getResources().getString(R.string.time_day)+" HH:mm");
                return sdf.format(currenTimeZone);
            }

        }

    }

    public static String getNotifacationTimeStr(long timeStamp) {
        if (timeStamp==0) return "";
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(timeStamp*1000);
        Date currenTimeZone = inputTime.getTime();
        long intelnal = (new Date()).getTime() - currenTimeZone.getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long lastDayInternal = currenTimeZone.getTime() - calendar.getTime().getTime();
        if(lastDayInternal < 4L * HOUR_TIME.longValue()) {
            lastDayInternal = 4L * HOUR_TIME.longValue();
        }

        return intelnal < MINITE_TIME.longValue()?"刚刚":(intelnal < HOUR_TIME.longValue()?
                intelnal / MINITE_TIME.longValue() + "分钟前"
                :(intelnal < HOUR_TIME.longValue() * 4L?
                intelnal / HOUR_TIME.longValue() + "小时前"
                :(intelnal < lastDayInternal?
                "今天" + getTimeHHMM(currenTimeZone)
                :(intelnal < lastDayInternal + DAY_TIME.longValue()?"昨天":Date2MMDD(currenTimeZone)))));
    }

    public static String Date2MMDD(Date d) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd", Locale.CHINA);
        return formatter.format(d);
    }

    public static String getTimeHHMM(Date d) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.CHINA);
        return formatter.format(d);
    }

}
