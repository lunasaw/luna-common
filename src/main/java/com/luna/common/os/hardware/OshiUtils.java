package com.luna.common.os.hardware;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Sets;


import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

/**
 * @author Luna
 */
public class OshiUtils {

    public static void main(String[] args) {
        System.out.println(oshiHardwareDTO);
    }
    private final static Logger    logger = LoggerFactory.getLogger(OshiUtils.class);

    private static OshiHardwareDTO oshiHardwareDTO;

    public static OshiHardwareDTO getOshiHardwareDTO() {
        return oshiHardwareDTO;
    }

    static {
        // oshi
        SystemInfo si = new SystemInfo();
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
        MotherboardDTO motherboardDO = new MotherboardDTO();
        motherboardDO.setManufacturer(hal.getComputerSystem().getBaseboard().getManufacturer());
        motherboardDO.setModel(hal.getComputerSystem().getBaseboard().getModel());
        motherboardDO.setVersion(hal.getComputerSystem().getBaseboard().getVersion());
        motherboardDO.setSerialNumber(hal.getComputerSystem().getBaseboard().getSerialNumber());

        // MAC地址s
        oshiHardwareDTO.setMacAddressSet(acquireMACAddressSet(hal));

        // 获取CPU有关信息
        ProcessorDTO processorDTO = new ProcessorDTO();
        processorDTO.setName(hal.getProcessor().toString());
        processorDTO.setPhysicalPackageCount(hal.getProcessor().getPhysicalPackageCount());
        processorDTO.setPhysicalProcessorCount(hal.getProcessor().getPhysicalProcessorCount());
        processorDTO.setLogicalProcessorCount(hal.getProcessor().getLogicalProcessorCount());
        processorDTO.setProcessorId(hal.getProcessor().getProcessorID());
        oshiHardwareDTO.setProcessorDTO(processorDTO);

        // 获取内存有关的信息
        MemoryDTO memoryDTO = new MemoryDTO();
        memoryDTO.setMemeryTotal(hal.getMemory().getTotal());
        memoryDTO.setSwapTotal(hal.getMemory().getSwapTotal());
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

        NetworkIF[] networkIFs = hal.getNetworkIFs();
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
}
