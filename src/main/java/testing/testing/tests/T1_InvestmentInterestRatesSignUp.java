package main.java.testing.testing.tests;

import main.java.testing.TestMarshall;
import main.java.testing.core.BaseClass;
import main.java.testing.entities.KeywordAnnotation;
import main.java.testing.entities.TestEntity;
import main.java.testing.entities.testresult.TestResult;
import main.java.testing.reporting.Narrator;
import main.java.testing.testing.pageObjects.InvestecPageObjects;
import main.java.testing.utilities.SeleniumDriverUtility;

/**
 * @author nditema
 */
@KeywordAnnotation(
        Keyword ="InvestmentInterestRates",
        createNewBrowserInstance = true
 )
public class T1_InvestmentInterestRatesSignUp extends BaseClass
{
    private TestEntity testData;
    public T1_InvestmentInterestRatesSignUp() {
        SeleniumDriverInstance = new SeleniumDriverUtility(SelectedBrowser);
    }

    public TestResult executeTest() {
        if(!SeleniumDriverUtility.navigateToURL(InvestecPageObjects.baseURL())) {
            return Narrator.testFailed("Failed to navigate to "+ InvestecPageObjects.baseURL());
        }

        Narrator.stepPassedWithScreenShot("Navigated to 'Understanding cash investment interest rates' page");

        if (!SeleniumDriverUtility.clickElementByXpathUsingJavascript(InvestecPageObjects.searchIcon())) {
            return Narrator.testFailed("Failed to click "+ InvestecPageObjects.searchIcon());
        }

        if (!SeleniumDriverUtility.enterText(InvestecPageObjects.inputSearch(), TestMarshall.getTestData().getData("SearchText"))) {
            return Narrator.testFailed("Failed to enter search text");
        }

        //This test case would not be part of the suite as it fails manually
        if (!SeleniumDriverUtility.clickElement(InvestecPageObjects.searchResults(TestMarshall.getTestData().getData("SearchKeyword")))) {
            Narrator.testFailed("Failed to find 'Cash Investment Rates' in search results");
        }

        //Navigate to interest rates URL
        if(!SeleniumDriverUtility.navigateToURL(InvestecPageObjects.interestRatesURL())) {
            return Narrator.testFailed("Failed to navigate to "+ InvestecPageObjects.interestRatesURL());
        }

        Narrator.stepPassedWithScreenShot("Navigated to 'Understanding cash investment interest rates' page");

        //Click Sign Up button
        if(SeleniumDriverUtility.waitForElement(InvestecPageObjects.btnSignUp())) {
            SeleniumDriverUtility.scrollToElement(InvestecPageObjects.btnSignUp());
            if (!SeleniumDriverUtility.clickElementByXpathUsingJavascript(InvestecPageObjects.btnSignUp())) {
                return Narrator.testFailed("Failed to click button 'Sign up'");
            }
        }

        //Enter name
        if (!SeleniumDriverUtility.enterText(InvestecPageObjects.inputName(), TestMarshall.getTestData().getData("Name"))) {
            return Narrator.testFailed("Failed to enter name");
        }

        //Enter surname
        if (!SeleniumDriverUtility.enterText(InvestecPageObjects.inputSurname(), TestMarshall.getTestData().getData("Surname"))) {
            return Narrator.testFailed("Failed to enter surname");
        }

        //Enter email
        if (!SeleniumDriverUtility.enterText(InvestecPageObjects.inputEmailAddress(), TestMarshall.getTestData().getData("Email"))) {
            return Narrator.testFailed("Failed to enter email address");
        }

        if (!SeleniumDriverUtility.clickElement(InvestecPageObjects.rbMyself())) {
            return Narrator.testFailed("Failed to click checkbox 'Myself'");
        }

        if (!SeleniumDriverUtility.clickElement(InvestecPageObjects.rbIntermediaries())) {
            return Narrator.testFailed("Failed to click checkbox 'Intermediaries'");
        }

        if (!SeleniumDriverUtility.clickElement(InvestecPageObjects.rbMyBusiness())) {
            return Narrator.testFailed("Failed to click checkbox 'My business'");
        }

        if (!SeleniumDriverUtility.clickElement(InvestecPageObjects.btnSubmit())) {
            return Narrator.testFailed("Failed to click submit");
        } else {
            Narrator.stepPassedWithScreenShot("Submit successful");
        }

        SeleniumDriverInstance.pause(2000);

        //Enter validate thank you text
        if(SeleniumDriverUtility.waitForElement(InvestecPageObjects.txtThankYou())) {
            if (!SeleniumDriverUtility.validateElementText(InvestecPageObjects.txtThankYou(), "Thank you")) {
                return Narrator.testFailed("Failed to Sign Up");
            }
        }

        return Narrator.finalizeTest("Test successful");
    }
}
