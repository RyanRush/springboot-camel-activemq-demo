package com.rushryan.jmscameldemoproject.Service;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
class SourceQueueToTargetQueueRouterTest {

    private static final String TEST_INPUT_QUEUE = "direct:InputQueue";
    private static final String MOCK_OUTPUT_QUEUE = "mock:OutputQueue";

    @EndpointInject(uri = MOCK_OUTPUT_QUEUE)
    private  MockEndpoint mockOutputQueue;

    @Autowired
    private CamelContext camelContext;

    @Produce(uri = TEST_INPUT_QUEUE)
    private ProducerTemplate producerTemplate;

    @BeforeEach
    public void setup() throws Exception {
        camelContext.getRouteDefinition(SourceQueueToTargetQueueRouter.ROUTE_ID)
                .autoStartup(true)
                .adviceWith(camelContext, new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith(TEST_INPUT_QUEUE);
                        interceptSendToEndpoint("jms:*")
                                .skipSendToOriginalEndpoint()
                                .to(MOCK_OUTPUT_QUEUE);
                    }
                });
    }

    @Test
    public void configure_noProcessing_MessageReceivedInOutputQueue() throws Exception {
        String testMessageBody = "Test Message";
        mockOutputQueue.expectedMessageCount(1);
        mockOutputQueue.expectedBodiesReceived(testMessageBody);

        producerTemplate.sendBody(testMessageBody);

        mockOutputQueue.assertIsSatisfied();
    }

}