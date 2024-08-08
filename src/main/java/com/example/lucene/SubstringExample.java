package com.example.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

public class SubstringExample {
    public static void main(String[] args) throws Exception {
        // 1. StandardAnalyzer 생성
        StandardAnalyzer analyzer = new StandardAnalyzer();

        Directory index = new ByteBuffersDirectory();

        // 3. IndexWriter 설정 및 생성
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config);

        // 4. 문서 추가
        addDoc(writer, "Lucene is a powerful search library");
        addDoc(writer, "Lucene supports prefix queries");
        addDoc(writer, "This is a simple example");
        writer.close();

        // 5. PrefixQuery 생성
        String prefix = "lu";
        Query q = new PrefixQuery(new Term("content", prefix));

        // 6. 인덱스를 검색하기 위해 DirectoryReader와 IndexSearcher 생성
        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        // 7. 쿼리 실행
        ScoreDoc[] hits = searcher.search(q, 10).scoreDocs;

        // 8. 결과 출력
        System.out.println("Found " + hits.length + " hits.");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("content"));
        }

        // 9. 리더 닫기
        reader.close();
    }

    private static void addDoc(IndexWriter w, String content) throws Exception {
        Document doc = new Document();
        doc.add(new TextField("content", content, Field.Store.YES));
        w.addDocument(doc);
    }
}
