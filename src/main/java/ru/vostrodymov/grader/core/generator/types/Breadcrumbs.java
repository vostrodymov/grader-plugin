package ru.vostrodymov.grader.core.generator.types;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class Breadcrumbs {
    private Breadcrumbs parent;
    private String name;

    public Breadcrumbs(String name) {
        this(name, null);
    }

    public Breadcrumbs(String name, Breadcrumbs parent) {
        setName(name);
        setParent(parent);
    }

    public String getPath(String splitter) {
        return Optional.ofNullable(parent)
                .map(q -> q.getPath(splitter))
                .map(q -> q + splitter)
                .orElse("") + name;
    }

    public String getWithoutRoot(String splitter) {
        return Optional.ofNullable(parent)
                .filter(q -> q.getParent() != null)
                .map(q -> q.getWithoutRoot(splitter))
                .map(q -> q + splitter)
                .orElse("") + name;
    }
}
