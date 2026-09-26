package com.ecommerce.project.service.implementations;

import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.service.interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void sendOrderConfirmationEmail(String user, OrderDTO orderDTO) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(orderDTO.getEmail());
        helper.setSubject("Order Confirmed - " + orderDTO.getOrderId());

        Context context = new Context();
        context.setVariable("order", orderDTO);
        context.setVariable("customerName", user);

        // Process Template
        String htmlContent = templateEngine.process("email/order-confirmation", context);

        helper.setText(htmlContent, true);
        mailSender.send(mimeMessage);
    }
}
