package com.hyjoy.gradle.plugin.language.model

import com.hyjoy.gradle.plugin.language.Log
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class ExcelDocument {

    private String path
    private int size

    ExcelDocument(String path, int size) {
        this.path = path
        this.size = size
    }

    List<List<String>> loadRowDatas() throws Exception {
        if (path == null || path.length() == 0) {
            throw new IllegalArgumentException("请填写正确的文件路径")
        }
        List<List<String>> rows = new ArrayList<>()
        FileInputStream fis = null
        XSSFWorkbook workbook = null
        try {
            fis = new FileInputStream(this.path)
            workbook = new XSSFWorkbook(fis)
            if (workbook.getNumberOfSheets() == 0) {
                throw IllegalArgumentException("请填充需要转换的excel sheet")
            }

            XSSFSheet sheet = workbook.getSheetAt(0)

            int rowNum = sheet.getLastRowNum()
            if (rowNum < 2) {
                throw new IllegalArgumentException("数据表Sheet 为空")
            }
            for (int rowIndex = 0; rowIndex < sheet.getLastRowNum(); rowIndex++) {
                XSSFRow row = sheet.getRow(rowIndex)
                if (row == null) break
                Log.log("getLastCellNum=" + row.getLastCellNum() + " size=" + size)
                int columnNum = Math.min(row.getLastCellNum(), size + 1)
                List<String> rowData = new ArrayList<>()
                for (int columnIndex = 0; columnIndex < columnNum; columnIndex++) {
                    Cell cell = row.getCell(columnIndex)
                    if (cell == null) continue
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
                        value = null
                    }
                    Log.log("value: " + value)
                    rowData.add(value)
                }
                if (rowData.size() > 0) {
                    rows.add(rowData)
                }
            }

            Log.log("Excel 语言数据Size：" + rows.size())
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage())
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
