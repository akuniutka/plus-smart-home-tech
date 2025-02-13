package ru.yandex.practicum.commerce.warehouse.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record DeliveryParams(BigDecimal deliveryWeight, BigDecimal deliveryVolume, boolean fragile) {

    private static final int SCALE = 3;

    public static DeliveryParams empty() {
        return new DeliveryParams(
                BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP),
                BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP),
                false
        );
    }

    public static DeliveryParams fromProduct(final Product product) {
        return new DeliveryParams(
                product.getWeight(),
                product.getDimension().getWidth()
                        .multiply(product.getDimension().getHeight())
                        .multiply(product.getDimension().getDepth())
                        .setScale(SCALE, RoundingMode.HALF_UP),
                Boolean.TRUE.equals(product.getFragile())
        );
    }

    public static DeliveryParams sum(final DeliveryParams a, final DeliveryParams b) {
        System.out.println(a.deliveryWeight);
        return new DeliveryParams(
                a.deliveryWeight.add(b.deliveryWeight),
                a.deliveryVolume.add(b.deliveryVolume),
                a.fragile || b.fragile
        );
    }

    public DeliveryParams multiply(final long multiplicand) {
        System.out.println(deliveryWeight);
        final BigDecimal multiplicand_ = BigDecimal.valueOf(multiplicand);
        return new DeliveryParams(
                deliveryWeight().multiply(multiplicand_),
                deliveryVolume().multiply(multiplicand_),
                fragile()
        );
    }
}
