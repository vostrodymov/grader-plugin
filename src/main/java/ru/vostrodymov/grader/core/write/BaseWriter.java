package ru.vostrodymov.grader.core.write;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseWriter<T extends BaseWriter<?>> {
    private final StringBuilder sb = new StringBuilder();
    private final AtomicInteger tabCounter = new AtomicInteger();
    private final T parent;

    public BaseWriter(T parent) {
        this.parent = parent;
    }

    public BaseWriter() {
        this.parent = null;
    }

    public T begin() {
        append("{").newLine();
        tabCounter.addAndGet(4);
        return take();
    }

    public T end() {
        tabCounter.addAndGet(-4);
        append("}").newLine();
        return take();
    }

    public T endAndSymbol(String symbol) {
        tabCounter.addAndGet(-4);
        append("}").append(symbol).newLine();
        return take();
    }

    public T newLine() {
        sb.append("\n");
        return take();
    }
    public T newLineAndTab() {
        tabCounter.addAndGet(4);
        return newLine();
    }
    public T revAndNewLine() {
        tabCounter.addAndGet(-4);
        return newLine();
    }


    public T tab() {
        sb.append(" ".repeat(this.tabCounter.get()));
        return take();
    }

    public T append(String text) {
        sb.append(text);
        return take();
    }


    public String toString() {
        return sb.toString();
    }

    protected T take() {
        return (T) this;
    }
}
