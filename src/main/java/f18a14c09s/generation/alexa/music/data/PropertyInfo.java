package f18a14c09s.generation.alexa.music.data;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class PropertyInfo {
    private String name;
    private Type type;
    private String description;
    private Required required;

    public PropertyInfo() {
    }

    public PropertyInfo(String name, String type, String description, String required) {
        this.name = name;
        this.type = Optional.ofNullable(type).map(Type::valueOfCaseInsensitive).orElse(null);
        this.description = description;
        this.required = Optional.ofNullable(required).map(Required::valueOf).orElse(null);
    }

    public Class<?> inferClass() {
        return Optional.ofNullable(type).map(Type::getClazz).orElse(null);
    }

    public String inferClassName() {
        String objectRegex = ".*See(?: the)? ([A-Za-z]+)(?: object)? for(?: more)? (?:details|information)\\..*";
        return inferClass() == Object.class ?
                getDescription().matches(objectRegex) ? getDescription().replaceAll(objectRegex, "$1") : null :
                Optional.ofNullable(inferClass()).map(Class::getSimpleName).orElse(null);
    }

    public boolean isRequired() {
        return required.isValue();
    }

    public enum Type {
        STRING(String.class),
        OBJECT(Object.class),
        LIST(ArrayList.class),
        BOOLEAN(Boolean.class),
        INTEGER(Long.class),
        LONG(Long.class);
        @Getter
        private Class<?> clazz;

        Type(Class<?> clazz) {
            this.clazz = clazz;
        }

        public static Type valueOfCaseInsensitive(String name) {
            return Type.valueOf(name.toUpperCase());
        }
    }

    public enum Required {
        yes(true),
        no(false);
        @Getter
        private final boolean value;

        Required(boolean value) {
            this.value = value;
        }
    }
}
