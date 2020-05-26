package com.github.jander99.async.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AsyncFakeFastService {

    @Async
    public CompletableFuture<Long> asyncWait(long millis) {
        waitFor(millis);
        return CompletableFuture.completedFuture(millis);
    }

    @Async("FastServiceThreadPool")
    public CompletableFuture<Long> pooledAsyncWait(long millis) {
        waitFor(millis);
        return CompletableFuture.completedFuture(millis);
    }

    private void waitFor(long millis) {
        try {
            log.debug("Waiting for {} ms on {} thread", millis, Thread.currentThread().getName());
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("Thread {} got interrupted.", Thread.currentThread().getName(), e);
        }
    }
}