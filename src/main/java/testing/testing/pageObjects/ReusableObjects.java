package main.java.testing.testing.pageObjects;

/**
 *
 * @author nditema
 */

public class ReusableObjects
{
    public static String baseURL() { return "https://www.investec.com/"; }

    public static String searchIcon() { return "//html/body/div[2]/header/div[2]/div[1]/div/div[2]/div[3]/a/div"; }

    public static String inputSearch() { return "//input[@id='searchBarInput']"; }

    public static String searchResults(String keyword) { return "//span[contains(text(),' "+keyword+"')]"; }

    public static String interestRatesURL() { return "https://www.investec.com/en_za/focus/money/understanding-interest-rates.html"; }

    public static String btnSignUp() { return "//html/body/div[2]/div[3]/div[7]/div[2]/div/div/div/div/div[2]/div[1]/div[2]/button"; }

    public static String inputName() { return "//span[contains(text(),'Enter your name here')]/../..//input"; }

    public static String inputSurname() { return "//span[contains(text(),'Enter your surname here')]/../..//input"; }

    public static String inputEmailAddress() { return "//span[contains(text(),'Enter your email address here')]/../..//input"; }

    public static String rbMyself() { return "//div[contains(text(),'Myself')]"; }

    public static String rbIntermediaries() { return "//div[contains(text(),'Intermediaries')]"; }

    public static String rbMyBusiness() { return "//div[contains(text(),'My business')]"; }

    public static String btnSubmit() { return "//button[contains(text(),'Submit')]"; }

    public static String txtThankYou() { return "//h3[@class='thank-you__heading']"; }
}
