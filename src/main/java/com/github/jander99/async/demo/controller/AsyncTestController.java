package com.github.jander99.async.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jander99.async.demo.model.AsyncPayload;
import com.github.jander99.async.demo.model.AsyncReply;
import com.github.jander99.async.demo.service.FakeOrchestrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@ControllerAdvice
public class AsyncTestController {

    private final FakeOrchestrationService fakeOrchestrationService;

    private final ObjectMapper objectMapper;

    @RequestMapping(path = "/", method = RequestMethod.POST)
    public ResponseEntity<?> postCommand(@RequestBody AsyncPayload payload) {

        try {
            log.debug("Entering Post: {}", objectMapper.writeValueAsString(payload));

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            AsyncReply reply = fakeOrchestrationService.makeOrchestratedCall(
                    payload.getNumIterations(),
                    payload.getFastService1Type(),
                    payload.getFastService2Type());
            stopWatch.stop();
            reply.setTotalExecTime(stopWatch.getTotalTimeMillis());
            log.debug("RESPONSE: {}", objectMapper.writeValueAsString(reply));
            return ResponseEntity.ok(reply);
        } catch (Exception e) {
            log.error("Wat.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
