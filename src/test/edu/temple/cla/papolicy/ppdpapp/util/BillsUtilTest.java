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
