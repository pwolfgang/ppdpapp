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
package edu.temple.cla.policydb.ppdpapp.api.filters;

/**
 * Filter to select the committee(s) that held hearings on a bill.
 * @author Paul Wolfgang
 */
public class BillsCommittee extends Filter {

    private final String parameterName;
    private final String primaryName;
    private String parameterValue;
    private String primaryValue;
    private String filterQualifier;
    private final String chamberNumber;

    /**
     * Construct a BillsCommittee object
     * @param id The unique id
     * @param tableId The table ID
     * @param description The description
     * @param columnName Null -- not used by this class
     * @param tableReference Reference to the CommitteeAliases table
     * @param additionalParam House or Senate
     */
    public BillsCommittee(int id, int tableId, String description, 
            String columnName, String tableReference, String additionalParam) {
        super(id, tableId, description, columnName, tableReference,
                additionalParam);
        parameterName = "F" + getId();
        if (getAdditionalParam().equals("House")) {
            chamberNumber = "1";
            primaryName = "hprimary";
        } else {
            chamberNumber = "2";
            primaryName = "sprimary";
        }
    }
    
    @Override
    public String getFilter() {
        return null;
    }
    
    @Override
    public String getConvertToBoolInt(int i) {
        return null;
    }
    
    @Override
    public String getSetFilterValue(int i) {
        return null;
    }

}
