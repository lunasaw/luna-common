package com.luna.common.file.visitor;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import com.luna.common.file.PathUtil;

/**
 * 文件拷贝的FileVisitor实现，用于递归遍历拷贝目录，此类非线程安全<br>
 * 此类在遍历源目录并复制过程中会自动创建目标目录中不存在的上级目录。
 *
 * @author looly
 * @since 5.5.1
 */
public class CopyVisitor extends SimpleFileVisitor<Path> {

    private final Path         source;
    private final Path         target;
    private final CopyOption[] copyOptions;
    private boolean            isTargetCreated;

    /**
     * 构造
     *
     * @param source 源Path
     * @param target 目标Path
     * @param copyOptions 拷贝选项，如跳过已存在等
     */
    public CopyVisitor(Path source, Path target, CopyOption... copyOptions) {
        if (PathUtil.exists(target, false) && !PathUtil.isDirectory(target)) {
            throw new IllegalArgumentException("Target must be a directory");
        }
        this.source = source;
        this.target = target;
        this.copyOptions = copyOptions;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
        throws IOException {
        initTarget();
        // 将当前目录相对于源路径转换为相对于目标路径
        final Path targetDir = target.resolve(source.relativize(dir));
        try {
            Files.copy(dir, targetDir, copyOptions);
        } catch (FileAlreadyExistsException e) {
            if (!Files.isDirectory(targetDir)) {
                throw e;
            }
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
        throws IOException {
        initTarget();
        Files.copy(file, target.resolve(source.relativize(file)), copyOptions);
        return FileVisitResult.CONTINUE;
    }

    /**
     * 初始化目标文件或目录
     */
    private void initTarget() {
        if (!this.isTargetCreated) {
            PathUtil.mkdir(this.target);
            this.isTargetCreated = true;
        }
    }
}
