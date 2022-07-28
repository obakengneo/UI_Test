package main.java.testing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Predicate;
import main.java.testing.entities.Enums;
import main.java.testing.entities.TestClass;
import main.java.testing.entities.TestEntity;
import main.java.testing.entities.testresult.TestResult;
import main.java.testing.reporting.Narrator;
import main.java.testing.utilities.ExcelReaderUtility;
import main.java.testing.utilities.SeleniumDriverUtility;

public class TestMarshall {

    private static List<TestEntity> testDataList;
    private static final List<TestResult> resultsList = new ArrayList();
    private static Boolean _isDataSet;
    private static TestEntity testData;
    private String testName;
    private static int iteration;
    private static Enums.TestType curTestType;

    public static void setTestType(Enums.TestType type) {
        curTestType = type;
    }

    public static Enums.TestType getTestType() {
        return curTestType;
    }

    public static void setIsDataSet(Boolean isDataSet) {
        _isDataSet = isDataSet;
    }

    public static void testData(TestEntity testdata) {
        testData = testdata;
    }

    public void testName(String testname) {
        this.testName = testname;
    }

    public void iteration(int Iteration) {
        iteration = Iteration;
    }

    public void addToTestDataList(TestEntity data) {
        testDataList.add(data);
    }

    public static void addToTestResultsList(TestResult data) {
        resultsList.add(data);
    }

    public List<TestResult> getResultsList() {
        return this.resultsList;
    }

    public static List<TestEntity> getTestDataList() {
        return testDataList;
    }

    public static int getIteration() {
        return iteration;
    }
    private static Enums.ResultStatus previousTestResult;

    public static Boolean getIsDataSet() {
        return _isDataSet;
    }

    public static TestEntity getTestData() {
        return testData;
    }

    public String getTestName() {
        return this.testName;
    }

    {
        readProjectProps();
        iteration = 0;
    }

    public TestMarshall(String ExcelPath) {
        Narrator.setup(ExcelPath);
        testDataList = ExcelReaderUtility.getTestDataFromExcelFile(ExcelPath);
    }


    public void runTests() {
        for (TestEntity TestData : testDataList) {

            iteration++;

            //Will skip test if the keyword CONTAINS ';'
            if(TestData.getTestMethod().contains(";")) continue;
            testData = TestData;
            TestClass currentTest = null;
            TestResult testResult = null;

            Narrator.addTest(testData.getTestCaseID(), testData.getTestDescription());
            currentTest = new TestClass(testData);
            testResult = currentTest.runTest();

            if (previousTestResult == Enums.ResultStatus.FAIL && currentTest.getBlockableStatus()) {
                Narrator.blockedTest();
                continue;
            }

            previousTestResult = (testResult == null) ? Enums.ResultStatus.FAIL : testResult.getResultStatus();
            resultsList.add(testResult);
        }
        tearDown();
    }

    public void tearDown() {
        if (SeleniumDriverUtility.getDriver() != null) {
            SeleniumDriverUtility.getDriver().quit();
        }
    }

    private static void readProjectProps() {
        Properties prop = new Properties();
        InputStream input = null;
        generateProjectProp();
        try {
            input = new FileInputStream("project.properties");
            prop.load(input);

            Narrator.setProjectName(prop.getProperty("projectName"));
        } catch (Exception e) {
            Narrator.logError("Could not find project.properties file with projectName field");
            Narrator.setProjectName("Unclaimed");
        }
    }

    private static void generateProjectProp() {
        try {
            File f = new File(System.getProperty("user.dir") + "\\project.properties");
            if (!f.exists()) {
                f.createNewFile();
                String projectDefault = "projectName=Unclaimed";
                BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "\\project.properties"));
                writer.write(projectDefault);

                writer.close();
            }

        } catch (Exception e) {
            Narrator.logError("Could not generate project.properties");
        }
    }

    public TestResult checkResults() {
        Predicate<TestResult> checkStatus = c -> c.getResultStatus().equals(Enums.ResultStatus.FAIL);
        Optional<TestResult> statusStream = resultsList.stream().filter(checkStatus).findFirst();
        if (!statusStream.isPresent()) {
            return new TestResult(resultsList.get(0).getTestEntity(), Enums.ResultStatus.PASS, "Passed", resultsList.get(0).getTestDuration());
        } else {
            return new TestResult(resultsList.get(0).getTestEntity(), Enums.ResultStatus.FAIL, "At least 1 device run failed", resultsList.get(0).getTestDuration());
        }
    }
}
