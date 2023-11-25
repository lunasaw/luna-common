package com.luna.common.os;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import oshi.SystemInfo;
import oshi.software.os.OSProcess;

/**
 * 获取当前系统信息
 * 
 * @author luna
 */
public class SystemInfoUtil {

    private static final SystemInfo SI = new SystemInfo();
    private static InetAddress localHost = null;

    /**
     * 获取所有进程
     *
     * @return
     */
    public static List<OSProcess> getProcesses() {
        return SI.getOperatingSystem().getProcesses();
    }

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
     * 本地IP 有可能拿到回环网卡
     *
     * @return IP地址
     */
    @Deprecated
    public static String getIP() {
        return getLocalHost().getHostAddress();
    }

    public static String getIpv4() {
        return Objects.requireNonNull(getAllIpv4()).stream().findFirst().orElse(null);
    }

    /**
     * 本级ip 过滤 回环地址、链路本地地址或多播地址
     * 
     * @return
     */
    public static List<String> getAllIpv4() {
        List<String> allIpAddress = getAllIpAddress();
        if (CollectionUtils.isEmpty(allIpAddress)) {
            return new ArrayList<>();
        }
        return allIpAddress.stream().filter(e -> !e.contains(":")).collect(Collectors.toList());
    }

    /**
     * 本级ip 过滤 回环地址、链路本地地址或多播地址
     * @return
     */
    public static String getNoLoopbackIP() {
        return getAddress().getHostAddress();
    }

    public static List<String> getAllIpAddress() {
        try {
            return getInetAddress(false).stream().map(InetAddress::getHostAddress).collect(Collectors.toList());
        } catch (SocketException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 获取非回环网卡IP
     * @return
     */
    public static InetAddress getAddress() {
        try {
            return getInetAddress(true).stream().findFirst().orElse(getLocalHost());
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws SocketException {
        System.out.println(getNoLoopbackIP());
        System.out.println(getInetAddress(false));
        System.out.println(getMacList());
        System.out.println(getAllIpAddress());
        System.out.println(getAddress());
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
            return null;
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
            ArrayList<String> macList = Lists.newArrayList();
            List<InetAddress> inetAddress = getInetAddress(false);

            for (InetAddress address : inetAddress) {
                String mac = getMac(address);
                if (StringUtils.isNotBlank(mac)) {
                    macList.add(mac);
                }
            }
            return macList;
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

    public static List<InetAddress> getInetAddress(Boolean filterLoopback) throws SocketException {
        List<InetAddress> ipList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();

                if (filterLoopback) {
                    if (address.isLinkLocalAddress() || address.isLoopbackAddress() || address.isMulticastAddress()) {
                        continue;
                    }
                }

                ipList.add(address);
            }
        }

        return ipList;
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
            return String.format("%.1f GB", (float)size / gb);
        } else if (size >= mb) {
            float f = (float)size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float)size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
        }
    }
}