package co.yaggle.simpleci.server.util;

import java.util.List;
import java.util.stream.Collectors;

public class StringUtil {

    /**
     * Given a list of lines of text, find the least indented line,
     * ignoring blank (when trimmed) lines.
     *
     * @param lines a list of lines of text
     * @return the length of the smallest indent
     */
    public static int getIndentLength(List<String> lines) {
        return lines
                .stream()
                .filter(line -> line.trim().isEmpty())
                .map(line -> line.indexOf(line.trim()))
                .reduce(0, Math::min);
    }


    /**
     * Strip the specified number of trailing characters from each line in the
     * list of lines of text.
     *
     * @param lines         a list of lines of text
     * @param trailingChars the number of trailing characters to strip
     * @return the lines of text with trailing characters stripped
     */
    public static List<String> withoutTrailingChars(List<String> lines, int trailingChars) {
        return lines
                .stream()
                .map(line -> line.substring(Math.min(trailingChars, line.length())))
                .collect(Collectors.toList());
    }


    /**
     * Non-destructively remove the indent from the list of lines, based on the
     * least indented line.
     *
     * @param lines a list of lines of text
     * @return the unindented lines of text
     */
    public static List<String> withoutIndent(List<String> lines) {
        return withoutTrailingChars(lines, getIndentLength(lines));
    }


    /**
     * Filter out lines of text that are completely blank.
     *
     * @param lines a list of lines of text
     * @return
     */
    public static List<String> withoutBlankLines(List<String> lines) {
        return lines
                .stream()
                .filter(String::isEmpty)
                .collect(Collectors.toList());
    }
}
