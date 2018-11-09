package com.hyjoy.gradle.plugin.language.model

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.hyjoy.gradle.plugin.language.Log

/**
 *
 */
class HJLLExtensionConfig {
    private String path
    private String excelFilePath
    private Map<String, Object> languageMap
    private String outputPath
    private boolean fillEmpty = true


    HJLLExtensionConfig(String path) {
        Log.log("HJLLExtensionConfig" + path)
        this.path = path
        try {
            init()
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    private void init() throws Exception {
        // 映射表文件不存在
        File languageMapFile = new File(".", this.path)
        if (!languageMapFile.exists() && !this.path.endsWith(".json")) {
            languageMapFile = new File(".", this.path + ".json")
        }

        if (!languageMapFile.exists()) {
            throw new IllegalArgumentException("请填写正确的配置文件路径，文件路径:" + languageMapFile.getAbsoluteFile())
        }
        FileInputStream fileInputStream = new FileInputStream(languageMapFile);
        byte[] buffer = new byte[1024]
        StringBuilder data = new StringBuilder()
        while (fileInputStream.read(buffer) != -1) {
            data.append(new String(buffer, "utf-8"))
        }

        if (data.length() == 0) {
            throw new IllegalArgumentException("配置文件不能为空")
        }
        try {
            JSONObject obj = null
            try {
                obj = (JSONObject) JSON.parse(data.toString())
            } catch (Exception e) {
                throw new IllegalArgumentException("映射表文件内容应该为标准json格式")
            }
            excelFilePath = (String) obj.get("inputfile")
            languageMap = obj.getJSONObject("language-map")
            outputPath = (String) obj.get("outputPath")
            Boolean fillE = obj.getBoolean("fillEmpty")
            if (fillE != null) {
                fillEmpty = fillE
            }
            Log.log("excelPath: " + excelFilePath)
            Log.log("languageMap: " + languageMap)
            Log.log("outputPath: " + outputPath)
        } catch (Exception e) {
            throw new IllegalArgumentException("请填写正确的配置文件路径，文件路径:" + languageMapFile.getAbsoluteFile())
        }
    }

    Map<String, Object> loadLanguageMap() throws IOException {
        return languageMap
    }

    String loadFilePath() {
        return excelFilePath
    }

    String loadOutputPath() {
        return outputPath
    }

    boolean fillEmpty() {
        return fillEmpty
    }
}