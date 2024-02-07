package ru.vostrodymov.grader.core.generator;

import ru.vostrodymov.grader.core.props.GraderProperties;

public interface Generator<T> {

    String run(T data, GraderProperties props);
}
