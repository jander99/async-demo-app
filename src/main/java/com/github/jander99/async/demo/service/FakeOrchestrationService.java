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

    private static final int FS1_MIN = 10;
    private static final int FS1_MAX = 95;

    private static final int FS2_MIN = 25;
    private static final int FS2_MAX = 100;

    private static final int FS3_MIN = 8;
    private static final int FS3_MAX = 25;

    private static final int SS_MIN = 100;
    private static final int SS_MAX = 320;


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

        List<Long> fastService1Latencies = null;
        List<Long> fastService2Latencies = null;
        List<Long> fastService3Latencies = null;

        List<CompletableFuture<Long>> asyncFastService1Latencies = null;
        List<CompletableFuture<Long>> asyncFastService2Latencies = null;


        /**
         * This is the first Service call. It will collect immediately which should block
         * execution of the rest of the HTTP request thread.
         */
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("FastService1Call");
        switch (fastService1Type) {
            case "Sequential":
                fastService1Latencies = fastService.callFastService(numIterations, FS1_MIN, FS1_MAX, false);
                break;
            case "Parallel":
                fastService1Latencies = fastService.callFastService(numIterations, FS1_MIN, FS1_MAX, true);
                break;
            case "Async":
                asyncFastService1Latencies = fastService.callFastServiceAsync(numIterations, FS1_MIN, FS1_MAX, false);
                break;
            case "AsyncPooled":
                asyncFastService1Latencies = fastService.callFastServiceAsync(numIterations, FS1_MIN, FS1_MAX, true);
                break;
        }
        stopWatch.stop();
        long fastService1ExecTime = stopWatch.getLastTaskTimeMillis();

        stopWatch.start("FastService1Collector");
        if (Objects.isNull(fastService1Latencies) && Objects.nonNull(asyncFastService1Latencies)) {
            fastService1Latencies = collectCompletedFutures(asyncFastService1Latencies);
        }
        stopWatch.stop();
        long fastService1Collector = stopWatch.getLastTaskTimeMillis();
        /** End First Service Call */


        /**
         * This is the second Service call. It will be collected after the next section of code is completed,
         * to simulate what happens when a block of code is allowed to continue asynchronously while another
         * part of the HTTP thread is processing. If any set of instructions does not rely on Service 2
         * to be complete before starting, this is the pattern you want to use.
         */
        stopWatch.start("FastService2Call");
        switch (fastService2Type) {
            case "Sequential":
                fastService2Latencies = fastService.callFastService(numIterations, FS2_MIN, FS2_MAX, false);
                break;
            case "Parallel":
                fastService2Latencies = fastService.callFastService(numIterations, FS2_MIN, FS2_MAX, true);
                break;
            case "Async":
                asyncFastService2Latencies = fastService.callFastServiceAsync(numIterations, FS2_MIN, FS2_MAX, false);
                break;
            case "AsyncPooled":
                asyncFastService2Latencies = fastService.callFastServiceAsync(numIterations, FS2_MIN, FS2_MAX, true);
                break;
        }
        stopWatch.stop();
        long fastService2ExecTime = stopWatch.getLastTaskTimeMillis();
        /** End Second Service Call */


        /**
         * This is the next block where we want to call a third Fast Service and then a Slow service in
         * sequence. This Fast Service is hardcoded to run sequentially, causing the Slow service to wait
         * until it has finished. Depending on the method that Fast Service 2 took, there could also be
         * processing still occuring asynchronously with this block of code.
         */
        stopWatch.start("FastCall3");
        fastService3Latencies = fastService.callFastService(numIterations, FS3_MIN, FS3_MAX, false);
        stopWatch.stop();
        long fastService3ExecTime = stopWatch.getLastTaskTimeMillis();

        stopWatch.start("SlowCall");
        long slowCallTime = slowService.callService(SS_MIN, SS_MAX, SS_MIN/2, SS_MAX);
        stopWatch.stop();
        long slowCallExecTime = stopWatch.getLastTaskTimeMillis();
        /** End Third Fast Service Call & Slow Service Call */


        /**
         * Now that Fast Service 3 and the Slow Service have completed, we want to collect the results
         * of Fast Service 2, provided that it was run Asynchronously.
         */
        stopWatch.start("FastService2Collector");
        if (Objects.isNull(fastService2Latencies) && Objects.nonNull(asyncFastService2Latencies)) {
            fastService2Latencies = collectCompletedFutures(asyncFastService2Latencies);
        }
        stopWatch.stop();
        long fastService2Collector = stopWatch.getLastTaskTimeMillis();
        /** End Fast Service 2 Async collection */


        /**
         * Let's build the total latency of the 3 Fast Services
         */
        if(Objects.nonNull(fastService1Latencies) && Objects.nonNull(fastService2Latencies) && Objects.nonNull(fastService3Latencies)) {
            long fastService1TotalLatency = fastService1Latencies.stream().reduce(Long::sum).orElse(-1L);
            long fastService2TotalLatency = fastService2Latencies.stream().reduce(Long::sum).orElse(-1L);
            long fastService3TotalLatency = fastService3Latencies.stream().reduce(Long::sum).orElse(-1L);

            AsyncReply.InnerReply slowCall = AsyncReply.InnerReply
                    .builder()
                    .execTime(slowCallExecTime)
                    .latency(slowCallTime)
                    .type("Sequential")
                    .build();

            AsyncReply.InnerReply fastService1 = AsyncReply.InnerReply
                    .builder()
                    .execTime(fastService1ExecTime+fastService1Collector)
                    .latency(fastService1TotalLatency)
                    .type(fastService1Type)
                    .build();

            AsyncReply.InnerReply fastService2 = AsyncReply.InnerReply
                    .builder()
                    .execTime(fastService2ExecTime+fastService2Collector)
                    .latency(fastService2TotalLatency)
                    .type(fastService2Type)
                    .build();

            AsyncReply.InnerReply fastService3 = AsyncReply.InnerReply
                    .builder()
                    .execTime(fastService3ExecTime)
                    .latency(fastService3TotalLatency)
                    .type("Sequential")
                    .build();


            return AsyncReply.builder()
                    .fastCall1(fastService1)
                    .fastCall2(fastService2)
                    .fastCall3(fastService3)
                    .slowCall(slowCall)
                    .totalExecTime(slowCallExecTime + fastService1ExecTime + fastService2ExecTime + fastService3ExecTime)
                    .totalLatency(fastService1TotalLatency + fastService2TotalLatency + fastService3TotalLatency + slowCallTime).build();
        } else {
            throw new Exception("Unable to somehow build the proper model object to send you.");
        }
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
