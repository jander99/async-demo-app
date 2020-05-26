package com.github.jander99.async.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AsyncReply {

    private InnerReply slowCall;

    private InnerReply fastCall1;

    private InnerReply fastCall2;

    private InnerReply fastCall3;

    private Long totalExecTime;

    private Long totalLatency;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class InnerReply {
        private Long latency;

        private Long execTime;

        private String type;
    }
}
