/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.cia.policydb.ppdpapp.util;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Paul
 */
public class BillsUtilTest {
    
    public BillsUtilTest() {
    }

    @Test
    public void testGenURLfromId() {
        String billId="20090HB0221";
        String expected = "http://www.legis.state.pa.us/cfdocs/billinfo/billinfo.cfm?syear=2009&sInd=0&body=H&type=B&bn=0221";
        String actual = BillsUtil.genURLfromId(billId);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testCreateBillLinks() {
        List<String> billIdList = Arrays.asList("20090HB0221", "20090SR0001");
        String expected = "<a href=\"http://www.legis.state.pa.us/cfdocs/billinfo/billinfo.cfm?syear=2009&sInd=0&body=H&type=B&bn=0221\">20090HB0221</a><br/>\n" +
                "<a href=\"http://www.legis.state.pa.us/cfdocs/billinfo/billinfo.cfm?syear=2009&sInd=0&body=S&type=R&bn=0001\">20090SR0001</a>";
        String actual=BillsUtil.createBillLinks(billIdList);
        assertEquals(expected, actual);
    }
    
}
