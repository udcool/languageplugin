package com.hyjoy.gradle.plugin.language;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

/**
 * Created by hyjoy on 2019/3/14.
 */
public class HJLXML {

    private String mDiractory;
    private String mShortName;
    private List<String> mDatas;
    private List<String> mCodes;

    public HJLXML(String diractory, String shortName, List<String> datas, List<String> codes) {
        this.mDiractory = diractory;
        this.mShortName = shortName;
        this.mDatas = datas;
        this.mCodes = codes;
    }


    public void executeWrite() throws IOException {
        String data = buildContent();
        writeXML(data);
    }


    private String buildContent() {
        int count = Math.min(mCodes.size(), mDatas.size());
        StringBuilder sb = new StringBuilder();
        sb.append("    <!-- ");
        sb.append("文本导入时间：");
        sb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date()));
        sb.append("     ");
        sb.append(System.getProperty("user.name"));
        sb.append(" -->\n");
        for (int i = 0; i < count; i++) {
            if (mCodes.get(i) == null || mCodes.get(i).trim().length() == 0) {
                continue;
            }

            sb.append("    <string name=\"");
            sb.append(mCodes.get(i).trim());
            sb.append("\">");
            sb.append(mDatas.get(i).replaceAll("'", "\\'"));
            sb.append("</string>\n");
        }

        return sb.toString();
    }

    public void writeXML(String data) throws IOException {
        String path;
        if (Objects.equals("en", mShortName) || mShortName.length() == 0) {
            path = "values";
        } else {
            path = "values-" + mShortName;
        }

        File file = new File(mDiractory + File.separator + path + File.separator + "strings.xml");
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        long seek = seekStepAndFixTAG(file);

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        randomAccessFile.seek(seek + "\n".length());

        randomAccessFile.write(("\n\n" + data + "\n").getBytes());
        randomAccessFile.close();
        seekStepAndFixTAG(file);
    }


    private long seekStepAndFixTAG(File file) throws IOException {
        boolean hasHead = false;
        boolean hasFoot = false;
        boolean headContent = false;

        Scanner scanner = new Scanner(file, "utf-8");
        long seek = 0L;
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.trim().length() > 0 && Objects.equals(line.trim(), "\n")) {
                headContent = true;
            }
            if (!headContent && line.contains("<resources>")) {
                hasHead = true;
            }
            if (line.contains("</resources>")) {
                hasFoot = true;
            } else {
                seek += line.length();
            }
        }

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        if (!hasHead) {
            randomAccessFile.seek(0L);
            randomAccessFile.write("<resources>\n".getBytes());
            seek += "<resources>\n".length();
        }

        // 需要写入
        if (!hasFoot) {
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.write("\n</resources>\n".getBytes());
        }

        return seek;
    }
}
