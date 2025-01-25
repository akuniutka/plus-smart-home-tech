package ru.yandex.practicum.commerce.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.commerce.warehouse.model.Stock;

import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, UUID> {

}
