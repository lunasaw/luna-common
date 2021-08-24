package com.luna.common.os.hardware;

/**
 * @author Tony
 */
public class MotherboardDTO {
    private String manufacturer;
    private String model;
    private String version;
    private String serialNumber;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override
    public String toString() {
        return "MotherboardDTO{" +
            "manufacturer='" + manufacturer + '\'' +
            ", model='" + model + '\'' +
            ", version='" + version + '\'' +
            ", serialNumber='" + serialNumber + '\'' +
            '}';
    }
}
