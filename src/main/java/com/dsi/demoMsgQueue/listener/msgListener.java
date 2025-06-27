package com.dsi.demoMsgQueue.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Component
public class msgListener {

    private final static Logger logger = LogManager.getLogger(msgListener.class);
    @JmsListener(destination = "GlassfishSurveyQueue")
    public void receiveMessage(final Message message) throws JMSException, IOException {
        showLogMessages(message);
        if (message instanceof TextMessage textMessage) {
            String messageType = message.getStringProperty("messageType");
            if (messageType != null && messageType.equalsIgnoreCase("json")) {
                String json = textMessage.getText();
                // Deserialize to generic map
                Map<String, Object> studentMap = new ObjectMapper().readValue(json, new TypeReference<>() {});
            }
        }else if (message instanceof BytesMessage bytesMessage) {
            String messageType = message.getStringProperty("messageType");

            if ("Image".equals(messageType)) {
                String fileName = message.getStringProperty("fileName");
                String contentType = message.getStringProperty("contentType");
                byte[] buffer = new byte[(int) bytesMessage.getBodyLength()];
                bytesMessage.readBytes(buffer);

                // Save to file
                Path imagePath = Path.of("uploads", fileName);
                Files.createDirectories(imagePath.getParent());
                try (FileOutputStream out = new FileOutputStream(imagePath.toFile())) {
                    out.write(buffer);
                }
                logger.info("Received image, saved as: {}, content-type: {}", imagePath, contentType);
            }
        }
    }

    private void showLogMessages(Message message) throws JMSException {
        if (message instanceof TextMessage) {
            logger.info("Message received: {}", ((TextMessage) message).getText());
        }
        logger.info("================ Received Message Statistics ==================");
        logger.info("Message id: {}", message.getJMSMessageID());
        logger.info("Destination: {}", message.getJMSDestination());
        logger.info("Sent time: {}", message.getJMSTimestamp());
        logger.info("Delivery time: {}", message.getJMSDeliveryTime());
        logger.info("Expiration time: {}", message.getJMSExpiration());
    }
}
