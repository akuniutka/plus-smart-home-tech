package ru.yandex.practicum.commerce.dto;

import lombok.Data;

@Data
public class Pageable {

    private Integer page;
    private Integer size;
    private String sort;
}
