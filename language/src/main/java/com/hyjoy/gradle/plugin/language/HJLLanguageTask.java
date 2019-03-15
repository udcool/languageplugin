package com.hyjoy.gradle.plugin.language;

import com.alibaba.fastjson.JSONObject;
import com.hyjoy.gradle.plugin.language.util.Log;
import com.hyjoy.gradle.plugin.language.util.Prediction;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hyjoy on 2019/3/14.
 */
public final class HJLLanguageTask {


    public static void execute(String filePath) throws IOException {
        if (filePath == null || filePath.trim().length() == 0) {
            throw new IllegalArgumentException("配置文件路径错误");
        }

        File file = new File(".", filePath);
        if (!file.exists() && !filePath.endsWith(".json")) {
            file = new File(".", filePath + ".json");
        }

        if (!file.exists()) {
            throw new IllegalArgumentException("配置文件路径错误, 文件路径：" + file.getAbsolutePath());
        }


        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[2048];

        StringBuilder sb = new StringBuilder();
        while (fis.read(buffer) != -1) {
            sb.append(new String(buffer, StandardCharsets.UTF_8));
        }

        if (sb.length() == 0) {
            throw new IllegalArgumentException("配置文件内容为空, 文件路径：" + file.getAbsolutePath());
        }

        final HJLConfig config = new HJLConfig();

        JSONObject jsonObject = JSONObject.parseObject(sb.toString());
        config.setExcelFilePath(jsonObject.getString("inputfile"));
        config.setOutputPath(jsonObject.getString("outputPath"));
        config.setFillEmpty(jsonObject.getBoolean("fillEmpty"));
        Integer index = jsonObject.getInteger("sheetIndex");
        config.setSheetIndex(index == null ? 0 : index);

        Map<String, Object> data = jsonObject.getJSONObject("language-map").getInnerMap();
        Map<String, String> languages = new HashMap<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            languages.put(entry.getKey(), entry.getValue().toString());
        }
        config.setLanguageMap(languages);

        Prediction.checkNotNull(config.getExcelFilePath(), "excel 配置路径不能为空");
        Prediction.checkNotNull(config.getOutputPath(), "string.xml 路径不能为空");
        Prediction.checkMapNotNull(config.getLanguageMap(), "language 对应表不能为空");

        String dir = file.getParent();
        final HJLExcel hjlExcel = new HJLExcel(dir + File.separator + config.getExcelFilePath(), config.getSheetIndex(), languages.size() + 1);
        Map<String, ArrayList<String>> datas = hjlExcel.getDatas();
        ExecutorService executors = Executors.newFixedThreadPool(5);
        for (final Map.Entry<String, String> entry : config.getLanguageMap().entrySet()) {
//            executors.execute(() -> {

            ArrayList<String> languageValues = datas.get(entry.getKey());
            if (languageValues == null || languageValues.size() == 0) {
                continue;
            }
            Log.debug("转换 " + entry.getKey() + "   shortName:" + entry.getValue());
            HJLXML hjlxml = new HJLXML(dir + File.separator + config.getOutputPath(), entry.getValue(), languageValues, hjlExcel.getCodes());
            try {
                hjlxml.executeWrite();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            });
        }

        fis.close();
    }
}
