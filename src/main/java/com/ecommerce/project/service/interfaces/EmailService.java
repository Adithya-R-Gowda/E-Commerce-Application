package com.ecommerce.project.service.interfaces;


import com.ecommerce.project.payload.OrderDTO;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendOrderConfirmationEmail(String user, OrderDTO orderDTO) throws MessagingException;
}
