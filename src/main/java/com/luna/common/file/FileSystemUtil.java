package com.luna.common.file;


import com.google.common.collect.ImmutableMap;
import com.luna.common.constant.StrPoolConstant;
import com.luna.common.text.CharsetUtil;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * {@link FileSystem}相关工具类封装<br>
 * 参考：https://blog.csdn.net/j16421881/article/details/78858690
 *
 * @author looly
 * @since 5.7.15
 */
public class FileSystemUtil {

	/**
	 * 创建 {@link FileSystem}
	 *
	 * @param path 文件路径，可以是目录或Zip文件等
	 * @return {@link FileSystem}
	 */
	public static FileSystem create(String path) {
		try {
			return FileSystems.newFileSystem(
					Paths.get(path).toUri(),
					ImmutableMap.of("create", "true"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 创建 Zip的{@link FileSystem}，默认UTF-8编码
	 *
	 * @param path 文件路径，可以是目录或Zip文件等
	 * @return {@link FileSystem}
	 */
	public static FileSystem createZip(String path) {
		return createZip(path, null);
	}

	/**
	 * 创建 Zip的{@link FileSystem}
	 *
	 * @param path 文件路径，可以是目录或Zip文件等
	 * @param charset 编码
	 * @return {@link FileSystem}
	 */
	public static FileSystem createZip(String path, Charset charset) {
		if(null == charset){
			charset = CharsetUtil.CHARSET_UTF_8;
		}
		final HashMap<String, String> env = new HashMap<>();
		env.put("create", "true");
		env.put("encoding", charset.name());

		try {
			return FileSystems.newFileSystem(
					URI.create("jar:" + Paths.get(path).toUri()), env);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取目录的根路径，或Zip文件中的根路径
	 *
	 * @param fileSystem {@link FileSystem}
	 * @return 根 {@link Path}
	 */
	public static Path getRoot(FileSystem fileSystem) {
		return fileSystem.getPath(StrPoolConstant.SLASH);
	}
}
