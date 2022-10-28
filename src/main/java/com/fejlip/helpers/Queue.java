package com.fejlip.helpers;

import java.util.ArrayList;
import java.util.List;

public class Queue {
    private final List<QueueItem> queue = new ArrayList<>();
    private boolean running = false;

    public void add(QueueItem item) {
        this.queue.add(item);
    }

    public QueueItem get() {
        QueueItem item = this.queue.get(0);
        queue.remove(0);
        return item;
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public void setRunning(boolean isRunning) {
        this.running = isRunning;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void clear() {
        this.queue.clear();
    }

}
