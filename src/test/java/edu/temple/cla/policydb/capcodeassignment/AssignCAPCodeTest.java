/*
 * Copyright (c) 2019, Temple University
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
package edu.temple.cla.policydb.capcodeassignment;

import edu.temple.cla.policydb.ppdpapp.api.daos.FileDAO;
import edu.temple.cla.policydb.ppdpapp.api.filters.Filter;
import edu.temple.cla.policydb.ppdpapp.api.models.MetaData;
import edu.temple.cla.policydb.ppdpapp.api.tables.Table;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Paul
 */
public class AssignCAPCodeTest {
    
    public AssignCAPCodeTest() {
    }


    /**
     * Test of createQuery method, of class AssignCAPCode.
     */
    @Test
    public void testCreateQuery() {
        Table table = new TestTable();
        Criteria criteria = new Criteria(5, "Local Taxes", 24, "taxes or revenue" 
                , "tax", 24, 1); 
        String expected = "UPDATE NewsClips SET CAPCode=1, CAPOk=0 WHERE "
                + "Code=24 AND (Abstract LIKE('%taxes%') OR Abstract LIKE('%revenue%')) AND tax<>0";
        String actual = AssignCAPCode.createQuery(criteria, table);
        assertEquals(expected, actual); 
    }
    
    private static class TestTable implements Table {

        @Override
        public int getId() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setId(int id) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getTableName() {
            return "NewsClips";
        }

        @Override
        public void setTableName(String tableName) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean isCode3() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getTableTitle() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setTableTitle(String tableTitle) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean isMajorOnly() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setMajorOnly(boolean majorOnly) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public int getMinYear() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setMinYear(int minYear) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public int getMaxYear() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setMaxYear(int maxYear) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public List<Filter> getFilterList() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public int getFilterListSize() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setFilterList(List<Filter> filterList) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public List<MetaData> getMetaDataList() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setMetaDataList(List<MetaData> metaDataList) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public char getQualifier() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setQualifier(char qualifier) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getTextColumn() {
            return "Abstract";
        }

        @Override
        public void setTextColumn(String textColumn) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getLinkColumn() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setLinkColumn(String linkColumn) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getCodeColumn() {
            return "Code";
        }

        @Override
        public void setCodeColumn(String codeColumn) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getDateColumn() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setDateColumn(String dateColumn) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getDateFormat() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setDateFormat(String dateFormat) {
            throw new UnsupportedOperationException("Not supported."); 
        }

        @Override
        public String getYearColumn() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setYearColumn(String yearColumn) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String[] getDrillDownColumns() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setDrillDownColumns(String[] drillDownColumns) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String[] getCodingColumns() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getCodingColumnsList() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setCodingColumns(String[] codingColumns) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setNoteColumn(String noteColumn) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getNoteColumn() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean isDataEntry() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setDataEntry(boolean isDataEntry) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean isEditable() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setEditable(boolean isEditable) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean isRequired() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setRequired(boolean isRequired) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Set<String> getColumns() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setColumns(Collection<String> columns) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getDocumentName() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setDocumentName(String documentName) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public int getNumCodesRequired() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setNumCodesRequired(int numCodesRequired) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Map<String, String> getTemplateParameters() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public ResponseEntity<?> uploadFile(String docObjJson, MultipartFile file) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public ResponseEntity<?> uploadFile(FileDAO fileDAO, MultipartFile file) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public ResponseEntity<?> publishDataset() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public ResponseEntity<?> updateCodes() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public ResponseEntity<?> updateAll() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public ResponseEntity<?> assignCAPCode() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setSessionFactory(SessionFactory sessionFactory) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getFileUploadHtml() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getFileUploadJavaScript() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public SessionFactory getSessionFactory() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setDataSource(DataSource datasource) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public DataSource getDataSource() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
    
}
