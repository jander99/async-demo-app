package com.github.jander99.async.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class FakeSlowService {

    private final static Long SLEEP_CEILING = 300L;


    public long callService(int numCalls, int primaryMinLatency, int primaryMaxLatency, int failoverMinLatency, int failoverMaxLatency) {

        if (numCalls != 1) {
            return -1;
        }

        long randomPrimaryLatency = new Random().nextInt(primaryMaxLatency - primaryMinLatency) + primaryMinLatency;
        long randomFailoverLatency = new Random().nextInt(failoverMaxLatency - failoverMinLatency) + failoverMinLatency;

        try {
            if (randomPrimaryLatency >= SLEEP_CEILING) {
                Thread.sleep(SLEEP_CEILING);
                Thread.sleep(randomFailoverLatency);
                return SLEEP_CEILING + randomFailoverLatency;
            } else {
                Thread.sleep(randomPrimaryLatency);
                return randomPrimaryLatency;
            }
        } catch (InterruptedException e) {
            log.error("Something got interrupted.", e);
        }

        return -1;
    }
}
