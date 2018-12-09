package f18a14c09s.generation.alexa.music.data;

import java.util.*;

public class ComponentClassInfo extends AbstractClassInfo {
    public ComponentClassInfo() {
    }

    public ComponentClassInfo(String name,
                              String description,
                              List<PropertyInfo> propertyInfo,
                              List<JsonExample> jsonExamples) {
        super(name, description, propertyInfo, jsonExamples);
    }
}
