package com.luna.common.os.hardware;

import java.util.*;

import com.luna.common.date.DateUnit;
import com.luna.common.date.DateUtils;
import com.luna.common.os.SystemInfoUtil;
import com.luna.common.text.Calculator;
import com.luna.common.text.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Sets;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

/**
 * @author Luna
 */
public class OshiUtils {

    public static void main(String[] args) {
        cpuInfo(getInstance().getHardware().getProcessor());
        memoryInfo(getInstance().getHardware().getMemory());
        jvmInfo();
        sysFiles(getInstance().getOperatingSystem());
        sysInfo();
        System.out.println(oshiHardwareDTO);
    }

    private final static Logger    logger           = LoggerFactory.getLogger(OshiUtils.class);

    private static final int       OSHI_WAIT_SECOND = 1000;

    private static OshiHardwareDTO oshiHardwareDTO;

    public static OshiHardwareDTO getOshiHardwareDTO() {
        return oshiHardwareDTO;
    }

    private static SystemInfo si = new SystemInfo();

    public static SystemInfo getInstance() {
        if (si == null) {
            si = new SystemInfo();
        }
        return si;
    }

    public OshiHardwareDTO refresh() {
        cpuInfo(getInstance().getHardware().getProcessor());
        memoryInfo(getInstance().getHardware().getMemory());
        jvmInfo();
        sysFiles(getInstance().getOperatingSystem());
        sysInfo();
        return oshiHardwareDTO;
    }

    static {
        // oshi
        HardwareAbstractionLayer hal = si.getHardware();

        oshiHardwareDTO = new OshiHardwareDTO();

        // 计算机名
        // 这儿设置一个默认值的原因是当操作系统的dns设置不对时，可能拿不到hostname
        oshiHardwareDTO.setHostName("UNKNOWN-HOSTNAME");
        oshiHardwareDTO.setHostName(si.getOperatingSystem().getNetworkParams().getHostName());

        // 主板、固件、制造商相关信息
        oshiHardwareDTO.setManufacturer(hal.getComputerSystem().getManufacturer());
        oshiHardwareDTO.setModel(hal.getComputerSystem().getModel());
        oshiHardwareDTO.setSerialNumber(hal.getComputerSystem().getSerialNumber());
        FirmwareDTO firmwareDTO = new FirmwareDTO();
        firmwareDTO.setManufacturer(hal.getComputerSystem().getFirmware().getManufacturer());
        firmwareDTO.setName(hal.getComputerSystem().getFirmware().getName());
        firmwareDTO.setDescription(hal.getComputerSystem().getFirmware().getDescription());
        firmwareDTO.setVersion(hal.getComputerSystem().getFirmware().getVersion());
        oshiHardwareDTO.setFirmwareDTO(firmwareDTO);
        MotherboardDTO motherboardDTO = new MotherboardDTO();
        motherboardDTO.setManufacturer(hal.getComputerSystem().getBaseboard().getManufacturer());
        motherboardDTO.setModel(hal.getComputerSystem().getBaseboard().getModel());
        motherboardDTO.setVersion(hal.getComputerSystem().getBaseboard().getVersion());
        motherboardDTO.setSerialNumber(hal.getComputerSystem().getBaseboard().getSerialNumber());
        oshiHardwareDTO.setMotherboardDTO(motherboardDTO);
        // MAC地址s
        oshiHardwareDTO.setMacAddressSet(acquireMACAddressSet(hal));

        // 获取CPU有关信息
        ProcessorDTO processorDTO = new ProcessorDTO();
        processorDTO.setName(hal.getProcessor().toString());
        processorDTO.setPhysicalPackageCount(hal.getProcessor().getPhysicalPackageCount());
        processorDTO.setPhysicalProcessorCount(hal.getProcessor().getPhysicalProcessorCount());
        processorDTO.setLogicalProcessorCount(hal.getProcessor().getLogicalProcessorCount());
        processorDTO.setProcessorId(hal.getProcessor().getProcessorIdentifier().getProcessorID());
        oshiHardwareDTO.setProcessorDTO(processorDTO);

        // 获取内存有关的信息
        MemoryDTO memoryDTO = new MemoryDTO();
        memoryDTO.setMemeryTotal(Calculator.getPrintSize(hal.getMemory().getTotal()));
        memoryDTO.setSwapTotal(Calculator.getPrintSize(hal.getMemory().getVirtualMemory().getSwapTotal()));
        oshiHardwareDTO.setMemoryDTO(memoryDTO);

        logger.info("init oshiHardwareDTO success, oshiHardwareDTO={}", oshiHardwareDTO);
    }

    /**
     * 获取mac地址
     * <p>
     * 可能有多个
     * </p>
     *
     * @return
     */
    private static Set<String> acquireMACAddressSet(HardwareAbstractionLayer hal) {
        Set<String> macAddressSet = Sets.newHashSet();

        List<NetworkIF> networkIFs = hal.getNetworkIFs();
        for (NetworkIF net : networkIFs) {
            // 排除几种常见的虚拟网卡
            if (!net.getDisplayName().startsWith("Microsoft Wi-Fi Direct Virtual") &&
                !net.getDisplayName().startsWith("Microsoft Hosted Network Virtual Adapter") &&
                !net.getDisplayName().startsWith("VirtualBox") &&
                !net.getDisplayName().startsWith("VMware") &&
                !net.getDisplayName().startsWith("Cisco AnyConnect") &&
                !net.getDisplayName().startsWith("TAP-Windows Adapter")) {
                macAddressSet.add(net.getMacaddr());
            }
        }

        return macAddressSet;
    }

