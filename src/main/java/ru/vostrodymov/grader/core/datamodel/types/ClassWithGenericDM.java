package ru.vostrodymov.grader.core.datamodel.types;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Setter
public class ClassWithGenericDM extends ClassDM {
    private List<ClassDM> genericArgs;

    public ClassWithGenericDM(String pack, String name, ClassDM... genericArgs) {
        super(pack, name);
        setGenericArgs(Arrays.asList(genericArgs));
    }

    @Override
    public void pushImport(Consumer<String> add) {
        super.pushImport(add);
        genericArgs.forEach(q -> q.pushImport(add));
    }

    @Override
    public String getName() {
        return super.getName() + "<" + genericArgs.stream().map(ClassDM::getName).collect(Collectors.joining(", ")) + ">";
    }
}
