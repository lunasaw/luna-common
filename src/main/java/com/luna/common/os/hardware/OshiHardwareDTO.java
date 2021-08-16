package com.luna.common.os.hardware;

import java.util.Set;

/**
 * @author Luna
 */
public class OshiHardwareDTO {
    /** hostname */
    private String               hostName;

    /** 制造商 */
    private String               manufacturer;

    /** 型号 */
    private String               model;

    /** 序列号 */
    private String               serialNumber;

    /** 固件信息 */
    private FirmwareDTO firmwareDTO;

    /** 主板信息 */
    private MotherboardDTO motherboardDTO;

    /** MAC地址List */
    private Set<String> macAddressSet;

    /** 处理器信息 */
    private ProcessorDTO processorDTO;

    /** 内存信息 */
    private MemoryDTO memoryDTO;

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
                '}';
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

    public void setMacAddressSet(Set<String> macAddressSet) {
        this.macAddressSet = macAddressSet;
    }

    public ProcessorDTO getProcessorDTO() {
        return processorDTO;
    }

    public void setProcessorDTO(ProcessorDTO processorDTO) {
        this.processorDTO = processorDTO;
    }

    public MemoryDTO getMemoryDTO() {
        return memoryDTO;
    }

    public void setMemoryDTO(MemoryDTO memoryDTO) {
        this.memoryDTO = memoryDTO;
    }
}
