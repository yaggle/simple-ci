package co.yaggle.simpleci.server.pipeline.parser;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class XmlUtil {

    /**
     * Get the {@link Node}s inside an element.
     *
     * @param element an {@link Element}
     * @return the nodes inside the element
     */
    public static List<Node> nodesInside(Element element) {
        List<Node> nodes = new ArrayList<>();
        for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
            nodes.add(node);
        }
        return nodes;
    }


    /**
     * Get a list of the elements inside the specified element.
     *
     * @param element an element
     * @return the elements inside the element
     */
    public static List<Element> elementsInside(Element element) {
        return nodesInside(element)
                .stream()
                .filter(Element.class::isInstance)
                .map(Element.class::cast)
                .collect(Collectors.toList());
    }


    /**
     * Get the lines of text inside the element, trimming blank space around them
     * and omitting blank lines.
     *
     * @param element an element
     * @return the lines of text inside the element
     */
    public static List<String> textLinesInside(Element element) {
        return new BufferedReader(new StringReader(element.getTextContent()))
                .lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
    }
}
