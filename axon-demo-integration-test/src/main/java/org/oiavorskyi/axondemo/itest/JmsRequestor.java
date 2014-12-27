package org.oiavorskyi.axondemo.itest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.SessionCallback;
import org.springframework.jms.support.JmsUtils;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Based on code and ideas from http://codedependents
 * .com/2010/03/04/synchronous-request-response-with-activemq-and-spring/
 *
 * The major difference is using of non-standard headers for CorrelationId and ReplyTo so it will
 * not interfere with production code which might use these headers for other reasons.
 */
@Component
public class JmsRequestor {

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private final JmsTemplate jmsTemplate;
    private final Destination testStatusDestination;

    @Autowired
    public JmsRequestor( final JmsTemplate jmsTemplate, final Destination testStatusDestination ) {
        this.jmsTemplate = jmsTemplate;
        this.testStatusDestination = testStatusDestination;
    }

    public Future<String> sendRequest( final Object message, final Destination requestDest ) {
        return executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return jmsTemplate.execute(new ProducerConsumer(message, requestDest,
                        testStatusDestination), true);
            }
        });
    }

    private static final class ProducerConsumer implements SessionCallback<String> {

        private final Object msg;

        private final Destination requestDestination;

        private final Destination statusReplyDestination;

        public ProducerConsumer( Object msg, Destination requestDestination,
                                 Destination statusReplyDestination ) {
            this.msg = msg;
            this.requestDestination = requestDestination;
            this.statusReplyDestination = statusReplyDestination;
        }

        public String doInJms( final Session session ) throws JMSException {
            MessageConsumer consumer = null;
            MessageProducer producer = null;
            try {
                final String testCorrelationID = UUID.randomUUID().toString();

                // Create the consumer first!
                consumer = session.createConsumer(statusReplyDestination, "TestCorrelationID = '" +
                        testCorrelationID + "'");
                // TODO: Valid message type
                final TextMessage textMessage = session.createTextMessage((String) msg);
                textMessage.setStringProperty("TestCorrelationID", testCorrelationID);
                textMessage.setStringProperty("TestStatusReplyTo", statusReplyDestination.toString());

                // Send the request second!
                producer = session.createProducer(requestDestination);
                producer.send(requestDestination, textMessage);

                // Block on receiving the response with a timeout
                Message response = consumer.receive();

                if ( response instanceof TextMessage ) {
                    return ((TextMessage) response).getText();
                } else {
                    throw new ClassCastException("Expected javax.jms.TextMessage but got" +
                            response.getClass().getName());
                }
            } finally {
                // Don't forget to close your resources
                JmsUtils.closeMessageConsumer(consumer);
                JmsUtils.closeMessageProducer(producer);
            }
        }
    }

}
