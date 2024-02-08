package ru.vostrodymov.grader.core.parser;

import com.intellij.psi.PsiClass;

public interface Extractor<T> {
    T take(String packageName, PsiClass clazz);
}
