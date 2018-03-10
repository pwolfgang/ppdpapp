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

import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to work with Bills Data
 * @author Paul
 */
public class BillsUtil {
    
    private static final String billURLFormat = "http://www.legis.state.pa.us/cfdocs/billinfo/billinfo.cfm?syear=%s&sInd=%s&body=%s&type=%s&bn=%s";
    private static final Pattern billIDPattern = Pattern.compile("(\\d{4})(\\d)(\\w)(\\w)(\\d{4})");
    
    public static String genURLfromId(String id) {
        Matcher matcher = billIDPattern.matcher(id);
        if (matcher.matches()) {
            String year = matcher.group(1);
            int intYear = Integer.parseInt(year);
            if (intYear % 2 == 0) intYear--;
            year = Integer.toString(intYear);
            String session = matcher.group(2);
            String chamber = matcher.group(3);
            String type = matcher.group(4);
            String number = matcher.group(5);
            String result = String.format(billURLFormat, year, session, chamber, type, number);
            return result;
        } else {
            throw new RuntimeException(id + " is not a vallid bill id");
        }
    }
    
    /**
     * Ensure that all of the year part of the BillId's are odd.
     *
     * @param billIdList List of BillId's to be modified.
     */
    private static String fixBillId(String billId) {
        int billYear = Integer.parseInt(billId.substring(0, 4));
        if (billYear % 2 == 0) {
            billYear--;
            billId = Integer.toString(billYear) + billId.substring(4);
        }
        return billId;
    }

    
    public static String createBillLinks(List<String> billIdList) {
        StringJoiner sj = new StringJoiner("<br/>\n");
        billIdList.forEach(id -> {
            id = fixBillId(id);
            sj.add(String.format("<a href=\"%s\">%s</a>", genURLfromId(id), id));});
        String result = sj.toString();
        return result;
    }
    
}
