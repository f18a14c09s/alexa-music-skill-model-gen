package f18a14c09s.generation.alexa.music.data;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class ClassInfo {
    private String name;
    private String description;
    private List<PropertyInfo> propertyInfo;
    private List<JsonExample> jsonExamples;

    public ClassInfo() {
    }

    public ClassInfo(String name, String description, List<PropertyInfo> propertyInfo, List<JsonExample> jsonExamples) {
        this.name = name;
        this.description = description;
        this.propertyInfo = propertyInfo;
        this.jsonExamples = jsonExamples;
    }

    public String inferBaseClassName() {
        return null;
    }

    public String inferClassName() {
        return getName();
    }
}