    /**
     * 设置CPU信息
     */
    private static void cpuInfo(CentralProcessor processor) {
        // CPU信息
        ProcessorDTO processorDTO = new ProcessorDTO();
        processorDTO.setName(processor.toString());
        processorDTO.setPhysicalPackageCount(processor.getPhysicalPackageCount());
        processorDTO.setPhysicalProcessorCount(processor.getPhysicalProcessorCount());
        processorDTO.setLogicalProcessorCount(processor.getLogicalProcessorCount());
        processorDTO.setProcessorId(processor.getProcessorIdentifier().getProcessorID());
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(OSHI_WAIT_SECOND);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice =
            ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq =
            ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
            - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal =
            ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys =
            ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user =
            ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait =
            ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle =
            ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        processorDTO.setUser(NumberUtil.decimalFormat("0.##%", NumberUtil.div(user, totalCpu)));
        processorDTO.setNice(NumberUtil.decimalFormat("0.##%", NumberUtil.div(nice, totalCpu)));
        processorDTO.setSystem(NumberUtil.decimalFormat("0.##%", NumberUtil.div(cSys, totalCpu)));
        processorDTO.setIdle(NumberUtil.decimalFormat("0.##%", NumberUtil.div(idle, totalCpu)));
        processorDTO.setWait(NumberUtil.decimalFormat("0.##%", NumberUtil.div(iowait, totalCpu)));
        oshiHardwareDTO.setProcessorDTO(processorDTO);
    }

    /**
     * 设置内存信息
     */
    private static void memoryInfo(GlobalMemory memory) {
        MemoryDTO memoryDTO = new MemoryDTO();
        memoryDTO.setMemeryTotal(Calculator.getPrintSize(memory.getTotal()));
        memoryDTO.setSwapTotal(Calculator.getPrintSize(memory.getVirtualMemory().getSwapTotal()));
        memoryDTO.setUsed(Calculator.getPrintSize(memory.getTotal() - memory.getAvailable()));
        memoryDTO.setFree(Calculator.getPrintSize(memory.getAvailable()));
        oshiHardwareDTO.setMemoryDTO(memoryDTO);
    }

    /**
     * 设置Java虚拟机
     */
    private static void jvmInfo() {
        Properties props = System.getProperties();
        JvmDTO jvmDTO = new JvmDTO();
        jvmDTO.setTotal(Calculator.getPrintSize(Runtime.getRuntime().totalMemory()));
        jvmDTO.setMax(Calculator.getPrintSize(Runtime.getRuntime().maxMemory()));
        jvmDTO.setFree(Calculator.getPrintSize(Runtime.getRuntime().freeMemory()));
        jvmDTO.setVersion(props.getProperty("java.version"));
        jvmDTO.setHome(props.getProperty("java.home"));
        jvmDTO.setRunTime(getRunTime());
        jvmDTO.setStartTime(getStartTime());
        oshiHardwareDTO.setJvmDTO(jvmDTO);
    }

    /**
     * JDK启动时间
     */
    public static String getStartTime() {
        return DateUtils.formatDateTime(DateUtils.getServerStartDate());
    }

    /**
     * JDK运行时间
     */
    public static String getRunTime() {
        return String
            .valueOf(DateUtils.between(DateUtils.getCurrentDate(), DateUtils.getServerStartDate(), DateUnit.HOUR));
    }

    /**
     * 设置磁盘信息
     */
    private static void sysFiles(OperatingSystem os) {
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fileStores = fileSystem.getFileStores(true);
        List<SysFile> list = new ArrayList<>();
        for (OSFileStore fs : fileStores) {
            long free = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            long used = total - free;
            SysFile sysFile = new SysFile();
            sysFile.setDirName(fs.getMount());
            sysFile.setSysTypeName(fs.getType());
            sysFile.setTypeName(fs.getName());
            sysFile.setTotal(Calculator.getPrintSize(total));
            sysFile.setFree(Calculator.getPrintSize(free));
            sysFile.setUsed(Calculator.getPrintSize(used));
            sysFile.setUsage(NumberUtil.decimalFormat("0.##%", NumberUtil.div(used, total)));
            list.add(sysFile);
        }
        oshiHardwareDTO.setSysFiles(list);
    }

    /**
     * 设置服务器信息
     */
    private static void sysInfo() {
        Properties props = System.getProperties();
        SystemInfoDTO systemInfoDTO = new SystemInfoDTO();
        systemInfoDTO.setComputerName(SystemInfoUtil.getHostName());
        systemInfoDTO.setComputerIp(SystemInfoUtil.getIP());
        systemInfoDTO.setOsName(props.getProperty("os.name"));
        systemInfoDTO.setOsArch(props.getProperty("os.arch"));
        systemInfoDTO.setUserDir(props.getProperty("user.dir"));
        oshiHardwareDTO.setSystemInfoDTO(systemInfoDTO);
    }
}
