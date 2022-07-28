package main.java.testing.entities;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import main.java.testing.entities.testresult.TestResult;
import main.java.testing.utilities.SeleniumDriverUtility;

import static java.lang.System.err;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TestClass {
    private final Object testClassInstance;
    private Boolean blockable = false;
    
    public TestClass(TestEntity testData) {
        this.testClassInstance = generateTest(testData);
    }

    public Boolean getBlockableStatus(){return blockable;}

    public void setBlockableStatus(Boolean block){blockable = block ;}

    private Object generateTest(TestEntity testData) {
        try {
            ClassPath.ClassInfo testClassInfo = getKeywordTestClass(testData.getTestMethod());

            if (testClassInfo == null) {
                throw new ClassNotFoundException("Test class with keyword - " + testData.getTestMethod() + " - could not be found!");
            }

            Class testClass = testClassInfo.load();
            
            Boolean hasAnnotation = testClassInfo.load().isAnnotationPresent(KeywordAnnotation.class);
            
            if (hasAnnotation) {
//                ensureNewBrowser = testClassInfo.load().getAnnotation(KeywordAnnotation.class).createNewBrowserInstance();
                setBlockableStatus(testClassInfo.load().getAnnotation(KeywordAnnotation.class).blockable());
//                setInitializeAPIStatus(testClassInfo.load().getAnnotation(KeywordAnnotation.class).apiTesting()); //Sets the API instance value based on keyword
            }

            Constructor constructor = testClass.getConstructor();
            return constructor.newInstance();
        }
        catch (Exception e) {
            if (e instanceof NoSuchMethodException) {
                System.err.println("Failed to Generate Test for '" + testData.getTestCaseID() + "'. Please Check Test Class Constructor. e - " + e.getMessage());
            }
            return null;
        }
    }

    private ClassPath.ClassInfo getKeywordTestClass(String keywordName) {
        try {

            //Get list of all loaded classes for the package - defined at runtime - we need to be able to isolate just the TestClasses
            // in order to extract the one matching the keyword to be executed
            ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader());

            //Next we set up Predicates (a type of query in java) to isolate the list of classes to only those pertaining to the framework
            // i.e. no dependencies included 
            ImmutableSet<ClassPath.ClassInfo> allClasses = classPath.getTopLevelClassesRecursive("main");

            //We then filter the classes to only those who have the required annotations - annotations used to add meta
            //data to the TestClasses so that we can scan them to read their Keywords - this uses Lambda notation only available in Java 8 and above.
            Predicate<ClassPath.ClassInfo> hasAnnotationPredicate = c -> c.load().isAnnotationPresent(KeywordAnnotation.class);
            Stream<ClassPath.ClassInfo> annotatedClasses = allClasses.stream().filter(hasAnnotationPredicate);

            //The filtered list is then queried a second time in order to retrieve the valid TestClass based on the keywordName
            Predicate<ClassPath.ClassInfo> checkKeywordPredicate = c -> c.load().getAnnotation(KeywordAnnotation.class).Keyword().equals(keywordName);
            Optional<ClassPath.ClassInfo> testClassOpt = annotatedClasses.filter(checkKeywordPredicate).findFirst();
            ClassPath.ClassInfo testClass;
            if (!testClassOpt.isPresent()) {
                throw new ClassNotFoundException("Could not find Keyword!");
            }
            else {
                testClass = testClassOpt.get();
            }

            if (testClass == null) {
                err.println("[ERROR] Failed to resolve TestClass for keyword - " + keywordName + " - error: Keyword not found");
            }

            return testClass;
        }
        catch (Exception ex) {
            err.println("[ERROR] Failed to resolve TestClass for keyword - " + keywordName + " - error: " + ex.getMessage());

            return null;
        }
    }

    public TestResult runTest() {
        try {
            if (this.testClassInstance.getClass().getAnnotation(KeywordAnnotation.class).createNewBrowserInstance()) {
                ensureNewBrowserInstance();
            }

            Method executeTestMethod = testClassInstance.getClass().getMethod("executeTest");
            return (TestResult) executeTestMethod.invoke(testClassInstance);
        }
        catch (Exception e) {
            return null;
        }
    }

    private void ensureNewBrowserInstance() {
        
       if(SeleniumDriverUtility.getDriver()!= null) {
         SeleniumDriverUtility.getDriver().quit();
         SeleniumDriverUtility.launchDriver();
       }
       else {
           SeleniumDriverUtility.launchDriver();
       }
    }
}
