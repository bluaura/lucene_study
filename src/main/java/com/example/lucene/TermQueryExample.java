package com.example.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;

public class TermQueryExample {

    public static void main(String[] args) throws Exception {
        // StandardAnalyzer를 사용하여 인덱스 설정
        Analyzer analyzer = new StandardAnalyzer();
        Directory index = new ByteBuffersDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config);

        // 문서 추가
        addDoc(writer, "Lucene in Action", "193398817");
        addDoc(writer, "Lucene for Dummies", "55320055Z");
        writer.close();

        // 검색
        searchByTerm(index, "title", "Lucene");
        searchByTerm(index, "isbn", "193398817");
    }

    private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        w.addDocument(doc);
    }

    private static void searchByTerm(Directory index, String field, String termText) throws IOException {
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        // TermQuery 생성
        Term term = new Term(field, termText);
        Query query = new TermQuery(term);
        TopDocs docs = searcher.search(query, 10);
        ScoreDoc[] hits = docs.scoreDocs;

        System.out.println("Found " + hits.length + " hits for term \"" + termText + "\" in field \"" + field + "\":");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("title") + " (ISBN: " + d.get("isbn") + ")");
        }

        reader.close();
    }
}
