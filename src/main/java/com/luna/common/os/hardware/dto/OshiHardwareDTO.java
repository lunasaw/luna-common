package com.luna.common.os.hardware.dto;

import java.util.List;
import java.util.Set;

import com.luna.common.os.hardware.FirmwareDTO;
import com.luna.common.os.hardware.MotherboardDTO;
import com.luna.common.os.hardware.SystemInfoDTO;

/**
 * @author Luna
 */
public class OshiHardwareDTO {
    /** hostname */
    private String           hostName;

    /** 制造商 */
    private String           manufacturer;

    /** 型号 */
    private String           model;

    /** 序列号 */
    private String           serialNumber;

    /** 固件信息 */
    private FirmwareDTO      firmwareDTO;

    /** 主板信息 */
    private MotherboardDTO   motherboardDTO;

    /** MAC地址List */
    private Set<String>      macAddressSet;

    /** 处理器信息 */
    private ProcessorDTO     processorDTO;

    /** 内存信息 */
    private MemoryDTO        memoryDTO;

    /** JVM */
    private JvmDTO           jvmDTO;

    /** 磁盘 */
    private List<SysFileDTO> sysFiles;

    /** 系统信息 */
    private SystemInfoDTO    systemInfoDTO;

    @Override
    public String toString() {
        return "OshiHardwareDTO{" +
            "hostName='" + hostName + '\'' +
            ", manufacturer='" + manufacturer + '\'' +
            ", model='" + model + '\'' +
            ", serialNumber='" + serialNumber + '\'' +
            ", firmwareDTO=" + firmwareDTO +
            ", motherboardDTO=" + motherboardDTO +
            ", macAddressSet=" + macAddressSet +
            ", processorDTO=" + processorDTO +
            ", memoryDTO=" + memoryDTO +
            ", jvmDTO=" + jvmDTO +
            ", sysFiles=" + sysFiles +
            ", systemInfoDTO=" + systemInfoDTO +
            '}';
    }

    public SystemInfoDTO getSystemInfoDTO() {
        return systemInfoDTO;
    }

    public void setSystemInfoDTO(SystemInfoDTO systemInfoDTO) {
        this.systemInfoDTO = systemInfoDTO;
    }

    public List<SysFileDTO> getSysFiles() {
        return sysFiles;
    }

    public void setSysFiles(List<SysFileDTO> sysFiles) {
        this.sysFiles = sysFiles;
    }

    public void setProcessorDTO(ProcessorDTO processorDTO) {
        this.processorDTO = processorDTO;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public FirmwareDTO getFirmwareDTO() {
        return firmwareDTO;
    }

    public void setFirmwareDTO(FirmwareDTO firmwareDTO) {
        this.firmwareDTO = firmwareDTO;
    }

    public MotherboardDTO getMotherboardDTO() {
        return motherboardDTO;
    }

    public void setMotherboardDTO(MotherboardDTO motherboardDTO) {
        this.motherboardDTO = motherboardDTO;
    }

    public Set<String> getMacAddressSet() {
        return macAddressSet;
    }

    public ProcessorDTO getProcessorDTO() {
        return processorDTO;
    }

    public void setMacAddressSet(Set<String> macAddressSet) {
        this.macAddressSet = macAddressSet;
    }

    public JvmDTO getJvmDTO() {
        return jvmDTO;
    }

    public void setJvmDTO(JvmDTO jvmDTO) {
        this.jvmDTO = jvmDTO;
    }

    public MemoryDTO getMemoryDTO() {
        return memoryDTO;
    }

    public void setMemoryDTO(MemoryDTO memoryDTO) {
        this.memoryDTO = memoryDTO;
    }
}
