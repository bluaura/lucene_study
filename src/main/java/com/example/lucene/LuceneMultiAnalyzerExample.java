package com.example.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LuceneMultiAnalyzerExample {
    public static void main(String[] args) throws Exception {
        // 기본 분석기를 설정합니다.
        StandardAnalyzer defaultAnalyzer = new StandardAnalyzer();

        // 필드별로 다른 분석기를 설정합니다.
        Map<String, Analyzer> analyzerPerField = new HashMap<>();
        analyzerPerField.put("isbn", new KeywordAnalyzer());
        analyzerPerField.put("title", new StandardAnalyzer());

        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(defaultAnalyzer, analyzerPerField);

        // 인덱스 설정
        Directory index = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config);

        // 문서 추가
        addDoc(writer, "Lucene in Action", "193398817");
        addDoc(writer, "Lucene for Dummies", "55320055Z");
        writer.close();

        // 검색
        searchByISBN(index, "193398817");
        searchByTitle(index, "Lucene");
    }

    private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        w.addDocument(doc);
    }

    private static void searchByISBN(Directory index, String isbn) throws IOException {
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        Query query = new TermQuery(new org.apache.lucene.index.Term("isbn", isbn));
        TopDocs docs = searcher.search(query, 10);
        ScoreDoc[] hits = docs.scoreDocs;

        System.out.println("Found " + hits.length + " hits for ISBN: " + isbn);
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("title") + " (ISBN: " + d.get("isbn") + ")");
        }

        reader.close();
    }

    private static void searchByTitle(Directory index, String title) throws IOException {
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        Query query = new TermQuery(new org.apache.lucene.index.Term("title", title));
        TopDocs docs = searcher.search(query, 10);
        ScoreDoc[] hits = docs.scoreDocs;

        System.out.println("Found " + hits.length + " hits for title: " + title);
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("title") + " (ISBN: " + d.get("isbn") + ")");
        }

        reader.close();
    }
}
