package com.example.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TopDocs;

public class BooleanQueryExample2 {
    public static void main(String[] args) throws Exception {
        // RAMDirectory를 사용해 인덱스 저장
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        // IndexWriter를 사용해 문서 추가
        IndexWriter writer = new IndexWriter(index, config);
        addDocument(writer, "Lucene Introduction", "Lucene is a powerful Java library used for full-text indexing and search.");
        addDocument(writer, "Introduction to Java", "Java is a popular programming language.");
        addDocument(writer, "Advanced Lucene", "This document covers advanced topics in Lucene.");
        writer.close();

        // DirectoryReader를 사용해 인덱스를 읽음
        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        // TermQuery 생성
        Query query1 = new TermQuery(new Term("title", "Lucene"));  // 제목에 "Lucene" 포함
        Query query2 = new TermQuery(new Term("content", "Java"));  // 내용에 "Java" 포함

        // BooleanQuery 생성
        BooleanQuery booleanQuery = new BooleanQuery.Builder()
                .add(query1, BooleanClause.Occur.SHOULD)  // 제목에 "Lucene"이 포함된 문서를 찾음
                .add(query2, BooleanClause.Occur.SHOULD)  // 내용에 "Java"가 포함된 문서를 찾음
                .build();

        // 검색 수행
        TopDocs docs = searcher.search(booleanQuery, 10);
        System.out.println("Total Hits: " + docs.totalHits.value);

        // 검색 결과 출력
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("Title: " + doc.get("title"));
        }

        reader.close();
    }

    // 문서 추가 함수
    private static void addDocument(IndexWriter writer, String title, String content) throws Exception {
        Document doc = new Document();
        doc.add(new TextField("title", title, TextField.Store.YES));
        doc.add(new TextField("content", content, TextField.Store.YES));
        writer.addDocument(doc);
    }
}
