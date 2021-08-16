package com.luna.common.os;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomUtils;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 获取当前系统信息
 * 
 * @author luna
 */
public class SystemInfoUtil {

    private static final SystemInfo SI                = new SystemInfo();

    /**
     * 获取所有进程
     *
     * @return
     */
    public static List<OSProcess> getProcesses() {
        return Arrays.asList(SI.getOperatingSystem().getProcesses(0, null));
    }


    private static InetAddress localHost = null;

    public static InetAddress getLocalHost() {
        try {
            if (localHost == null) {
                localHost = InetAddress.getLocalHost();
            }
            return localHost;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 本地IP
     *
     * @return IP地址
     */
    public static String getIP() {
        return getLocalHost().getHostAddress();
    }

    /**
     * 获取用户机器名称
     *
     * @return
     */
    public static String getHostName() {
        return getLocalHost().getHostName();
    }

    /**
     * 获取Mac地址
     *
     * @return Mac地址，例如：F0-4D-A2-39-24-A6
     */
    public static String getMac(InetAddress addr) {
        try {
            NetworkInterface byInetAddress = NetworkInterface.getByInetAddress(addr);
            byte[] hardwareAddress = byInetAddress.getHardwareAddress();
            return getMacFromBytes(hardwareAddress);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMac() {
        return getMac(getLocalHost());
    }

    /**
     * 获取所有网卡的Mac地址
     * 
     * @return
     */
    public static List<String> getMacList() {
        try {
            ArrayList<String> list = Lists.newArrayList();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface iface = networkInterfaces.nextElement();
                List<InterfaceAddress> addrs = iface.getInterfaceAddresses();
                List<String> collect = addrs.stream().filter(interfaceAddress -> {
                    try {
                        return NetworkInterface.getByInetAddress(interfaceAddress.getAddress())
                            .getHardwareAddress() != null;
                    } catch (SocketException e) {
                        return false;
                    }
                }).map(address -> getMac(address.getAddress()))
                    .distinct().collect(Collectors.toList());
                list.addAll(collect);
            }
            return list.stream().distinct().collect(Collectors.toList());
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取本机随机Mac地址
     * 
     * @return
     */
    public static String getRandomMac() {
        List<String> macList = getMacList();
        return macList.get(RandomUtils.nextInt(0, macList.size() - 1));
    }

    /**
     * 获取当前系统名称
     *
     * @return 当前系统名，例如： windows xp
     */
    public static String getSystemName() {
        Properties sysProperty = System.getProperties();
        // 系统名称
        return sysProperty.getProperty("os.name");
    }

    private static String getMacFromBytes(byte[] bytes) {
        StringBuffer mac = new StringBuffer();
        byte currentByte;
        boolean first = false;
        for (byte b : bytes) {
            if (first) {
                mac.append("-");
            }
            currentByte = (byte)((b & 240) >> 4);
            mac.append(Integer.toHexString(currentByte));
            currentByte = (byte)(b & 15);
            mac.append(Integer.toHexString(currentByte));
            first = true;
        }
        return mac.toString().toUpperCase();
    }

    /**
     * 字节转换
     *
     * @param size 字节大小
     * @return 转换后值
     */
    public String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB" , (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB" , f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB" , f);
        } else {
            return String.format("%d B" , size);
        }
    }
}