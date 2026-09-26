package com.ecommerce.project.payload;

import com.ecommerce.project.model.Order;
import com.ecommerce.project.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderCreatedEvent {
    private String userName;
    private OrderDTO orderDTO;
}
