package main.java.testing.utilities;

import java.io.File;

import main.java.testing.entities.DataColumn;
import main.java.testing.entities.DataRow;
import main.java.testing.entities.Enums;
import main.java.testing.entities.TestEntity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import static java.lang.System.err;
import static java.lang.System.out;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReaderUtility {
    private static final List<TestEntity> testDataList = new ArrayList<>();
    private static Sheet _workSheet;
    private static Workbook _workbook;
    private static final String excel_extension = ".xlsx";

    public ExcelReaderUtility() {
        System.setProperty("java.awt.headless", "true");
    }

    public static List<TestEntity> getTestDataFromExcelFile(String filePath) {
        if (filePath.contains(excel_extension)) {
            _workbook = getExcelWorkbook(filePath);
            readExcelWorkSheet(_workbook);
            retrieveTestDataFromSheet();
        }
        else {//Assuming the path provided is a folder path with Excel sheets in for a regression pack
            List<String> excelFilePaths = new ArrayList<>();
            List<File> fileList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File(filePath).listFiles())));
            fileList = fileList.stream().filter(s -> {
                s.getName();
                return s.getName().toLowerCase().contains(excel_extension.toLowerCase());
            }).collect(Collectors.toList());
            fileList.forEach(s -> excelFilePaths.add(s.getAbsolutePath()));
            excelFilePaths.sort((s1, s2) -> s1.compareTo(s2));//Sorting List Alphabetically

            for (String currentExcelFilePath : excelFilePaths) {
                _workbook = getExcelWorkbook(currentExcelFilePath);
                readExcelWorkSheet(_workbook);
                retrieveTestDataFromSheet();
                _workbook = null;
            }
        }
        return testDataList;
    }

    public static Workbook getExcelWorkbook(String filePath) {
        try (InputStream reader = Files.newInputStream(Paths.get(filePath))) {
            return WorkbookFactory.create(reader);
        }
        catch (Exception e) {
            return null;
        }
    }

    private static boolean readExcelWorkSheet(Workbook workbook) {
        try {
            _workSheet = workbook.getSheetAt(0);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private static boolean retrieveTestDataFromSheet()
    {
        int lastColumn = 0;
        if (_workSheet == null) {
            return false;
        }
        try {
            for (Row row : _workSheet) {
                String firstCellValue = getCellValue(row.getCell(0));
                if (!"".equals(firstCellValue)) {
                    lastColumn = row.getLastCellNum();
                    getTestParameters(row.getRowNum(), row.getRowNum() + 1, lastColumn);
                }
            }
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private static String getCellValue(Cell cell) {
        String cellValue = "";
        try {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    cellValue = cell.getRichStringCellValue().getString();
                    break;
                case Cell.CELL_TYPE_BLANK:
                    cellValue = cell.getRichStringCellValue().getString();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        cellValue = cell.getDateCellValue().toString();
                    }
                    else {
                        cellValue = NumberToTextConverter.toText(cell.getNumericCellValue());
                    }
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    cellValue = String.valueOf(cell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    cellValue = String.valueOf(cell.getCellFormula());
                    break;
                default:
            }
            if (cellValue == null) {
                cellValue = "";
            }
        }
        catch (Exception e) {
            return "";
        }
        return cellValue;
    }

    private static void getTestParameters(int parameterRowIndex, int valueRowIndex, int lastColumn) {
        TestEntity testData = new TestEntity();
        Row parameterRow;
        Row valueRow;
        String testCaseId;
        String methodName;
        String testDescription = "";
        int testParemeterStartcolumn = 3;

        parameterRow = _workSheet.getRow(parameterRowIndex);
        valueRow = _workSheet.getRow(valueRowIndex);

        testCaseId = getCellValue(parameterRow.getCell(0)).trim();
        methodName = getCellValue(parameterRow.getCell(1)).trim();
        // Check the formatting of the inputfile, if the test description column is missing
        // and a test data parameter is present, reset the start column for data to 2.
        if (getCellValue(_workSheet.getRow(parameterRowIndex + 1).getCell(2)).equals("")) {
            testDescription = getCellValue(parameterRow.getCell(2)).trim();
        }
        else {
            testParemeterStartcolumn = 2;
        }
        testData.setTestCaseID(testCaseId);
        testData.setTestMethod(methodName);
        testData.setTestDescription(testDescription);

        try {
            for (int i = testParemeterStartcolumn; i < lastColumn; i++) {
                String parameter = getCellValue(parameterRow.getCell(i)).trim();
                String value = getCellValue(valueRow.getCell(i)).trim();
                if (!"".equals(parameter)) {
                    testData.addParameter(parameter, value);
                }
            }
        }
        catch (Exception ex) {
            //Parameters were not detected - keyword might not have data requirements.
        }
        testDataList.add(testData);
    }

    public static LinkedList<DataColumn> convertExcelToColumn(String ExcelFilePath) {
        DataRow newRow = new DataRow();
        Workbook book = getExcelWorkbook(ExcelFilePath);
        readExcelWorkSheet(book);
        List<String> Headers = new ArrayList<>();

        //Row 1 is treated as Header values
        Row row1 = _workSheet.getRow(0);

        int colCounts = row1.getLastCellNum();
        for (int j = 0; j < colCounts; j++) {
            Cell cell = row1.getCell(j);
            String value = cell.getStringCellValue();
            Headers.add(value);
        }

        //All other rows are treated as Values
        List<String> Values = new ArrayList<>();
        int rowsCount = _workSheet.getLastRowNum() + 1;
        for (int i = 1; i < rowsCount; i++) {
            for (int j = 0; j < colCounts; j++) {
                Row row = _workSheet.getRow(i);
                Cell cell = row.getCell(j);
                String value = cell.getStringCellValue();
                Values.add(value);
            }
        }

        for (int i = 0; i < Values.size(); i++) {
            DataColumn newColumn = new DataColumn("", "", Enums.ResultStatus.UNCERTAIN);

            newColumn.setColumnHeader(Headers.get((i % (Headers.size()))));
            newColumn.setColumnValue(Values.get(i));

            newRow.addToDataColumns(newColumn);
        }

        return newRow.getDataColumns();
    }
}
