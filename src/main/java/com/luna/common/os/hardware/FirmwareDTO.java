package com.luna.common.os.hardware;

/**
 * @author Tony
 */
public class FirmwareDTO {
    private String manufacturer;
    private String name;
    private String description;
    private String version;

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "FirmwareDTO{" +
            "manufacturer='" + manufacturer + '\'' +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", version='" + version + '\'' +
            '}';
    }
}
