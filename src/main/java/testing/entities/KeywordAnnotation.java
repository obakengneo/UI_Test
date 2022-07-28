package main.java.testing.entities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface KeywordAnnotation {
    String Keyword();
    //Use to restart - init the selenium instance
    boolean createNewBrowserInstance() default false;    

    //This will mean the test will log a skip if the previous test failed
    boolean blockable() default false;
}
