package com.luna.common.swing;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import com.luna.common.file.FileTools;
import org.apache.commons.lang3.ArrayUtils;

import com.luna.common.exception.UtilException;
import com.luna.common.img.ImgUtil;

/**
 * {@link Robot} 封装工具类，提供截屏等工具
 *
 * @author looly
 * @since 4.1.14
 */
public class RobotUtil {

    private static Robot ROBOT;
    private static int   delay;

    static {
        try {
            ROBOT = new Robot();
        } catch (AWTException e) {
            throw new UtilException(e);
        }
    }

    public static void main(String[] args) {
        captureScreen(0, 0, 200, 300, FileTools.touch("/Users/weidian/Downloads/2.jpg"));
    }

    /**
     * 获取 Robot 单例实例
     *
     * @return {@link Robot}单例对象
     * @since 5.7.6
     */
    public static Robot getRobot() {
        return ROBOT;
    }

    /**
     * 获取全局默认的延迟时间
     *
     * @return 全局默认的延迟时间
     * @since 5.7.6
     */
    public static int getDelay() {
        return delay;
    }

    /**
     * 设置默认的延迟时间<br>
     * 当按键执行完后的等待时间，也可以用ThreadUtil.sleep方法代替
     *
     * @param delayMillis 等待毫秒数
     * @since 4.5.7
     */
    public static void setDelay(int delayMillis) {
        delay = delayMillis;
    }

    /**
     * 模拟鼠标移动
     *
     * @param x 移动到的x坐标
     * @param y 移动到的y坐标
     * @since 4.5.7
     */
    public static void mouseMove(int x, int y) {
        ROBOT.mouseMove(x, y);
    }

    /**
     * 模拟单击<br>
     * 鼠标单击包括鼠标左键的按下和释放
     *
     * @since 4.5.7
     */
    public static void click() {
        ROBOT.mousePress(InputEvent.BUTTON1_MASK);
        ROBOT.mouseRelease(InputEvent.BUTTON1_MASK);
        delay();
    }

    /**
     * 模拟右键单击<br>
     * 鼠标单击包括鼠标右键的按下和释放
     *
     * @since 4.5.7
     */
    public static void rightClick() {
        ROBOT.mousePress(InputEvent.BUTTON3_MASK);
        ROBOT.mouseRelease(InputEvent.BUTTON3_MASK);
        delay();
    }

    /**
     * 模拟鼠标滚轮滚动
     *
     * @param wheelAmt 滚动数，负数表示向前滚动，正数向后滚动
     * @since 4.5.7
     */
    public static void mouseWheel(int wheelAmt) {
        ROBOT.mouseWheel(wheelAmt);
        delay();
    }

    /**
     * 模拟键盘点击<br>
     * 包括键盘的按下和释放
     *
     * @param keyCodes 按键码列表，见{@link KeyEvent}
     * @since 4.5.7
     */
    public static void keyClick(int... keyCodes) {
        for (int keyCode : keyCodes) {
            ROBOT.keyPress(keyCode);
            ROBOT.keyRelease(keyCode);
        }
        delay();
    }

    /**
     * 打印输出指定字符串（借助剪贴板）
     *
     * @param str 字符串
     */
    public static void keyPressString(String str) {
        ClipboardUtil.setStr(str);
        keyPressWithCtrl(KeyEvent.VK_V);
        // 粘贴
        delay();
    }

    /**
     * shift+ 按键
     *
     * @param key 按键
     */
    public static void keyPressWithShift(int key) {
        ROBOT.keyPress(KeyEvent.VK_SHIFT);
        ROBOT.keyPress(key);
        ROBOT.keyRelease(key);
        ROBOT.keyRelease(KeyEvent.VK_SHIFT);
        delay();
    }

    /**
     * ctrl+ 按键
     *
     * @param key 按键
     */
    public static void keyPressWithCtrl(int key) {
        ROBOT.keyPress(KeyEvent.VK_CONTROL);
        ROBOT.keyPress(key);
        ROBOT.keyRelease(key);
        ROBOT.keyRelease(KeyEvent.VK_CONTROL);
        delay();
    }

    /**
     * alt+ 按键
     *
     * @param key 按键
     */
    public static void keyPressWithAlt(int key) {
        ROBOT.keyPress(KeyEvent.VK_ALT);
        ROBOT.keyPress(key);
        ROBOT.keyRelease(key);
        ROBOT.keyRelease(KeyEvent.VK_ALT);
        delay();
    }

    /**
     * 截取全屏
     *
     * @return 截屏的图片
     */
    public static BufferedImage captureScreen() {
        return captureScreen(ScreenUtil.getRectangle());
    }

    /**
     * 截取全屏到文件
     *
     * @param outFile 写出到的文件
     * @return 写出到的文件
     */
    public static File captureScreen(File outFile) {
        ImgUtil.write(captureScreen(), outFile);
        return outFile;
    }

    /**
     * 截屏
     *
     * @param screenRect 截屏的矩形区域
     * @return 截屏的图片
     */
    public static BufferedImage captureScreen(Rectangle screenRect) {
        return ROBOT.createScreenCapture(screenRect);
    }

    public static Robot getRobot(GraphicsDevice graphicsDevice) {
        try {
            return new Robot(graphicsDevice);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对指定设备截屏
     *
     * @param robot
     * @param screenRect
     * @return
     */
    public static BufferedImage captureScreen(Robot robot, Rectangle screenRect) {
        return robot.createScreenCapture(screenRect);
    }

    /**
     * 截屏
     *
     * @param screenRect 截屏的矩形区域
     * @param outFile 写出到的文件
     * @return 写出到的文件
     */
    public static File captureScreen(Rectangle screenRect, File outFile) {
        ImgUtil.write(captureScreen(screenRect), outFile);
        return outFile;
    }

    /**
     * 截屏
     *
     * 截屏的矩形区域
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param outFile 写出到的文件
     * @return 写出到的文件
     *
     */
    public static File captureScreen(int x1, int y1, int x2, int y2, File outFile) {
        Rectangle rectangle = new Rectangle(x1, y1, x2 - x1, y2 - y1);
        ImgUtil.write(captureScreen(rectangle), outFile);
        return outFile;
    }

    /**
     * 等待指定毫秒数
     */
    public static void delay() {
        if (delay > 0) {
            ROBOT.delay(delay);
        }
    }

    public static Robot getRobot(Integer i) {
        GraphicsDevice screenDevice = getScreenDevice(i);
        return getRobot(screenDevice);
    }

    public static GraphicsDevice getScreenDevice(Integer i) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screenDevices = ge.getScreenDevices();
        if (ArrayUtils.isEmpty(screenDevices)) {
            throw new RuntimeException("没有找到可用的屏幕设备");
        }
        return screenDevices[i];
    }
}
