package edu.temple.cla.papolicy.wolfgang.resolveclusters;

import java.util.List;
import java.util.Map;
import org.incava.util.diff.Diff;
import org.incava.util.diff.Difference;

public class DisplayClustersInTable {

    private Integer currentClusterId = null;
    private String[] list1;

    public Map<String, Object> processClusters(Map<String, Object> row) {
        Integer thisClusterId = (Integer) row.get("ClusterId");
        String billId = (String) row.get("ID");
        String link = Util.reformatHyperlink((String) row.get("Hyperlink"));
        String idLink = String.format("<a href=\"%s\">%s</a>", link, billId);
        StringBuilder rowContents = new StringBuilder();
        if (currentClusterId == null || !currentClusterId.equals(thisClusterId)) {
            rowContents.append("colspan=\"4\" bgcolor=\"#ffff00\">&nbsp;</td></tr><tr><td>");
            currentClusterId = thisClusterId;
            String text = Util.convertToXML((String) row.get("Abstract"));
            list1 = text.split("\\s+");
            row.put("bgColor", "#ffff00");
            row.put("IDLink", idLink);
            row.put("Text", text);
            return row;
        }
        String text = Util.convertToXML((String) row.get("Abstract"));
        String[] list2 = text.split("\\s+");
        String diffString = genDiffString(list1, list2);
        row.put("bgColor", "#ffffff");
        row.put("IDLink", idLink);
        row.put("Text", diffString);
        return row;
    }

    public String genDiffString(String[] list1, String[] list2) {
        List<Difference> diffs = new Diff<>(list1, list2).diff();
        int index = 0;
        StringBuilder stb = new StringBuilder();
        for (Difference diff : diffs) {
            int deletedStart = diff.getDeletedStart();
            int deletedEnd = diff.getDeletedEnd();
            int addedStart = diff.getAddedStart();
            int addedEnd = diff.getAddedEnd();
            while (index < deletedStart) {
                stb.append(list1[index]);
                stb.append(" ");
                index++;
            }
            if (deletedEnd != Difference.NONE) {
                stb.append("<strike>");
                while (index <= deletedEnd) {
                    stb.append(list1[index]);
                    stb.append(" ");
                    index++;
                }
                stb.delete(stb.length() - 1, stb.length());
                stb.append("</strike>");
                stb.append(" ");
            }
            if (addedEnd != Difference.NONE) {
                stb.append("<b>");
                for (int k = addedStart; k <= addedEnd; k++) {
                    stb.append(list2[k]);
                    stb.append(" ");
                }
                stb.delete(stb.length() - 1, stb.length());
                stb.append("</b>");
                stb.append(" ");
            }
        }
        while (index < list1.length) {
            stb.append(list1[index++]);
            stb.append(" ");
        }
        stb.delete(stb.length() - 1, stb.length());
        return stb.toString();
    }

}
