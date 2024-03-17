package com.oceancode.cloud.common.util;

import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.function.Function;

public final class CallUtil {
    private CallUtil() {
    }

    /**
     * @param maxAttempts 重试次数
     * @param backoff     间隔多久重试一次 默认2000ms
     * @param function
     * @return
     */
    public static void retryWithFixed(int maxAttempts, long backoff, RetryCall function) {
        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(maxAttempts);
        retryTemplate.setRetryPolicy(retryPolicy);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(backoff);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.execute(context -> {
            try {
                function.apply();
            } catch (Throwable t) {
                throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, t);
            }
            return null;
        });
    }

    public static void retryWithFixed(int maxAttempts, RetryCall function) {
        retryWithFixed(maxAttempts, 500, function);
    }

    public static interface RetryCall {
        void apply();
    }
}
