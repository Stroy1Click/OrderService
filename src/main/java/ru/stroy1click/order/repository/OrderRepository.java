package ru.stroy1click.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.stroy1click.order.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

}
