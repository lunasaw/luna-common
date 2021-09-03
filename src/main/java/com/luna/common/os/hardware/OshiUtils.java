package com.luna.common.os.hardware;

import java.util.*;
import java.util.stream.Collectors;

import com.luna.common.date.DateUnit;
import com.luna.common.date.DateUtils;
import com.luna.common.os.SystemInfoUtil;
import com.luna.common.os.hardware.dto.JvmDTO;
import com.luna.common.os.hardware.dto.MemoryDTO;
import com.luna.common.os.hardware.dto.ProcessorDTO;
import com.luna.common.os.hardware.dto.SysFileDTO;
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

    private final static Logger logger           = LoggerFactory.getLogger(OshiUtils.class);

    private static final int    OSHI_WAIT_SECOND = 1000;

    /**
     * 刷新系统状态
     * 
     * @param oshiHardwareDTO
     * @param si
     */
    public static void refresh(OshiHardwareDTO oshiHardwareDTO, SystemInfo si) {
        Processor processor = cpuInfo(si.getHardware().getProcessor());
        Memory memory = memoryInfo(si.getHardware().getMemory());
        Jvm jvmDTO = jvmInfo();
        List<SysFile> list = sysFiles(si.getOperatingSystem());
        SystemInfoDTO systemInfoDTO = sysInfo();
        oshiHardwareDTO.setSystemInfoDTO(systemInfoDTO);
        oshiHardwareDTO.setJvmDTO(convertJvm(jvmDTO));
        List<SysFileDTO> sysFileDTOS = list.stream().map(OshiUtils::convertSysFile).collect(Collectors.toList());
        oshiHardwareDTO.setSysFiles(sysFileDTOS);
        oshiHardwareDTO.setProcessorDTO(converProcessor(processor));
        oshiHardwareDTO.setMemoryDTO(convertMemory(memory));
    }

    public static OshiHardwareDTO baseInfo(SystemInfo si) {
        OshiHardwareDTO oshiHardwareDTO = new OshiHardwareDTO();
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
        Processor processor = new Processor();
        processor.setName(hal.getProcessor().toString());
        processor.setPhysicalPackageCount(hal.getProcessor().getPhysicalPackageCount());
        processor.setPhysicalProcessorCount(hal.getProcessor().getPhysicalProcessorCount());
        processor.setLogicalProcessorCount(hal.getProcessor().getLogicalProcessorCount());
        processor.setProcessorId(hal.getProcessor().getProcessorIdentifier().getProcessorID());
        oshiHardwareDTO.setProcessorDTO(converProcessor(processor));

        logger.info("init oshiHardwareDTO success, oshiHardwareDTO={}", oshiHardwareDTO);
        return oshiHardwareDTO;
    }

    /**
     * 获取mac地址
     * <p>
     * 可能有多个
     * </p>
     *
     * @return
     */
    public static Set<String> acquireMACAddressSet(HardwareAbstractionLayer hal) {
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
    public static Processor cpuInfo(CentralProcessor processor) {
        // CPU信息
        Processor processorDTO = new Processor();
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
        processorDTO.setUser(NumberUtil.div(user, totalCpu));
        processorDTO.setNice(NumberUtil.div(nice, totalCpu));
        processorDTO.setSystem(NumberUtil.div(cSys, totalCpu));
        processorDTO.setIdle(NumberUtil.div(idle, totalCpu));
        processorDTO.setWait(NumberUtil.div(iowait, totalCpu));
        return processorDTO;
    }

    /**
     * 设置内存信息
     */
    public static Memory memoryInfo(GlobalMemory memory) {
        Memory memoryDTO = new Memory();
        memoryDTO.setMemeryTotal(memory.getTotal());
        memoryDTO.setSwapTotal(memory.getVirtualMemory().getSwapTotal());
        memoryDTO.setUsed(memory.getTotal() - memory.getAvailable());
        memoryDTO.setFree(memory.getAvailable());
        return memoryDTO;
    }

    /**
     * 设置Java虚拟机
     */
    public static Jvm jvmInfo() {
        Properties props = System.getProperties();
        Jvm jvm = new Jvm();
        jvm.setTotal(Runtime.getRuntime().totalMemory());
        jvm.setMax(Runtime.getRuntime().maxMemory());
        jvm.setFree(Runtime.getRuntime().freeMemory());
        jvm.setVersion(props.getProperty("java.version"));
        jvm.setHome(props.getProperty("java.home"));
        jvm.setRunTime(getRunTime(DateUnit.HOUR));
        jvm.setStartTime(getStartTime());
        return jvm;
    }

    /**
     * JDK启动时间
     */
    public static Date getStartTime() {
        return DateUtils.getServerStartDate();
    }

    /**
     * JDK运行时间
     */
    public static Long getRunTime(DateUnit unit) {
        return DateUtils.between(DateUtils.getCurrentDate(), DateUtils.getServerStartDate(),
            Optional.of(unit).orElse(DateUnit.HOUR));
    }

    /**
     * 设置磁盘信息
     */
    public static List<SysFile> sysFiles(OperatingSystem os) {
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
            sysFile.setTotal(total);
            sysFile.setFree(free);
            sysFile.setUsed(used);
            list.add(sysFile);
        }
        return list;
    }

    /**
     * 设置服务器信息
     */
    public static SystemInfoDTO sysInfo() {
        Properties props = System.getProperties();
        SystemInfoDTO systemInfoDTO = new SystemInfoDTO();
        systemInfoDTO.setComputerName(SystemInfoUtil.getHostName());
        systemInfoDTO.setComputerIp(SystemInfoUtil.getIP());
        systemInfoDTO.setOsName(props.getProperty("os.name"));
        systemInfoDTO.setOsArch(props.getProperty("os.arch"));
        systemInfoDTO.setUserDir(props.getProperty("user.dir"));
        return systemInfoDTO;
    }

    public static SysFileDTO convertSysFile(SysFile sysFile) {
        if (sysFile == null) {
            return null;
        }
        SysFileDTO sysFileDTO = new SysFileDTO();
        sysFileDTO.setDirName(sysFile.getDirName());
        sysFileDTO.setSysTypeName(sysFile.getSysTypeName());
        sysFileDTO.setTypeName(sysFile.getTypeName());
        sysFileDTO.setTotal(Calculator.getPrintSize(sysFile.getTotal()));
        sysFileDTO.setFree(Calculator.getPrintSize(sysFile.getFree()));
        sysFileDTO.setUsed(Calculator.getPrintSize(sysFile.getUsed()));
        sysFileDTO.setUsage(NumberUtil.decimalFormat("0.##%", NumberUtil.div(sysFile.getUsed(), sysFile.getTotal())));
        return sysFileDTO;
    }

    public static MemoryDTO convertMemory(Memory memory) {
        if (memory == null) {
            return null;
        }
        MemoryDTO memoryDTO = new MemoryDTO();
        memoryDTO.setMemeryTotal(Calculator.getPrintSize(memory.getMemeryTotal()));
        memoryDTO.setSwapTotal(Calculator.getPrintSize(memory.getSwapTotal()));
        memoryDTO.setUsed(Calculator.getPrintSize(memory.getUsed()));
        memoryDTO.setFree(Calculator.getPrintSize(memory.getFree()));
        return memoryDTO;
    }

    public static ProcessorDTO converProcessor(Processor processor) {
        if (processor == null) {
            return null;
        }
        ProcessorDTO processorDTO = new ProcessorDTO();
        processorDTO.setName(processor.getName());
        processorDTO.setPhysicalPackageCount(processor.getPhysicalPackageCount());
        processorDTO.setPhysicalProcessorCount(processor.getPhysicalProcessorCount());
        processorDTO.setLogicalProcessorCount(processor.getLogicalProcessorCount());
        processorDTO.setProcessorId(processor.getProcessorId());
        processorDTO.setUser(NumberUtil.decimalFormat("0.##%", processor.getUser()));
        processorDTO.setNice(NumberUtil.decimalFormat("0.##%", processor.getNice()));
        processorDTO.setSystem(NumberUtil.decimalFormat("0.##%", processor.getSystem()));
        processorDTO.setIdle(NumberUtil.decimalFormat("0.##%", processor.getIdle()));
        processorDTO.setWait(NumberUtil.decimalFormat("0.##%", processor.getWait()));
        return processorDTO;
    }

    public static JvmDTO convertJvm(Jvm jvm) {
        if (jvm == null) {
            return null;
        }
        JvmDTO jvmDTO = new JvmDTO();
        jvmDTO.setTotal(Calculator.getPrintSize(jvm.getTotal()));
        jvmDTO.setMax(Calculator.getPrintSize(jvm.getMax()));
        jvmDTO.setFree(Calculator.getPrintSize(jvm.getFree()));
        jvmDTO.setVersion(jvm.getVersion());
        jvmDTO.setHome(jvm.getHome());
        jvmDTO.setRunTime(getRunTime(null).toString());
        jvmDTO.setStartTime(DateUtils.formatDateTime(getStartTime()));
        return jvmDTO;
    }
}
