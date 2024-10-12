package io.finlandjourney;

import java.time.*;

import lombok.*;

@Data
@AllArgsConstructor
public class Order {

    private String orderId;
    private int quantity;
    private int price;
    private OrderType orderType;
    private LocalDateTime creationTime;
}

