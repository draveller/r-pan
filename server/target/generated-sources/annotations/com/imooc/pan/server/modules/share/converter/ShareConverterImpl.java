package com.imooc.pan.server.modules.share.converter;

import com.imooc.pan.server.modules.share.context.CreateShareUrlContext;
import com.imooc.pan.server.modules.share.po.CreateShareUrlPO;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-06T20:55:14+0800",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 1.8.0_151 (Oracle Corporation)"
)
@Component
public class ShareConverterImpl implements ShareConverter {

    @Override
    public CreateShareUrlContext convertPO2Context(CreateShareUrlPO createShareUrlPO) {
        if ( createShareUrlPO == null ) {
            return null;
        }

        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();

        createShareUrlContext.setShareName( createShareUrlPO.getShareName() );
        createShareUrlContext.setShareType( createShareUrlPO.getShareType() );
        createShareUrlContext.setShareDayType( createShareUrlPO.getShareDayType() );

        createShareUrlContext.setUserId( com.imooc.pan.server.common.utils.UserIdUtil.get() );

        return createShareUrlContext;
    }
}
