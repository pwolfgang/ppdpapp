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

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Before;

/**
 *
 * @author Paul
 */
public class TemplateTest {
    
    private Map<String, String> variables;
    
    public TemplateTest() {
    }
    
    @Before
    public void setUp() {
        variables = new HashMap<>();
        variables.put("true", "t");
        variables.put("false", "");
        variables.put("one", "1");
        variables.put("two", "2");
    }

    @Test
    public void falseReturnsAnEmptyLine() {
        Template instance = new Template(variables);
        String result = instance.evaluate("$if(false)The Quick Brown Fox");
        String expected = "";
        assertEquals(expected, result);
    }
    
    @Test 
    public void trueReturnsFollowingText() {
        Template instance = new Template(variables);
        String result = instance.evaluate("$if(true)The Quick Brown Fox");
        String expected = "The Quick Brown Fox";
        assertEquals(expected, result);
    }
    
    @Test
    public void replaceOneVariable() {
        Template instance = new Template(variables);
        String result = instance.evaluate("There is ${one} brown fox");
        String expected = "There is 1 brown fox";
        assertEquals(expected, result);        
    }
    
    @Test
    public void replaceVariableAfterTrue() {
        Template instance = new Template(variables);
        String result = instance.evaluate("$if(true)There is ${one} brown fox");
        String expected = "There is 1 brown fox";
        assertEquals(expected, result);                
    }
    
    @Test
    public void lineOnlyConainsAVariable() {
        Template instance = new Template(variables);
        String result = instance.evaluate("${one}");
        String expected = "1";
        assertEquals(expected, result);                        
    }
    
    @Test
    public void valueContainsABackSlash() {
        variables.put("foo", "'date:\\'mediumDate\\''");
        Template instance = new Template(variables);
        String result = instance.evaluate("${foo}");
        String expected = "'date:\\'mediumDate\\''";
        assertEquals(expected, result);
    }
    
    @Test
    public void valueIsMultiLine() {
        String s = "1";
        String v = "            var lastDate" + s + " = localStorage.getItem('lastDate" + s + "');\n"
                + "            if (lastDate" + s + " === null) {\n"
                + "                $scope.dt" + s + " = new Date();\n"
                + "            } else {\n"
                + "                $scope.dt" + s + " = new Date(lastDate);\n"
                + "            }\n";
        variables.put("foo", v);
        Template instance = new Template(variables);
        String result = instance.evaluate("${foo}");
        assertEquals(v, result);
    } 
    
}
