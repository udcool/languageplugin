package com.hyjoy.gradle.plugin.language.model

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

public class ExcelDocument {

    private String path

    public ExcelDocument(String path) {
        this.path = path
    }

    public List<List<String>> loadRowDatas() throws Exception {
        if (path == null || path.length() == 0) {
            throw new IllegalArgumentException("请填写正确的文件路径");
        }
        List<List<String>> rows = new ArrayList<>()
        FileInputStream fis = null
        XSSFWorkbook workbook = null
        try {
            fis = new FileInputStream(this.path)
            workbook = new XSSFWorkbook(fis)
            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()) {
                Iterator<Cell> iterator = rowIterator.next().cellIterator()
                List<String> row = new LinkedList<>()
                while (iterator.hasNext()) {
                    Cell cell = iterator.next()
                    String value
                    if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                        value = cell.getNumericCellValue() + ""
                    } else if (cell.getCellTypeEnum() == CellType.STRING) {
                        value = cell.getStringCellValue()

                    } else if (cell.getCellTypeEnum() == CellType.ERROR) {
                        value = cell.getErrorCellValue() + ""

                    } else if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
                        value = cell.getBooleanCellValue() + ""
                    } else {
                        value = ""
                    }
                    row.add(value)
                }
                rows.add(row)
            }
        } catch (Exception e) {
            e.printStackTrace()
            println(e.message())
        } finally {
            if (fis != null) {
                fis.close()
            }
            if (workbook != null) {
                workbook.close()
            }
        }
        return rows
    }
}
