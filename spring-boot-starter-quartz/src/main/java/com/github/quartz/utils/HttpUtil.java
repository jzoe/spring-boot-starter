package com.github.quartz.utils;import java.io.PrintWriter;import java.net.InetAddress;import java.net.UnknownHostException;/** * @DESCRIPTION: 网络相关工具 * @AUTHER: chenmin * @CREATE BY: 18/1/14 下午3:51 */public class HttpUtil {    /**     * 获取系统IP地址     * @return     */    public static String getIpAddress() {        try {            InetAddress inetAddress = InetAddress.getLocalHost();            return inetAddress.getHostAddress();        } catch (UnknownHostException e) {            return "";        }    }    public static void close(PrintWriter pw) {        if (pw != null) {            pw.flush();            pw.close();        }    }}