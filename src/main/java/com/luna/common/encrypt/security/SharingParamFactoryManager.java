package com.luna.common.encrypt.security;

import java.util.Arrays;

/**
 * @author luna
 */
public class SharingParamFactoryManager {

    public static SharingParamManager build(String... param) {
        return new SharingParamManager(new SecurityManager(), SharingParamManager.CURRENT_DEFAULT_VERSION, param);
    }

    public static SharingParamManager buildEmpty() {
        return new SharingParamManager(new SecurityManager(), SharingParamManager.CURRENT_DEFAULT_VERSION, null);
    }

    public static SharingParamManager build(SecurityManager securityManager, String version, String... param) {
        return new SharingParamManager(securityManager, version, param);
    }

    public static SharingParamManager buildEmpty(SecurityManager securityManager) {
        return new SharingParamManager(securityManager, null, null);
    }
}
