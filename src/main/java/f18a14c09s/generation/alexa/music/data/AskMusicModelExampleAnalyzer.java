package f18a14c09s.generation.alexa.music.data;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.stream.*;

public class AskMusicModelExampleAnalyzer {
    public static void main(String... args) throws IOException {
        getJsonExamplesByDescription().entrySet()
                .forEach(json -> System.out.printf("%s:%n\t%s%n", json.getKey(), json.getValue()));
    }

    public static Map<String, String> getJsonExamplesByDescription() throws IOException {
        Map<String, String> retval = new HashMap<>();
        Connection conn = Jsoup.connect("https://developer.amazon.com/docs/music-skills/api-reference-overview.html");
        Elements overviewLinks = conn.get().body().select("a[href=\"../music-skills/api-reference-overview.html\"]");
        overviewLinks.forEach(a -> {
            List<String> links =
                    Optional.ofNullable(a.parent()).map(Element::parent).<List<Element>>map(Element::children).orElse(
                            Collections.emptyList())
                            .stream()
                            .map(li -> li.selectFirst("a").attr("abs:href"))
                            .filter(link -> !a.attr("href").equals(link))
                            .collect(Collectors.toList());
//            System.out.printf("Overview %s:%s%n",
//                    a.attr("href"),
//                    links.stream().map(link -> String.format("%n\t%s", link)).collect(Collectors.joining()));
            links.forEach(link -> {
                try {
                    Elements sectionHeaders =
                            Jsoup.connect(link).get().body().select("h3[id~=example-((.+-(request|response)))]");
//                    sectionHeaders.forEach(hdr -> System.out.printf("Section: %s%n", hdr.text()));
                    sectionHeaders.forEach(hdr -> {
                        Element sibling = hdr.nextElementSibling();
                        for (; !sibling.classNames().contains("language-json") || sibling.selectFirst("code") == null;
                             sibling = sibling.nextElementSibling())
                            ;
                        Element code = sibling.selectFirst("code");
//                        System.out.printf("%s:%n\t%s%n", hdr.text(), code.text());
                        retval.put(hdr.text(), code.text());
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
        return retval;
    }
}
