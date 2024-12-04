package com.choose.config;

import com.choose.constant.CommonConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class JacksonObjectMapper extends ObjectMapper {

    public JacksonObjectMapper() {
        // 创建一个SimpleModule，用于添加自定义的序列化器和反序列化器
        SimpleModule simpleModule = new SimpleModule()
                // 将BigInteger和Long类型序列化为字符串
                .addSerializer(BigInteger.class, ToStringSerializer.instance)
                .addSerializer(Long.class, ToStringSerializer.instance)

                // 添加日期类型的反序列化器
                .addDeserializer(Date.class, new DateDeserializers.DateDeserializer(DateDeserializers.DateDeserializer.instance, new SimpleDateFormat(), CommonConstants.DateFormat.NORM_DATETIME_PATTERN))
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(CommonConstants.DateFormat.NORM_DATETIME_PATTERN)))
                .addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(CommonConstants.DateFormat.NORM_DATE_PATTERN)))
                .addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(CommonConstants.DateFormat.NORM_TIME_PATTERN)))

                // 添加日期类型的序列化器
                .addSerializer(Date.class, new DateSerializer(false, new SimpleDateFormat(CommonConstants.DateFormat.NORM_DATETIME_PATTERN)))
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(CommonConstants.DateFormat.NORM_DATETIME_PATTERN)))
                .addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(CommonConstants.DateFormat.NORM_DATE_PATTERN)))
                .addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(CommonConstants.DateFormat.NORM_TIME_PATTERN)));

        // 注册功能模块，例如，可以添加自定义序列化器和反序列化器
        this.registerModule(simpleModule);
        // 属性值为Null时，不显示属性键
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}