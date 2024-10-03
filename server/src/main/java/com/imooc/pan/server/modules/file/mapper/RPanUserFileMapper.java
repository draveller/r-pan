package com.imooc.pan.server.modules.file.mapper;

import com.imooc.pan.server.modules.file.context.FileSearchContext;
import com.imooc.pan.server.modules.file.context.QueryFileListContext;
import com.imooc.pan.server.modules.file.entity.RPanUserFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imooc.pan.server.modules.file.vo.FileSearchResultVO;
import com.imooc.pan.server.modules.file.vo.RPanUserFileVO;

import java.util.List;

/**
* @author 18063
* @description 针对表【r_pan_user_file(用户文件信息表)】的数据库操作Mapper
* @createDate 2024-09-28 14:11:44
* @Entity com.imooc.pan.server.modules.file.entity.RPanUserFile
*/
public interface RPanUserFileMapper extends BaseMapper<RPanUserFile> {

    List<RPanUserFileVO> selectFileList(QueryFileListContext context);

    List<FileSearchResultVO> searchFile(FileSearchContext context);

}




