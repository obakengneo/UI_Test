package main.java.testing.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestEntity {
    private String TestCaseId;
    private String TestMethod;
    private String TestDescription;
    private Map<String, String> TestParameters;
    private Map<String, ArrayList<String>> ExtractedParameters;
    public TestEntity() {}
    public void setTestCaseID(String id){this.TestCaseId = id;}
    public void setTestMethod(String testMethod){this.TestMethod = testMethod;}
    public void setTestDescription(String description){this.TestDescription = description;}
    public String getTestCaseID(){return this.TestCaseId;}
    public String getTestMethod(){
        return this.TestMethod;
    }
    public String getTestDescription(){return this.TestDescription;}
    public Map<String, ArrayList<String>> getExtractedParams(){return this.ExtractedParameters;}
    public void addParameter(String parameterName, String parameterValue) {
        if (TestParameters == null) {
            this.TestParameters = new HashMap<String, String>();
        }
        this.TestParameters.put(parameterName, parameterValue);
    }
    public String getData(String parameterName) {
        String returnedValue = this.TestParameters.get(parameterName);

        if (returnedValue == null) {
            System.err.println(" Parameter ' " + parameterName + " ' not found");
            returnedValue = "";
        }
        return returnedValue;
    }
}
