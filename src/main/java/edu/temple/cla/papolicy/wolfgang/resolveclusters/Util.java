package edu.temple.cla.papolicy.wolfgang.resolveclusters;
import java.util.HashMap;
import java.util.Map;


/**
 * Class to contain static utility methods
 *
 * @author Paul Wolfgang
 */
public class Util {
    
    private static final Map<Character, String> xmlCharMap = new HashMap<>();
    private static final Map<String, Character> xmlCodeMap = new HashMap<>();
    
    static {
        String[][] xmlTransTable = new String[][] {
            {"<", "&lt;"},
            {">", "&gt;"},
            {"&", "&amp;"},
            {"\"", "&quot;"},
            {"'", "&apos;"}};
        for (String[] entry : xmlTransTable) {
            xmlCharMap.put(entry[0].charAt(0), entry[1]);
            xmlCodeMap.put(entry[1], entry[0].charAt(0));
        }
    }
    
    /** Method to remove multiple spaces and replace with a single space
     * @param line The line containing the text
     * @returns The line with extra spaces removed
     */
    public static String removeExtraSpaces(String line) {
        StringBuilder result = new StringBuilder();
        boolean seenSpace = false;
        for (int i = 0; i < line.length(); i++) {
            Character c = line.charAt(i);
            if (Character.isWhitespace(c)) {
                if (!seenSpace) {
                    result.append(' ');
                    seenSpace = true;
                }
            } else {
                if (seenSpace) {
                    seenSpace = false;
                }
                result.append(c);
            }
        }
        return result.toString();
    }
    
    /** Method to convert string content to XML format.
     *  @param source The source string
     *  @return The converted string
     */
    public static String convertToXML(String source) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            String replacement = xmlCharMap.get(c);
            if (replacement != null) {
                result.append(replacement);
            } else if (c > 127) {
                replacement = String.format("&#%d;", (int) c);
                result.append(replacement);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /** Method to convert string content from XML format.
     *  @param source The source string
     *  @return The converted string
     */
    public static String convertFromXML(String source) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (c == '&') {
                int nextSemi = source.indexOf(";", i);
                if (nextSemi != -1) {
                    String code = source.substring(i, nextSemi+1);
                    Character cprime = xmlCodeMap.get(code);
                    if (cprime != null) {
                        result.append(cprime);
                        i = nextSemi;
                    } else {
                        code = code.substring(2, code.length()-1);
                        try {
                            cprime = new Character((char)Integer.parseInt(code));
                            result.append(cprime);
                            i = nextSemi;
                        } catch (NumberFormatException ex) {
                            result.append(c);
                        }
                    }
                } else {
                    result.append(c);
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static String reformatHyperlink(String hyperlink) {
        int firstSharp = hyperlink.indexOf("#");
        if (firstSharp == -1) {
            return hyperlink;
        } else {
            int secondSharp = hyperlink.indexOf("#", firstSharp + 1);
            if (secondSharp == -1) {
                secondSharp = hyperlink.length();
            }
            return hyperlink.substring(firstSharp + 1, secondSharp);
        }
    }


}
