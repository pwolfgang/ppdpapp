package edu.temple.cla.papolicy.wolfgang.resolveclusters;

import java.io.PrintWriter;
import java.util.List;
import org.incava.util.diff.Diff;
import org.incava.util.diff.Difference;

public class DisplayClustersInTable {

    public static void processCluster(List<String[]> cluster, PrintWriter out) {
        if (cluster.isEmpty()) {
            return;
        }
        String[] firstRow = cluster.get(0);
        out.println("<tr>");
        for (String col : firstRow) {
            out.println("<td>" + col + "</td>");
        }
        out.println("</tr>");
        String[] list1 = Util.convertToXML(firstRow[1]).split("\\s+");
        for (int i = 1; i < cluster.size(); i++) {
            String[] currentRow = cluster.get(i);
            String[] list2 = Util.convertToXML(currentRow[1]).split("\\s+");
            String diffString = genDiffString(list1, list2);
            out.println("<tr>");
            for (int j = 0; j < currentRow.length; j++) {
                if (j != 1) {
                    out.println("<td>" + currentRow[j] + "</td>");
                } else {
                    out.println("<td>" + diffString + "</td>");
                }
            }
            out.println("</tr>");
        }
        out.println("<tr><td colspan=\"4\" bgcolor=\"#ffff00\">&nbsp</td></tr>");
    }

    public static String genDiffString(String[] list1, String[] list2) {
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
