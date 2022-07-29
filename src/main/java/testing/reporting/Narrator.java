package main.java.testing.reporting;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.err;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.java.testing.TestMarshall;
import main.java.testing.entities.Enums;
import main.java.testing.entities.testresult.TestResult;
import main.java.testing.utilities.SeleniumDriverUtility;
import org.joda.time.DateTime;
import org.joda.time.Duration;

public class Narrator {

    private static String _reportDirectory;
    private static ExtentReports extentReport = null;

    private static ExtentTest currentTest;
    private static String _extentFile = null;
    private static DateTime startTime;

    private static String _error;
    private static String narratorLog;
    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:ms");
    private static String _absoluteScreenshot;

    private static String projectName = "Investec";
    private static String reportName = "Testing";
    private static String description = "";

    public static void setDescription(String name) {
        description = name;
    }
    
    public static void setProjectName(String name) {
        projectName = name;
    }

    public static void setAbsScreenshotPath(String path) {
        _absoluteScreenshot = path;
    }

    public static void setError(String error) {
        _error = error;
    }

    public static String getError() {
        return _error;
    }

    public static void checkReporters() {
        if (reportName == null || reportName == "") {
            reportName = "Unknown Test";
        }
    }
    
    public static void checkOnlyReporters()    {
         if (reportName == null || reportName == "") {
             setup(reportName);
        }
    }

    public static void setup(String testpack) {
        _reportDirectory = System.getProperty("user.dir") + "\\reports\\" + getFileName(testpack) + "\\" + getCurTime() + "\\";
        _extentFile = "extentReport.html";
        narratorLog = _reportDirectory + "\\Narrator_Log.txt";
        new File(_reportDirectory).mkdirs();

        createNarratorLog();

        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(_reportDirectory + _extentFile);
        htmlReporter.config().setTimeStampFormat("dd/MM/yyyy hh:mm:ss a");
        Locale.setDefault(Locale.ENGLISH);

        extentReport = new ExtentReports();

        extentReport.attachReporter(htmlReporter);
        extentReport.setSystemInfo("OS", System.getProperty("os.name"));

        if (SeleniumDriverUtility.getBrowser() != null) {
            extentReport.setSystemInfo("Browser", SeleniumDriverUtility.getBrowser().name());
        }

        extentReport.setAnalysisStrategy(AnalysisStrategy.TEST);
        extentReport.flush();
    }

    private static void createNarratorLog() {
        try {
            File logFile = new File(getNarratorLog());
            if (!logFile.exists()) {
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
            }
        } catch (Exception e) {
        }
    }

    public static String getNarratorLog() {
        return narratorLog;
    }

    public static void warning(String message) {
        checkReporters();
        currentTest.log(Status.WARNING, message);
        logInfo(message);
    }

    public static void warning(String messageToFormat, CodeLanguage codeLanguage) {
        checkReporters();
        currentTest.log(Status.WARNING, MarkupHelper.createCodeBlock(messageToFormat, codeLanguage));
        logInfo(messageToFormat);
    }

    public static void warning(String message, ExtentTest test) {
        test.log(Status.WARNING, message);
        logInfo(message);
    }

    public static void warning(String messageToFormat, CodeLanguage codeLanguage, ExtentTest test) {
        test.log(Status.WARNING, MarkupHelper.createCodeBlock(messageToFormat, codeLanguage));
        logInfo(messageToFormat);
    }

