package com.jkzz.smart_mines.hikvision;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class CommonKit {

    private static String path;

    static {
        path = (System.getProperty("user.dir") + "\\Application\\lib\\").replaceAll("\\\\", "\\\\\\\\");
        try {
            path=URLDecoder.decode(path,"utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取海康lib目录
     *
     * @return 海康lib目录
     */
    public static String getHikPath() {
        return path;
    }

    public static void setHikPath() {
        String libPath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "lib/";
        try {
            libPath = URLDecoder.decode(libPath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        path = libPath.substring(1).replaceAll("/", "\\\\");
    }

}
