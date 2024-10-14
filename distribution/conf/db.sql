SET NAMES utf8mb4;

SET FOREIGN_KEY_CHECKS = 0;


-- ----------------------------
-- Table structure for r_pan_user
-- ----------------------------
DROP TABLE IF EXISTS `r_pan_user`;
CREATE TABLE `r_pan_user`
(
    `id`          BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    `username`    VARCHAR(255)                   NOT NULL COMMENT '用户名',
    `password`    VARCHAR(255)                   NOT NULL COMMENT '密码',
    `salt`        VARCHAR(255)                   NOT NULL COMMENT '随机盐值',
    `question`    VARCHAR(255)                   NOT NULL DEFAULT '' COMMENT '密保问题',
    `answer`      VARCHAR(255)                   NOT NULL DEFAULT '' COMMENT '密保答案',
    `create_time` DATETIME                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_username` (`username`) COMMENT '用户名必须唯一'
) COMMENT = '用户信息表';


-- ----------------------------
-- Table structure for r_pan_third_party_auth
-- ----------------------------
DROP TABLE IF EXISTS `r_pan_third_party_auth`;
CREATE TABLE `r_pan_third_party_auth`
(
    `id`           BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    `user_id`      BIGINT UNSIGNED                NOT NULL COMMENT '关联的用户ID',
    `provider`     VARCHAR(100)                   NOT NULL COMMENT 'OAuth 提供商名称 (如 github, google)',
    `provider_uid` VARCHAR(255)                   NOT NULL COMMENT '提供商用户唯一标识符',
    `create_time`  DATETIME                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_provider_provider_uid` (`provider`, `provider_uid`) COMMENT '提供商和其用户ID的组合必须唯一'
) COMMENT = '第三方授权登录表';


-- ----------------------------
-- Table structure for r_pan_user_file
-- ----------------------------
DROP TABLE IF EXISTS `r_pan_user_file`;
CREATE TABLE `r_pan_user_file`
(
    `id`             BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    `user_id`        BIGINT UNSIGNED                NOT NULL COMMENT '用户ID',
    `parent_id`      BIGINT UNSIGNED                NOT NULL COMMENT '上级文件夹ID, 顶级文件夹为0',
    `real_file_id`   BIGINT UNSIGNED                NOT NULL COMMENT '真实文件ID',
    `filename`       VARCHAR(255)                   NOT NULL COMMENT '文件名',
    `file_size_desc` VARCHAR(255)                   NOT NULL DEFAULT '--' COMMENT '文件大小展示字符',
    `file_type`      TINYINT(1) UNSIGNED            NOT NULL DEFAULT 0 COMMENT '文件类型, 1-普通文件; 2-压缩文件; 3-excel; 4-word; 5-pdf; 6-txt; 7-图片; 8-音频; 9-视频; 10-ppt; 11-源码文件; 12-csv',
    `folder_flag`    TINYINT(1) UNSIGNED            NOT NULL COMMENT '是否为文件夹, 0-否; 1-是',
    `del_flag`       TINYINT(1) UNSIGNED            NOT NULL DEFAULT 0 COMMENT '逻辑删除标识, 0-未删; 1-已删',
    `create_user`    BIGINT UNSIGNED                NOT NULL COMMENT '创建人ID',
    `update_user`    BIGINT UNSIGNED                NOT NULL COMMENT '更新人ID',
    `create_time`    DATETIME                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) COMMENT ='用户文件信息表';


