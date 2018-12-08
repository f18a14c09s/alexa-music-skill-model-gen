package f18a14c09s.integration.alexa.music.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.*;

public class AskMusicExampleBasedTestJavaGenerator {

    private ObjectMapper jsonMapper = new ObjectMapper();


    public static void main(String... args) throws IOException {
        System.out.printf("%s%n",
                new AskMusicExampleBasedTestJavaGenerator().generateTestClass(ExampleMessage.class.getName(),
                        ExampleMessage.class.getName(),
                        ExampleMessageTest.TEST_CASE));
    }

    private String recursivelyGenerate(String expression, Object value) {
        String simpleValueComparison = getSimpleValueComparison(expression, value);
        if (value == null) {
            return String.format("%nassertNull(%s);", expression);
        } else if (simpleValueComparison != null) {
            return simpleValueComparison;
        } else if (value instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) value;
            return String.format("%n%s", map.entrySet().stream().map(child -> {
                String key = (String) child.getKey();
                return recursivelyGenerate(String.format("%s.get%s%s()",
                        expression,
                        Character.toUpperCase(key.charAt(0)),
                        key.substring(1)), child.getValue());
            }).collect(Collectors.joining()));
        } else if (value instanceof List<?>) {
            List<?> list = (List<?>) value;
            StringBuilder assertionCode = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                assertionCode.append(recursivelyGenerate(String.format("%s.get(%s)", expression, i), list.get(i)));
            }
            return assertionCode.toString();
        } else {
            throw new IllegalArgumentException("Not sure how to handle.");
        }
    }


    /**
     * TODO: Add support for Date and Calendar?
     *
     * @param o
     * @return
     */
    private String getSimpleValueComparison(String expression, Object o) {
        if (o instanceof CharSequence || o instanceof Enum<?>) {
            return String.format("assertEquals(%s, \"%s\");", expression, o.toString().replaceAll("\"", "\\\\\""));
        } else if (o instanceof Byte || o instanceof Short || o instanceof Integer || o instanceof Long) {
            return String.format("assertEquals(((Number)%s).longValue(), %sL);", expression, o);
        } else if (o instanceof Float || o instanceof Double) {
            return String.format("assertEquals(((Number)%s).doubleValue(), %sD);", expression, o);
        } else if (o instanceof Boolean) {
            return String.format("assert%s%s((boolean)%s);",
                    Character.toUpperCase(o.toString().charAt(0)),
                    o.toString().substring(1),
                    expression);
        } else if (o instanceof Character) {
            return String.format("assertEquals((char)%s, '%s');", expression, o);
        }
        return null;
    }

    public String generateTestClass(String apiName, String implName, String sampleJson) throws IOException {
        String implPackageName = implName.substring(0, implName.lastIndexOf('.'));
        String implSimpleName = implName.substring(implName.lastIndexOf('.') + 1);
        String apiPackageName = apiName.substring(0, apiName.lastIndexOf('.'));
        String apiSimpleName = apiName.substring(apiName.lastIndexOf('.') + 1);
        StringBuilder retval = new StringBuilder();
        retval.append(String.format("package %s;", implPackageName));
        List<String> classesToImport = new ArrayList<>(Stream.of(ObjectMapper.class, Test.class, IOException.class)
                .map(Class::getName)
                .collect(Collectors.toList()));
        if (!apiPackageName.equals(implPackageName)) {
            classesToImport.add(apiName);
        }
        for (String className : classesToImport) {
            retval.append(String.format("%nimport %s;", className));
        }
        for (Package pkg : Stream.of("java.util", "java.util.function")
                .map(Package::getPackage)
                .collect(Collectors.toList())) {
            retval.append(String.format("%nimport %s.*;", pkg.getName()));
        }
        for (Class<?> staticMethodsClass : Arrays.asList(Assertions.class)) {
            retval.append(String.format("%nimport static %s.*;", staticMethodsClass.getName()));
        }
        retval.append(String.format("%npublic class %sTest {", implSimpleName));
        retval.append(String.format("public static final String TEST_CASE = \"%s\";",
                sampleJson.replaceAll("\"", "\\\\\"").replaceAll("[\\r\\n]+", " ")));
        retval.append(String.format(
                "%n@Test%nvoid testDeserialization() throws IOException {%nObjectMapper jsonMapper = new ObjectMapper();"));
        String variableName = "subject";
        if (apiName.equals(implName)) {
            retval.append(String.format("%s %s = jsonMapper.readValue(TEST_CASE, %1$s.class);",
                    implSimpleName,
                    variableName));
        } else {
            retval.append(String.format("%s obj = jsonMapper.readValue(TEST_CASE, %1$s.class);", apiSimpleName));
            retval.append(String.format("%s %s = (%1$s)obj;", implSimpleName, variableName));
        }
        Object map = jsonMapper.readValue(sampleJson, HashMap.class);
        retval.append(recursivelyGenerate(variableName, map));
        retval.append(String.format("%n}%n}"));
        return retval.toString();
    }

}
