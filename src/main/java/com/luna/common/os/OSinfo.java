package com.luna.common.os;

/**
 *  @author luna
 */
public class OSinfo {

    private static final String OS        = System.getProperty("os.name").toLowerCase();

    private static final OSinfo INSTANCE = new OSinfo();

    private EPlatform           platform;

    private OSinfo() {}

    public static boolean isLinux() {
        return OS.contains("linux");
    }

    public static boolean isMacOS() {
        return OS.contains("mac") && OS.contains("os") && !OS.contains("x");
    }

    public static boolean isMacOSX() {
        return OS.contains("mac") && OS.contains("os") && OS.contains("x");
    }

    public static boolean isWindows() {
        return OS.contains("windows");
    }

    public static boolean isOS2() {
        return OS.contains("os/2");
    }

    public static boolean isSolaris() {
        return OS.contains("solaris");
    }

    public static boolean isSunOS() {
        return OS.contains("sunos");
    }

    public static boolean isMPEiX() {
        return OS.contains("mpe/ix");
    }

    public static boolean isHPUX() {
        return OS.contains("hp-ux");
    }

    public static boolean isAix() {
        return OS.contains("aix");
    }

    public static boolean isOS390() {
        return OS.contains("os/390");
    }

    public static boolean isFreeBSD() {
        return OS.contains("freebsd");
    }

    public static boolean isIrix() {
        return OS.contains("irix");
    }

    public static boolean isDigitalUnix() {
        return OS.contains("digital") && OS.contains("unix");
    }

    public static boolean isNetWare() {
        return OS.contains("netware");
    }

    public static boolean isOSF1() {
        return OS.contains("osf1");
    }

    public static boolean isOpenVMS() {
        return OS.contains("openvms");
    }

    /**
     * 获取操作系统名字
     * 
     * @return 操作系统名
     */
    public static EPlatform getOSname() {
        if (isAix()) {
            INSTANCE.platform = EPlatform.AIX;
        } else if (isDigitalUnix()) {
            INSTANCE.platform = EPlatform.Digital_Unix;
        } else if (isFreeBSD()) {
            INSTANCE.platform = EPlatform.FreeBSD;
        } else if (isHPUX()) {
            INSTANCE.platform = EPlatform.HP_UX;
        } else if (isIrix()) {
            INSTANCE.platform = EPlatform.Irix;
        } else if (isLinux()) {
            INSTANCE.platform = EPlatform.Linux;
        } else if (isMacOS()) {
            INSTANCE.platform = EPlatform.Mac_OS;
        } else if (isMacOSX()) {
            INSTANCE.platform = EPlatform.Mac_OS_X;
        } else if (isMPEiX()) {
            INSTANCE.platform = EPlatform.MPEiX;
        } else if (isNetWare()) {
            INSTANCE.platform = EPlatform.NetWare_411;
        } else if (isOpenVMS()) {
            INSTANCE.platform = EPlatform.OpenVMS;
        } else if (isOS2()) {
            INSTANCE.platform = EPlatform.OS2;
        } else if (isOS390()) {
            INSTANCE.platform = EPlatform.OS390;
        } else if (isOSF1()) {
            INSTANCE.platform = EPlatform.OSF1;
        } else if (isSolaris()) {
            INSTANCE.platform = EPlatform.Solaris;
        } else if (isSunOS()) {
            INSTANCE.platform = EPlatform.SunOS;
        } else if (isWindows()) {
            INSTANCE.platform = EPlatform.Windows;
        } else {
            INSTANCE.platform = EPlatform.Others;
        }
        return INSTANCE.platform;
    }
}
