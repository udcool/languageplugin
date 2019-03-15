package com.hyjoy.gradle.plugin.language;

import java.util.Map;

/**
 * Created by hyjoy on 2019/3/14.
 */
public class HJLConfig {
    private String path;
    private String excelFilePath;
    private Map<String, String> languageMap;
    private String outputPath;
    private boolean fillEmpty = true;
    private int sheetIndex;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExcelFilePath() {
        return excelFilePath;
    }

    public void setExcelFilePath(String excelFilePath) {
        this.excelFilePath = excelFilePath;
    }

    public Map<String, String> getLanguageMap() {
        return languageMap;
    }

    public void setLanguageMap(Map<String, String> languageMap) {
        this.languageMap = languageMap;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public boolean isFillEmpty() {
        return fillEmpty;
    }

    public void setFillEmpty(boolean fillEmpty) {
        this.fillEmpty = fillEmpty;
    }

    public int getSheetIndex() {
        return sheetIndex;
    }

    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }
}
