package com.example.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PdfIndexer {
    public static void main(String[] args) {
        try {
            // Tika 인스턴스 생성
            Tika tika = new Tika();

            // Lucene 설정
            Directory indexDirectory = new ByteBuffersDirectory();
            StandardAnalyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter indexWriter = new IndexWriter(indexDirectory, config);

            // PDF, PowerPoint, Excel 파일이 있는 디렉토리 경로
            Path folderPath = Paths.get("C:\\Users\\c\\OneDrive\\문서\\TestDocs");

            // 디렉토리 내의 파일들을 처리
            Files.walk(folderPath)
                .filter(Files::isRegularFile)  // 파일만 필터링
                .forEach(filePath -> {
                    try (InputStream stream = new FileInputStream(filePath.toFile())) {
                        Metadata metadata = new Metadata();

                        // 파일의 MIME 타입 감지
                        String mimeType = tika.detect(stream, metadata);

                        // PDF, PowerPoint, Excel 파일만 인덱싱
                        if (mimeType.equals(MediaType.application("pdf").toString()) || 
                            mimeType.equals(MediaType.application("vnd.openxmlformats-officedocument.presentationml.presentation").toString()) ||
                            mimeType.equals(MediaType.application("vnd.ms-powerpoint").toString()) ||
                            mimeType.equals(MediaType.application("vnd.openxmlformats-officedocument.spreadsheetml.sheet").toString()) || 
                            mimeType.equals(MediaType.application("vnd.ms-excel").toString())) {

                            // 파일 내용을 Tika로 추출
                            String content = tika.parseToString(filePath.toFile());

                            // Lucene 문서 생성 및 인덱싱
                            Document doc = new Document();
                            doc.add(new StringField("filename", filePath.getFileName().toString(), Field.Store.YES));
                            doc.add(new TextField("contents", content, Field.Store.YES));
                            indexWriter.addDocument(doc);

                            System.out.println("Indexed: " + filePath.getFileName() + " (" + mimeType + ")");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            // 인덱스 작성 완료
            indexWriter.close();

            // 검색 예제 (예: "your search query")
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(indexDirectory));
            QueryParser queryParser = new QueryParser("contents", analyzer);
            Query query = queryParser.parse("python");

            TopDocs results = searcher.search(query, 10);
            for (ScoreDoc scoreDoc : results.scoreDocs) {
                Document foundDoc = searcher.doc(scoreDoc.doc);
                System.out.println("파일명: " + foundDoc.get("filename"));
//                System.out.println("내용: " + foundDoc.get("contents"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
