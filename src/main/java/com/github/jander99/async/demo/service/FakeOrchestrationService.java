package com.github.jander99.async.demo.service;

import com.github.jander99.async.demo.model.AsyncReply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FakeOrchestrationService {

    private static final int FS1_MIN = 15;
    private static final int FS1_MAX = 95;

    private static final int FS2_MIN = 25;
    private static final int FS2_MAX = 100;

    private static final int FS3_MIN = 10;
    private static final int FS3_MAX = 25;

    private static final int SS_MIN = 100;
    private static final int SS_MAX = 350;


    private static final List<String> serviceTypes = Arrays.asList("Async", "Parallel", "Sequential", "AsyncPooled");
    private final FakeFastService fastService;
    private final FakeSlowService slowService;

    public AsyncReply makeOrchestratedCall(int numIterations, String fastService1Type, String fastService2Type) throws Exception {

        boolean hasValidServiceTypes =
                checkServiceType(fastService1Type) &&
                        checkServiceType(fastService2Type);

        if (!hasValidServiceTypes || numIterations < 1) {
            throw new Exception("Derped the input somehow!");
        }

        List<Long> prefetchLatencies = null;
        List<Long> geocodeLatencies = null;
        List<Long> minimapFetchLatencies;

        List<CompletableFuture<Long>> asyncPrefetchLatencies = null;
        List<CompletableFuture<Long>> asyncGeocodeLatencies = null;


        // Return a list of latencies of the 'Minimap' call
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("FastCall1");
        switch (fastService1Type) {
            case "Sequential":
                prefetchLatencies = fastService.callFastService(numIterations, FS1_MIN, FS1_MAX, false);
                break;
            case "Parallel":
                prefetchLatencies = fastService.callFastService(numIterations, FS1_MIN, FS1_MAX, true);
                break;
            case "Async":
                asyncPrefetchLatencies = fastService.callFastServiceAsync(numIterations, FS1_MIN, FS1_MAX, false);
                break;
            case "AsyncPooled":
                asyncPrefetchLatencies = fastService.callFastServiceAsync(numIterations, FS1_MIN, FS1_MAX, true);
                break;
        }
        stopWatch.stop();
        long fastService1ExecTime = stopWatch.getLastTaskTimeMillis();

        // Collect these immediately.
        stopWatch.start("FastService1Collector");
        if (Objects.isNull(prefetchLatencies) && asyncPrefetchLatencies != null) {
            prefetchLatencies = collectCompletedFutures(asyncPrefetchLatencies);
        }
        stopWatch.stop();
        long fastService1Collector = stopWatch.getLastTaskTimeMillis();



        // Return a list of latencies of the 'Taxware' call
        stopWatch.start("FastCall2");
        switch (fastService2Type) {
            case "Sequential":
                geocodeLatencies = fastService.callFastService(numIterations, FS2_MIN, FS2_MAX, false);
                break;
            case "Parallel":
                geocodeLatencies = fastService.callFastService(numIterations, FS2_MIN, FS2_MAX, true);
                break;
            case "Async":
                asyncGeocodeLatencies = fastService.callFastServiceAsync(numIterations, FS2_MIN, FS2_MAX, false);
                break;
            case "AsyncPooled":
                asyncGeocodeLatencies = fastService.callFastServiceAsync(numIterations, FS2_MIN, FS2_MAX, true);
                break;
        }
        stopWatch.stop();
        long fastService2ExecTime = stopWatch.getLastTaskTimeMillis();


        // Begin the loop

        stopWatch.start("FastCall3");
        minimapFetchLatencies = fastService.callFastService(numIterations, FS3_MIN, FS3_MAX, false);
        stopWatch.stop();
        long fastService3ExecTime = stopWatch.getLastTaskTimeMillis();

        stopWatch.start("SlowCall");
        long slowCallTime = slowService.callService(1, SS_MIN, SS_MAX, SS_MIN/2, SS_MAX);
        stopWatch.stop();
        long slowCallExecTime = stopWatch.getLastTaskTimeMillis();

        // End loop

        // for Async returns here
        stopWatch.start("FastService2Collector");
        if (Objects.isNull(geocodeLatencies) && asyncGeocodeLatencies != null) {
            geocodeLatencies = collectCompletedFutures(asyncGeocodeLatencies);
        }
        stopWatch.stop();
        long fastService2Collector = stopWatch.getLastTaskTimeMillis();


        // Add together all latencies (reducer probably)
        long prefetchLatency = prefetchLatencies.stream().reduce(Long::sum).orElse(-1L);
        long geocodeLatency = geocodeLatencies.stream().reduce(Long::sum).orElse(-1L);
        long minimapFetchLatency = minimapFetchLatencies.stream().reduce(Long::sum).orElse(-1L);

        AsyncReply.InnerReply slowCall = AsyncReply.InnerReply
                .builder()
                .execTime(slowCallExecTime)
                .latency(slowCallTime)
                .type("Sequential")
                .build();

        AsyncReply.InnerReply fastService1 = AsyncReply.InnerReply
                .builder()
                .execTime(fastService1ExecTime+fastService1Collector)
                .latency(prefetchLatency)
                .type(fastService1Type)
                .build();

        AsyncReply.InnerReply fastService2 = AsyncReply.InnerReply
                .builder()
                .execTime(fastService2ExecTime+fastService2Collector)
                .latency(geocodeLatency)
                .type(fastService2Type)
                .build();

        AsyncReply.InnerReply fastService3 = AsyncReply.InnerReply
                .builder()
                .execTime(fastService3ExecTime)
                .latency(minimapFetchLatency)
                .type("Sequential")
                .build();


        return AsyncReply.builder()
                .fastCall1(fastService1)
                .fastCall2(fastService2)
                .fastCall3(fastService3)
                .slowCall(slowCall)
                .totalExecTime(slowCallExecTime + fastService1ExecTime + fastService2ExecTime + fastService3ExecTime)
                .totalLatency(prefetchLatency + geocodeLatency + minimapFetchLatency + slowCallTime).build();

    }

    private boolean checkServiceType(String serviceType) {
        return serviceTypes.contains(serviceType);
    }

    List<Long> collectCompletedFutures(List<CompletableFuture<Long>> list) {
        return list.stream().map(l -> {
            Long theLong = -1L;
            try {
                theLong = l.get(); // This is where blocking occurs.
            } catch (InterruptedException | ExecutionException e) {
                log.error("Oh no.", e);
            }
            return theLong;
        }).collect(Collectors.toList());
    }
}
