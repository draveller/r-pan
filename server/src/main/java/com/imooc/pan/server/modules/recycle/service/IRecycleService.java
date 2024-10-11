package com.imooc.pan.server.modules.recycle.service;

import com.imooc.pan.server.modules.file.vo.RPanUserFileVO;
import com.imooc.pan.server.modules.recycle.context.DeleteContext;
import com.imooc.pan.server.modules.recycle.context.QueryRecycleFileListContext;
import com.imooc.pan.server.modules.recycle.context.RestoreContext;

import java.util.List;

public interface IRecycleService {

    /**
     * 查询用户的回收站文件列表
     */
    List<RPanUserFileVO> recycles(QueryRecycleFileListContext context);

    /**
     * 文件还原
     */
    void restore(RestoreContext context);

    /**
     * 文件彻底删除
     */
    void delete(DeleteContext context);

}
