package com.ibra.tacticalrpg.ai;

public abstract class Task<T> {
    private T object;
    private TaskState state = TaskState.FRESH;

    public enum TaskState {
        FRESH,
        RUNNING,
        SUCCEEDED,
        FAILED
    }

    public abstract void run();

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    protected void success() {
        state = TaskState.SUCCEEDED;
    }

    protected void fail() {
        state = TaskState.FAILED;
    }

    protected void running() {
        state = TaskState.RUNNING;
    }

    public TaskState getState() {
        return state;
    }
}
