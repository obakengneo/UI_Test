package main.java.testing.utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import main.java.testing.TestMarshall;
import main.java.testing.entities.Enums;
import main.java.testing.reporting.Narrator;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumDriverUtility {

    private static WebDriver driver;
    private static int _timeout = 30;
    private static Boolean _driverRunning = false;
    private static int screenshot_counter = 0;
    private static Enums.BrowserType selectedBrowser;

    public static WebDriver getDriver() {
        return driver;
    }

    public SeleniumDriverUtility(Enums.BrowserType browser) {
        setBrowser(browser);
    }

    public static void setBrowser(Enums.BrowserType browser) {
        selectedBrowser = browser;
    }

    public static Enums.BrowserType getBrowser() {
        return selectedBrowser;
    }

    public static void launchDriver() {
        try {
            if (selectedBrowser == Enums.BrowserType.CHROME) {
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
            } else {
                return;
            }

            driver.manage().window().maximize();
            _driverRunning = true;

        } catch (Exception e) {
            System.err.println("[ERROR] - Failed to set the Driver, exception thrown is " + e.getMessage());
        }
    }

    public static int getTimeout() {
        return SeleniumDriverUtility._timeout;
    }

    public static Boolean navigateToURL(String URL) {
        try {
            driver.navigate().to(URL);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //Wait for element to be visible
    public static Boolean waitForElement(String xpath) {
        boolean foundElement = false;
        try {
            int waitCount = 0;
            while (!foundElement && waitCount < getTimeout()) {
                try {
                    WebDriverWait wait = new WebDriverWait(driver, 1);
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
                    foundElement = true;
                } catch (Exception e) {
                    waitCount++;
                }
            }
            return foundElement;
        } catch (Exception e) {
            return false;
        }
    }

    public static Boolean clickElement(String xpath) {
        try {
            waitForElement(xpath);
            WebDriverWait wait = new WebDriverWait(driver, 1);
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            element.click();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Boolean enterText(String xpath, String text) {
        try {
            waitForElement(xpath);
            WebDriverWait wait = new WebDriverWait(driver, 4);
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            element.sendKeys(text);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String retrieveElementText(String xpath) {
        try {
            waitForElement(xpath);
            WebDriverWait wait = new WebDriverWait(driver, 1);
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));

            return element.getText();
        } catch (Exception e) {
            return null;
        }
    }

    public void pause(int milisecondsToWait) {
        try {
            Thread.sleep(milisecondsToWait);
        } catch (InterruptedException ignored) {

        }
    }

    public static boolean scrollToElement(String elementXpath) {
        try {
            WebElement element = driver.findElement(By.xpath(elementXpath));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            Narrator.logDebug("Scrolled to element - " + elementXpath);
            return true;
        } catch (Exception e) {
            Narrator.logError("Error scrolling to element - " + elementXpath + " - " + Arrays.toString(e.getStackTrace()));
            return false;
        }
    }

    public static Boolean validateElementText(String xpath, String text) {
        try {
            waitForElement(xpath);
            WebDriverWait wait = new WebDriverWait(driver, 1);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));

            return text.equals(retrieveElementText(xpath));
        } catch (Exception e) {
            return false;
        }
    }

    public static String takeScreenshot(String description, Boolean isError, WebDriver driver) {
        try {
            screenshot_counter++;
            StringBuilder imageFilePathBuilder = new StringBuilder();
            imageFilePathBuilder.append(Narrator.getReportDirectory());
            StringBuilder relativePathBuilder = new StringBuilder();
            String folderName = (TestMarshall.getTestData().getTestCaseID() == null) ? "Screenshots" : TestMarshall.getTestData().getTestCaseID();

            if (isError) {
                relativePathBuilder.append(folderName).append("\\Failed_");
            } else {
                relativePathBuilder.append(folderName).append("\\Passed_");
            }
            relativePathBuilder.append("_").append(screenshot_counter).append(".png");

            imageFilePathBuilder.append(relativePathBuilder);

            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File(imageFilePathBuilder.toString()));
            Narrator.setAbsScreenshotPath(imageFilePathBuilder.toString());

            return "./" + relativePathBuilder;

        } catch (IOException | WebDriverException e) {
            Narrator.setAbsScreenshotPath("");
            return null;
        }
    }

    public static boolean clickElementByXpathUsingJavascript(String elementXpath) {
        try {
            SeleniumDriverUtility.waitForElement(elementXpath);
            WebElement element;
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            element = driver.findElement(By.xpath(elementXpath));
            executor.executeScript("arguments[0].click();", element);
            Thread.sleep(2500);
            return true;
        } catch (InterruptedException e) {
            System.out.println("Failed to click Element by Xpath using Javascript " + e.getMessage());
            return false;
        }
    }
}
