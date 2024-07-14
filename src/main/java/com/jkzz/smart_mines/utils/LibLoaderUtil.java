package com.jkzz.smart_mines.utils;

import com.jkzz.smart_mines.hikvision.CommonKit;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public final class LibLoaderUtil {

    public static boolean loadStatus = true;

    /**
     * 加载项目下的lib文件，DLL或SO
     *
     * @param dirPath 需要扫描的文件路径，项目下的相对路径
     */
    public synchronized static void loader(String dirPath) throws IOException {
        Enumeration<URL> dir = Thread.currentThread().getContextClassLoader().getResources(dirPath);
        // 获取操作系统类型
        String systemType = System.getProperty("os.name");
        //String systemArch = System.getProperty("os.arch");
        // 获取动态链接库后缀名
        String ext = (systemType.toLowerCase().contains("win")) ? ".dll" : ".so";
        while (dir.hasMoreElements()) {
            URL url = dir.nextElement();
            String protocol = url.getProtocol();
            if ("jar".equals(protocol)) {
                JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                JarFile jarFile = jarURLConnection.getJarFile();
                // 遍历Jar包
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String entityName = jarEntry.getName();
                    if (jarEntry.isDirectory() || !entityName.startsWith(dirPath)) {
                        continue;
                    }
                    if (entityName.endsWith(ext)) {
                        loadJarNative(jarEntry);
                    }
                }
            } else if ("file".equals(protocol)) {
                CommonKit.setHikPath();
            }
        }
    }

    /**
     * 创建动态链接库缓存文件，然后加载资源文件
     */
    private static void loadJarNative(JarEntry jarEntry) throws IOException {
        File path = new File(".");
        // 将所有动态链接库dll/so文件都放在一个临时文件夹下，然后进行加载
        // 这是因为项目为可执行jar文件的时候不能很方便的扫描里面文件
        // 此目录放置在与项目同目录下的Application\lib文件夹下
        String rootOutputPath = path.getAbsoluteFile().getParent() + File.separator + "Application";
        String entityName = jarEntry.getName();
        String fileName = entityName.substring(entityName.lastIndexOf("/") + 1);
        File tempFile = new File(rootOutputPath + File.separator + entityName);

        // 如果缓存文件路径不存在，则创建路径
        if (!tempFile.getParentFile().exists()) {
            tempFile.getParentFile().mkdirs();
        }
        // 如果缓存文件存在，则删除
        if (tempFile.exists()) {
            tempFile.delete();
        }

        InputStream in = null;
        BufferedInputStream reader = null;
        FileOutputStream writer = null;
        try {
            // 读取文件形成输入流
            in = LibLoaderUtil.class.getResourceAsStream(entityName);
            if (in == null) {
                in = LibLoaderUtil.class.getResourceAsStream("/" + entityName);
                if (null == in) {
                    return;
                }
            }
            LibLoaderUtil.class.getResource(fileName);
            reader = new BufferedInputStream(in);
            writer = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];

            while (reader.read(buffer) > 0) {
                writer.write(buffer);
                buffer = new byte[1024];
            }
        } catch (IOException e) {
            loadStatus = false;
        } finally {
            if (in != null) {
                in.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }

}
