package com.github.jander99.async.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FakeFastService {

    private final AsyncFakeFastService asyncFakeFastService;

    public List<Long> callFastService(int numIterations, int minLatency, int maxLatency, boolean parallel) {
        List<Long> list = generateLatencies(numIterations, minLatency, maxLatency);
        if(parallel) {
            list.parallelStream().forEach(this::waitFor);
        } else {
            list.forEach(this::waitFor);
        }
        return list;
    }

    public List<CompletableFuture<Long>> callFastServiceAsync(int numIterations, int minLatency, int maxLatency, boolean isPooled) {
        List<Long> list = generateLatencies(numIterations, minLatency, maxLatency);
        List<CompletableFuture<Long>> asyncList;
        if (isPooled) {
            asyncList = list.parallelStream().map(asyncFakeFastService::pooledAsyncWait).collect(Collectors.toList());
        } else {
            asyncList = list.parallelStream().map(asyncFakeFastService::asyncWait).collect(Collectors.toList());
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
}
