package com.luna.common.os.hardware;

import java.util.List;

/**
 * @author Tony
 */
public class CLInfoDTO {
    /** OpenCL的设备信息 */
    private List<OpenCLDeviceDTO> openCLDeviceDTOList;

    public List<OpenCLDeviceDTO> getOpenCLDeviceDTOList() {
        return openCLDeviceDTOList;
    }

    public void setOpenCLDeviceDTOList(List<OpenCLDeviceDTO> openCLDeviceDTOList) {
        this.openCLDeviceDTOList = openCLDeviceDTOList;
    }
}
