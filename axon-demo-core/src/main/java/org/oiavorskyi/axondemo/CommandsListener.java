package org.oiavorskyi.axondemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
public class CommandsListener implements MessageListener {

    private static Logger log = LoggerFactory.getLogger(CommandsListener.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination testStatusDestination;

    @Override
    public void onMessage( Message message ) {
        log.debug("Received new message");

        String testCorrelationID = null;
        try {
            testCorrelationID = message.getStringProperty("TestCorrelationID");
        } catch ( JMSException e ) {
            log.debug("Can't get TestCorrelationID property from message. Assuming no status " +
                    "reporting is necessary");
        }

        try {
            String messageBody = ((TextMessage) message).getText();
            log.debug("Message contains following body: {}", messageBody);

            // Will need to add command identification logic
            handleStartCargoTrackingCommand(messageBody);
            reportStatusIfRequired(testCorrelationID, "OK");
        } catch ( Exception e ) {
            log.error("Failed to extract body from {}", message);
            reportStatusIfRequired(testCorrelationID, "FAILURE");
        }

    }

    private void reportStatusIfRequired( final String testCorrelationID, final String status ) {
        if (testCorrelationID != null) {
            log.debug("Sending status {} for testing purposes using {}", status);
            jmsTemplate.send(testStatusDestination, new MessageCreator() {
                @Override
                public Message createMessage( Session session ) throws JMSException {
                    TextMessage message = session.createTextMessage(status);
                    message.setStringProperty("TestCorrelationID", testCorrelationID);

                    return message;
                }
            });
        }
    }

    // Starting from this point no JMS dependencies!!!
    public void handleStartCargoTrackingCommand( String message ) {
    }

}
