/* 
 * Copyright (c) 2018, Temple University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * All advertising materials features or use of this software must display 
 *   the following  acknowledgement
 *   This product includes software developed by Temple University
 * * Neither the name of the copyright holder nor the names of its 
 *   contributors may be used to endorse or promote products derived 
 *   from this software without specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
