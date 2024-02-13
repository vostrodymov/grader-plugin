package ru.vostrodymov.grader.core.generator.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.vostrodymov.grader.core.datamodel.types.ClassDM;

@Getter
@RequiredArgsConstructor
public class JavaCode {
    private final ClassDM clazz;
    private final String code;
}
