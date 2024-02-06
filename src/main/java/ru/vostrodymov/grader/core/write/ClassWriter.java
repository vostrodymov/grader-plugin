package ru.vostrodymov.grader.core.write;

import ru.vostrodymov.grader.core.datamodel.types.ClassDM;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ClassWriter extends BaseWriter<ClassWriter> {
    private String packageName;
    private final Set<String> imports = new HashSet<>();

    public ClassWriter() {
    }

    public ClassWriter writePackage(String pack) {
        this.packageName = pack;
        return take();
    }

    public ClassWriter writeImport(String imp) {
        putImport(new ClassDM(imp));
        return take();
    }

    public ClassWriter writeImport(ClassDM imp) {
        putImport(imp);
        return take();
    }

    public ClassWriter writeClassName(String className, ClassDM extendsArg, ClassDM... implementArgs) {
        this.append("public class ").append(className);
        if (Objects.nonNull(extendsArg)) {
            extendsArg.pushImport(imports::add);
            this.append(" extends ").append(extendsArg.getName());
        }
        if (implementArgs.length > 0) {
            String[] items = new String[implementArgs.length];
            int i = 0;
            for (var el : implementArgs) {
                el.pushImport(imports::add);
                items[i] = el.getName();
                i++;
            }
            this.append(" implements ").append(String.join(", ", items));
        }
        this.append(" ");
        return take();
    }

    public ClassWriter writeInterfaceName(String interfaceName, ClassDM... implementArgs) {
        this.append("public interface ").append(interfaceName);
        if (implementArgs.length > 0) {
            String[] items = new String[implementArgs.length];
            int i = 0;
            for (var el : implementArgs) {
                el.pushImport(imports::add);
                items[i] = el.getName();
                i++;
            }
            this.append(" extends ").append(String.join(", ", items));
        }
        this.append(" ");
        return take();
    }

    public ClassWriter writeProperty(ClassDM type, String name) {
        return writeProperty(type, name, false);
    }

    public ClassWriter writeProperty(ClassDM type, String name, boolean isFinal) {
        putImport(type);
        this.tab().append("private ").append((isFinal ? "final " : "")).append(type.getName()).append(" ").append(name).append(";").newLine();
        return take();
    }

    public ClassWriter writeJavadoc(String... text) {
        if (text != null) {
            tab().append("/**").newLine();
            for (var el : text) {
                tab().append(" *").append(el).newLine();
            }
            tab().append("*/").newLine();
        }
        return take();
    }

    private void putImport(ClassDM clazz) {
        if (clazz != null && !clazz.isSystemClass()) {
            this.imports.add(clazz.getFullName());
        }
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";")
                .append(System.lineSeparator())
                .append(System.lineSeparator());
        for (var el : imports) {
            sb.append("import ").append(el).append(";").append(System.lineSeparator());
        }
        if (!imports.isEmpty()) {
            sb.append(System.lineSeparator());
        }

        return sb.append(super.toString()).toString();
    }
}
