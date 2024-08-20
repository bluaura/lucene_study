package com.example.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;

public class MultiFormatIndexer {
    private static void indexDocument(Directory index, File file) throws Exception {
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config);

    	// Tika 파서 설정
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();

        // 파일 파싱
        try (FileInputStream stream = new FileInputStream(file)) {
            parser.parse(stream, handler, metadata, context);
        }

        // 문서 정보
        String content = handler.toString();
        String filename = file.getName();
        String fileType = metadata.get(Metadata.CONTENT_TYPE);
        
        // 인덱싱 문서 생성
        Document doc = new Document();
        doc.add(new StringField("filename", filename, Field.Store.YES));
        doc.add(new TextField("content", content, Field.Store.YES));
        doc.add(new StringField("type", fileType, Field.Store.YES));
        
        // 문서 추가
        writer.addDocument(doc);
        writer.close();
        
        // XML 핸들러 사용 - 파싱된 구조를 XML로 출력
        ToXMLContentHandler handler2 = new ToXMLContentHandler();

        // 파일 파싱 과정 모니터링
        monitorParsingProcess(parser, file.getCanonicalPath(), handler2, metadata, context);
        }

    public static void deleteDocumentByFilename(Directory index, String filename) throws Exception {
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config);

        // 파일명을 기반으로 한 TermQuery를 사용하여 인덱스에서 해당 문서 삭제
        Term term = new Term("filename", filename);
        Query query = new TermQuery(term);
        
        // 문서 삭제
        writer.deleteDocuments(query);
        writer.commit(); // 삭제된 내용을 인덱스에 반영
        System.out.println("Document with filename '" + filename + "' deleted.");
        writer.close();
    }

    // 텍스트를 분석하고 토큰화된 결과를 출력하는 함수
    private static void analyzeText(Analyzer analyzer, String text) throws IOException {
        // TokenStream을 사용해 Analyzer로 텍스트를 분석
        try (TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(text))) {
            // TokenStream으로부터 토큰을 추출하는 속성 설정
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

            // TokenStream을 초기화 (reset)
            tokenStream.reset();

            System.out.println("Analyzed tokens:");
            // TokenStream으로부터 토큰을 순차적으로 추출하고 출력
            while (tokenStream.incrementToken()) {
                String token = charTermAttribute.toString();
                System.out.println(token);
            }

            // TokenStream 마무리 (end, close)
            tokenStream.end();
        }
    }

    // 파싱 과정을 모니터링하고 출력하는 함수
    private static void monitorParsingProcess(AutoDetectParser parser, String filePath, 
    		ToXMLContentHandler handler, Metadata metadata, ParseContext context) {
    	String outputFile = "parsingOutput.txt";

    	try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            try (FileInputStream stream = new FileInputStream(new File(filePath))) {
            	writer.write("Starting parsing for file: " + filePath);
            	writer.newLine();
                System.out.println("Starting parsing for file: " + filePath);

                // 파싱 시작
                parser.parse(stream, handler, metadata, context);

                writer.write("Parsing completed. Output:");
                writer.newLine();
                System.out.println("Parsing completed. Output:");

                // 파싱된 텍스트 또는 XML 출력
                writer.write(handler.toString());
                writer.newLine();
                System.out.println(handler.toString());

                // 메타데이터 출력
                writer.write("\nExtracted Metadata:");
                writer.newLine();
                System.out.println("\nExtracted Metadata:");
                String[] metadataNames = metadata.names();
                for (String name : metadataNames) {
                	writer.write(name + ": " + metadata.get(name));
                	writer.newLine();
                    System.out.println(name + ": " + metadata.get(name));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
    	}catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        // Lucene 설정
        Analyzer analyzer = new StandardAnalyzer();
        Directory index = new ByteBuffersDirectory();

        // 인덱싱할 문서 폴더
        File docsFolder = Paths.get("C:\\Users\\c\\OneDrive\\문서\\TestDoc2").toFile();
        for (File file : docsFolder.listFiles()) {
            if (!file.isDirectory()) {
            	try {
                    indexDocument(index, file);            		
            	}catch (Exception e) {
            		e.printStackTrace();
            	}
            }
        }

        // 검색 테스트
        String querystr = "python";
        Query q = new QueryParser("content", analyzer).parse(querystr);

        // 검색 실행
        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, 100);
        ScoreDoc[] hits = docs.scoreDocs;

        // 결과 출력
        System.out.println("Found " + hits.length + " hits.");
        for (ScoreDoc hit : hits) {
            Document d = searcher.doc(hit.doc);
            System.out.println(d.get("filename") + " - Type: " + d.get("type"));
        }

        // 문서 삭제 예시
        String filenameToDelete = "Learning Pandas.pdf";  // 삭제할 파일 이름
        deleteDocumentByFilename(index, filenameToDelete);

        // 다시 검색 실행
        reader = DirectoryReader.open(index);
        searcher = new IndexSearcher(reader);
        docs = searcher.search(q, 100);
        hits = docs.scoreDocs;

        System.out.println("Found " + hits.length + " hits.");
        for (ScoreDoc hit : hits) {
            Document d = searcher.doc(hit.doc);
            System.out.println(d.get("filename") + " - Type: " + d.get("type"));
        }

        reader.close();

        String testText = "이 문장은 한글 텍스트 분석을 위한 테스트입니다.";
        analyzeText(analyzer, testText);
    }
}
