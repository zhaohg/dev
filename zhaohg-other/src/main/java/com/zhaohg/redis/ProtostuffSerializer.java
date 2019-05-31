package com.zhaohg.redis;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import java.util.concurrent.ConcurrentHashMap;

/**
 * protostuff序列化工具
 * @author zhaohg
 * @Date 2015-7-22
 * @Time 上午10:05:20
 */
public class ProtostuffSerializer {
    private static ConcurrentHashMap<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();

    private static <T> Schema<T> getSchema(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(clazz);
            cachedSchema.put(clazz, schema);
        }
        return schema;
    }

    public <T> byte[] serialize(final T source) {
        VO<T> vo = new VO<T>(source);
        final LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            final Schema<VO> schema = getSchema(VO.class);
            return serializeInternal(vo, schema, buffer);
        } catch (final Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    public <T> T deserialize(final byte[] bytes) {
        try {
            Schema<VO> schema = getSchema(VO.class);
            VO vo = deserializeInternal(bytes, schema.newMessage(), schema);
            if (vo != null && vo.getValue() != null) {
                return (T) vo.getValue();
            }
        } catch (final Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return null;
    }

    private <T> byte[] serializeInternal(final T source, final Schema<T> schema, final LinkedBuffer buffer) {
        return ProtostuffIOUtil.toByteArray(source, schema, buffer);
    }

    private <T> T deserializeInternal(final byte[] bytes, final T result, final Schema<T> schema) {
        ProtostuffIOUtil.mergeFrom(bytes, result, schema);
        return result;
    }
}