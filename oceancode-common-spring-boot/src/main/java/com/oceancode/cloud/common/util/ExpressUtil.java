/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

public final class ExpressUtil {
    private static final SpelExpressionParser EXPRESSION_PARSER;
    private static final ParserContext TEMPLATE_PARSER_CONTEXT = new ParserContext() {
        @Override
        public boolean isTemplate() {
            return true;
        }

        @Override
        public String getExpressionPrefix() {
            return "#{";
        }

        @Override
        public String getExpressionSuffix() {
            return "}";
        }
    };

    private ExpressUtil() {
    }

    static {
        EXPRESSION_PARSER = new SpelExpressionParser();
    }

    public static <T> T parse(String express, Map<String, Object> params, Class<T> resultTypeClass) {
        ExpressionParser parser = new SpelExpressionParser();
        // 将ioc容器设置到上下文中
        StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext(ComponentUtil.getApplicationContext());
        standardEvaluationContext.addPropertyAccessor(new BeanFactoryAccessor());
        // 将自定义参数添加到上下文
        standardEvaluationContext.setVariables(params);
        Expression expression = parser.parseExpression(express, new TemplateParserContext());
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
