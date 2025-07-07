package com.ibra.tacticalrpg.ai;

import java.util.ArrayList;
import java.util.List;

public class Sequence<T> extends Task<T> {
    private final List<Task<T>> children;
    private int currentChild = 0;

    public Sequence() {
        this.children = new ArrayList<>();
    }

    @Override
    public void run() {
        if (currentChild >= children.size()) {
            success();
            return;
        }

        Task<T> current = children.get(currentChild);
        current.setObject(getObject());
        current.run();

        if (current.getState() == TaskState.SUCCEEDED) {
            currentChild++;
            if (currentChild >= children.size()) {
                success();
                currentChild = 0;
            } else {
                running();
            }
        } else if (current.getState() == TaskState.FAILED) {
            fail();
            currentChild = 0;
        } else {
            running();
        }
    }

    public void addChild(Task<T> child) {
        children.add(child);
    }
}
