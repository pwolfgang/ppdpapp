/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.cla.papolicy.wolfgang.resolveclusters;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 *
 * @author Paul Wolfgang
 */
public class IndexController extends AbstractController {
    
    private JdbcTemplate jdbcTemplate;

    public IndexController() {
    }

    @Override
    protected ModelAndView handleRequestInternal(
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();
        String query = "select ID, Abstract, Hyperlink, Code, Cluster from Bills_Data_1977_12 "
                + "where not isNull(Cluster) order by Cluster, ID desc";
        List<Map<String, Object>> theList = jdbcTemplate.queryForList(query);
        List<List<String[]>> clusters = new ArrayList<>();
        Integer currentClusterId = null;
        List<String[]> currentCluster = null;
        for (Map<String, Object> row : theList) {
            String ID = (String)row.get("ID");
            String theAbstract = (String)row.get("Abstract");
            String hyperlink = (String)row.get("Hyperlink");
            Integer code = (Integer)row.get("Code");
            Integer clusterId = (Integer)row.get("Cluster");
            if (!clusterId.equals(currentClusterId)) {
                if (currentCluster != null) {
                    clusters.add(currentCluster);
                }
                currentClusterId = clusterId;
                currentCluster = new ArrayList<>();
            }
            String[] currentBill = new String[4];
            hyperlink = Util.reformatHyperlink(hyperlink);
            currentBill[0] = String.format("<a href=\"%s\">%s</a>", hyperlink, ID);
            currentBill[1] = Util.convertFromXML(theAbstract);
            currentBill[2] = String.format("<input type=\"text\" name=\"%s\" value=\"%d\"\n" +
                                           "onblur=\"update(this.name, this.value)\"/>", ID, code);
            currentBill[3] = clusterId.toString();
            currentCluster.add(currentBill);
        }
        clusters.add(currentCluster);
        PrintWriter out = new PrintWriter(response.getOutputStream());
        DisplayClustersInTable.printHTMLHeader(out, "Cluster Resolution");
        DisplayClustersInTable.printTableHeader(out);
        for (List<String[]> cluster : clusters) {
            DisplayClustersInTable.processCluster(cluster, out);
        }
        DisplayClustersInTable.printTableFooter(out);
        out.flush();
        return null;
    }

    /**
     * Set the jdbcTemplate from the parameter in the dispatcher-servlet.xml
     * file. (Called by the Spring framework.)
     *
     * @param jdbcTemplate the jdbcTemplate to set
     */
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
