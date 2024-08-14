package com.oat.common.utils;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

@Log4j2
public class CsvFileUtil {
    public static void readCsvFile(String fileLocation, ArrayList<String[]> valueList){
       try{
            File csvFile = new File(fileLocation);
            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            String line = "";

            while(reader.ready()){
                line = reader.readLine();
                String[] splitLine = line.split(",");
                valueList.add(splitLine);
            }
            reader.close();
        } catch(FileNotFoundException e ){
            log.warn("在 "+ fileLocation + " 没有找到文件");
        } catch(IOException e){
            log.warn("文件 " + fileLocation + " 读写错误");
        }
    }
    public static void readLinesOfCsvFile(String fileLocation, ArrayList<String> lineList){
        try{
            File csvFile = new File(fileLocation);
            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            String line = "";

            while(reader.ready()){
                line = reader.readLine();
                lineList.add(line);
            }
            reader.close();
        } catch(FileNotFoundException e ){
            log.warn("在 "+ fileLocation + " 没有找到文件");
        } catch(IOException e){
            log.warn("文件 " + fileLocation + " 读写错误");
        }
    }

    public static void writeCsvFileByLines(String fileLocation, ArrayList<String> lines) {
        writeCsvFile(fileLocation, null, lines );
    }

    public static void writeCsvFileBySplitLines(String fileLocation, ArrayList<String[]> splitLines) {
        writeCsvFile(fileLocation, splitLines, null );
    }
    public static void writeCsvFile(String fileLocation, ArrayList<String[]> splitLines, ArrayList<String> lines) {

        try{
            createFileRecursion(fileLocation, 0);
            File csvFile = new File(fileLocation);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(csvFile), "utf8");
            if (splitLines != null) {
                for (int i = 0; i < splitLines.size(); i++) {
                    String[] splitLine = splitLines.get(i);
                    String line = joinString(splitLine, ",");
                    writer.write(line);
                    writer.write("\r\n");
                }
            } else if (lines != null){
                for (int i = 0; i < lines.size(); i++) {
                    writer.write(lines.get(i));
                    writer.write("\r\n");
                }
            }
            writer.flush();
            writer.close();
        }catch(FileNotFoundException e ){
            log.warn("在 "+ fileLocation + " 没有找到文件");
        } catch(IOException e){
            log.warn("文件 " + fileLocation + " 读写错误");
        }
    }

    public static String joinString(String[] array, String s) {
        String result = "";
        for (int i = 0; i < array.length; i++ ){
            if (result.length() > 0 ){
                result +=",";
            }
            result += array[i];
        }
        return result;
    }

    public static ArrayList<String[]> transferEntityToSplitLines(ArrayList<String> properties, ArrayList<?> list) throws IllegalAccessException {
        ArrayList<String[]> splitLines = new ArrayList<>();
        transferEntityToStringArray(properties, list, splitLines, null);
        return splitLines;
    }

    public static ArrayList<String> transferEntityToLines(ArrayList<String> properties, ArrayList<?> list) throws IllegalAccessException {
        ArrayList<String> lines = new ArrayList<>();
        transferEntityToStringArray(properties, list, null, lines);
        return lines;
    }
    public static void transferEntityToStringArray(ArrayList<String> properties, ArrayList<?> list,
                                                   ArrayList<String[]> splitLines, ArrayList<String> lines )
            throws IllegalAccessException {
    if (splitLines == null){
        splitLines = new ArrayList<>();
    }
    if (lines == null){
        lines = new ArrayList<>();
    }
    ArrayList<String> contentList = new ArrayList<>();
    String[] titles = properties.toArray(new String[properties.size()]);

    splitLines.add(titles);
    lines.add(joinString(titles,","));
    for (Object obj: list){
        Class<?> classOfObj = obj.getClass();
        Class<?> classSuper = obj.getClass().getSuperclass();
        String[] splitLine = new String[properties.size()];
        for (int i = 0; i< properties.size(); i++) {
            Field field = null;
            try{
                field = classOfObj.getDeclaredField(properties.get(i));
            } catch(NoSuchFieldException e){
                try {
                    field = classSuper.getDeclaredField(properties.get(i));
                }catch (NoSuchFieldException e2){
                    log.error("字段名错误： 类 "+ classOfObj.getName() + " 没有字段名 " + properties.get(i));
                }
            }
            if (field != null){
                field.setAccessible(true);
                Object value = field.get(obj);
                if (value != null) {
                    if (field.getType().getName().equals("java.util.Date")) {
                        splitLine[i] = DateUtil.getDateString((Date) field.get(obj));
                    } else {
                        splitLine[i] = field.get(obj).toString();
                    }
                }else {
                    splitLine[i] = null;
                }
            }
        }
        splitLines.add(splitLine);
        contentList.add(joinString(splitLine,","));
    }
    Collections.sort(contentList);
    lines.addAll(contentList);
}

    public static void exportCsv(ArrayList<String> properties, ArrayList<?> list, String fileLocation) throws IllegalAccessException {
        ArrayList<String> lines =  transferEntityToLines(properties,list);
        writeCsvFileByLines(fileLocation, lines);
    }

    public static void createFileRecursion(String fileName, int depth) throws IOException {
        Path path = Paths.get(fileName);
        if (Files.exists(path)){
            return;
        }
        if (Files.exists(path.getParent())) {
            if (depth == 0) {
                Files.createFile(path);
            } else {
                Files.createDirectories(path);
            }
        }else {
            createFileRecursion(path.getParent().toString(), depth + 1);
            createFileRecursion(fileName, depth);
        }
    }
}
