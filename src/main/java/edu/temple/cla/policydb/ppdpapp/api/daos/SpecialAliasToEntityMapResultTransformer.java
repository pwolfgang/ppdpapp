/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.cla.policydb.ppdpapp.api.daos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.transform.ResultTransformer;

/**
 * This is a special version of the AliasToEntityMapResultTransformer that is
 * used to transform queries of the document tables. In some document tables
 * the ID field is an integer and in others it is a string.  This transformer
 * will convert all ID fields to a string, and leave the others alone.
 * @author Paul Wolfgang
 */
public class SpecialAliasToEntityMapResultTransformer implements ResultTransformer {
    
    public static SpecialAliasToEntityMapResultTransformer INSTANCE = new SpecialAliasToEntityMapResultTransformer();
    
    private SpecialAliasToEntityMapResultTransformer() {}
    
    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Map<String, Object> result = new HashMap<>(tuple.length);
        for (int i = 0; i < tuple.length; i++) {
            String alias = aliases[i];
            if (aliases != null) {
                if (alias.equals("ID")) {
                    result.put(alias, tuple[i].toString());
                } else {
                    result.put(alias, tuple[i]);
                }
            }
        }
        return result;
    }
    
    @Override
    public List transformList(List collection) {
        return collection;
    }
    
}
