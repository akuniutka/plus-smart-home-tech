package ru.yandex.practicum.commerce.warehouse.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@ToString
@EqualsAndHashCode
public class BookedProducts {

    private static final int SCALE = 3;

    private BigDecimal deliveryVolume;
    private BigDecimal deliveryWeight;
    private boolean fragile;

    public BookedProducts() {
        this.deliveryVolume = BigDecimal.valueOf(0, SCALE);
        this.deliveryWeight = BigDecimal.valueOf(0, SCALE);
        this.fragile = false;
    }

    public void addProduct(final Product product, long times) {
        final BigDecimal _times = BigDecimal.valueOf(times);
        accumulateVolume(product.getDimension(), _times);
        accumulateWeight(product.getWeight(), _times);
        accumulateFragile(product.getFragile());
    }

    private void accumulateVolume(final Dimension dimension, final BigDecimal times) {
        deliveryVolume = dimension.getWidth()
                .multiply(dimension.getHeight())
                .multiply(dimension.getDepth())
                .multiply(times)
                .add(deliveryVolume)
                .setScale(SCALE, RoundingMode.HALF_UP);
    }

    private void accumulateWeight(final BigDecimal productWeight, final BigDecimal times) {
        deliveryWeight = productWeight.multiply(times).add(deliveryWeight);
    }

    private void accumulateFragile(final Boolean isProductFragile) {
        fragile = fragile || Boolean.TRUE.equals(isProductFragile);
    }
}
