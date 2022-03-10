package utils;

import java.util.LinkedList;

public class LimitedQueue<E> extends LinkedList<E> { // утилитный класс для создания ограниченного списка.
    private final int limit;

    public LimitedQueue(int limit) {
        this.limit = limit;
    }

    @Override // переопределил метод для добавления в список, чтобы удалялся первый объект при превышении лимита.
    public boolean add(E o) {
        super.add(o);
        while (size() > limit) {
            super.remove(0);
        }
        return true;
    }
}
