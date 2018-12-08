package f18a14c09s.generation.alexa.music.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.*;
import java.util.stream.*;

public class AskMusicExampleBasedJavaGenerator {
    @Getter
    private Map<String, String> supportingClasses = new HashMap<>();

    private ObjectMapper jsonMapper = new ObjectMapper();

    /**
     * Returns the class body as modeled by "value."  Populates "supportingClasses" with any new classes that are
     * generated.
     *
     * @param identifier
     * @param value
     * @param supportingClasses
     * @param packageName
     * @return String class body.
     */
    private String recursivelyGenerate(String identifier, Object value, String packageName) {
        Class<?> simpleType = getSimpleType(value);
        if (simpleType != null) {
            return String.format("private %s %s;", simpleType.getSimpleName(), identifier);
        } else if (value instanceof Map<?, ?>) {
            Map<String, ?> map = (Map<String, ?>) value;
            if (identifier == null) {
                StringBuilder retval = new StringBuilder();
                map.entrySet()
                        .forEach(prop -> retval.append(String.format("%n%s",
                                recursivelyGenerate(prop.getKey(), prop.getValue(), packageName))));
                return retval.toString();
            } else {
                String newClassName = declareClass(packageName, identifier, map);
                return String.format("private %s %s;", newClassName, identifier);
            }
        } else if (value instanceof List<?>) {
            return String.format("private %s %s;", getListType((List<?>) value, packageName, identifier), identifier);
        } else {
            throw new IllegalArgumentException("Not sure how to handle.");
        }
    }

    /**
     * Returns the list type inferred from "list."  Adds new entries to "supportingClasses" if any nested classes are
     * found.
     *
     * @param list
     * @param packageName
     * @param identifier
     * @return String representing a, possibly nested, List type.
     */
    private String getListType(List<?> list, String packageName, String identifier) {
        Set<Class<?>> uniqueClasses = list.stream().map(Object::getClass).collect(Collectors.toSet());
        String elementType = uniqueClasses.size() == 1 ?
                Optional.ofNullable(getSimpleType(list.get(0))).map(Class::getSimpleName).orElse(null) :
                "?";
        if (elementType != null) {
            return String.format("ArrayList<%s>", elementType);
        } else if (list.get(0) instanceof Map<?, ?>) {
            String childClassName = declareClass(packageName, identifier, (Map<String, ?>) list.get(0));
            return String.format("ArrayList<%s>", childClassName);
        } else if (list.get(0) instanceof List<?>) {
            return String.format("ArrayList<%s>", getListType((List<?>) list.get(0), packageName, identifier));
        } else {
            throw new UnsupportedOperationException("Not sure how to handle.");
        }
    }

    /**
     * Generates source code for a new class that models the values in "map" using the specified package name and with
     * and simple name derived from "identifier."  Places the result in "supportingClasses."
     *
     * @param packageName
     * @param identifier
     * @param map
     * @return String resulting class simple name.
     */
    private String declareClass(String packageName, String identifier, Map<String, ?> map) {
        String classNameFormatString = String.format("%s.%s%s",
                packageName,
                Character.toUpperCase(identifier.charAt(0)),
                identifier.substring(1)) + "%s";
        int i = 0;
        for (; supportingClasses.containsKey(String.format(classNameFormatString, i == 0 ? "" : Integer.toString(i)));
             i++)
            ;
        String newClassName = String.format(classNameFormatString, i == 0 ? "" : Integer.toString(i));
        String simpleName = newClassName.substring(newClassName.lastIndexOf('.') + 1);
        // Add the key now so that class names are incremented in the order that they are found:
        supportingClasses.put(newClassName, "");
        StringBuilder newClassSourceCode = new StringBuilder();
        newClassSourceCode.append(getPackageDeclarationAndImports(packageName));
        newClassSourceCode.append(String.format("%n@Getter%n@Setter%n@JsonIgnoreProperties(ignoreUnknown = true)"));
        newClassSourceCode.append(String.format("%npublic class %s {", simpleName));
        newClassSourceCode.append(recursivelyGenerate(null, map, packageName));
        newClassSourceCode.append(String.format("%n}"));
        supportingClasses.put(newClassName, newClassSourceCode.toString());
        return simpleName;
    }


    /**
     * TODO: Add support for Date and Calendar?
     *
     * @param o
     * @return Class&lt;?&gt; if simple type is applicable; null otherwise.
     */
    private Class<?> getSimpleType(Object o) {
        if (o instanceof String || o instanceof Character || o instanceof Boolean) {
            return o.getClass();
        } else if (o instanceof Byte || o instanceof Short || o instanceof Integer || o instanceof Long) {
            return Long.class;
        } else if (o instanceof Float || o instanceof Double) {
            return Double.class;
        }
        return null;
    }

    /**
     * @param packageName
     * @return String source code header containing package declaration and import statements.
     */
    private String getPackageDeclarationAndImports(String packageName) {
        StringBuilder retval = new StringBuilder();
        retval.append(String.format("package %s;", packageName));
        List<String> classesToImport = new ArrayList<>(Stream.of(ArrayList.class,
                JsonIgnoreProperties.class,
                JsonDeserialize.class,
                Getter.class,
                Setter.class).map(Class::getName).collect(Collectors.toList()));
        for (String className : classesToImport) {
            retval.append(String.format("%nimport %s;", className));
        }
        return retval.toString();
    }

    public void addClass(String apiName, String implName, String sampleJson) throws IOException {
        String implPackageName = implName.substring(0, implName.lastIndexOf('.'));
        String implSimpleName = implName.substring(implName.lastIndexOf('.') + 1);
        String apiPackageName = apiName.substring(0, apiName.lastIndexOf('.'));
        String apiSimpleName = apiName.substring(apiName.lastIndexOf('.') + 1);
        StringBuilder subjectSourceCode = new StringBuilder();
        subjectSourceCode.append(getPackageDeclarationAndImports(implPackageName));
        if (!apiPackageName.equals(implPackageName)) {
            subjectSourceCode.append(String.format("%nimport %s;", apiName));
        }
        subjectSourceCode.append(String.format("%n@JsonDeserialize%npublic class %s extends %s<%1$s.Payload> {",
                implSimpleName,
                apiSimpleName));
        subjectSourceCode.append(String.format("%n@Getter%n@Setter%n@JsonIgnoreProperties(ignoreUnknown = true)"));
        subjectSourceCode.append(String.format("%npublic static final class Payload {"));
        Map<String, ?> map = jsonMapper.readValue(sampleJson, HashMap.class);
        if (map.get("payload") != null) {
            String result = recursivelyGenerate(null, map.get("payload"), implPackageName);
            subjectSourceCode.append(result);
        }
        subjectSourceCode.append(String.format("%n}%n}"));
        supportingClasses.put(implName, subjectSourceCode.toString());
    }

}
