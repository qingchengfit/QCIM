package com.tencent.qcloud.timchat.chatutils;

import android.content.Context;
import com.tencent.qcloud.timchat.R;
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

    public static String getSalary(float min, float max) {
        if (min == 0 && max == 0) {
            return "不限";
        } else if (min == 0) {
            return "<" + getMoney(max);
        } else if (max == 0) {
            return "<" + getMoney(min);
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
            return String.format(Locale.CHINA, "%.1fK", m);
        }
    }

    /**
     * 获取年龄限制
     */
    public static String getGender(int gender) {
        switch (gender) {
            case 0:
                return "男性";
            case 1:
                return "女性";
            default:
                return "不限";
        }
    }

    /**
     * 获取工作年龄限制
     */
    public static String getWorkYear(int min, int max) {
        if (min == -1 && max == -1) {
            return "不限";
        } else if (min == -1) {
            return "<" + max + "年";
        } else if (max == -1) {
            return ">" + min + "年";
        } else {
            return min + "-" + max + "年";
        }
    }

    /**
     * 获取年龄
     */
    public static String getAge(int min, int max) {
        if (min == -1 && max == -1) {
            return "不限";
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
    public static String getHeight(float min, float max) {
        if (min == -1 && max == -1) {
            return "不限";
        } else if (min == -1) {
            return "<" + max + "cm";
        } else if (max == -1) {
            return ">" + min + "cm";
        } else {
            return min + "-" + max + "cm";
        }
    }

    public static String getWeight(float min, float max) {
        if (min == -1 && max == -1) {
            return "不限";
        } else if (min == -1) {
            return "<" + max + "kg";
        } else if (max == -1) {
            return ">" + min + "kg";
        } else {
            return min + "-" + max + "kg";
        }
    }

    public static int getBirthdayAge(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int curYear = c.get(Calendar.YEAR);
        c.setTime(d);
        return c.get(Calendar.YEAR) - curYear;
    }
}