-- ----------------------------
-- Table structure for r_pan_file
-- ----------------------------
DROP TABLE IF EXISTS `r_pan_file`;
CREATE TABLE `r_pan_file`
(
    `id`                        BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    `filename`                  VARCHAR(255)                   NOT NULL COMMENT '文件名称',
    `real_path`                 VARCHAR(800)                   NOT NULL COMMENT '文件物理路径',
    `file_size`                 VARCHAR(255)                   NOT NULL COMMENT '文件实际大小',
    `file_size_desc`            VARCHAR(255)                   NOT NULL DEFAULT '' COMMENT '文件大小展示字符',
    `file_suffix`               VARCHAR(255)                   NOT NULL DEFAULT '' COMMENT '文件后缀',
    `file_preview_content_type` VARCHAR(255)                   NOT NULL DEFAULT '' COMMENT '文件预览的响应头Content-Type的值',
    `identifier`                VARCHAR(255)                   NOT NULL DEFAULT '' COMMENT '文件唯一标识',
    `create_user`               BIGINT UNSIGNED                NOT NULL COMMENT '创建人ID',
    `create_time`               DATETIME                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`               DATETIME                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) COMMENT ='物理文件信息表';


-- ----------------------------
-- Table structure for r_pan_file_chunk
-- ----------------------------
DROP TABLE IF EXISTS `r_pan_file_chunk`;
CREATE TABLE `r_pan_file_chunk`
(
    `id`              BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    `identifier`      VARCHAR(255)                   NOT NULL COMMENT '文件唯一标识',
    `real_path`       VARCHAR(800)                   NOT NULL COMMENT '分片真实的存储路径',
    `chunk_number`    INT UNSIGNED                   NOT NULL COMMENT '分片编号',
    `expiration_time` DATETIME                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '过期时间',
    `create_user`     BIGINT UNSIGNED                NOT NULL COMMENT '创建人',
    `create_time`     DATETIME                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) COMMENT ='文件分片信息表';


-- ----------------------------
-- Table structure for r_pan_share
-- ----------------------------
DROP TABLE IF EXISTS `r_pan_share`;
CREATE TABLE `r_pan_share`
(
    `id`             BIGINT              NOT NULL AUTO_INCREMENT COMMENT '主键',
    `share_name`     VARCHAR(255)        NOT NULL COMMENT '分享名称',
    `share_type`     TINYINT(1)          NOT NULL DEFAULT 0 COMMENT '分享类型（0 有提取码）',
    `share_day_type` TINYINT(1)          NOT NULL DEFAULT 0 COMMENT '分享类型（0 永久有效；1 7天有效；2 30天有效）',
    `share_day`      INT                 NOT NULL DEFAULT 0 COMMENT '分享有效天数',
    `share_end_time` DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分享结束时间',
    `share_url`      VARCHAR(255)        NOT NULL DEFAULT '' COMMENT '分享链接地址',
    `share_code`     VARCHAR(255)        NOT NULL DEFAULT '' COMMENT '分享提取码',
    `share_status`   TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '分享状态（0 正常；1 有文件被删除）',
    `create_user`    BIGINT              NOT NULL COMMENT '分享创建人',
    `create_time`    DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_create_user_time` (`create_user`, `create_time`) USING BTREE COMMENT '创建人、创建时间唯一索引'
) COMMENT = '用户分享表';

-- ----------------------------
-- Table structure for r_pan_share_file
-- ----------------------------
DROP TABLE IF EXISTS `r_pan_share_file`;
CREATE TABLE `r_pan_share_file`
(
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `share_id`    BIGINT   NOT NULL COMMENT '分享id',
    `file_id`     BIGINT   NOT NULL COMMENT '文件记录ID',
    `create_user` BIGINT   NOT NULL COMMENT '分享创建人',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_share_id_file_id` (`share_id`, `file_id`) USING BTREE COMMENT '分享ID、文件ID联合唯一索引'
) COMMENT = '用户分享文件表';


-- ----------------------------
-- Table structure for r_pan_user_search_history
-- ----------------------------
DROP TABLE IF EXISTS `r_pan_user_search_history`;
CREATE TABLE `r_pan_user_search_history`
(
    `id`             BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    `user_id`        BIGINT UNSIGNED                NOT NULL COMMENT '用户ID',
    `search_content` VARCHAR(255)                   NOT NULL COMMENT '搜索文案',
    `create_time`    DATETIME                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_user_id_search_content` (`user_id`, `search_content`) COMMENT '用户id和搜索内容唯一索引'
) COMMENT = '用户搜索历史表';



-- ----------------------------
-- Table structure for r_pan_error_log
-- ----------------------------
DROP TABLE IF EXISTS `r_pan_error_log`;
CREATE TABLE `r_pan_error_log`
(
    `id`          BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    `log_content` TEXT                           NOT NULL COMMENT '日志内容',
    `log_status`  INT                            NOT NULL DEFAULT 0 COMMENT '日志状态, 0-未处理; 1-已处理',
    `create_user` BIGINT                         NOT NULL COMMENT '创建人',
    `update_user` BIGINT                         NOT NULL COMMENT '更新人',
    `create_time` DATETIME                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) COMMENT ='错误日志表';


SET FOREIGN_KEY_CHECKS = 1;