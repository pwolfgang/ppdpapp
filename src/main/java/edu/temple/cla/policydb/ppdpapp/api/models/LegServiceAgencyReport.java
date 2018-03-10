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

/**
 *
 * @author Paul
 */
public class LegServiceAgencyReport {
    
    private String ID;
    private String Title;
    private String Organization;
    private String Date;
    private String Hyperlink;
    private String Abstract;
    private String LegRequest;
    private String Recomendation;
    private String Tax;
    private String Elderly;
    private String FinalCode;

    /**
     * @return the ID
     */
    public String getID() {
        return ID;
    }

    /**
     * @param ID the ID to set
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * @return the Title
     */
    public String getTitle() {
        return Title;
    }

    /**
     * @param Title the Title to set
     */
    public void setTitle(String Title) {
        this.Title = Title;
    }

    /**
     * @return the Organization
     */
    public String getOrganization() {
        return Organization;
    }

    /**
     * @param Organization the Organization to set
     */
    public void setOrganization(String Organization) {
        this.Organization = Organization;
    }

    /**
     * @return the Date
     */
    public String getDate() {
        return Date;
    }

    /**
     * @param Date the Date to set
     */
    public void setDate(String Date) {
        this.Date = Date;
    }

    /**
     * @return the Hyperlink
     */
    public String getHyperlink() {
        return Hyperlink;
    }

    /**
     * @param Hyperlink the Hyperlink to set
     */
    public void setHyperlink(String Hyperlink) {
        this.Hyperlink = Hyperlink;
    }

    /**
     * @return the Abstract
     */
    public String getAbstract() {
        return Abstract;
    }

    /**
     * @param Abstract the Abstract to set
     */
    public void setAbstract(String Abstract) {
        this.Abstract = Abstract;
    }

    /**
     * @return the LegRequest
     */
    public String getLegRequest() {
        return LegRequest;
    }

    /**
     * @param LegRequest the LegRequest to set
     */
    public void setLegRequest(String LegRequest) {
        this.LegRequest = LegRequest;
    }

    /**
     * @return the Recomendation
     */
    public String getRecomendation() {
        return Recomendation;
    }

    /**
     * @param Recomendation the Recomendation to set
     */
    public void setRecomendation(String Recomendation) {
        this.Recomendation = Recomendation;
    }

    /**
     * @return the Tax
     */
    public String getTax() {
        return Tax;
    }

    /**
     * @param Tax the Tax to set
     */
    public void setTax(String Tax) {
        this.Tax = Tax;
    }

    /**
     * @return the Elderly
     */
    public String getElderly() {
        return Elderly;
    }

    /**
     * @param Elderly the Elderly to set
     */
    public void setElderly(String Elderly) {
        this.Elderly = Elderly;
    }

    /**
     * @return the FinalCode
     */
    public String getFinalCode() {
        return FinalCode;
    }

    /**
     * @param FinalCode the FinalCode to set
     */
    public void setFinalCode(String FinalCode) {
        this.FinalCode = FinalCode;
    }
    
    

}
