/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.cla.papolicy.ppdpapp.template;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to perform simple template expansion.
 *
 * @author Paul
 */
public class Template {

    private final Map<String, String> variables;
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{[^}]*\\}");
    private static final Pattern IF_PATTERN = Pattern.compile("\\$if\\(([^\\)]+)\\)(.*)");

    /**
     * Construct a new Template object
     *
     * @param variables A map of the variable values
     */
    public Template(Map<String, String> variables) {
        this.variables = variables;
    }

    /**
     * Replace each variable with it value.
     * @param line The input line
     * @return The input with all variables replaced with their values.
     */
    public String evaluate(String line) {
        // Lines beginning with $if(variable)text are skpped if the variable
        // is an empty string or not defined.
        Matcher m = IF_PATTERN.matcher(line);
        if (m.find()) {
            String variable = m.group(1);
            if (variables.get(variable) == null || variables.get(variable).isEmpty()) {
                return "";
            } else {
                line = m.group(2);
            }
        }
        m = PLACEHOLDER_PATTERN.matcher(line);
        StringBuilder sb = new StringBuilder();
        int index = 0;
        while (m.find()) {
            String matched = m.group();
            String variable = matched.substring(2, matched.length()-1);
            String value = variables.getOrDefault(variable, "");
            if (index != m.start()) {
                sb.append(line.substring(index, m.start()));
            }
            sb.append(value);
            index = m.end();
        }
        if (index < line.length()) {
            sb.append(line.substring(index));
        }
        return sb.toString();
    }

}
