package main.java.testing.entities;

import java.util.LinkedList;

public class DataRow {
    private final LinkedList<DataColumn> DataColumns;

    public DataRow() {
        DataColumns = new LinkedList<>();
    }

    public LinkedList<DataColumn> getDataColumns() {
        return this.DataColumns;
    }

    public void addToDataColumns(DataColumn column) {
        this.DataColumns.add(column);
    }
}
