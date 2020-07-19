package ru.zakrzhevskiy.lighthouse.model;

public class OrderStatusStatistic {

    private String name = "";
    private Integer count = 0;

    public OrderStatusStatistic() {
    }

    public OrderStatusStatistic(String name, Integer count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void addCount() {
        this.count++;
    }
}
