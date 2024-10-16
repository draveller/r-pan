package com.imooc.pan.web.serializer;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.imooc.pan.core.utils.EntityIdUtil;

import java.io.IOException;
import java.util.Objects;

/**
 * Id自动加密的JSON序列化器
 * 用于返回实体Long类型ID字段的自动序列化
 */
public class IdEncryptSerializer extends JsonSerializer<Long> {
    /**
     * Method that can be called to ask implementation to serialize
     * values of type this serializer handles.
     *
     * @param value       Value to serialize; can <b>not</b> be null.
     * @param gen         Generator used to output resulting Json content
     * @param serializers Provider that can be used to get serializers for
     *                    serializing Objects value contains, if any.
     */
    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (Objects.isNull(value)) {
            gen.writeString(StrUtil.EMPTY);
        } else {
            gen.writeString(EntityIdUtil.encrypt(value));
        }
    }

}
