package com.luna.common.os;

import com.luna.common.dto.constant.ResultCode;
import com.luna.common.exception.BaseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 * @author isluna
 */
public class ProcessUtils {

    private static final SystemInfo SI = new SystemInfo();

    /**
     * 获取所有进程
     *
     * @return
     */
    public static List<OSProcess> getProcesses() {
        return SI.getOperatingSystem().getProcesses();
    }

    /**
     * 获取所有进程，用filename过滤
     *
     * @param fileName
     * @return
     */
    public static List<OSProcess> getProcessesByFileName(String fileName) {
        String fileNameWithoutExtension = removeExtension(fileName);

        List<OSProcess> osProcessList = getProcesses();

        List<OSProcess> result = osProcessList.stream()
                .filter(osProcess -> StringUtils.equals(removeExtension(osProcess.getName()), fileNameWithoutExtension))
                .collect(Collectors.toList());

        return result;
    }

    public static List<OSProcess> getProcessesByPath(String path) {
        List<OSProcess> osProcessList = getProcesses();

        List<OSProcess> result = osProcessList.stream()
                .filter(osProcess -> formatPath(osProcess.getPath()).startsWith(formatPath(path)))
                .collect(Collectors.toList());

        return result;
    }

    /**
     * 格式化路径，\替换为/
     *
     * @param path
     * @return
     */
    public static String formatPath(String path) {
        return path.replace("\\", "/");
    }

    public static String removeExtension(String filename) {
        int idx = filename.lastIndexOf(".");
        if (idx == -1) {
            return filename;
        }

        return filename.substring(0, idx);
    }

    public static void killProcessGracefully(Process process, String fileName) {
        boolean hasTerminated = false;

        // destroy
        process.destroy();
        try {
            hasTerminated = process.waitFor(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignore
        }
        if (hasTerminated) {
            return;
        }

        // destroyForcibly
        process.destroyForcibly();
        try {
            hasTerminated = process.waitFor(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignore
        }
        if (hasTerminated) {
            return;
        }

        // os kill
        osKill(fileName);
        try {
            hasTerminated = process.waitFor(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignore
        }
        if (!hasTerminated) {
            throw new BaseException(ResultCode.ERROR_SYSTEM_EXCEPTION, ResultCode.MSG_ERROR_SYSTEM_EXCEPTION);
        }
    }

    public static void osKill(String fileName) {
        // os kill
        List<OSProcess> osProcessList = getProcessesByFileName(fileName);
        osProcessList.forEach(osProcess -> {
            osKill(osProcess.getProcessID());
        });
    }

    /**
     * 操作系统级别杀进程
     *
     * @param processId
     */
    public static void osKill(int processId) {
        try {
            if (SystemUtils.IS_OS_WINDOWS) {
                Runtime.getRuntime().exec("taskkill /F /T /PID " + processId);
                return;
            }
            if (SystemUtils.IS_OS_LINUX) {
                Runtime.getRuntime().exec("kill -9 " + processId);
                return;
            }
            if (SystemUtils.IS_OS_MAC) {
                Runtime.getRuntime().exec("kill -9 " + processId);
                return;
            }
        } catch (IOException e) {
            // ignore
        }
        throw new BaseException(ResultCode.ERROR_SYSTEM_EXCEPTION, "平台不支持");
    }

    /**
     * 根据path查找进程并kill
     *
     * @param path
     */
    public static void getProcessesAndKill(String path) {
        List<OSProcess> osProcessList = getProcessesByPath(path);
        osProcessList.forEach(osProcess -> osKill(osProcess.getProcessID()));
    }
}
