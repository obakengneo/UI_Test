package main.java.testing.entities;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Predicate;

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
