package com.example.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

public class MultiAnalyzerExample {

    public static void main(String[] args) throws Exception {
        // 인덱스 저장을 위한 디렉토리 설정
        Directory index = new ByteBuffersDirectory();

        // StandardAnalyzer와 NoriAnalyzer(한국어 분석기) 설정
        Analyzer standardAnalyzer = new StandardAnalyzer();
        Analyzer koreanAnalyzer = new KoreanAnalyzer();

        // 두 분석기로 문서를 인덱싱
        indexDocumentWithMultipleAnalyzers(index, standardAnalyzer, koreanAnalyzer);

        // 인덱싱된 문서 검색 테스트
        searchDocuments(index, "content", "문서는");
        
    }

    private static void indexDocumentWithMultipleAnalyzers(Directory index, Analyzer standardAnalyzer, Analyzer koreanAnalyzer) throws Exception {
        IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer); // 기본 설정은 StandardAnalyzer로
        try (IndexWriter writer = new IndexWriter(index, config)) {
            Document doc = new Document();

            // StandardAnalyzer로 인덱싱
//            String englishContent = "This is a sample document with English content.";
            String content = "이 문서는 한국어 내용을 포함하고 있습니다.";
            doc.add(new TextField("content", content, Field.Store.YES));

            // NoriAnalyzer로 인덱싱
            doc.add(new TextField("content", content, Field.Store.YES));

            // 문서를 인덱스에 추가
            writer.addDocument(doc);
        }
    }

    private static void searchDocuments(Directory index, String field, String queryStr) throws Exception {
        // 검색 설정
        Analyzer standardAnalyzer = new StandardAnalyzer();
        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        // StandardAnalyzer로 쿼리 실행
        QueryParser parser = new QueryParser(field, standardAnalyzer);
        Query query = parser.parse(queryStr);

        // 검색 실행
        TopDocs results = searcher.search(query, 10);
        System.out.println("Found " + results.totalHits + " hits.");

        // 검색 결과 출력
        for (ScoreDoc scoreDoc : results.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("Content: " + doc.get("content"));
        }

        reader.close();
    }
    
    // 인덱스에 있는 모든 문서의 내용을 출력하는 함수
    private static void printAllIndexedContent(Directory index) throws Exception {
        // IndexReader를 사용하여 인덱스에서 모든 문서를 읽어옴
        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        // 모든 문서를 검색하기 위해 MatchAllDocsQuery 사용
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE); // 인덱스된 모든 문서를 가져옴

        // 모든 문서를 순회하면서 각 문서의 내용을 출력
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("Content : " + doc.get("content"));
//            System.out.println("Content (Korean): " + doc.get("content_ko"));
            System.out.println("-------------------------");
        }

        // 리더 닫기
        reader.close();
    }
}
