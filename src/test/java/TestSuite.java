package test.java;

import main.java.testing.TestMarshall;
import main.java.testing.core.BaseClass;
import main.java.testing.entities.Enums;
import org.testng.annotations.Test;

/**
 *
 * @author nditema
 */
public class TestSuite extends BaseClass {
    @Test
    public void Test() {
        String testPack = System.getProperty("user.dir")+"\\testpacks\\TestPack.xlsx";
        SelectedBrowser = Enums.BrowserType.CHROME;
        TestMarshall instance = new TestMarshall(testPack);
        instance.runTests();
    }
}
