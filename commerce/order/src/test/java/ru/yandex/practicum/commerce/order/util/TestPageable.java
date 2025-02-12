package ru.yandex.practicum.commerce.order.util;

import ru.yandex.practicum.commerce.dto.store.Pageable;

import static ru.yandex.practicum.commerce.order.util.TestModels.PAGEABLE;
import static ru.yandex.practicum.commerce.order.util.TestModels.SORT_BY;

public final class TestPageable {

    public static final int PAGE = PAGEABLE.getPageNumber();
    public static final int SIZE = PAGEABLE.getPageSize();

    private TestPageable() {
        throw new AssertionError();
    }

    public static Pageable create() {
        final Pageable pageable = new Pageable();
        pageable.setPage(PAGE);
        pageable.setSize(SIZE);
        pageable.setSort(SORT_BY);
        return pageable;
    }
}
