package cn.com.minimalist.framework.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LogUtils {
    /**
     * 获取异常的调用堆栈信息。
     * @return 调用堆栈
     */
    public static String toStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            e.printStackTrace(pw);
            return sw.toString();
        } catch(Exception e1) {
            return "";
        }
    }
}
