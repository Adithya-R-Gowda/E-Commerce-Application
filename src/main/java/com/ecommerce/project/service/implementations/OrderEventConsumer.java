package com.ecommerce.project.service.implementations;

import com.ecommerce.project.model.ProcessedEvent;
import com.ecommerce.project.payload.OrderCreatedEvent;
import com.ecommerce.project.repositories.ProcessedEventRepository;
import com.ecommerce.project.service.interfaces.EmailService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class OrderEventConsumer {
    private EmailService emailService;
    private ProcessedEventRepository processedEventRepository;

    @RabbitListener(queues = "${spring.rabbitmq.queue.name}")
    // TODO: ADD a Retry Mechanism
    public void handleOrderEvent(OrderCreatedEvent event) throws MessagingException {
        log.info("Received order event: id={}, user={}", event.getOrderDTO().getOrderId(), event.getUserName());

        Long orderId = event.getOrderDTO().getOrderId();

        try{
            //Idempotency Check
            if (processedEventRepository.existsById(orderId)) {
                log.info("Duplicate event detected for order {}", orderId);
                return;
            }
            emailService.sendOrderConfirmationEmail(event.getUserName(), event.getOrderDTO());
            processedEventRepository.save(new ProcessedEvent(orderId, LocalDateTime.now()));
        } catch (Exception ex) {
            log.error("Error sending email for order id={}", event.getOrderDTO().getOrderId(), ex);
            throw ex; // allow retry & DLQ handling
        }
    }
}
