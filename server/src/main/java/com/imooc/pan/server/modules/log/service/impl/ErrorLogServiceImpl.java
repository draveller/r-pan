package com.imooc.pan.server.modules.log.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.pan.server.modules.log.entity.RPanErrorLog;
import com.imooc.pan.server.modules.log.mapper.RPanErrorLogMapper;
import com.imooc.pan.server.modules.log.service.IErrorLogService;
import org.springframework.stereotype.Service;

/**
 * @author 18063
 * @description 针对表【r_pan_error_log(错误日志表)】的数据库操作Service实现
 * @createDate 2024-09-28 14:12:17
 */
@Service
public class ErrorLogServiceImpl extends ServiceImpl<RPanErrorLogMapper, RPanErrorLog>
        implements IErrorLogService {

}




