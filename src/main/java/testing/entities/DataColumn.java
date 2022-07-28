package main.java.testing.entities;

public class DataColumn {
    private String columnHeader;
    private String columnValue;

    public DataColumn(String header, String value, Enums.ResultStatus status) {
        this.columnHeader = header;
        this.columnValue = value;
    }

    public void setColumnHeader(String header) {
        this.columnHeader = header;
    }

    public void setColumnValue(String value) {
        this.columnValue = value;
    }
}
