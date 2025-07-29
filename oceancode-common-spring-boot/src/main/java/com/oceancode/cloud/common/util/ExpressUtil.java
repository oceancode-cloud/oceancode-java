/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public final class ExpressUtil {
    private static final SpelExpressionParser EXPRESSION_PARSER;
    private static final TemplateParserContext TEMPLATE_PARSER_CONTEXT = new TemplateParserContext();
    private static final TemplateParserContext SIMPLE_PARSE_CONTEXT = new TemplateParserContext("${", "}");

    private ExpressUtil() {
    }

    static {
        EXPRESSION_PARSER = new SpelExpressionParser();
    }

    public static <T> T parse(String express, Map<String, Object> params, Class<T> resultTypeClass) {
        return parse(express, params, resultTypeClass, false);
    }

    public static <T> T parse(String express, Map<String, Object> params, Class<T> resultTypeClass, boolean isSimpleTemplate) {
        if (Objects.isNull(params)) {
            params = Collections.emptyMap();
        }
        ExpressionParser parser = new SpelExpressionParser();
        // 将ioc容器设置到上下文中
        Object rootObj = ComponentUtil.getApplicationContext();
        PropertyAccessor propertyAccessor = null;
        TemplateParserContext templateParserContext = TEMPLATE_PARSER_CONTEXT;
        if (isSimpleTemplate) {
            rootObj = params;
            propertyAccessor = new MapAccessor();
            templateParserContext = SIMPLE_PARSE_CONTEXT;
        } else {
            propertyAccessor = new BeanFactoryAccessor();
        }
        StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext(rootObj);
        standardEvaluationContext.addPropertyAccessor(propertyAccessor);
        // 将自定义参数添加到上下文
        standardEvaluationContext.setVariables(params);
        Expression expression = parser.parseExpression(express, templateParserContext);
        return expression.getValue(standardEvaluationContext, resultTypeClass);
    }

    public static <T> T parse(String express, String key, Object value, Class<T> resultTypeClass) {
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setVariable(key, value);
        Expression expression = EXPRESSION_PARSER.parseExpression(express);
        return expression.getValue(evaluationContext, resultTypeClass);
    }

    public static <T> T parse(String express, Object value, Class<T> resultTypeClass) {
        return parse(express, "value", value, resultTypeClass);
    }

}
