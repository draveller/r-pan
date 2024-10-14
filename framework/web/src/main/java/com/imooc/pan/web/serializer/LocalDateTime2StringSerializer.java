package com.imooc.pan.web.serializer;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Date转String的JSON序列化器
 * 用于返回实体Date类型字段的自动序列化
 */
public class LocalDateTime2StringSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime ldt, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (ldt == null) {
            jsonGenerator.writeString(StrUtil.EMPTY);
        } else {
            jsonGenerator.writeString(ldt.toString());
        }
    }
}
