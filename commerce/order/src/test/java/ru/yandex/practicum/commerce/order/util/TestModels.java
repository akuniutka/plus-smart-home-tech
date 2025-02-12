package ru.yandex.practicum.commerce.order.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.UUID;

public final class TestModels {

    public static final String USERNAME = "alice";

    public static final UUID ORDER_ID = UUID.fromString("f8991fc5-1c29-4395-b781-7717893cea92");

    public static final UUID PAYMENT_ID = UUID.fromString("ff035258-939e-4f09-8119-61459eb68949");
    public static final BigDecimal TOTAL_PRICE = BigDecimal.valueOf(2598L, 2);
    public static final BigDecimal DELIVERY_PRICE = BigDecimal.valueOf(599L, 2);
    public static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(1999L, 2);

    public static final UUID DELIVERY_ID = UUID.fromString("1c44948b-78ca-48a5-80e5-6a901af8c117");
    public static final BigDecimal DELIVERY_WEIGHT = BigDecimal.valueOf(14000L, 3);
    public static final BigDecimal DELIVERY_VOLUME = BigDecimal.valueOf(54000L, 3);
    public static final boolean DELIVERY_FRAGILE = true;

    public static final String DELIVERY_COUNTRY = "NOLAND";
    public static final String DELIVERY_CITY = "STONE CITY";
    public static final String DELIVERY_STREET = "FIRST LANE";
    public static final String DELIVERY_HOUSE = "15";
    public static final String DELIVERY_FLAT = "42";

    public static final UUID ORDER_ID_OTHER = UUID.fromString("2b8dc08c-af0a-422f-b54b-eb2157e8bf59");

    public static final UUID PAYMENT_ID_OTHER = UUID.fromString("6b32d033-a160-4193-9f22-a2714c512735");
    public static final BigDecimal TOTAL_PRICE_OTHER = BigDecimal.valueOf(7199L, 2);
    public static final BigDecimal DELIVERY_PRICE_OTHER = BigDecimal.valueOf(1200L, 2);
    public static final BigDecimal PRODUCT_PRICE_OTHER = BigDecimal.valueOf(5999L, 2);

    public static final UUID DELIVERY_ID_OTHER = UUID.fromString("83b2e862-2613-4b3e-a910-b24afafd6983");
    public static final BigDecimal DELIVERY_WEIGHT_OTHER = BigDecimal.valueOf(65500L, 3);
    public static final BigDecimal DELIVERY_VOLUME_OTHER = BigDecimal.valueOf(33333L, 3);
    public static final boolean DELIVERY_FRAGILE_OTHER = false;

    public static final String[] SORT_BY = {"state", "totalPrice"};
    public static final org.springframework.data.domain.Pageable PAGEABLE = PageRequest.of(1, 2, Sort.by(SORT_BY));

    public static final String TEST_EXCEPTION_MESSAGE = "Test exception message";

    private TestModels() {
        throw new AssertionError();
    }
}
