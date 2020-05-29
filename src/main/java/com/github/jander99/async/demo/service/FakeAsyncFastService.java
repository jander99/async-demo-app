package com.github.jander99.async.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class FakeAsyncFastService {


    private final RestTemplate restTemplate;

    private final int port = 8000;

    public FakeAsyncFastService(@Qualifier("asyncFastServiceTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public CompletableFuture<Long> asyncWait(long millis) {
        externalLocalCall(millis);
        return CompletableFuture.completedFuture(millis);
    }

    @Async("FastServiceThreadPool")
    public CompletableFuture<Long> pooledAsyncWait(long millis) {
        externalLocalCall(millis);
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

    private void externalLocalCall(long millis) {
        try {
            restTemplate.getForObject(String.format("http://localhost:%s/?t=%s",port,millis),String.class);
        } catch (RestClientException rce) {
            log.error("Uh oh.", rce);
        }
    }
}
