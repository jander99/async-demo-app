package com.github.jander99.async.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FakeFastService {

    private final FakeAsyncFastService fakeAsyncFastService;

    private final RestTemplate restTemplate;

    private final String goserverLocation;

    public FakeFastService(@Qualifier("fastServiceTemplate") RestTemplate restTemplate,
                           FakeAsyncFastService fakeAsyncFastService,
                           @Value("${goserver.path}") String goserverLocation) {
        this.restTemplate = restTemplate;
        this.fakeAsyncFastService = fakeAsyncFastService;
        this.goserverLocation = goserverLocation;
    }

    public List<Long> callFastService(int numIterations, int minLatency, int maxLatency, boolean parallel) {
        List<Long> list = generateLatencies(numIterations, minLatency, maxLatency);
        if(parallel) {
            list.parallelStream().forEach(this::externalLocalCall);
        } else {
            list.forEach(this::externalLocalCall);
        }
        return list;
    }

    public List<CompletableFuture<Long>> callFastServiceAsync(int numIterations, int minLatency, int maxLatency, boolean isPooled) {
        List<Long> list = generateLatencies(numIterations, minLatency, maxLatency);
        List<CompletableFuture<Long>> asyncList;
        if (isPooled) {
            asyncList = list.parallelStream().map(fakeAsyncFastService::pooledAsyncWait).collect(Collectors.toList());
        } else {
            asyncList = list.parallelStream().map(fakeAsyncFastService::asyncWait).collect(Collectors.toList());
        }
        return asyncList;
    }

    private List<Long> generateLatencies(int num, int min, int max) {
        List<Long> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < num; i++) {
            long randomLatency = random.nextInt(max - min) + min;
            list.add(randomLatency);
        }
        return list;
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
        StopWatch watch = new StopWatch("RestTemplate");
        watch.start();
        try {
            String url = String.format(goserverLocation + "/?t=%s",millis);
            restTemplate.getForObject(url,String.class);
        } catch (RestClientException rce) {
            log.error("Uh oh.", rce);
        }
        watch.stop();
    }
}
