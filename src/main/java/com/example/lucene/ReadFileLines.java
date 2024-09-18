package com.example.lucene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadFileLines {
    public static void main(String[] args) {
        String filePath = "C:\\Workspaces\\testfile.txt";  // 읽고자 하는 파일의 경로를 입력하세요
        int totalLines = 0;
        int currentLine = 0;

        // 전체 라인 수를 먼저 계산합니다.
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.readLine() != null) {
                totalLines++;
            }
        } catch (IOException e) {
            System.out.println("파일을 읽는 중 오류가 발생했습니다: " + e.getMessage());
            return;
        }

        if (totalLines == 0) {
            System.out.println("파일이 비어있습니다.");
            return;
        }

        // 파일을 다시 읽으며 진행률을 표시합니다.
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                currentLine++;
                // 진행률 계산
                int progress = (currentLine * 100) / totalLines;
                System.out.print("\r진행률: " + progress + "%");
            }
        } catch (IOException e) {
            System.out.println("파일을 읽는 중 오류가 발생했습니다: " + e.getMessage());
        }

        System.out.println("\n파일 읽기가 완료되었습니다.");
    }
}
