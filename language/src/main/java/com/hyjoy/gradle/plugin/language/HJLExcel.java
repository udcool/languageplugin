package com.hyjoy.gradle.plugin.language;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hyjoy on 2019/3/14.
 */
public class HJLExcel {

    private String mFilePath;
    private int mSheetNum;
    private int mColumnCount;

    private List<String> mCodes = new ArrayList<>();
    private List<String> mLanguages = new ArrayList<>();

    private Map<String, ArrayList<String>> mDatas = new HashMap<>();

    public HJLExcel(String filePath, int sheetIndex, int columnCount) {
        this.mFilePath = filePath;
        this.mSheetNum = sheetIndex;
        this.mColumnCount = columnCount;

        init();
    }

    private void init() {
        File file = new File(mFilePath);

        if (!file.exists()) {
            throw new IllegalArgumentException("无效的excel文件路径配置 mFilePath:" + file.getAbsolutePath());
        }

        FileInputStream fileInputStream = null;
        XSSFWorkbook xssfWorkbook = null;

        try {
            fileInputStream = new FileInputStream(this.mFilePath);
            xssfWorkbook = new XSSFWorkbook(fileInputStream);
            if (xssfWorkbook.getNumberOfSheets() <= mSheetNum) {
                throw new IllegalArgumentException("当前需要生成的sheet index 不存在");
            }

            XSSFSheet sheet = xssfWorkbook.getSheetAt(mSheetNum);
            // 开始读取sheet
            // 读取language
            ArrayList<String> languages = readLanguages(sheet, mColumnCount);
            mLanguages.addAll(languages);

            // 读取code
            ArrayList<String> codes = readCodes(sheet);
            mCodes.addAll(codes);

            if (mLanguages.size() <= 0) {
                throw new IllegalArgumentException("Excel sheet " + mSheetNum + " 中无语言标签");
            }
            if (mCodes.size() <= 0) {
                throw new IllegalArgumentException("Excel sheet " + mSheetNum + " 中Code 数据");
            }

            Map<String, ArrayList<String>> contents = readContents(sheet, mCodes.size() + 1, mColumnCount, mLanguages, mCodes);
            mDatas.putAll(contents);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (xssfWorkbook != null) {
                try {
                    xssfWorkbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取language
     *
     * @param sheet
     */
    private ArrayList<String> readLanguages(XSSFSheet sheet, int columnCount) {

        XSSFRow row = sheet.getRow(0);
        ArrayList<String> list = new ArrayList<>(row.getPhysicalNumberOfCells());
        for (int i = 1; i < columnCount; i++) {
            Cell cell = row.getCell(i);
            list.add(readCell(cell));
        }

        return list;
    }

    private ArrayList<String> readCodes(XSSFSheet sheet) {
        ArrayList<String> codes = new ArrayList<>(sheet.getLastRowNum());
        int rowCnt = sheet.getLastRowNum() + 1;
        for (int i = 1; i < rowCnt; i++) {
            Cell cell = sheet.getRow(i).getCell(0);
            codes.add(cell.getStringCellValue());
        }

        return codes;
    }


    private Map<String, ArrayList<String>> readContents(XSSFSheet sheet, int rowNum, int cellNum, List<String> languages, List<String> codes) {
        HashMap<String, ArrayList<String>> map = new HashMap<>();

        int count = Math.min(cellNum, sheet.getRow(0).getPhysicalNumberOfCells());
        for (int i = 1; i < count; i++) {
            ArrayList<String> list = readColumnDatas(sheet, i, rowNum);
            map.put(languages.get(i - 1), list);
        }

        return map;
    }


    private ArrayList<String> readRowDatas(Row row, int cellNum) {
        ArrayList<String> list = new ArrayList<>();

        int count = Math.min(cellNum, row.getPhysicalNumberOfCells());
        for (int i = 1; i < count; i++) {
            list.add(readCell(row.getCell(i)));
        }
        for (int i = list.size(); i < cellNum; i++) {
            list.add(null);
        }
        return list;
    }

    private ArrayList<String> readColumnDatas(XSSFSheet sheet, int columnIndex, int rowNum) {
        ArrayList<String> list = new ArrayList<>();

        int count = Math.min(rowNum, sheet.getLastRowNum() + 1);
        for (int i = 1; i < count; i++) {
            XSSFRow row = sheet.getRow(i);
            list.add(readCell(row.getCell(columnIndex)));
        }
        for (int i = list.size(); i < rowNum - 1; i++) {
            list.add(null);
        }
        return list;
    }

    /**
     * 读取cell值
     *
     * @param cell :
     * @return :
     */
    private String readCell(Cell cell) {
        String value;
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            value = cell.getNumericCellValue() + "";
        } else if (cell.getCellTypeEnum() == CellType.STRING) {
            value = cell.getStringCellValue();
        } else if (cell.getCellTypeEnum() == CellType.ERROR) {
            value = cell.getErrorCellValue() + "";
        } else if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
            value = cell.getBooleanCellValue() + "";
        } else {
            value = null;
        }
        return value;
    }

    public Map<String, ArrayList<String>> getDatas() {
        return mDatas;
    }

    public List<String> getLanguages() {
        return mLanguages;
    }

    public List<String> getCodes() {
        return mCodes;
    }
}
