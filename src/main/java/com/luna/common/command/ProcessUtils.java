package com.luna.common.command;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.google.common.collect.ImmutableMap;
import com.luna.common.constant.StrPoolConstant;
import com.luna.common.dto.constant.ResultCode;
import com.luna.common.exception.BaseException;
import com.luna.common.os.SystemInfoUtil;

import oshi.software.os.OSProcess;

/**
 * @author luna
 */
public class ProcessUtils {

    /** 替换${} */
    private static final String PARAM_PHONE_REGEX = "\\$\\{\\w+\\}";

    /**
     * 执行多行命令
     * 
     * @param commands
     */
    public static void runCommand(List<String> commands) {
        commands.forEach(ProcessUtils::runCommand);
    }

    /**
     * 命令行构建
     * 
     * @param template 命令模版
     * @param params 参数<k,v>
     * @return
     */
    public static String processBuild(String template, Map<String, Object> params) {
        Matcher m = Pattern.compile(PARAM_PHONE_REGEX).matcher(template);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String param = m.group();
            Object value = params.get(param.substring(2, param.length() - 1));
            m.appendReplacement(sb, Objects.isNull(value) ? StringUtils.EMPTY : value.toString());
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * mkdir 命令创建文件
     * 
     * @param filePath
     * @return 命令输出
     */
    public static String mkdir(String filePath) {
        return runCommand(processBuild(CommandConstant.MKDIR, ImmutableMap.of(CommandConstant.FILE_PATH, filePath)));
    }

    /**
     * touch 创建文件
     * 
     * @param fileName
     * @return 命令输出
     */
    public static String touch(String fileName) {
        return runCommand(processBuild(CommandConstant.TOUCH, ImmutableMap.of(CommandConstant.FILE_NAME, fileName)));
    }

    /**
     * rm -rf 删除路径
     * 
     * @param filePath
     * @return
     */
    public static String delete(String filePath) {
        return runCommand(
            processBuild(CommandConstant.DELETE_PATH, ImmutableMap.of(CommandConstant.FILE_PATH, filePath)));
    }

    /**
     * zip 压缩文件夹
     * 
     * @param fileName xxx.zip
     * @param filePath 添加文件夹
     * @return 命令输出
     */
    public static String zip(String fileName, String filePath) {
        return runCommand(
            processBuild(CommandConstant.ZIP,
                ImmutableMap.of(CommandConstant.FILE_NAME, fileName, CommandConstant.FILE_PATH, filePath)));
    }

    /**
     * unzip 解压文件
     * 
     * @param fileName xxx.zip 待解压文件
     * @param filePath 解压存放路径
     * @return 命令输出
     */
    public static String unzip(String fileName, String filePath) {
        return runCommand(
            processBuild(CommandConstant.UN_ZIP,
                ImmutableMap.of(CommandConstant.FILE_NAME, fileName, CommandConstant.FILE_PATH, filePath)));
    }

    /**
     * win 强制删除文件夹
     *
     * @param filePath 文件夹路径
     * @return 命令输出
     */
    public static String winDelDir(String filePath) {
        return runCommand(
            processBuild(CommandConstant.WIN_DELETE_DIRECTORY,
                ImmutableMap.of(CommandConstant.FILE_PATH, filePath)));
    }

    /**
     * win 强制删除文件
     *
     * @param fileName 文件路径
     * @return 命令输出
     */
    public static String winDelFile(String fileName) {
        return runCommand(
            processBuild(CommandConstant.WIN_DELETE_FILE,
                ImmutableMap.of(CommandConstant.FILE_PATH, fileName)));
    }

    /**
     * 执行命令行
     * 
     * @param command 命令行
     * @return 包含 \n 的命令输出字符串
     */
    public static String runCommand(String command) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            Process process = Runtime.getRuntime().exec(command);
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            while (true) {
                String line = input.readLine();
                if (StringUtils.isBlank(line)) {
                    break;
                }
                stringBuilder.append(line).append(StrPoolConstant.LF);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            throw new BaseException(ResultCode.ERROR_SYSTEM_EXCEPTION, "命令行执行异常");
        }
    }

    /**
     * 获取所有进程，用filename过滤
     * 
     * @param fileName 文件名
     * @return 获取所有进程List
     */
    public static List<OSProcess> getProcessesByFileName(String fileName) {
        String fileNameWithoutExtension = removeExtension(fileName);

        List<OSProcess> osProcessList = SystemInfoUtil.getProcesses();

        return osProcessList.stream()
            .filter(osProcess -> StringUtils.equals(removeExtension(osProcess.getName()), fileNameWithoutExtension))
            .collect(Collectors.toList());
    }

    public static List<OSProcess> getProcessesByPath(String path) {
        List<OSProcess> osProcessList = SystemInfoUtil.getProcesses();

        return osProcessList.stream()
            .filter(osProcess -> formatPath(osProcess.getPath()).startsWith(formatPath(path)))
            .collect(Collectors.toList());
    }

    /**
     * 格式化路径，\替换为/
     * 
     * @param path
     * @return 替换后的路径
     */
    public static String formatPath(String path) {
        return path.replace(StrPoolConstant.BACKSLASH, StrPoolConstant.SLASH);
    }

    public static String removeExtension(String filename) {
        int idx = filename.lastIndexOf(StrPoolConstant.DOT);
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
        osProcessList.forEach(osProcess -> osKill(osProcess.getProcessID()));
    }

    /**
     * 操作系统级别杀进程
     * 
     * @param processId 进程ID
     */
    public static void osKill(int processId) {
        if (SystemUtils.IS_OS_WINDOWS) {
            runCommand(
                processBuild(CommandConstant.WIN_TASK_KILL,
                    ImmutableMap.of(CommandConstant.PROCESS_ID, processId)));
            return;
        }
        if (SystemUtils.IS_OS_LINUX) {
            runCommand(
                processBuild(CommandConstant.LINUX_TASK_KILL,
                    ImmutableMap.of(CommandConstant.PROCESS_ID, processId)));
            return;
        }
        if (SystemUtils.IS_OS_MAC_OSX) {
            runCommand(
                processBuild(CommandConstant.LINUX_TASK_KILL,
                    ImmutableMap.of(CommandConstant.PROCESS_ID, processId)));
        }
    }

    /**
     * 根据path查找进程并kill
     * 
     * @param path 进程路径
     */
    public static void getProcessesAndKill(String path) {
        List<OSProcess> osProcessList = getProcessesByPath(path);
        osProcessList.forEach(osProcess -> osKill(osProcess.getProcessID()));
    }
}
