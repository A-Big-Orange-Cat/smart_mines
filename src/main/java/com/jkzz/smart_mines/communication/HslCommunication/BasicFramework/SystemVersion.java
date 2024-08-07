package com.jkzz.smart_mines.communication.HslCommunication.BasicFramework;

/**
 * 系统版本类，由三部分组成，包含了一个大版本，小版本，修订版，还有一个开发者维护的内部版
 * System version class, consisting of three parts, including a major version, minor version, revised version, and an internal version maintained by the developer
 */
public class SystemVersion {

    private int m_MainVersion = 2;
    private int m_SecondaryVersion = 0;
    private int m_EditVersion = 0;
    private int m_InnerVersion = 0;


    /**
     * 根据格式化字符串的版本号初始化，例如：1.0或1.0.0或1.0.0.0503
     * Initialize according to the version number of the formatted string, for example: 1.0 or 1.0.0 or 1.0.0.0503
     *
     * @param VersionString 格式化的字符串，例如：1.0.0或1.0.0.0503
     */
    public SystemVersion(String VersionString) {
        String[] temp = VersionString.split("\\.");
        if (temp.length >= 3) {
            m_MainVersion = Integer.parseInt(temp[0]);
            m_SecondaryVersion = Integer.parseInt(temp[1]);
            m_EditVersion = Integer.parseInt(temp[2]);

            if (temp.length >= 4) {
                m_InnerVersion = Integer.parseInt(temp[3]);
            }
        }
    }

    /**
     * 根据指定的主版本，次版本，修订版来实例化一个对象
     * Instantiate an object based on the specified major, minor, and revision
     *
     * @param main 主版本
     * @param sec  次版本
     * @param edit 修订版
     */
    public SystemVersion(int main, int sec, int edit) {
        m_MainVersion = main;
        m_SecondaryVersion = sec;
        m_EditVersion = edit;
    }


    /**
     * 根据指定的主版本，次版本，修订版，内部版本来实例化一个对象
     * Instantiate an object based on the specified major, minor, revision, and build
     *
     * @param main  主版本
     * @param sec   次版本
     * @param edit  修订版
     * @param inner 内部版本号
     */
    public SystemVersion(int main, int sec, int edit, int inner) {
        m_MainVersion = main;
        m_SecondaryVersion = sec;
        m_EditVersion = edit;
        m_InnerVersion = inner;
    }

    /**
     * 主版本
     *
     * @return int数据
     */
    public int MainVersion() {
        return m_MainVersion;
    }

    /**
     * 次版本
     *
     * @return int数据
     */
    public int SecondaryVersion() {
        return m_SecondaryVersion;
    }

    /**
     * 修订版
     *
     * @return int数据
     */
    public int EditVersion() {
        return m_EditVersion;
    }

    /**
     * 内部版本号，或者是版本号表示为年月份+内部版本的表示方式
     *
     * @return int数据
     */
    public int InnerVersion() {
        return m_InnerVersion;
    }


    /**
     * 根据格式化为支持返回的不同信息的版本号
     * C返回1.0.0.0
     * N返回1.0.0
     * S返回1.0
     *
     * @param format 格式化信息
     * @return 字符串数据
     */
    public String toString(String format) {
        if (format == "C") {
            return MainVersion() + "." + SecondaryVersion() + "." + EditVersion() + "." + InnerVersion();
        }

        if (format == "N") {
            return MainVersion() + "." + SecondaryVersion() + "." + EditVersion();
        }

        if (format == "S") {
            return MainVersion() + "." + SecondaryVersion();
        }

        return toString();
    }


    /**
     * 获取版本号的字符串形式，如果内部版本号为0，则显示时不携带
     *
     * @return 字符串数据
     */
    @Override
    public String toString() {
        if (InnerVersion() == 0) {
            return MainVersion() + "." + SecondaryVersion() + "." + EditVersion();
        } else {
            return MainVersion() + "." + SecondaryVersion() + "." + EditVersion() + "." + InnerVersion();
        }
    }


    /**
     * 判断版本是否一致
     *
     * @param sv 对比的版本
     * @return 是否一致
     */
    public boolean IsSameVersion(SystemVersion sv) {
        if (this.m_MainVersion != sv.m_MainVersion) {
            return false;
        }

        if (this.m_SecondaryVersion != sv.m_SecondaryVersion) {
            return false;
        }

        if (this.m_EditVersion != sv.m_EditVersion) {
            return false;
        }

        if (this.m_InnerVersion != sv.m_InnerVersion) {
            return false;
        }

        return true;
    }

    /**
     * 判断是不是小于指定的版本
     *
     * @param sv 对比的版本
     * @return 是否小于
     */
    public boolean IsSmallerThan(SystemVersion sv) {
        if (this.m_MainVersion < sv.m_MainVersion) {
            return true;
        } else if (this.m_MainVersion > sv.m_MainVersion) {
            return false;
        }

        if (this.m_SecondaryVersion < sv.m_SecondaryVersion) {
            return true;
        } else if (this.m_SecondaryVersion > sv.m_SecondaryVersion) {
            return false;

        }

        if (this.m_EditVersion < sv.m_EditVersion) {
            return true;
        } else if (this.m_EditVersion > sv.m_EditVersion) {
            return false;
        }

        if (this.m_InnerVersion < sv.m_InnerVersion) {
            return true;
        } else if (this.m_InnerVersion > sv.m_InnerVersion) {
            return false;
        }

        return false;
    }

}
