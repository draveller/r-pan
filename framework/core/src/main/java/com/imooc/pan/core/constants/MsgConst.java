package com.imooc.pan.core.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MsgConst {

    public static final String FILE_DOES_NOT_EXISTS = "文件不存在";
    public static final String FILE_UNIFICATION_CANNOT_BE_EMPTY = "文件唯一标识不能为空";
    public static final String FILE_NAME_CANNOT_BE_EMPTY = "文件名称不能为空";
    public static final String FILE_SIZE_CANNOT_BE_EMPTY = "文件大小不能为空";
    public static final String FILE_PATHS_CANNOT_BE_EMPTY = "文件路径列表不能为空";
    public static final String FILE_REAL_PATH_CANNOT_BE_EMPTY = "文件真实路径不能为空";
    public static final String FILE_INPUT_STREAM_CANNOT_BE_EMPTY = "文件输入流不能为空";
    public static final String FILE_OUTPUT_STREAM_CANNOT_BE_EMPTY = "文件输出流不能为空";
    public static final String FILE_CHUNK_PATHS_CANNOT_BE_EMPTY = "文件分片路径列表不能为空";
    public static final String FILE_TOTAL_CHUNKS_CANNOT_BE_EMPTY = "文件分片总数不能为空";
    public static final String FILE_CHUNK_NUMBER_CANNOT_BE_EMPTY = "文件分片序号不能为空";
    public static final String FILE_CHUNK_SIZE_CANNOT_BE_EMPTY = "文件分片大小不能为空";
    public static final String FILE_CHUNK_UPLOAD_FAILED = "文件分片上传失败";
    public static final String FILE_DOWNLOAD_FAILED = "文件下载失败";
    public static final String FOLDERS_DOWNLOADING_UNSUPPORTED = "文件夹暂不支持下载";

    public static final String USER_DOES_NOT_EXISTS = "用户不存在";
    public static final String USER_ID_CANNOT_BE_EMPTY = "用户ID不能为空";

    public static final String CACHE_NOT_FOUND = "找不到缓存";
}
