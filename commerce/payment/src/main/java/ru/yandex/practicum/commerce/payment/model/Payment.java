package ru.yandex.practicum.commerce.payment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments", schema = "payment")
@Data
@EqualsAndHashCode(of = "paymentId")
public class Payment {

    @Id
    private UUID paymentId;

    private UUID orderId;

    private BigDecimal totalPayment;
    private BigDecimal productTotal;
    private BigDecimal deliveryTotal;
    private BigDecimal feeTotal;

    @Enumerated(EnumType.STRING)
    private PaymentState state;
}
