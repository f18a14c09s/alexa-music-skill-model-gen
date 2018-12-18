package f18a14c09s.generation.alexa.music.gen;

import f18a14c09s.integration.alexa.music.catalog.data.AbstractCatalog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CatalogClassGenerator {
    public static void main(String... args) throws IOException {
        String[] array =
                {"BroadcastChannel", "Station", "Genre", "Genre", "MusicAlbum", "Album", "MusicGroup", "Artist",
                        "MusicPlaylist", "Playlist", "MusicRecording", "Track"};
        for (int i = 0; i < array.length; i += 2) {
            String classSimpleName = String.format("%sCatalog", array[i]);
            String entityClassName = array[i + 1];
            String packageName = AbstractCatalog.class.getPackage().getName();
            try (FileWriter fileWriter = new FileWriter(new File(new File(new File(args[0]),
                    packageName.replace(".", File.separator)), String.format("%s.java", classSimpleName)))) {
                fileWriter.write(String.format("package %s;" + "%nimport lombok.*;" +
                                "%nimport com.fasterxml.jackson.annotation.JsonIgnoreProperties;" +
                                "%n@Getter%n@Setter%n@JsonIgnoreProperties(ignoreUnknown = true)" +
                                "%npublic class %s extends %s<%s> {" + "%npublic %2$s(){super(null, null);}" + "%n}",
                        packageName,
                        classSimpleName,
                        AbstractCatalog.class.getSimpleName(),
                        entityClassName));
            }
        }
    }
}
