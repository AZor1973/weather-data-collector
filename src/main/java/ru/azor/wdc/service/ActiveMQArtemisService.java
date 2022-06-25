package ru.azor.wdc.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.azor.wdc.enums.ErrorValues;
import java.io.IOException;

@Slf4j
@Setter
@Getter
@RequiredArgsConstructor
@Service
public class ActiveMQArtemisService {
    @Value("${confirmation-queue-name}")
    private String confirmationQueueName;
    @Value("${error-queue-name}")
    private String errorQueueName;
    private final CurrentWeatherService currentWeatherService;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = "${set-queue-name}")
    public void listenCityNameFromActiveMQ(@Payload String cityName) throws IOException {
        if (cityName.equals(ErrorValues.NO_SERVICE.name())){
            sendCityNameToActiveMQ();
            return;
        }
        if (currentWeatherService.getCurrentWeatherResponseFromGetRequest(cityName).equals(ErrorValues.NOT_FOUND.name())) {
            log.error("Invalid received city name");
            sendErrorCityNameToActiveMQ();
            sendCityNameToActiveMQ();
        } else {
            currentWeatherService.setCityName(cityName);
            currentWeatherService.getCurrentWeatherAsScheduled();
            log.info("Received: " + cityName);
            sendCityNameToActiveMQ();
        }
    }

    private void sendErrorCityNameToActiveMQ() {
        try{
            jmsTemplate.convertAndSend(errorQueueName, ErrorValues.NOT_FOUND.name());
            log.error("Error send: " + ErrorValues.NOT_FOUND.name());
        }catch (Exception exception){
            log.error("Failed in sending to artemis");
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void sendCityNameToActiveMQ() {
        String cityName = currentWeatherService.getCityName() == null ?
                CurrentWeatherService.DEFAULT_CITY_NAME : currentWeatherService.getCityName();
        try{
            jmsTemplate.convertAndSend(confirmationQueueName, cityName);
            log.info("Send: " + cityName);
        }catch (Exception exception){
            log.error("Failed in sending to artemis");
        }
    }

//    public int countPendingMessages(String destination, JmsTemplate jmsTemplate) {
//        Integer totalPendingMessages = jmsTemplate
//                .browse(destination, (session, browser) ->
//                        Collections.list(browser.getEnumeration()).size());
//        log.info("Count: " + totalPendingMessages);
//        return totalPendingMessages == null ? 0 : totalPendingMessages;
//    }

//    public int countMessages(String destination) throws JMSException {
//        QueueConnection connection = null;
//        int messageCount;
//        try {
//            QueueConnectionFactory cf = (QueueConnectionFactory) jmsTemplate.getConnectionFactory();
//            assert cf != null;
//            connection = cf.createQueueConnection();
//            QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
//            Queue managementQueue = session.createQueue("activemq.management");
//            QueueRequestor requester = new QueueRequestor(session, managementQueue);
//            connection.start();
//            Message m = session.createMessage();
//            JMSManagementHelper.putAttribute(m, ResourceNames.QUEUE + destination, "messageCount");
//            Message reply = requester.request(m);
//            try {
//                messageCount = (Integer) JMSManagementHelper.getResult(reply, Integer.class);
//            } catch (Exception e) {
//                messageCount = 0;
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        } finally {
//            if (connection != null) {
//                connection.close();
//            }
//        }
//        log.info("Count of messages in " + destination + ": " + messageCount);
//        return messageCount;
//    }
//
//    public void deleteAllMessages(String destination) throws JMSException {
//        while (countMessages(destination) > 0) {
//            deleteMessage(destination);
//        }
//    }
//
//    public void deleteMessage(String destination) {
//        jmsTemplate.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
//        jmsTemplate.execute(session -> {
//            try (MessageConsumer consumer = session.createConsumer(
//                    jmsTemplate.getDestinationResolver()
//                            .resolveDestinationName(session, destination, false))) {
//                Message received = consumer.receive(5000);
//                if (received != null) {
//                    received.acknowledge();
//                    return true;
//                }
//            } catch (Exception e) {
//                return false;
//            }
//            return false;
//        }, true);
//    }
}
