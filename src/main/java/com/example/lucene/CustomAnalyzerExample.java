package com.example.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CustomAnalyzerExample {
    public static void main(String[] args) throws Exception {
        Analyzer analyzer = new CustomKoreanAnalyzer();
        Directory index = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config);

        addDoc(writer, "Lucene in Action (두번째 버전)", "[978-1933988177]");
        addDoc(writer, "Lucene for Dummies", "978-1234567890");
        writer.close();

        search(index, analyzer, "content", "(두번째 버전)");
        search(index, analyzer, "content", "ISBN [978-1933988177]");
    }

    private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("content", title, Field.Store.YES));
        doc.add(new TextField("isbn", isbn, Field.Store.YES));
        w.addDocument(doc);
    }

    private static void search(Directory index, Analyzer analyzer, String field, String queryStr) throws Exception {
        QueryParser parser = new QueryParser(field, analyzer);
        Query query = parser.parse(queryStr);

        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(query, 10);
        ScoreDoc[] hits = docs.scoreDocs;

        System.out.println("Found " + hits.length + " hits for query \"" + queryStr + "\":");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("content") + " (ISBN: " + d.get("isbn") + ")");
        }

        reader.close();
    }
}
