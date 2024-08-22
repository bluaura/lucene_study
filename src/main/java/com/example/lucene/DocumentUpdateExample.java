package com.example.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

public class DocumentUpdateExample {
    public static void main(String[] args) {
        try {
            // 인덱스를 위한 디렉토리 설정
            Directory index = new ByteBuffersDirectory();
            Analyzer analyzer = new StandardAnalyzer();
            Analyzer ko_analyzer = new KoreanAnalyzer();

            // 인덱스 생성 및 초기 문서 추가
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            try (IndexWriter writer = new IndexWriter(index, config)) {
                // 첫 번째 문서 인덱싱
                addDocument(writer, "document1.pdf", "This is the original content.");
            }

            // 기존 문서에 내용 추가
//            IndexWriterConfig ko_config = new IndexWriterConfig(ko_analyzer);
            IndexWriterConfig ko_config = new IndexWriterConfig(analyzer);
            try (IndexWriter writer = new IndexWriter(index, ko_config)) {
                updateDocument(writer, "document1.pdf", "Here is some new content added to the document.");
            }

            // 인덱싱된 모든 문서 출력 (문서 업데이트 후 결과 확인)
            printAllIndexedContent(index);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 새로운 문서를 인덱스에 추가하는 함수
    private static void addDocument(IndexWriter writer, String filename, String content) throws Exception {
        Document doc = new Document();
        doc.add(new StringField("filename", filename, Field.Store.YES));
        doc.add(new TextField("content", content, Field.Store.YES));
        writer.addDocument(doc);
        writer.commit();  // 인덱스에 변경 사항 반영
        System.out.println("Document added: " + filename);
    }

    // 기존 문서에 내용을 추가하거나 업데이트하는 함수
    private static void updateDocument(IndexWriter writer, String filename, String newContent) throws Exception {
        // 기존 문서를 찾기 위한 Term 생성
        Term term = new Term("filename", filename);

        // 새로운 내용을 추가한 새 문서 생성
        Document updatedDoc = new Document();
        updatedDoc.add(new StringField("filename", filename, Field.Store.YES));
        updatedDoc.add(new TextField("content", newContent, Field.Store.YES));

        // 기존 문서 업데이트 (Term을 기반으로 기존 문서를 삭제하고 새 문서를 추가)
        writer.updateDocument(term, updatedDoc);
        writer.commit();  // 인덱스에 변경 사항 반영
        System.out.println("Document updated: " + filename);
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
            System.out.println("Filename: " + doc.get("filename"));
            System.out.println("Content: " + doc.get("content"));
            System.out.println("-------------------------");
        }

        // 리더 닫기
        reader.close();
    }
}
