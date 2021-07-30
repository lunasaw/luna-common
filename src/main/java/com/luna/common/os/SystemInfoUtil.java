package com.luna.common.os;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 获取当前系统信息
 * @author luna
 */
public class SystemInfoUtil {
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
     * 获取C盘卷 序列号
     *
     * @return
     */
    public static String getDiskNumber() {
        String line = "";
        String HdSerial = "";
        // 记录硬盘序列号

        try {

            Process proces = Runtime.getRuntime().exec("cmd /c dir c:");
            // 获取命令行参数
            BufferedReader buffreader = new BufferedReader(
                new InputStreamReader(proces.getInputStream()));

            while ((line = buffreader.readLine()) != null) {

                if (line.indexOf("卷的序列号是 ") != -1) {
                    // 读取参数并获取硬盘序列号

                    HdSerial = line.substring(line.indexOf("卷的序列号是 ")
                        + "卷的序列号是 ".length(), line.length());
                    break;
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return HdSerial;
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
}