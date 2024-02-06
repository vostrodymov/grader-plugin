package ru.vostrodymov.grader.core.datamodel.types;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@Setter
@NoArgsConstructor
public class ClassDM {
    @JsonAlias("package")
    private String pack;
    @JsonAlias("name")
    private String name;

    public ClassDM(String value) {
        if (Objects.nonNull(value)) {
            var lastDot = value.lastIndexOf(".");
            if (lastDot > 0) {
                this.setPack(value.substring(0, lastDot));
                this.setName(value.substring(lastDot + 1));
            } else {
                this.setName(value);
            }
        }
    }

    public ClassDM(String pack, String name) {
        this.pack = pack;
        this.name = name;
    }


    /**
     * Возвращает полный путь к класу
     */
    public String getFullName() {
        return toString();
    }

    /**
     * Возвращает признак что тип является системным (JDK)
     */
    public boolean isSystemClass() {
        return Objects.isNull(this.pack);
    }

    public boolean isString() {
        return getFullName().equals("java.lang.String");
    }

    /**
     * Возвращает наименование для переменной составленное из имени класса с маленькой буквы
     */
    public String getPropertyName() {
        return this.name.substring(0, 1).toLowerCase(Locale.ROOT)
                + this.name.substring(1);
    }

    public void pushImport(Consumer<String> add) {
        add.accept(getFullName());
    }

    @Override
    public String toString() {
        return Optional.ofNullable(pack).map(q -> q + ".").orElse("") + name;
    }
}
