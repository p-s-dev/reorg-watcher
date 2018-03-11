package com.psdev.monitor.chainreorg.model;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by psdev on 5/13/17.
 */
public class AutoDiscardingDeque<E> extends LinkedBlockingDeque<E> {

    public AutoDiscardingDeque() {
        super();
    }

    public AutoDiscardingDeque(int capacity) {
        super(capacity);
    }

    @Override
    public synchronized boolean offerFirst(E e) {
        if (remainingCapacity() == 0) {
            removeLast();
        }
        super.offerFirst(e);
        return true;
    }
}
