package com.unemployed.coreconnect.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

public class CustomRetryListener implements RetryListener {
    private static final Logger log = LoggerFactory.getLogger(CustomRetryListener.class);

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        log.info("Opening retry context");
        
        return true;
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        log.info("Closing retry context");
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        int remainingAttempts = context.getRetryCount();

        log.error("[{}] Retrying...", remainingAttempts);
    }
}
