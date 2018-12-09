package f18a14c09s.integration.alexa.music.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import f18a14c09s.generation.alexa.music.data.AbstractClassInfo;
import f18a14c09s.generation.alexa.music.data.ComponentClassInfo;
import f18a14c09s.generation.alexa.music.data.JsonExample;
import f18a14c09s.generation.alexa.music.data.MessageClassInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.*;

public class AskMusicDocBasedTestJavaGenerator {

    private ObjectMapper jsonMapper = new ObjectMapper();

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

    public Map<String, String> generateTestClasses(String apiClassName,
                                                   String implPackageName,
                                                   AbstractClassInfo classInfo) throws IOException {
        String apiPackageName = apiClassName.substring(0, apiClassName.lastIndexOf('.'));
        String apiClassSimpleName = apiClassName.substring(apiClassName.lastIndexOf('.') + 1);
        Map<String, String> retval = new HashMap<>();
        String implName = String.format("%s.%s", implPackageName, classInfo.inferClassName());
        for (int i = 0; i < classInfo.getJsonExamples().size(); i++) {
            JsonExample jsonExample = classInfo.getJsonExamples().get(i);
            StringBuilder sourceCode = new StringBuilder();
            sourceCode.append(String.format("package %s;", implPackageName));
            List<String> classesToImport = new ArrayList<>(Stream.of(ObjectMapper.class, Test.class, IOException.class)
                    .map(Class::getName)
                    .collect(Collectors.toList()));
            if (!apiPackageName.equals(implPackageName)) {
                classesToImport.add(apiClassName);
            }
            for (String className : classesToImport) {
                sourceCode.append(String.format("%nimport %s;", className));
            }
            for (Package pkg : Stream.of("java.util", "java.util.function")
                    .map(Package::getPackage)
                    .collect(Collectors.toList())) {
                sourceCode.append(String.format("%nimport %s.*;", pkg.getName()));
            }
            for (Class<?> staticMethodsClass : Arrays.asList(Assertions.class)) {
                sourceCode.append(String.format("%nimport static %s.*;", staticMethodsClass.getName()));
            }
            sourceCode.append(Optional.ofNullable(jsonExample.getDescription())
                    .map(description -> String.format("%n/**%s%n */",
                            Arrays.stream(description.trim().split("(\\r?\\n)+"))
                                    .map(line -> String.format("%n * %s", line))
                                    .collect(Collectors.joining())))
                    .orElse(""));
            String testClassSimpleName = String.format("%sTest%s", classInfo.inferClassName(), i + 1);
            sourceCode.append(String.format("%npublic class %s {", testClassSimpleName));
            sourceCode.append(String.format("public static final String TEST_CASE = \"%s\";",
                    jsonExample.getJsonValue().replaceAll("\"", "\\\\\"").replaceAll("[\\r\\n]+", " ")));
            sourceCode.append(String.format(
                    "%n@Test%nvoid testDeserialization() throws IOException {%nObjectMapper jsonMapper = new ObjectMapper();"));
            String variableName = "subject";
            if (apiClassName.equals(implName)) {
                sourceCode.append(String.format("%s %s = jsonMapper.readValue(TEST_CASE, %1$s.class);",
                        classInfo.inferClassName(),
                        variableName));
            } else {
                sourceCode.append(String.format("%s obj = jsonMapper.readValue(TEST_CASE, %1$s.class);",
                        apiClassSimpleName));
                sourceCode.append(String.format("%s %s = (%1$s)obj;", classInfo.inferClassName(), variableName));
            }
            Object map = jsonMapper.readValue(jsonExample.getJsonValue(), HashMap.class);
            sourceCode.append(recursivelyGenerate(variableName, map));
            sourceCode.append(String.format("%n}%n}"));
            retval.put(String.format("%s.%s", implPackageName, testClassSimpleName), sourceCode.toString());
        }
        return retval;
    }

    public Map<String, String> generateTestClasses(String implPackageName, ComponentClassInfo classInfo) throws
            IOException {
        return generateTestClasses(String.format("%s.%s", implPackageName, classInfo.inferClassName()),
                implPackageName,
                classInfo);
    }

    public Map<String, String> generateTestClasses(String implPackageName, MessageClassInfo classInfo) throws
            IOException {
        return generateTestClasses(classInfo.getMessageType().getName(), implPackageName, classInfo);
    }

}
