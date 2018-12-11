package f18a14c09s.generation.alexa.music.gen.data;

import java.util.*;

public class CatalogClassInfo extends AbstractClassInfo {
    public CatalogClassInfo() {
    }

    public CatalogClassInfo(String name,
                            String description,
                            List<PropertyInfo> propertyInfo,
                            List<JsonExample> jsonExamples) {
        super(name, description, propertyInfo, jsonExamples);
    }

    @Override
    public String inferClassSimpleName() {
        String regex = "^.*\\( *(?:AMAZON)\\.([A-Za-z]+) *\\)$";
        return Optional.ofNullable(getName())
                .filter(name -> name.matches(regex))
                .map(name -> String.format("%sCatalog", name.replaceAll(regex, "$1")))
                .orElse(null);
    }

    public String inferTypeConstantName() {
        String regex = "^.*\\( *(?:AMAZON)\\.([A-Za-z]+) *\\)$";
        return Optional.ofNullable(getName())
                .filter(name -> name.matches(regex))
                .map(name -> String.format("AMAZON%s",
                        name.replaceAll(regex, "$1").replaceAll("[A-Z]", "_$0").toUpperCase()))
                .orElse(null);
    }

    public String inferEntityClassSimpleName() {
        String regex = "^([A-Za-z]+) catalog.*$";
        return Optional.ofNullable(getName())
                .filter(name -> name.matches(regex))
                .map(name -> String.format("%s", name.replaceAll(regex, "$1")))
                .orElse(null);
    }
}
