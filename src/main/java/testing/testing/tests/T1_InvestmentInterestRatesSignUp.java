package main.java.testing.testing.tests;

import main.java.testing.TestMarshall;
import main.java.testing.core.BaseClass;
import main.java.testing.entities.KeywordAnnotation;
import main.java.testing.entities.TestEntity;
import main.java.testing.entities.testresult.TestResult;
import main.java.testing.reporting.Narrator;
import main.java.testing.testing.pageObjects.ReusableObjects;
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
        if(!SeleniumDriverUtility.navigateToURL(ReusableObjects.baseURL())) {
            return Narrator.testFailed("Failed to navigate to "+ ReusableObjects.baseURL());
        }

        Narrator.stepPassedWithScreenShot("Navigated to 'Understanding cash investment interest rates' page");

        if (!SeleniumDriverUtility.clickElementByXpathUsingJavascript(ReusableObjects.searchIcon())) {
            return Narrator.testFailed("Failed to click "+ ReusableObjects.searchIcon());
        }

        if (!SeleniumDriverUtility.enterText(ReusableObjects.inputSearch(), TestMarshall.getTestData().getData("SearchText"))) {
            return Narrator.testFailed("Failed to enter search text");
        }

        //This test case would not be part of the suite as it fails manually
        if (!SeleniumDriverUtility.clickElement(ReusableObjects.searchResults(TestMarshall.getTestData().getData("SearchKeyword")))) {
            Narrator.testFailed("Failed to find 'Cash Investment Rates' in search results");
        }

        //Navigate to interest rates URL
        if(!SeleniumDriverUtility.navigateToURL(ReusableObjects.interestRatesURL())) {
            return Narrator.testFailed("Failed to navigate to "+ ReusableObjects.interestRatesURL());
        }

        Narrator.stepPassedWithScreenShot("Navigated to 'Understanding cash investment interest rates' page");

        if(SeleniumDriverUtility.waitForElement(ReusableObjects.btnSignUp())) {
            SeleniumDriverUtility.scrollToElement(ReusableObjects.btnSignUp());
            if (!SeleniumDriverUtility.clickElementByXpathUsingJavascript(ReusableObjects.btnSignUp())) {
                return Narrator.testFailed("Failed to click button 'Sign up'");
            }
        }

        if (!SeleniumDriverUtility.enterText(ReusableObjects.inputName(), TestMarshall.getTestData().getData("Name"))) {
            return Narrator.testFailed("Failed to enter name");
        }

        if (!SeleniumDriverUtility.enterText(ReusableObjects.inputSurname(), TestMarshall.getTestData().getData("Surname"))) {
            return Narrator.testFailed("Failed to enter surname");
        }

        if (!SeleniumDriverUtility.enterText(ReusableObjects.inputEmailAddress(), TestMarshall.getTestData().getData("Email"))) {
            return Narrator.testFailed("Failed to enter email address");
        }

        if (!SeleniumDriverUtility.clickElement(ReusableObjects.rbMyself())) {
            return Narrator.testFailed("Failed to click checkbox 'Myself'");
        }

        if (!SeleniumDriverUtility.clickElement(ReusableObjects.rbIntermediaries())) {
            return Narrator.testFailed("Failed to click checkbox 'Intermediaries'");
        }

        if (!SeleniumDriverUtility.clickElement(ReusableObjects.rbMyBusiness())) {
            return Narrator.testFailed("Failed to click checkbox 'My business'");
        }

        if (!SeleniumDriverUtility.clickElement(ReusableObjects.btnSubmit())) {
            return Narrator.testFailed("Failed to click submit");
        } else {
            Narrator.stepPassedWithScreenShot("Submit successful");
        }

        SeleniumDriverInstance.pause(2000);

        if(SeleniumDriverUtility.waitForElement(ReusableObjects.txtThankYou())) {
            if (!SeleniumDriverUtility.validateElementText(ReusableObjects.txtThankYou(), "Thank you")) {
                return Narrator.testFailed("Failed to Sign Up");
            }
        }

        return Narrator.finalizeTest("Test successful");
    }
}
