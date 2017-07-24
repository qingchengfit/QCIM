package com.tencent.qcloud.timchat.chatutils;

import android.content.Context;
import com.tencent.qcloud.timchat.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * power by
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 * MM.:  .:'   `:::  .:`MMMMMMMMMMM|`MMM'|MMMMMMMMMMM':  .:'   `:::  .:'.MM
 * MMMM.     :          `MMMMMMMMMM  :*'  MMMMMMMMMM'        :        .MMMM
 * MMMMM.    ::    .     `MMMMMMMM'  ::   `MMMMMMMM'   .     ::   .  .MMMMM
 * MMMMMM. :   :: ::'  :   :: ::'  :   :: ::'      :: ::'  :   :: ::.MMMMMM
 * MMMMMMM    ;::         ;::         ;::         ;::         ;::   MMMMMMM
 * MMMMMMM .:'   `:::  .:'   `:::  .:'   `:::  .:'   `:::  .:'   `::MMMMMMM
 * MMMMMM'     :           :           :           :           :    `MMMMMM
 * MMMMM'______::____      ::    .     ::    .     ::     ___._::____`MMMMM
 * MMMMMMMMMMMMMMMMMMM`---._ :: ::'  :   :: ::'  _.--::MMMMMMMMMMMMMMMMMMMM
 * MMMMMMMMMMMMMMMMMMMMMMMMMM::.         ::  .--MMMMMMMMMMMMMMMMMMMMMMMMMMM
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMM-.     ;::-MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM. .:' .M:F_P:MMMMMMMMMMMMMMMMMMMMMMMMMMM
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM.   .MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\ /MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMVMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 * Created by Paper on 2017/5/26.
 */

public class RecruitBusinessUtils {


    public static String getAge(String s) {
        Date date = null;
        try {
            s = s.replace("T", " ");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

            date = formatter.parse(s);

        } catch (ParseException e) {
            return "";
        } catch (Exception e) {
            return "";
        }
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int curYear = c.get(Calendar.YEAR);
        c.setTime(date);
        return (curYear-c.get(Calendar.YEAR))+"";
    }

    public static String getSalary(Integer min, Integer max) {
        if (min == null || max == null){
            return "";
        }
        return getSalary(min,max,"面议");
    }
    public static String getSalary(float min, float max ,String replace) {
        if (min < 0 && max <= 0) {
            return replace;
        } else if (min == 0 && max == 0) {
            return replace;
        } else if (min <= 0) {
            return "<" + getMoney(max);
        } else if (max <= 0) {
            return ">" + getMoney(min);
        } else {
            return getMoney(min).replace("K", "") + "-" + getMoney(max);
        }
    }

    public static String getDegree(Context context, int x) {
        if (x < 0) return "不限";
        String[] degrees = context.getResources().getStringArray(R.array.education_degree);
        if (x > 0 && x < degrees.length) {
            return degrees[x];
        } else {
            return "";
        }
    }

    public static String getMoney(float m) {
        if (m > 1000) {
            if (m % 1000 != 0) {
                return String.format(Locale.CHINA, "%.1fK", m / 1000);
            } else {
                return String.format(Locale.CHINA, "%.0fK", m / 1000);
            }
        } else {
            return String.format(Locale.CHINA, "%.1f", m);
        }
    }

    /**
     * 获取年龄限制
     */
    public static String getGender(int gender) {
        return getGender(gender,"");
    }
    public static String getGender(int gender,String plus) {
        switch (gender) {
            case 0:
                return "男性";
            case 1:
                return "女性";
            default:
                return plus+"不限";
        }
    }

    /**
     * 获取工作年龄限制
     */
    public static String getWorkYear(int min, int max) {
        return getWorkYear(min, max, "");
    }

    public static String getWorkYear(int min, int max, String plus) {
        if (min == -1 && max == -1) {
            return plus + "不限";
        } else if (min == 0 && max == 0) {
            return "应届生";
        } else if (min == -1) {
            return "<" + max + "年";
        } else if (max == -1) {
            return ">" + min + "年";
        } else {
            return min + "-" + max + "年";
        }
    }

    public static String getResumeWorkYear(int year){
        if (year == 0){
            return "应届生";
        }
        return year + "年经验";
    }

    /**
     * 获取年龄
     */
    public static String getAge(int min, int max) {
        return getAge(min,max,"");
    }
    public static String getAge(int min, int max, String plus) {
        if (min == -1 && max == -1) {
            return plus+"不限";
        } else if (min == -1) {
            return "<" + max + "岁";
        } else if (max == -1) {
            return ">" + min + "岁";
        } else {
            return min + "-" + max + "岁";
        }
    }

    /**
     * 获取身高限制
     */
    public static String getHeight(Float min, Float max) {
        return getHeight(min,max,"");
    }
    public static String getHeight(Float min, Float max,String plus) {
        if (min == -1 && max == -1) {
            return plus+"不限";
        } else if (min == -1) {
            return "<" + max.intValue() + "cm";
        } else if (max == -1) {
            return ">" + min.intValue() + "cm";
        } else {
            return min.intValue() + "-" + max.intValue() + "cm";
        }
    }

    public static String getWeight(float min, float max) {
        return getWeight(min,max,"");
    }
    public static String getWeight(float min, float max,String plus) {
        if (min == -1 && max == -1) {
            return plus+"不限";
        } else if (min == -1) {
            return "<" + max + "kg";
        } else if (max == -1) {
            return ">" + min + "kg";
        } else {
            return min + "-" + max + "kg";
        }
    }

    public static Object dealData(Object data){
        if (data == null){
            return "";
        }
        return data + " · ";
    }

    public static String getResumeHeight(String height){
        if (height.contains(".")){
            return height.split("\\.")[0];
        }
        return height;
    }


}
