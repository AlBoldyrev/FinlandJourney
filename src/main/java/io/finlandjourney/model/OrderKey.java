package io.finlandjourney.model;

import java.time.LocalDateTime;

import lombok.*;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class OrderKey implements Comparable<OrderKey> {
    private final int price;
    private final LocalDateTime creationTime;

    @Override
    public int compareTo(OrderKey other) {
        int priceComparison = Integer.compare(this.price, other.price);
        if (priceComparison != 0) {
            return priceComparison;
        }
        return this.creationTime.compareTo(other.creationTime);
    }
}
