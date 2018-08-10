package com.hyjoy.gradle.plugin.language.model

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject

/**
 *
 */
public class HJLLExtensionConfig {
    private String path
    private String excelFilePath
    private Map<String, Object> languageMap
    private String outputPath;


    public HJLLExtensionConfig(String path) {
        println("HJLLExtensionConfig" + path)
        this.path = path
        try {
            init()
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    private void init() throws IOException {
        // 映射表文件不存在
        File languageMapFile = new File(".", this.path)
        if (!languageMapFile.exists() && !this.path.endsWith(".json")) {
            languageMapFile = new File(".", this.path + ".json")
        }
        println("文件路径:" + languageMapFile.getAbsoluteFile())
        if (!languageMapFile.exists()) {
            System.out.println("映射表为空")
            return
        }
        FileInputStream fileInputStream = new FileInputStream(languageMapFile);
        byte[] buffer = new byte[1024]
        StringBuilder data = new StringBuilder()
        while (fileInputStream.read(buffer) != -1) {
            data.append(new String(buffer, "utf-8"))
        }

        if (data.length() == 0) {
            System.out.println("映射表为空")
            return
        }
        println(data.toString())
        JSONObject obj = (JSONObject) JSON.parse(data.toString())
        try {
            excelFilePath = (String) obj.get("inputfile")
            languageMap = obj.getJSONObject("language-map")
            outputPath = (String) obj.get("outputPath")
        } catch (Exception e) {
            println("filepath: " + excelFilePath)
            e.printStackTrace()
        }
    }


    public Map<String, Object> loadLanguageMap() throws IOException {
        return languageMap
    }

    public String loadFilePath() {
        return excelFilePath
    }

    public String loadOutputPath() {
        return outputPath
    }
}