package com.luna.common.command;

/**
 * @author luna
 * 2021/8/16
 */
public interface CommandConstant {

    String FILE_NAME            = "fileName";

    String FILE_PATH            = "filePath";

    String PROCESS_ID           = "processId";

    String ZIP                  = "zip -q -r ${fileName} ${filePath}";

    String UN_ZIP               = "unzip ${fileName} -d ${filePath}";

    String MKDIR                = "mkdir ${filePath}";

    String TOUCH                = "touch ${fileName}";

    String DELETE_PATH          = "rm -rf ${filePath}";

    /** win task kill */
    String WIN_TASK_KILL        = "taskkill /F /T /PID ${processId}";

    /** linux task kill */
    String LINUX_TASK_KILL      = "kill -9 ${processId}";

    /** win 强制删除文件文件夹和文件夹内所有文件 */
    String WIN_DELETE_DIRECTORY = "rd/s/q ${filePath}";

    /** win 强制删除文件 */
    String WIN_DELETE_FILE      = "del/f/s/q ${fileName}";
}
