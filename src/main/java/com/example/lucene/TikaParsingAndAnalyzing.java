package com.example.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;

import java.io.*;

public class TikaParsingAndAnalyzing {

    public static void main(String[] args) {
        // 파일 경로 설정
        String filePath = "C:\\Users\\c\\OneDrive\\문서\\TestDocs\\AndroidInternals.pdf";
        String outputFilePath = "parsing_and_analysis_output.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            // Tika 파싱 및 Lucene 분석 과정을 파일로 출력
            parseAndAnalyzeDocument(filePath, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Tika로 문서 파싱하고 Lucene Analyzer로 분석된 결과를 파일로 출력
    private static void parseAndAnalyzeDocument(String filePath, BufferedWriter writer) throws IOException {
        try {
            // Tika 설정
            AutoDetectParser parser = new AutoDetectParser();
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();
            BodyContentHandler handler = new BodyContentHandler(-1);

            // 파일 파싱
            try (FileInputStream stream = new FileInputStream(new File(filePath))) {
                writer.write("Starting parsing for file: " + filePath + "\n");
                parser.parse(stream, handler, metadata, context);
                writer.write("Parsing completed. Parsed content:\n");
                writer.write(handler.toString() + "\n");

                // 메타데이터 출력
                writer.write("\nExtracted Metadata:\n");
                String[] metadataNames = metadata.names();
                for (String name : metadataNames) {
                    writer.write(name + ": " + metadata.get(name) + "\n");
                }
            }

            // Lucene Analyzer 설정
            Analyzer analyzer = new StandardAnalyzer();
            writer.write("\nStarting analysis of parsed content:\n");
            analyzeText(analyzer, handler.toString(), writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lucene Analyzer로 텍스트 분석 후 파일로 출력
    private static void analyzeText(Analyzer analyzer, String text, BufferedWriter writer) throws IOException {
        try (TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(text))) {
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

            // TokenStream 초기화
            tokenStream.reset();

            // 분석된 토큰 출력
            writer.write("Analyzed tokens:\n");
            while (tokenStream.incrementToken()) {
                String token = charTermAttribute.toString();
                writer.write(token + "\n");
            }

            // TokenStream 종료
            tokenStream.end();
        }
    }
}
