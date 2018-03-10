/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
