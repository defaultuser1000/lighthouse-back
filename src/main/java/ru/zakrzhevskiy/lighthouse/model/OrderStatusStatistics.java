package ru.zakrzhevskiy.lighthouse.model;

import java.util.LinkedList;
import java.util.List;

public class OrderStatusStatistics {

    private List<OrderStatusStatistic> orderStatusStatistics = new LinkedList<>();

    public void setOrderStatusStatistics(List<OrderStatusStatistic> orderStatusStatistics) {
        this.orderStatusStatistics = orderStatusStatistics;
    }

    public List<OrderStatusStatistic> getOrderStatusStatistics() {
        return this.orderStatusStatistics;
    }

    public void add(OrderStatusStatistic object) {
        orderStatusStatistics.add(object);
    }

    public OrderStatusStatistic getOrderStatusStatistic(String statusName) {
        return this.orderStatusStatistics.stream().filter(orderStatusStatistic -> orderStatusStatistic.getName().equals(statusName)).findFirst().orElse(new OrderStatusStatistic());
    }

}
