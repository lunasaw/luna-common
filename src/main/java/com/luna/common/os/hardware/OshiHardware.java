package com.luna.common.os.hardware;
import java.util.List;
import java.util.Set;

/**
 * @author Luna
 */
public class OshiHardware {
    /** hostname */
    private String         hostName;

    /** 制造商 */
    private String         manufacturer;

    /** 型号 */
    private String         model;

    /** 序列号 */
    private String         serialNumber;

    /** 固件信息 */
    private FirmwareDTO    firmwareDTO;

    /** 主板信息 */
    private MotherboardDTO motherboardDTO;

    /** MAC地址List */
    private Set<String>    macAddressSet;

    /** 处理器信息 */
    private Processor      processor;

    /** 内存信息 */
    private Memory         memory;

    /** JVM */
    private Jvm            jvm;

    /** 磁盘 */
    private List<SysFile>  sysFiles;

    /** 系统信息 */
    private SystemInfoDTO  systemInfoDTO;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("OshiHardwareDTO{");
        sb.append("hostName='").append(hostName).append('\'');
        sb.append(", manufacturer='").append(manufacturer).append('\'');
        sb.append(", model='").append(model).append('\'');
        sb.append(", serialNumber='").append(serialNumber).append('\'');
        sb.append(", firmwareDTO=").append(firmwareDTO);
        sb.append(", motherboardDTO=").append(motherboardDTO);
        sb.append(", macAddressSet=").append(macAddressSet);
        sb.append(", processor=").append(processor);
        sb.append(", memory=").append(memory);
        sb.append(", jvm=").append(jvm);
        sb.append(", sysFiles=").append(sysFiles);
        sb.append(", systemInfoDTO=").append(systemInfoDTO);
        sb.append('}');
        return sb.toString();
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

    public Processor getProcessor() {
        return processor;
    }

    public void setProcessor(Processor processor) {
        this.processor = processor;
    }

    public Memory getMemory() {
        return memory;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    public Jvm getJvm() {
        return jvm;
    }

    public void setJvm(Jvm jvm) {
        this.jvm = jvm;
    }

    public List<SysFile> getSysFiles() {
        return sysFiles;
    }

    public void setSysFiles(List<SysFile> sysFiles) {
        this.sysFiles = sysFiles;
    }

    public SystemInfoDTO getSystemInfoDTO() {
        return systemInfoDTO;
    }

    public void setSystemInfoDTO(SystemInfoDTO systemInfoDTO) {
        this.systemInfoDTO = systemInfoDTO;
    }
}
