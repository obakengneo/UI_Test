package main.java.testing.entities;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Enums {
    public enum ResultStatus {PASS, FAIL, UNCERTAIN}
    public enum BrowserType{CHROME, IE, FIREFOX, EDGE}
    public enum TestType{SELENIUM}

    public enum Environment {
        QA("https://www.investec.com/");
        public final String URL;
        Environment(String _url)
        {
            this.URL = _url;
        }
    }
}
