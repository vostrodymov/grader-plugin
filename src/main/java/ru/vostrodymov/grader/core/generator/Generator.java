package ru.vostrodymov.grader.core.generator;

public interface Generator<T> {

    String run(T data);
}