    public static void stepPassedWithScreenShot(String message) {
        checkReporters();
        try {
            String image = takeScreenshot(message, false);

            currentTest.pass(message, MediaEntityBuilder.createScreenCaptureFromPath(image).build());
            logPass(message);
            extentReport.flush();
        } catch (IOException ex) {
            Logger.getLogger(Narrator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void blockedTest() {
        checkReporters();
        currentTest.skip("Test was blocked by previous test failure.");
        extentReport.flush();
        logFailure("Test was blocked due to previous test failure");
    }

    public static TestResult testFailed(String message) {
        try {
            checkReporters();
            String image = takeScreenshot(message, false);

            if (image.length() < 1) {
                currentTest.fail(message);
            } else {
                currentTest.fail(message, MediaEntityBuilder.createScreenCaptureFromPath(image).build());
            }

            setError(message);
            logFailure(message);
        } catch (IOException ex) {
            if (currentTest != null) {
                currentTest.fail(message);
            }
        }
        if (extentReport != null) {
            extentReport.flush();
        }

        TestResult result = new TestResult(TestMarshall.getTestData(), Enums.ResultStatus.FAIL, message, getTotalExecutionTime());
        TestMarshall.addToTestResultsList(result);
        
        return result;
    }

    private static String takeScreenshot(String message, boolean testStatus) {
        try {
            return SeleniumDriverUtility.takeScreenshot(message, testStatus, SeleniumDriverUtility.getDriver());
        } catch (Exception e) {

        }
        return "";
    }
    
    private static void addExtractedParameters() {
        ArrayList keys = new ArrayList();
        ArrayList values = new ArrayList();
        ArrayList status = new ArrayList();
        if (TestMarshall.getTestData().getExtractedParams() != null) {
            String logMessage = "Extracted Parameters:";

            String extractedParameters = "<span style='font-weight:bold;font-family: Georgia;'>" + logMessage + "</span></br><table>";

            for (String key : TestMarshall.getTestData().getExtractedParams().keySet()) {
                keys.add(key);
                for (String value : TestMarshall.getTestData().getExtractedParams().get(key)) {
                    status.add(TestMarshall.getTestData().getExtractedParams().get(key).get(1));
                    values.add(value);

                }
            }

            for (int i = 0; i < keys.size(); i++) {
                if (status.get(i).equals("PASS")) {
                    extractedParameters += "<tr style='background: #60A84D;'><td>" + keys.get(i) + "</td><td>" + values.get(i) + "</td></tr>";
                } else if (status.get(i).equals("FAIL")) {
                    extractedParameters += "<tr style='background: #FF4536;'><td>" + keys.get(i) + "</td><td>" + values.get(i) + "</td></tr>";
                } else if (status.get(i).equals("WARNING")) {
                    extractedParameters += "<tr style='background: #FF8E1A;'><td>" + keys.get(i) + "</td><td>" + values.get(i) + "</td></tr>";
                } else {
                    extractedParameters += "<tr><td>" + keys.get(i) + "</td><td>" + values.get(i) + "</td></tr>";
                }
            }

            extractedParameters += "</table>";
            currentTest.log(Status.INFO, extractedParameters);
        }
    }

    public static TestResult finalizeTest(String message) {
        try {
            checkReporters();
            String image = takeScreenshot(message, true);

            if (image.length() < 1) {
                currentTest.pass(message);
            } else {
                currentTest.pass(message, MediaEntityBuilder.createScreenCaptureFromPath(image).build());
            }

            addExtractedParameters();
            extentReport.flush();
            logPass("Test Complete.");

        } catch (Exception e) {

        }
        return new TestResult(TestMarshall.getTestData(), Enums.ResultStatus.PASS, message, getTotalExecutionTime());
    }

    public static void addTest(String name, String description) {
        try {
            checkReporters();

            currentTest = extentReport.createTest(name, description);
            extentReport.flush();

            setStartTime();
            int totalTest = TestMarshall.getTestDataList().size();
            logInfo("Starting Test -- " + TestMarshall.getIteration() + " out of " + totalTest + " -- " + name);

        } catch (Exception e) {
            err.println("No report object exists!" + e.getMessage());
        }
    }

    public static String getReportDirectory() {
        return _reportDirectory;
    }

    private static String getCurTime() {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss");

        return ft.format(date);
    }

    private static String getFileName(String filepath) {
        try {
            File f = new File(filepath);
            return f.getName().split("\\.")[0];
        } catch (Exception e) {
            return null;
        }
    }

    public static void setStartTime() {
        startTime = new DateTime();
    }

    public static long getTotalExecutionTime() {
        DateTime endTime = new DateTime();
        Duration testDuration = new Duration(startTime, endTime);
        return testDuration.getStandardSeconds();
    }

    public static void logError(String error) {

        try {
            writeToLogFile("- [EROR] " + error);
        } catch (IOException ex) {
            Logger.getLogger(Narrator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void logDebug(String debug) {
        try {
            writeToLogFile("- [DBUG] " + debug);
        } catch (IOException ex) {
            Logger.getLogger(Narrator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void logPass(String pass) {
        try {
            writeToLogFile("- [PASS] " + pass);
        } catch (IOException ex) {
            Logger.getLogger(Narrator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void logFailure(String failure) {
        try {
            writeToLogFile("- [FAIL] " + failure);
        } catch (IOException ex) {
            Logger.getLogger(Narrator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void logInfo(String info) {
        try {
            writeToLogFile("- [INFO] " + info);
        } catch (IOException ex) {
            Logger.getLogger(Narrator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void writeToLogFile(String logMessage) throws IOException {

        File file = new File(getNarratorLog());
        String formatStr = "%n%-24s %-20s %-60s %-25s";
        if (!file.exists()) {
            file.createNewFile();
            PrintWriter writer = new PrintWriter(new FileWriter(file, true));
            writer.println(String.format(formatStr, "", "-- NARRATOR LOG FILE --", "", ""));
            writer.close();
        }

        //Writes info to the text file        
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(file, true));
            writer.println(String.format(formatStr, dateFormat.format(new Date()), logMessage, "", ""));
            writer.close();
        } catch (IOException e) {
            System.out.printf(e.getMessage());
        }
    }
}
