package com.hjhq.teamface.basis.util.log;

import android.text.TextUtils;
import android.util.Log;

import com.hjhq.teamface.basis.BuildConfig;
import com.hjhq.teamface.basis.constants.Constants;

import java.util.Locale;

/**
 * @author Administrator
 * @date 2017/7/30
 */

public class LogUtil {
    private static final String TAG = "输出日志";
    public static boolean debug = BuildConfig.DEBUG;

    public static void e(String format, Object... args) {
        e(format(format, args));
    }

    public static void e(Throwable t, String format, Object... args) {
        e(t, format(format, args));
    }

    public static void e(String message) {
        if (debug && isLoggable(Log.ERROR)) {
            LogRedirector.e(TAG, message);
        }
    }

    public static void e(Throwable t, String message) {
        if (isLoggable(Log.ERROR)) {
            LogRedirector.e(TAG, message, t);
        }
    }

    public static void w(String format, Object... args) {
        w(format(format, args));
    }

    public static void w(Throwable t, String format, Object... args) {
        w(t, format(format, args));
    }

    public static void w(String message) {
        if (isLoggable(Log.WARN)) {
            LogRedirector.w(TAG, message);
        }
    }

    public static void w(Throwable t, String message) {
        if (isLoggable(Log.WARN)) {
            LogRedirector.w(TAG, message, t);
        }
    }

    public static void i(String format, Object... args) {
        i(format(format, args));
    }

    public static void i(Throwable t, String format, Object... args) {
        i(t, format(format, args));
    }

    public static void i(String message) {
        if (isLoggable(Log.INFO)) {
            LogRedirector.i(TAG, message);
        }
    }

    public static void i(Throwable t, String message) {
        if (isLoggable(Log.INFO)) {
            LogRedirector.i(TAG, message, t);
        }
    }

    public static void d(String format, Object... args) {
        d(format(format, args));
    }

    public static void d(Throwable t, String format, Object... args) {
        d(t, format(format, args));
    }

    public static void d(String message) {
        if (isLoggable(Log.DEBUG)) {
            LogRedirector.d(TAG, message);
        }
    }

    public static void d(Throwable t, String message) {
        if (isLoggable(Log.DEBUG)) {
            LogRedirector.d(TAG, message, t);
        }
    }

    public static void v(String format, Object... args) {
        v(format(format, args));
    }

    public static void v(Throwable t, String format, Object... args) {
        v(t, format(format, args));
    }

    public static void v(String message) {
        if (isLoggable(Log.VERBOSE)) {
            LogRedirector.v(TAG, message);
        }
    }

    public static void v(Throwable t, String message) {
        if (isLoggable(Log.VERBOSE)) {
            LogRedirector.v(TAG, message, t);
        }
    }

    private static String format(String format, Object... args) {
        return String.format(Locale.US, format, args);
    }

    /**
     * Applies an internal policy on whether to use {@link LogRedirector#isLoggable(String, int)}.
     * This listener is sometimes sidestepped to avoid Android's default fairly awkward
     * {@link Log#isLoggable(String, int)} listener from blocking important messages while still
     * offering users of Stetho to suppress our error/warning logs via
     * {@link LogRedirector#setLogger(LogRedirector.Logger)}.
     */
    public static boolean isLoggable(int priority) {
        switch (priority) {
            case Log.ERROR:
            case Log.DEBUG:
            case Log.WARN:
                return true;
            default:
                return LogRedirector.isLoggable(TAG, priority);
        }
    }

    /**
     * 分段打印日志
     *
     * @param TAG
     * @param msg
     */
    public static void i(String TAG, String msg) {
        if (!Constants.IS_DEBUG) {
            return;
        }
        if (TextUtils.isEmpty(msg)) {
            Log.i("LogUtil", "Content is empty!");
        }
        int strLength = msg.length();
        int start = 0;
        int end = 3500;
        for (int i = 0; i < 100; i++) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.i(TAG + (i + 1) + "〓", msg.substring(start, end));
                start = end;
                end = end + 3500;
            } else {
                Log.i(TAG + (i + 1) + "〓", msg.substring(start, strLength));
                break;
            }
        }
    }

    private static String generateTag(StackTraceElement caller) {
        String tag = "%s.%s(Line:%d)"; // 占位符
        String callerClazzName = caller.getClassName(); // 获取到类名
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber()); // 替换
        tag = TextUtils.isEmpty(TAG) ? tag : TAG + ":" + tag;
        return tag;
    }

    public static void detailInfo(String tag, String content) {
        Log.e("★★★★★★★★★★★★", tag);
        i("内容>>", content);

    }
}
