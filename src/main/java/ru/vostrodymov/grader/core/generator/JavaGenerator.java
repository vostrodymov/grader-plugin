package ru.vostrodymov.grader.core.generator;

import ru.vostrodymov.grader.core.datamodel.types.ClassDM;
import ru.vostrodymov.grader.core.generator.types.JavaCode;

import java.util.Arrays;
import java.util.Locale;

public abstract class JavaGenerator<T> implements Generator<T, JavaCode> {

    protected ClassDM getClassDm(String className, String pack, String mask) {
        var maskParts = mask.split("/");
        var packParts = pack.split("\\.");
        for (var el : maskParts) {
            switch (el.toLowerCase(Locale.ROOT)) {
                case "..":
                    packParts = Arrays.copyOfRange(packParts, 0, packParts.length - 1);
                    break;
                case ".":
                    break;
                default:
                    packParts = Arrays.copyOf(packParts, packParts.length + 1);
                    packParts[packParts.length - 1] = el;
                    break;
            }
        }

        return new ClassDM(String.join(".", packParts), className);
    }
}
