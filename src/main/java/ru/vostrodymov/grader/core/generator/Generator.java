package ru.vostrodymov.grader.core.generator;

import ru.vostrodymov.grader.core.generator.types.JavaCode;
import ru.vostrodymov.grader.core.props.GraderProperties;

public interface Generator<T, R> {

    R run(T data, GraderProperties props);
}
