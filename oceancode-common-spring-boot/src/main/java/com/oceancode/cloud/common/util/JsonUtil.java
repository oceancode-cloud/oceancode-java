/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import com.oceancode.cloud.api.Result;
import com.oceancode.cloud.api.TypeEnum;
import com.oceancode.cloud.common.entity.ResultData;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * <p>
 * json utils
 * </p>
 *
 * @author Dynamic Gen
 * @since 1.0
 */
public final class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final static TypeEnumDeserializer typeEnumDeserializer = new TypeEnumDeserializer();
    private final static TypeEnumSerializer typeEnumSerializer = new TypeEnumSerializer();

    private JsonUtil() {
    }

    static {
        // 反序列化：JSON字段中有Java对象中没有的字段时不报错
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 序列化：序列化BigDecimal时不使用科学计数法输出
        OBJECT_MAPPER.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        // 序列化：Java对象为空的字段不拼接JSON
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, false);
    }

    public static <T extends TypeEnum<?>> void registerTypeEnum(Class<T> classType) {
        registerTypeEnum(classType, typeEnumSerializer, typeEnumDeserializer);
    }

    public static JavaType resolveType(Type type, Class<?> contextClass) {
        TypeFactory factory = OBJECT_MAPPER.getTypeFactory();
        if (contextClass != null && type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] argTypes = parameterizedType.getActualTypeArguments();
            if (contextClass.isArray()) {
                return factory.constructArrayType(factory.constructType(type));
            } else if (Collection.class.isAssignableFrom(contextClass)) {
                return factory.constructCollectionType((Class<? extends Collection<?>>) contextClass,
                        factory.constructType(argTypes[0]));
            } else if (Map.class.isAssignableFrom(contextClass)) {
                return factory.constructMapType((Class<? extends Map<?, ?>>) contextClass,
                        factory.constructType(argTypes[0]),
                        factory.constructType(argTypes[1]));
            }
        }
        return factory.constructType(type);

    }

    private static void registerTypeEnum(Class typeClass, TypeEnumSerializer typeEnumSerializer, TypeEnumDeserializer typeEnumDeserializer) {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(typeClass, typeEnumSerializer);
        simpleModule.addDeserializer(typeClass, typeEnumDeserializer);
        OBJECT_MAPPER.registerModule(simpleModule);
    }

    public static String toJson(Object value) {
        if (Objects.isNull(value)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR, "toJson failed", e);
        }
    }

    public static <T> T toBean(String jsonString, JavaType type) {
        if (!StringUtils.hasText(jsonString)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(jsonString, type);
        } catch (JsonProcessingException e) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR, e);
        }
    }

    public static <T> T toBean(String jsonString, Class<T> clazz) {
        if (!StringUtils.hasText(jsonString)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR, e);
        }
    }

    public static <T> List<T> toList(String jsonString, Class<T> clazz) {
        if (!StringUtils.hasText(jsonString)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(jsonString, resolveType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR, e);
        }
    }

    private static class TypeEnumSerializer extends JsonSerializer<TypeEnum> {

        @Override
        public void serialize(TypeEnum value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeObject(value.getValue());
        }
    }

    public static class TypeEnumDeserializer extends JsonDeserializer<TypeEnum> {

        @Override
        public TypeEnum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            Class typeClass = BeanUtils.findPropertyType(jsonParser.currentName(), jsonParser.currentValue().getClass());
            String valueClass = getValueType(typeClass);
            Object value = TypeUtil.convertToBySimpleName(valueClass, node.asText());
            return TypeEnum.from(value, typeClass);
        }
    }

    private static String getValueType(Class<TypeEnum<?>> typeClass) {
        return TypeEnum.getValueTypeName(typeClass);
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    public static <T> T mapToBean(Map map, Class<T> typeClass) {
        return OBJECT_MAPPER.convertValue(map, typeClass);
    }

    public static <T, R extends Result<?>> Result<T> withType(String jsonString, Class<R> resultTypeClass, Class<T> resultDataClass) {
        TypeFactory typeFactory = OBJECT_MAPPER.getTypeFactory();
        JavaType javaType = typeFactory.constructParametricType(resultTypeClass, resultDataClass);
        try {
            return OBJECT_MAPPER.readValue(jsonString, javaType);
        } catch (JsonProcessingException e) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR, e);
        }
    }

    public static <T> Result<T> withType(String jsonString, Class<T> resultDataType) {
        return withType(jsonString, ResultData.class, resultDataType);
    }

    public static <T, R extends Result<?>> Result<List<T>> withListType(String jsonString, Class<R> resultTypeClass, Class<T> resultDataClass) {
        TypeFactory typeFactory = OBJECT_MAPPER.getTypeFactory();
        JavaType inner = typeFactory.constructParametricType(List.class, resultDataClass);
        JavaType javaType = typeFactory.constructParametricType(resultTypeClass, inner);
        try {
            return OBJECT_MAPPER.readValue(jsonString, javaType);
        } catch (JsonProcessingException e) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR, e);
        }
    }

    public static <T> Result<List<T>> withListType(String jsonString, Class<T> resultDataClass) {
        return withListType(jsonString, ResultData.class, resultDataClass);
    }
}