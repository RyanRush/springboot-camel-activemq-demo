package com.rushryan.jmscameldemoproject.Service;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SourceQueueToTargetQueueRouter extends RouteBuilder {

    private static final Logger log = LoggerFactory.getLogger(SourceQueueToTargetQueueRouter.class);
    public static final String ROUTE_ID = "route-id";


    private final String inputQueue;
    private final String outputQueue;

    public SourceQueueToTargetQueueRouter(@Value("${input.queue}") String inputQueue, @Value("${output.queue}") String outputQueue) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

    @Override
    public void configure() {
        from(inputQueue)
                .routeId(ROUTE_ID)
                .log(LoggingLevel.INFO, log, "Message received")
                .process(processMessage())
                .to(outputQueue);
    }

    // Do any required message processing here
    private Processor processMessage() {
        return Exchange::getMessage;
    }

}
