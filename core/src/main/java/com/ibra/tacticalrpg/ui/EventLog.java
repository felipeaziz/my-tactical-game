package com.ibra.tacticalrpg.ui;

import java.util.LinkedList;
import java.util.Queue;

public class EventLog {
    private final int maxSize;
    private final Queue<String> log = new LinkedList<>();

    public EventLog(int maxSize) {
        this.maxSize = maxSize;
    }

    public void add(String msg) {
        if (log.size() >= maxSize) log.poll();
        log.offer(msg);
    }

    public Queue<String> getLog() {
        return log;
    }
}
