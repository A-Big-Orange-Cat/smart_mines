package com.jkzz.smart_mines.communication.HslCommunication;

import com.jkzz.smart_mines.communication.HslCommunication.BasicFramework.SoftBasic;

import java.security.MessageDigest;
import java.util.Date;

public class Authorization {


    private static String Declaration = "禁止对本组件进行反编译，篡改源代码，如果用于商业用途，将追究法律责任，如需注册码，请联系作者，QQ号：200962190，邮箱：hsl200909@163.com，欢迎企业合作。";
    private static Date niahdiahduasdbubfas = new Date();
    private static long naihsdadaasdasdiwasdaid = 0;
    private static long moashdawidaisaosdas = 0;
    private static double nuasgdawydbishcgas = 8;
    private static int nuasgdaaydbishdgas = 0;
    private static int nuasgdawydbishdgas = 8;
    private static double nuasgdawydaishdgas = 24;
    private static int nasidhadguawdbasd = 1000;
    private static int niasdhasdguawdwdad = 12345;
    private static int hidahwdauushduasdhu = 23456;
    private static long iahsduiwikaskfhishfdi = 0;
    private static int zxnkasdhiashifshfsofh = 0;

    static {
        niahdiahduasdbubfas = iashdagsdawbdawda();
        if (naihsdadaasdasdiwasdaid != 0) {
            naihsdadaasdasdiwasdaid = 0;
        }

        if (nuasgdawydaishdgas != 24) {
            nuasgdawydaishdgas = 24;
        }

        if (nuasgdaaydbishdgas != 0) {
            nuasgdaaydbishdgas = 0;
        }

        if (nuasgdawydbishdgas != 24) {
            nuasgdawydbishdgas = 24;
        }
        //System.Threading.ThreadPool.QueueUserWorkItem( new System.Threading.WaitCallback( asidhiahfaoaksdnasoif ), null );
    }

    public static boolean nzugaydgwadawdibbas() {
        moashdawidaisaosdas++;
        // 如果需要移除验证，这里返回true即可。
        return true;
        //if (naihsdadaasdasdiwasdaid == niasdhasdguawdwdad && nuasgdaaydbishdgas > 0) return nuasduagsdwydbasudasd( );
        //if (((iashdagsdawbdawda( ).getTime() - niahdiahduasdbubfas.getTime()) / 1000d / 60d / 60) < nuasgdawydaishdgas) // .TotalHours < nuasgdawydaishdgas)
        //{
        //    return nuasduagsdwydbasudasd( );
        //}
        //
        //return asdhuasdgawydaduasdgu( );
    }

    /// <summary>
    /// 商业授权则返回true，否则返回false
    /// </summary>
    /// <returns>是否成功进行商业授权</returns>
    public static boolean asdniasnfaksndiqwhawfskhfaiw() {
        // 这里是一些只能商业授权对象使用的接口方法
        // 如果需要移除验证，这里返回true即可。
        return true;
        //if (naihsdadaasdasdiwasdaid == niasdhasdguawdwdad && nuasgdaaydbishdgas > 0) return nuasduagsdwydbasudasd( );
        //if (((iashdagsdawbdawda( ).getTime() - niahdiahduasdbubfas.getTime()) / 1000d / 60d / 60d ) < nuasgdawydbishdgas) // .TotalHours < nuasgdawydbishdgas)
        //{
        //    return nuasduagsdwydbasudasd( );
        //}
        //return asdhuasdgawydaduasdgu( );
    }

    private static boolean nuasduagsdwydbasudasd() {
        return true;
    }

    private static boolean asdhuasdgawydaduasdgu() {
        return false;
    }

    private static boolean ashdadgawdaihdadsidas() {
        return niasdhasdguawdwdad == 12345;
    }

    private static Date iashdagsdawbdawda() {
        return new Date();
    }

    private static Date iashdagsaawbdawda() {
        return Utilities.AddDateDays(new Date(), 1);
    }

    private static Date iashdagsaawadawda() {
        return Utilities.AddDateDays(new Date(), 2);
    }

    private static void oasjodaiwfsodopsdjpasjpf() {
        // System.Threading.Interlocked.Increment( ref iahsduiwikaskfhishfdi );
    }

    static String nasduabwduadawdb(String miawdiawduasdhasd) {
        StringBuilder asdnawdawdawd = new StringBuilder();
        try {
            MessageDigest asndiawdniad = MessageDigest.getInstance("MD5");
            byte[] asdadawdawdas = asndiawdniad.digest(Utilities.csharpString2Byte(miawdiawduasdhasd));
            for (int andiawbduawbda = 0; andiawbduawbda < asdadawdawdas.length; andiawbduawbda++) {
                asdnawdawdawd.append(SoftBasic.ByteToHexString(new byte[]{(byte) (~asdadawdawdas[andiawbduawbda])}));
            }
            return asdnawdawdawd.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 设置本组件系统的授权信息，如果激活失败，只能使用24小时，24小时后所有的网络通信不会成功<br />
     * Set the authorization information of this component system. If the activation fails, it can only be used for 8 hours. All network communication will not succeed after 8 hours
     *
     * @param code 授权码
     * @return 是否激活成功
     */
    public static boolean SetAuthorizationCode(String code) {
        if (nasduabwduadawdb(code).equals("E02318723C72E86E5A273A74774748FC"))         // 普通vip群
        {
            nuasgdaaydbishdgas = 1;
            nuasgdawydbishcgas = 286512937;
            nuasgdawydaishdgas = 24 * 365 * 10;
            return nuasduagsdwydbasudasd();
        } else if (nasduabwduadawdb(code).equals("8D66ADEEDA05B088846F83AFCFAF53B6"))    // 高级测试用户，实例化没有上限，时间延长到3个月
        {
            nuasgdaaydbishdgas = 1;
            nuasgdawydbishcgas = 286512937;
            nuasgdawydaishdgas = 24 * 90;
            return nuasduagsdwydbasudasd();
        } else if (nasduabwduadawdb(code).equals("D6D7408A5F179B3D6A1E2046F6372B53"))    // 超级vip群的固定的激活码
        {
            nuasgdaaydbishdgas = 10000;
            nuasgdawydbishcgas = nuasgdawydbishdgas;
            naihsdadaasdasdiwasdaid = niasdhasdguawdwdad;
            return nuasduagsdwydbasudasd();
        }
        return asdhuasdgawydaduasdgu();
    }
}
