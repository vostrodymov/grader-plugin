package ru.vostrodymov.grader.core.generator;

import ru.vostrodymov.grader.core.write.ClassWriter;

public class RestControllerGenerator implements Generator<Object> {

    @Override
    public String run(Object data) {
        var writer = new ClassWriter();
        return writer.toString();
    }
}
