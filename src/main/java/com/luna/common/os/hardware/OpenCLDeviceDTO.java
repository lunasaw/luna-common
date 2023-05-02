package com.luna.common.os.hardware;

import com.luna.common.os.hardware.constant.OpenCLDeviceCLDeviceVendorIdConstant;
import com.luna.common.os.hardware.constant.OpenCLDeviceCLPlatformICDSuffixKHRConstant;
import com.luna.common.os.hardware.constant.OpenCLDeviceClDeviceTypeConstant;

/**
 * @author Tony
 */
public class OpenCLDeviceDTO {
    /**
     * OpenCL平台的厂商ICD扩展后缀，见{@link OpenCLDeviceCLPlatformICDSuffixKHRConstant}}
     */
    private String clPlatformICDSuffixKHR;

    /** 设备id */
    private long   id;

    /** 设备类型，见{@link OpenCLDeviceClDeviceTypeConstant}} */
    private String clDeviceType;

    /** Intel CPU GPU、AMD CPU、NVIDIA GPU的话会是设备的名字，AMD GPU的话会是芯片代号 */
    private String clDeviceName;

    /** 只有AMD GPU有，设备的实际名字 */
    private String clDeviceBoardNameAMD;

    /** 制造商，制造商名字可能多种多样，使用clDeviceVendorId判断 */
    private String clDeviceVendor;

    /** 制造商id，见{@link OpenCLDeviceCLDeviceVendorIdConstant} */
    private String clDeviceVendorId;

    /** OpenCL的版本 */
    private String clDeviceVersion;

    /** OpenCl的C版本 */
    private String clDeviceOpenCLCVersion;

    /** 驱动版本，NVIDIA GPU的话会是安装驱动的版本号，其他都是内部版本号 */
    private String clDriverVersion;

    /** 显存大小，单位为字节，如果是Intel核显或者AMD APU的话，显存大小没有参考价值 */
    private long   clDeviceGlobalMemSize;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getClDeviceType() {
        return clDeviceType;
    }

    public void setClDeviceType(String clDeviceType) {
        this.clDeviceType = clDeviceType;
    }

    public String getClDeviceName() {
        return clDeviceName;
    }

    public void setClDeviceName(String clDeviceName) {
        this.clDeviceName = clDeviceName;
    }

    public String getClDeviceBoardNameAMD() {
        return clDeviceBoardNameAMD;
    }

    public void setClDeviceBoardNameAMD(String clDeviceBoardNameAMD) {
        this.clDeviceBoardNameAMD = clDeviceBoardNameAMD;
    }

    public String getClDeviceVendor() {
        return clDeviceVendor;
    }

    public void setClDeviceVendor(String clDeviceVendor) {
        this.clDeviceVendor = clDeviceVendor;
    }

    public String getClDeviceVendorId() {
        return clDeviceVendorId;
    }

    public void setClDeviceVendorId(String clDeviceVendorId) {
        this.clDeviceVendorId = clDeviceVendorId;
    }

    public String getClDeviceVersion() {
        return clDeviceVersion;
    }

    public void setClDeviceVersion(String clDeviceVersion) {
        this.clDeviceVersion = clDeviceVersion;
    }

    public String getClDriverVersion() {
        return clDriverVersion;
    }

    public void setClDriverVersion(String clDriverVersion) {
        this.clDriverVersion = clDriverVersion;
    }

    public long getClDeviceGlobalMemSize() {
        return clDeviceGlobalMemSize;
    }

    public void setClDeviceGlobalMemSize(long clDeviceGlobalMemSize) {
        this.clDeviceGlobalMemSize = clDeviceGlobalMemSize;
    }

    public String getClPlatformICDSuffixKHR() {
        return clPlatformICDSuffixKHR;
    }

    public void setClPlatformICDSuffixKHR(String clPlatformICDSuffixKHR) {
        this.clPlatformICDSuffixKHR = clPlatformICDSuffixKHR;
    }

    public String getClDeviceOpenCLCVersion() {
        return clDeviceOpenCLCVersion;
    }

    public void setClDeviceOpenCLCVersion(String clDeviceOpenCLCVersion) {
        this.clDeviceOpenCLCVersion = clDeviceOpenCLCVersion;
    }

    @Override
    public String toString() {
        String builder = "OpenCLDeviceDO [clPlatformICDSuffixKHR=" +
                clPlatformICDSuffixKHR +
                ", id=" +
                id +
                ", clDeviceType=" +
                clDeviceType +
                ", clDeviceName=" +
                clDeviceName +
                ", clDeviceBoardNameAMD=" +
                clDeviceBoardNameAMD +
                ", clDeviceVendor=" +
                clDeviceVendor +
                ", clDeviceVendorId=" +
                clDeviceVendorId +
                ", clDeviceVersion=" +
                clDeviceVersion +
                ", clDeviceOpenCLCVersion=" +
                clDeviceOpenCLCVersion +
                ", clDriverVersion=" +
                clDriverVersion +
                ", clDeviceGlobalMemSize=" +
                clDeviceGlobalMemSize +
                "]";
        return builder;
    }

}
