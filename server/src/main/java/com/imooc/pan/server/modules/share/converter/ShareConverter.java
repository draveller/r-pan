package com.imooc.pan.server.modules.share.converter;

import com.imooc.pan.core.constants.GlobalConst;
import com.imooc.pan.core.utils.IdUtil;
import com.imooc.pan.server.common.utils.UserIdUtil;
import com.imooc.pan.server.modules.share.context.CreateShareUrlContext;
import com.imooc.pan.server.modules.share.po.CreateShareUrlPO;
import org.mapstruct.Mapper;

import java.util.Arrays;
import java.util.List;

/**
 * 分享模块实体转化工具类
 */
@Mapper(componentModel = "spring")
public interface ShareConverter {

    default CreateShareUrlContext convertPO2Context(CreateShareUrlPO po) {
        CreateShareUrlContext context = new CreateShareUrlContext();
        context.setShareName(po.getShareName());
        context.setShareType(po.getShareType());
        context.setShareDayType(po.getShareDayType());

        List<Long> shareFileIds = Arrays.stream(po.getShareFileIds().split(GlobalConst.COMMON_SEPARATOR))
                .map(IdUtil::decrypt).toList();
        context.setShareFileIdList(shareFileIds);
        context.setUserId(UserIdUtil.get());
        return context;
    }

}
