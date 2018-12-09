package f18a14c09s.generation.alexa.music.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.*;
import java.util.stream.*;

public class AskMusicDocBasedJavaGenerator {
    @Getter
    private Map<String, String> supportingClasses = new HashMap<>();

    private ObjectMapper jsonMapper = new ObjectMapper();

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

    /**
     * @param implPackageName
     * @param classInfo
     * @throws IOException
     */
    public void addClass(String implPackageName, MessageClassInfo classInfo) {
        String implSimpleName = classInfo.inferClassName();
        String implName = String.format("%s.%s", implPackageName, implSimpleName);
        StringBuilder subjectSourceCode = new StringBuilder();
        subjectSourceCode.append(getPackageDeclarationAndImports(implPackageName));
        if (!classInfo.getMessageType().getPackage().getName().equals(implPackageName)) {
            subjectSourceCode.append(String.format("%nimport %s;", classInfo.getMessageType().getName()));
        }
        subjectSourceCode.append(String.format("/**%s%n */",
                Arrays.stream(classInfo.getDescription().split("(\\r?\\n)+"))
                        .map(line -> String.format("%n * %s", line))
                        .collect(Collectors.joining())));
        subjectSourceCode.append(String.format("%n@JsonDeserialize%npublic class %s extends %s<%1$s.Payload> {",
                implSimpleName,
                classInfo.getMessageType().getSimpleName()));
        subjectSourceCode.append(String.format("%n@Getter%n@Setter%n@JsonIgnoreProperties(ignoreUnknown = true)"));
        subjectSourceCode.append(String.format("%npublic static final class Payload {"));
        classInfo.getPropertyInfo()
                .stream()
                .map(propertyInfo -> String.format("/**%s%n */%nprivate %s %s;",
                        Arrays.stream(propertyInfo.getDescription().trim().split("(\\r?\\n)+"))
                                .map(line -> String.format("%n * %s", line))
                                .collect(Collectors.joining()),
                        propertyInfo.inferClassName(),
                        propertyInfo.getName()))
                .forEach(subjectSourceCode::append);
        subjectSourceCode.append(String.format("%n}%n}"));
        supportingClasses.put(implName, subjectSourceCode.toString());
    }

    /**
     * @param packageName
     * @param classInfo
     * @throws IOException
     */
    public void addClass(String packageName, ComponentClassInfo classInfo) {
        String implSimpleName = classInfo.inferClassName();
        String implName = String.format("%s.%s", packageName, implSimpleName);
        StringBuilder subjectSourceCode = new StringBuilder();
        subjectSourceCode.append(getPackageDeclarationAndImports(packageName));
        subjectSourceCode.append(String.format("/**%s%n */",
                Arrays.stream(classInfo.getDescription().split("(\\r?\\n)+"))
                        .map(line -> String.format("%n * %s", line))
                        .collect(Collectors.joining())));
        subjectSourceCode.append(String.format("%n@Getter%n@Setter%n@JsonIgnoreProperties(ignoreUnknown = true)"));
        subjectSourceCode.append(String.format("%npublic class %s%s {",
                implSimpleName,
                classInfo.inferBaseClassName() == null ?
                        "" :
                        String.format(" extends %s", classInfo.inferBaseClassName())));
        classInfo.getPropertyInfo()
                .stream()
                .map(propertyInfo -> String.format("/**%s%n */%nprivate %s %s;",
                        Arrays.stream(propertyInfo.getDescription().trim().split("(\\r?\\n)+"))
                                .map(line -> String.format("%n * %s", line))
                                .collect(Collectors.joining()),
                        propertyInfo.inferClassName(),
                        propertyInfo.getName()))
                .forEach(subjectSourceCode::append);
        subjectSourceCode.append(String.format("%n}"));
        supportingClasses.put(implName, subjectSourceCode.toString());
    }

}
