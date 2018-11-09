/*
 * Copyright 2018 firefly1126, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.gradle_plugin_android_aspectjx
 */
package com.hyjoy.gradle.plugin.language

import com.hyjoy.gradle.plugin.language.model.ExcelDocument
import com.hyjoy.gradle.plugin.language.model.HJLLExtensionConfig
import com.hyjoy.gradle.plugin.language.model.Language
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.nio.charset.StandardCharsets

class HJLLTask extends DefaultTask {

    @Input
    public HJLLanguageExtension extension() {
        return project.extensions.getByType(HJLLanguageExtension.class)
    }


    @TaskAction
    void transiformLanguage() {
        def extension = extension()
        Log.log(extension.configFile)
        def config = new HJLLExtensionConfig(extension.configFile)
        Map<String, Object> map = config.loadLanguageMap()
        List<List<String>> rows = new ExcelDocument(config.loadFilePath(), config.loadLanguageMap().size()).loadRowDatas()
        String outputPath = config.loadOutputPath()
        Map<String, List<Language>> sData = loadLanguages(rows, config.fillEmpty())
        writeXML(outputPath, map, sData)
    }

    void writeXML(String outputPath, Map<String, Object> map, Map<String, List<Language>> sData) throws IOException {
        String endTag = "</resources>"
        for (Map.Entry<String, List<Language>> entry : sData.entrySet()) {
            RandomAccessFile randomAccessFile = null
            try {
                if (entry.getValue().size() == 0) continue
                String key = (String) map.get(entry.getKey())
                String path
                if (key != null && !Objects.equals("", key)) {
                    path = "values-" + key
                } else {
                    path = "values"
                }
                File file1 = new File(".", outputPath + path + "/strings.xml")
                if (!file1.exists()) {
                    if (!file1.getParentFile().exists()) {
                        file1.getParentFile().mkdirs()
                    }
                    file1.createNewFile()
                }
                randomAccessFile = new RandomAccessFile(file1, "rw")
                long totalLength = randomAccessFile.length()
                if (totalLength > "<resources>".size()) {
                    long step = 0l
                    String endStr
                    int index = -1
                    while (index < 0) {
                        step += endTag.length() * 2
                        if (totalLength - step <= 0) {
                            break
                        }
                        randomAccessFile.seek(totalLength - step)
                        byte[] buff = new byte[(int) step]
                        randomAccessFile.readFully(buff)
                        endStr = new String(buff)
                        index = endStr.indexOf(endTag)
                    }
                    if (randomAccessFile.length() - step + index > 0) {
                        randomAccessFile.seek(randomAccessFile.length() - step + index)
                    }
                } else {
                    randomAccessFile.write("<resources>\n".getBytes(StandardCharsets.UTF_8))
                }

                StringBuilder sb = new StringBuilder()
                for (Language language : entry.getValue()) {
                    if (Objects.isNull(language) || Objects.isNull(language.value) || Objects.isNull(language.code) || Objects.equals("", language.value.trim()))
                        continue
                    sb.append("    <string name=\"")
                    sb.append(language.code)
                    sb.append("\">")
                    sb.append(language.value)
                    sb.append("</string>\n")
                }
                randomAccessFile.write(sb.toString().getBytes(StandardCharsets.UTF_8))
                randomAccessFile.write("</resources>".getBytes(StandardCharsets.UTF_8))
            } catch (Exception e) {
                throw new IllegalArgumentException(e)
            } finally {
                if (randomAccessFile != null) {
                    randomAccessFile.close()
                }
            }
        }
    }

    private
    static Map<String, List<Language>> loadLanguages(List<List<String>> rows, boolean fillEmpty) {
        Map<String, List<Language>> sData = new HashMap<>()
        // key， 以及 value
        if (rows.size() > 1) {
            List<String> languages = rows.get(0)

            if (languages.size() == 0) {
                throw IllegalArgumentException("语言头信息错误（第一行数据）")
            }

            for (String language : languages) {
                Log.log(language)
                sData.put(language, new ArrayList<>())
            }
            for (int i = 1; i < rows.size(); i++) {
                List<String> data1 = rows.get(i)
                String code = data1.get(0)
                for (int j = 1; j < data1.size(); j++) {
                    Language language = new Language()
                    language.code = code
                    if (data1.get(j) == null || "" == data1.get(j).trim()) {
                        if (!fillEmpty) continue
                    }
                    language.value = data1.get(j)

                    sData.get(languages.get(j)).add(language)
                }
            }
        }
        return sData
    }
}