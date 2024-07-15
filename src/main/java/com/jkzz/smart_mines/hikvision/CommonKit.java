package com.jkzz.smart_mines.hikvision;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Objects;

public class CommonKit {

    private static String path;

    static {
        path = (System.getProperty("user.dir") + "\\Application\\lib\\").replace("\\\\", "\\\\\\\\");
        try {
            path = URLDecoder.decode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private CommonKit() {

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
        String libPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath() + "lib/";
        try {
            libPath = URLDecoder.decode(libPath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        path = libPath.substring(1).replace("/", "\\\\");
    }

}
