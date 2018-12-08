package f18a14c09s.generation.alexa.music.data;

import f18a14c09s.integration.alexa.data.Request;
import f18a14c09s.integration.alexa.data.Response;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AskMusicExampleBasedJavaFileGenerator {
    public static void main(String... args) throws IOException {
        inferClasses().entrySet().forEach(clazz -> {
            String className = clazz.getKey();
            String sourceCode = clazz.getValue();
            File testSources = new File(
                    "C:\\Users\\fjohnson\\code\\amazon-integration\\alexa-music-skill-model-4j\\src\\main\\java");
            File packageDir =
                    new File(testSources, className.substring(0, className.lastIndexOf('.')).replaceAll("\\.", "\\\\"));
            packageDir.mkdirs();
            File javaFile =
                    new File(packageDir, String.format("%s.java", className.substring(className.lastIndexOf('.') + 1)));
            try (FileWriter fileWriter = new FileWriter(javaFile)) {
                fileWriter.write(sourceCode);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        });
    }

    public static Map<String, String> inferClasses() throws IOException {
        AskMusicExampleBasedJavaGenerator generator = new AskMusicExampleBasedJavaGenerator();
        AskMusicModelExampleAnalyzer.getJsonExamplesByDescription().entrySet().forEach(json -> {
            String packageName = "f18a14c09s.integration.alexa.music.data";
            StringBuilder classSimpleName = new StringBuilder("");
            boolean upper = true;
            for (char c : json.getKey().toCharArray()) {
                if (Character.isWhitespace(c)) {
                    upper = true;
                } else if (upper) {
                    classSimpleName.append(Character.toUpperCase(c));
                    upper = false;
                } else {
                    classSimpleName.append(c);
                }
            }
            if (classSimpleName.toString().endsWith("Requests") || classSimpleName.toString().endsWith("Responses")) {
                classSimpleName.deleteCharAt(classSimpleName.length() - 1);
            }
            if (classSimpleName.toString().startsWith("Example")) {
                classSimpleName.replace(0, "Example".length(), "");
            }
//            classSimpleName.append("Test");
            String className = String.format("%s.%s", packageName, classSimpleName);
            try {
                generator.addClass(classSimpleName.toString().endsWith("Request") ?
                                Request.class.getName() :
                                classSimpleName.toString().endsWith("Response") ? Response.class.getName() : null,
                        className,
                        json.getValue().replaceAll("[\\r\\n]+]", " "));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        });
        return generator.getSupportingClasses();
    }
}
