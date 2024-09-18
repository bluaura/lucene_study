package com.example.lucene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadFileLines {
    public static void main(String[] args) {
        String filePath = "C:\\Workspaces\\testfile.txt";  // 읽고자 하는 파일의 경로를 입력하세요
        int lineCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.readLine() != null) {
                lineCount++;
            }
        } catch (IOException e) {
            System.out.println("파일을 읽는 중 오류가 발생했습니다: " + e.getMessage());
        }

        System.out.println("파일의 라인 수: " + lineCount);
    }
}
