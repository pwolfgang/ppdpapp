/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.cla.policydb.ppdpapp.util;

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
