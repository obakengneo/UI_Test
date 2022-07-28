package main.java.testing.entities.testresult;
import main.java.testing.entities.Enums;
import main.java.testing.entities.TestEntity;

public class TestResult
{
    private TestEntity testData;
    private Enums.ResultStatus testStatus;
    private long testDuration;

    public TestResult(TestEntity testData, Enums.ResultStatus testStatus, String errorMessage, long testDuration)
    {
        this.testData = testData;
        this.testStatus = testStatus;
        this.testDuration = testDuration;
    }

    public TestEntity getTestEntity()
    {
        return this.testData;
    }

    public Enums.ResultStatus getResultStatus()
    {
        return this.testStatus;
    }

    public long getTestDuration()
    {
        return this.testDuration;
    }
}
