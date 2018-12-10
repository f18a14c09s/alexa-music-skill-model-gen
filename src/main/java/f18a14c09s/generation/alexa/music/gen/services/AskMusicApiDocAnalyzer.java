package f18a14c09s.generation.alexa.music.gen.services;

import f18a14c09s.generation.alexa.music.gen.data.*;
import f18a14c09s.integration.alexa.data.AbstractMessage;
import f18a14c09s.integration.alexa.music.messagetypes.Request;
import f18a14c09s.integration.alexa.music.messagetypes.Response;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class AskMusicApiDocAnalyzer {
    public static void main(String... args) throws IOException {
        AskMusicApiDocAnalyzer analyzer = new AskMusicApiDocAnalyzer();
        analyzer.getMessageModel().stream().forEach(msgInfo -> {
            System.out.printf(
                    "Message %s:%n\tRequest:%n\t\tNamespace: %s%n\t\tName: %1$s%n\t\tInferred Class Name: %s%n\t\tDescription:%s%n\t\tProperty Info:%s%n\t\tJSON Examples:%s%n\tResponse:%n\t\tNamespace: %s%n\t\tName: %s%n\t\tInferred Class Name: %s%n\t\tDescription:%s%n\t\tProperty Info:%s%n\t\tJSON Examples:%s%n",
                    msgInfo.getRequestInfo().getName(),
                    msgInfo.getRequestInfo().getNamespace(),
                    msgInfo.getRequestInfo().inferClassName(),
                    Arrays.stream(msgInfo.getRequestInfo().getDescription().split("\\r?\\n"))
                            .map(paragraph -> String.format("%n\t\t\t%s", paragraph))
                            .collect(Collectors.joining()),
                    msgInfo.getRequestInfo()
                            .getPropertyInfo()
                            .stream()
                            .map(propertyInfo -> String.format(
                                    "%n\t\t\t%s:%n\t\t\t\tType: %s%n\t\t\t\tInferred Class Name: %s%n\t\t\t\tRequired: %s%n\t\t\t\tDescription:%s",
                                    propertyInfo.getName(),
                                    propertyInfo.getType(),
                                    propertyInfo.inferClassName(),
                                    propertyInfo.getRequired(),
                                    Arrays.stream(propertyInfo.getDescription().split("\\r?\\n"))
                                            .map(paragraph -> String.format("%n\t\t\t\t\t%s", paragraph))
                                            .collect(Collectors.joining())))
                            .collect(Collectors.joining()),
                    msgInfo.getRequestInfo()
                            .getJsonExamples()
                            .stream()
                            .map(json -> String.format("%n\t\t\t%s:%n\t\t\t\t%s",
                                    json.getDescription().replaceAll("(\\r?\\n)+", "  "),
                                    json.getJsonValue().replaceAll("(\\r?\\n)+", "  ")))
                            .collect(Collectors.joining()),
                    msgInfo.getResponseInfo().getNamespace(),
                    msgInfo.getResponseInfo().getName(),
                    msgInfo.getResponseInfo().inferClassName(),
                    Arrays.stream(msgInfo.getResponseInfo().getDescription().split("\\r?\\n"))
                            .map(paragraph -> String.format("%n\t\t\t%s", paragraph))
                            .collect(Collectors.joining()),
                    msgInfo.getResponseInfo()
                            .getPropertyInfo()
                            .stream()
                            .map(propertyInfo -> String.format(
                                    "%n\t\t\t%s:%n\t\t\t\tType: %s%n\t\t\t\tInferred Class Name: %s%n\t\t\t\tRequired: %s%n\t\t\t\tDescription:%s",
                                    propertyInfo.getName(),
                                    propertyInfo.inferClassName(),
                                    propertyInfo.getType(),
                                    propertyInfo.getRequired(),
                                    Arrays.stream(propertyInfo.getDescription().split("\\r?\\n"))
                                            .map(paragraph -> String.format("%n\t\t\t\t\t%s", paragraph))
                                            .collect(Collectors.joining())))
                            .collect(Collectors.joining()),
                    msgInfo.getResponseInfo()
                            .getJsonExamples()
                            .stream()
                            .map(json -> String.format("%n\t\t\t%s:%n\t\t\t\t%s",
                                    json.getDescription().replaceAll("(\\r?\\n)+", "  "),
                                    json.getJsonValue().replaceAll("(\\r?\\n)+", "  ")))
                            .collect(Collectors.joining()));
        });
        analyzer.getComponentModel().stream().forEach(classInfo -> {
            System.out.printf(
                    "Component %s:%n\tInferred Class Name: %s%n\tDescription:%s%n\tProperty Info:%s%n\tJSON Examples:%s%n",
                    classInfo.getName(),
                    classInfo.inferClassName(),
                    Arrays.stream(classInfo.getDescription().split("\\r?\\n"))
                            .map(paragraph -> String.format("%n\t\t\t%s", paragraph))
                            .collect(Collectors.joining()),
                    classInfo.getPropertyInfo()
                            .stream()
                            .map(propertyInfo -> String.format(
                                    "%n\t\t%s:%n\t\t\tType: %s%n\t\t\tInferred Class Name: %s%n\t\t\tRequired: %s%n\t\t\tDescription:%s",
                                    propertyInfo.getName(),
                                    propertyInfo.getType(),
                                    propertyInfo.inferClassName(),
                                    propertyInfo.getRequired(),
                                    Arrays.stream(propertyInfo.getDescription().split("\\r?\\n"))
                                            .map(paragraph -> String.format("%n\t\t\t\t%s", paragraph))
                                            .collect(Collectors.joining())))
                            .collect(Collectors.joining()),
                    classInfo.getJsonExamples()
                            .stream()
                            .map(json -> String.format("%n\t\t%s:%n\t\t\t%s",
                                    json.getDescription().replaceAll("(\\r?\\n)+", "  "),
                                    json.getJsonValue().replaceAll("(\\r?\\n)+", "  ")))
                            .collect(Collectors.joining()));
        });
    }

    public List<MessageInfo> getMessageModel() throws IOException {
        List<MessageInfo> retval = new ArrayList<>();
        Connection conn = Jsoup.connect("https://developer.amazon.com/docs/music-skills/api-reference-overview.html");
        Element overviewLink = conn.get().body().selectFirst("a[href=\"../music-skills/api-reference-overview.html\"]");
        List<String> links = Optional.ofNullable(overviewLink.parent())
                .map(Element::parent).<List<Element>>map(Element::children).orElse(Collections.emptyList())
                .stream()
                .map(li -> li.selectFirst("a").attr("abs:href"))
                .filter(link -> !overviewLink.attr("abs:href").equalsIgnoreCase(link) &&
                        !link.equalsIgnoreCase("https://developer.amazon.com/docs/music-skills/api-error-response.html") &&
                        !link.equalsIgnoreCase(
                                "https://developer.amazon.com/docs/music-skills/api-components-reference.html"))
                .collect(Collectors.toList());
//        System.out.printf("Overview Link: %s%n", overviewLink.attr("abs:href"));
        links.forEach(link -> {
//            System.out.printf("Processing link %s.%n", link);
            try {
                Document document = Jsoup.connect(link).get();
                Element mainColumn = document.body().selectFirst("div.mainColumn");
                Element h1 = mainColumn.selectFirst("h1");
                StringBuilder description = new StringBuilder();
                for (Element candidate = h1.nextElementSibling(); candidate != null && candidate.is("p");
                     candidate = candidate.nextElementSibling()) {
                    description.append(String.format("%s%n", candidate.text()));
                }
                MessageClassInfo requestInfo =
                        getMessageClassInfo(getPropertyTable(mainColumn.selectFirst("h3#request-header")),
                                getPropertyTable(mainColumn.selectFirst("h3#request-payload")),
                                description.toString(),
                                document.body().selectFirst("h3[id~=example-.+-requests?]"),
                                Request.class);
                retval.add(new MessageInfo(requestInfo,
                        getMessageClassInfo(getPropertyTable(mainColumn.selectFirst("h3#response-header")),
                                getPropertyTable(mainColumn.selectFirst("h3#response-payload")),
                                String.format("@see %s", requestInfo.inferClassName()),
                                document.body().selectFirst("h3[id~=example-.+-responses?]"),
                                Response.class)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return retval;
    }

    public List<ComponentClassInfo> getComponentModel() throws IOException {
        List<ComponentClassInfo> retval = new ArrayList<>();
        Document document =
                Jsoup.connect("https://developer.amazon.com/docs/music-skills/api-components-reference.html").get();
        Element mainColumn = document.body().selectFirst("div.mainColumn");
        Elements componentSectionHeaders = mainColumn.select("h2");
        for (int i = 0; i < componentSectionHeaders.size(); i++) {
            Element componentSectionHeader = componentSectionHeaders.get(i);
            String name = componentSectionHeader.text();
            StringBuilder description = new StringBuilder();
            for (Element candidate = componentSectionHeader.nextElementSibling();
                 candidate != null && candidate.is("p"); candidate = candidate.nextElementSibling()) {
                description.append(String.format("%s%n", candidate.text()));
            }
            List<Element> structureSections = getComponentStructureSections(componentSectionHeader);
            if (structureSections.size() > 1) {
                System.err.printf(
                        "Ignoring component %s as it contains multiple structure sections which is not supported.%n",
                        name);
                continue;
            }
            List<JsonExample> jsonExamples = new ArrayList<>();
            getComponentExampleSections(componentSectionHeader).stream()
                    .map(this::getComponentJsonExamples)
                    .forEach(jsonExamples::addAll);
            retval.add(new ComponentClassInfo(name,
                    description.toString(),
                    structureSections.isEmpty() ?
                            Collections.emptyList() :
                            getPropertyInfo(getPropertyTable(structureSections.get(0))),
                    jsonExamples));
        }
        return retval;
    }

    private List<Element> getComponentStructureSections(Element componentSectionHeader) {
        List<Element> retval = new ArrayList<>();
        for (Element sibling = componentSectionHeader.nextElementSibling(); sibling != null;
             sibling = sibling.nextElementSibling()) {
            if (sibling.is("h3[id~=structure(-[0-9]+)?]")) {
                retval.add(sibling);
            } else if (sibling.is("h1,h2")) {
                break;
            }
        }
        return retval;
    }

    private List<Element> getComponentExampleSections(Element componentSectionHeader) {
        List<Element> retval = new ArrayList<>();
        for (Element sibling = componentSectionHeader.nextElementSibling(); sibling != null;
             sibling = sibling.nextElementSibling()) {
            if (sibling.is("h3[id~=examples(-[0-9]+)?]")) {
                retval.add(sibling);
            } else if (sibling.is("h1,h2")) {
                break;
            }
        }
        return retval;
    }

    private MessageClassInfo getMessageClassInfo(Element headerPropertyTable,
                                                 Element payloadPropertyTable,
                                                 String description,
                                                 Element examplesSectionHeader,
                                                 Class<? extends AbstractMessage> messageType) {
        return new MessageClassInfo(getMessageHeaderPropertyValueInfo(headerPropertyTable, "name"),
                description,
                getPropertyInfo(payloadPropertyTable),
                getMessgeJsonExamples(examplesSectionHeader),
                messageType,
                getMessageHeaderPropertyValueInfo(headerPropertyTable, "namespace"));
    }

    private String getMessageHeaderPropertyValueInfo(Element headerTable, String propertyName) {
        if (headerTable != null) {
            Map<String, Integer> headers = getTableHeaderMap(headerTable);
            for (Element row : headerTable.selectFirst("tbody").select("tr")) {
                Elements cols = row.select("td");
                UnaryOperator<String> getCellData =
                        s -> Optional.ofNullable(headers.get(s)).map(cols::get).map(Element::text).orElse(null);
                if (propertyName.equals(getCellData.apply("Field"))) {
                    return getCellData.apply("Value");
                }
            }
        }
        return null;
    }

    private List<PropertyInfo> getPropertyInfo(Element table) {
        List<PropertyInfo> retval = new ArrayList<>();
        if (table != null) {
            Map<String, Integer> headers = getTableHeaderMap(table);
            for (Element row : table.selectFirst("tbody").select("tr")) {
                Elements cols = row.select("td");
                UnaryOperator<String> getCellData =
                        s -> Optional.ofNullable(headers.get(s)).map(cols::get).map(Element::text).orElse(null);
                retval.add(new PropertyInfo(getCellData.apply("Field"),
                        getCellData.apply("Type"),
                        getCellData.apply("Description"),
                        getCellData.apply("Required?")));
            }
        }
        return retval;
    }

    private Map<String, Integer> getTableHeaderMap(Element table) {
        Map<String, Integer> retval = new HashMap<>();
        if (table != null) {
            Element tr = table.selectFirst("thead").selectFirst("tr");
            Elements thList = tr.select("th");
            for (int i = 0; i < thList.size(); i++) {
                retval.put(thList.get(i).text(), i);
            }
        }
        return retval;
    }

    private Element getPropertyTable(Element sectionHeader) {
        if (sectionHeader != null) {
            for (Element sibling = sectionHeader.nextElementSibling(); sibling != null && sibling.is("p,table");
                 sibling = sibling.nextElementSibling()) {
                if (sibling.is("table")) {
                    return sibling;
                }
            }
        }
        return null;
    }

    private List<JsonExample> getMessgeJsonExamples(Element sectionHeader) {
        List<JsonExample> retval = new ArrayList<>();
        if (sectionHeader != null) {
            StringBuilder description = new StringBuilder();
            for (Element sibling = sectionHeader.nextElementSibling(); sibling != null;
                 sibling = sibling.nextElementSibling()) {
                if (sibling.is("p")) {
                    description.append(String.format("%s%n", sibling.text()));
                } else if (sibling.is("div.language-json")) {
                    Element code = sibling.selectFirst("code");
                    if (code != null) {
                        retval.add(new JsonExample(code.text(), description.toString()));
                    }
                    description.setLength(0);
                } else if (sibling.is("h1,h2,h3")) {
                    break;
                }
            }
        }
        return retval;
    }

    private List<JsonExample> getComponentJsonExamples(Element sectionHeader) {
        List<JsonExample> retval = new ArrayList<>();
        if (sectionHeader != null) {
            StringBuilder description = new StringBuilder();
            for (Element sibling = sectionHeader.nextElementSibling(); sibling != null;
                 sibling = sibling.nextElementSibling()) {
                if (sibling.is("p")) {
                    description.append(String.format("%s%n", sibling.text()));
                } else if (sibling.is("div.language-json")) {
                    Element code = sibling.selectFirst("code");
                    if (code != null) {
                        retval.add(new JsonExample(code.text(), description.toString()));
                    }
                    description.setLength(0);
                } else if (sibling.is("h1,h2,h3")) {
                    break;
                }
            }
        }
        return retval;
    }
}
