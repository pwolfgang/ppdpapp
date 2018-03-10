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
package edu.temple.cla.policydb.ppdpapp.api.models;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToEntityMapResultTransformer;

/**
 *
 * @author Paul
 */
public class TypeAheadData {
    
    private final String columnName;
    private final String tableName;
    private final String fieldName;
    private final String fieldNamePlural;
    private String nameField;
    private String typeAheadFieldValues;
    
    public TypeAheadData(String columnName, String tableName) {
        this.columnName = columnName;
        this.tableName = tableName;
        fieldName = tableName.toLowerCase();
        fieldNamePlural = fieldName + "s";
    }

    public void loadTypeAheadData(Session sess) {
        sess.doWork((Connection connection) -> {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet rs = meta.getPrimaryKeys(null, null, tableName);
            String primaryKey = null;
            while (rs.next()) {
                primaryKey = rs.getString("COLUMN_NAME");
            }
            rs = meta.getColumns(null, null, tableName, null);
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                if (!columnName.equals(primaryKey)) {
                    nameField = columnName;
                }
            }
        });
        SQLQuery query = sess.createSQLQuery("select * from " + tableName);
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        List<Map<String, Object>> typeAheadDataObjects = query.list();
        StringJoiner stj = new StringJoiner(",\n", "[", "]");
        typeAheadDataObjects.forEach((Map<String, Object> typeAheadDataObject) -> {
            StringJoiner sj2 = new StringJoiner(",", "{", "}");
            typeAheadDataObject.forEach((String k, Object v) -> {
                StringBuilder stb = new StringBuilder();
                stb.append("\"").append(k).append("\"");
                stb.append(":");
                if (v instanceof Number) {
                    stb.append(v);
                } else {
                    stb.append("\"").append(v).append("\"");
                }
                sj2.add(stb);
            });
            String entry = sj2.toString();
            stj.add(entry);
        });
        typeAheadFieldValues = stj.toString();
    }
    
    public String getTypeAheadFieldJs() {
        return "$scope." + fieldNamePlural + "=" + typeAheadFieldValues + ";\n";
    }
    
    public String getTypeAheadFieldHtml() {
        StringBuilder stb = new StringBuilder()
                .append("        <div class=\"form-group row\"><div class=\"col-md-12\">\n")
                .append("<p>").append(columnName).append("</p>\n")
                .append("                <input type=\"text\" ng-model=\"")
                .append(columnName)
                .append("\" typeahead=\"")
                .append(fieldName)
                .append(".")
                .append(nameField)
                .append(" for ")
                .append(fieldName)
                .append(" in ")
                .append(fieldNamePlural)
                .append(" | filter:$viewValue\" class=\"form-control\" placeholder=\"")
                .append(columnName)
                .append("\">\n")
                .append("            </div></div>\n");
        return stb.toString();
    }
}
