/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.cla.policydb.ppdpapp.api.controllers;

import edu.temple.cla.policydb.ppdpapp.api.tables.Table;
import edu.temple.cla.policydb.ppdpapp.api.tables.TableLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Paul Wolfgang
 */
@Controller
public class IndexController extends HttpServlet {

    @Autowired
    private TableLoader tableLoader;
    
    @RequestMapping(value="/", method=GET)
    public ModelAndView index(HttpServletRequest request,
            HttpServletResponse response) {
        List<Table> tables = getEditableTables(tableLoader);
        Map<String, Object> model = new HashMap<>();
        model.put("tables", tables);
        return new ModelAndView("index", model);
    }

    public static List<Table> getEditableTables(TableLoader tableLoader) {
        List<Table> tables = new ArrayList<>(tableLoader.getTables());
        Iterator<Table> itr = tables.listIterator();
        while (itr.hasNext()) {
            Table table = itr.next();
            if (!table.isEditable()) itr.remove();
        }
        return tables;
    }
    
}
