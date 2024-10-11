package com.imooc.pan.core.utils;

import cn.hutool.core.date.DateUtil;
import com.imooc.pan.core.constants.GlobalConst;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

/**
 * 公用的文件工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    /**
     * 获取文件的后缀
     *
     * @param filename
     * @return
     */
    public static String getFileSuffix(String filename) {
        if (StringUtils.isBlank(filename) || filename.lastIndexOf(GlobalConst.POINT_STR) == GlobalConst.MINUS_ONE_INT) {
            return GlobalConst.EMPTY_STR;
        }
        return filename.substring(filename.lastIndexOf(GlobalConst.POINT_STR)).toLowerCase();
    }

    /**
     * 获取文件的类型
     *
     * @param filename
     * @return
     */
    public static String getFileExtName(String filename) {
        if (StringUtils.isBlank(filename) || filename.lastIndexOf(GlobalConst.POINT_STR) == GlobalConst.MINUS_ONE_INT) {
            return GlobalConst.EMPTY_STR;
        }
        return filename.substring(filename.lastIndexOf(GlobalConst.POINT_STR) + GlobalConst.ONE_INT).toLowerCase();
    }

    /**
     * 通过文件大小转化文件大小的展示名称
     *
     * @param totalSize
     * @return
     */
    public static String byteCountToDisplaySize(Long totalSize) {
        if (Objects.isNull(totalSize)) {
            return GlobalConst.EMPTY_STR;
        }
        return org.apache.commons.io.FileUtils.byteCountToDisplaySize(totalSize);
    }

    /**
     * 批量删除物理文件
     *
     * @param realFilePathList
     */
    public static void deleteFiles(List<String> realFilePathList) throws IOException {
        if (CollectionUtils.isEmpty(realFilePathList)) {
            return;
        }
        for (String realFilePath : realFilePathList) {
            org.apache.commons.io.FileUtils.forceDelete(new File(realFilePath));
        }
    }

    /**
     * 生成文件的存储路径
     * <p>
     * 生成规则：基础路径 + 年 + 月 + 日 + 随机的文件名称
     *
     * @param basePath
     * @param filename
     * @return
     */
    public static String generateStoreFileRealPath(String basePath, String filename) {
        return new StringBuffer(basePath)
                .append(File.separator)
                .append(DateUtil.thisYear())
                .append(File.separator)
                .append(DateUtil.thisMonth() + 1)
                .append(File.separator)
                .append(DateUtil.thisDayOfMonth())
                .append(File.separator)
                .append(UUIDUtil.getUUID())
                .append(getFileSuffix(filename))
                .toString();
    }

    /**
     * 将文件的输入流写入到文件中
     * 使用底层的sendfile零拷贝来提高传输效率
     *
     * @param inputStream
     * @param targetFile
     * @param totalSize
     */
    public static void writeStream2File(InputStream inputStream, File targetFile, Long totalSize) throws IOException {
        createFile(targetFile);
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(targetFile, "rw");
             FileChannel outputChannel = randomAccessFile.getChannel();
             ReadableByteChannel inputChannel = Channels.newChannel(inputStream)) {
            outputChannel.transferFrom(inputChannel, 0L, totalSize);
            inputStream.close();
        }

    }

    /**
     * 创建文件
     * 包含父文件一起视情况去创建
     *
     * @param targetFile
     */
    public static void createFile(File targetFile) throws IOException {
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        if (!Boolean.TRUE.equals(targetFile.createNewFile())) {
            throw new RuntimeException("文件创建失败");
        }
    }

    /**
     * 生成默认的文件存储路径
     * <p>
     * 生成规则：当前登录用户的文件目录 + rpan
     *
     * @return
     */
    public static String generateDefaultStoreFileRealPath() {
        return System.getProperty("user.home") + File.separator + "rpan";
    }

    /**
     * 生成默认的文件分片的存储路径前缀
     *
     * @return
     */
    public static String generateDefaultStoreFileChunkRealPath() {
        return System.getProperty("user.home") +
                File.separator + "rpan" +
                File.separator + "chunks";
    }

    /**
     * 生成文件分片的存储路径
     * <p>
     * 生成规则：基础路径 + 年 + 月 + 日 + 唯一标识 + 随机的文件名称 + __,__ + 文件分片的下标
     *
     * @param basePath
     * @param identifier
     * @param chunkNumber
     * @return
     */
    public static String generateStoreFileChunkRealPath(String basePath, String identifier, Integer chunkNumber) {
        return basePath +
                File.separator + DateUtil.thisYear() +
                File.separator + (DateUtil.thisMonth() + 1) +
                File.separator + DateUtil.thisDayOfMonth() +
                File.separator + identifier +
                File.separator + UUIDUtil.getUUID() +
                GlobalConst.COMMON_SEPARATOR + chunkNumber;
    }

    /**
     * 追加写文件
     *
     * @param target
     * @param source
     */
    public static void appendWrite(Path target, Path source) throws IOException {
        Files.write(target, Files.readAllBytes(source), StandardOpenOption.APPEND);
    }

    /**
     * 利用零拷贝技术读取文件内容并写入到文件的输出流中
     *
     * @param input
     * @param out
     * @param length
     * @throws IOException
     */
    public static void writeFile2OutputStream(FileInputStream input, OutputStream out, long length) throws IOException {
        FileChannel fileChannel = input.getChannel();
        WritableByteChannel writableByteChannel = Channels.newChannel(out);
        fileChannel.transferTo(GlobalConst.ZERO_LONG, length, writableByteChannel);
        out.flush();
        input.close();
        out.close();
        fileChannel.close();
        writableByteChannel.close();
    }

    /**
     * 普通的流对流数据传输
     *
     * @param inputStream
     * @param outputStream
     */
    public static void writeStream2StreamNormal(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != GlobalConst.MINUS_ONE_INT) {
            outputStream.write(buffer, GlobalConst.ZERO_INT, len);
        }
        outputStream.flush();
        inputStream.close();
        outputStream.close();
    }

}
