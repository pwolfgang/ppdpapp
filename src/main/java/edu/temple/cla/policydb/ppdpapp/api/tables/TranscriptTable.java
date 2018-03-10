/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.cla.policydb.ppdpapp.api.tables;

/**
 *
 * @author Paul Wolfgang
 */
public class TranscriptTable extends AbstractTable {
    
    @Override
    public String getTextFieldsHtml() {
        StringBuilder stb = new StringBuilder(super.getTextFieldsHtml());
        stb.append("<div class=\"row\">\n");
        stb.append("<div class=\"col-md-4\">\n");
        stb.append("<p>Committees</p>");
        stb.append("<p ng-bind-html=\"Committees\"></p>");
        stb.append("</div>");
        stb.append("<div class=\"col-md-4\">\n");
        stb.append("<p>Bills</p>");
        stb.append("<p ng-bind-html=\"Bills\"</p>");
        stb.append("</div>");
        stb.append("<div class=\"col-md-4\">\n");
        stb.append("<p>WitnessTranscriptURLs</p>");
        stb.append("<p ng-bind-html=\"WitnessTranscriptURLs\"></p>");
        stb.append("</div>");
        stb.append("</div>\n");
        return stb.toString();
    }
    
    @Override
    public String getTextFields() {
        StringBuilder stb = new StringBuilder(super.getTextFields());
        stb.append(",\n");
        stb.append("Committees: $scope.Committees,\n");
        stb.append("Bills: $scope.Bills,\n");
        stb.append("WitnessTranscriptURLs: $scope.WitnessTranscriptURLs");
        return stb.toString();
    }
    
    @Override
    public String getTextFieldsSetValues() {
        StringBuilder stb = new StringBuilder(super.getTextFieldsSetValues());
        stb.append("\n");
        stb.append("$scope.Committees = res.Committees;\n");
        stb.append("$scope.Bills = res.Bills;\n");
        stb.append("$scope.WitnessTranscriptURLs = res.WitnessTranscriptURLs;");
        return stb.toString();
    }
    
    
}
