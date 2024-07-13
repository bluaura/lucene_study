package com.example.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

public class StringFieldExample {
    public static void main(String[] args) throws Exception {
        // Create the analyzer
        Analyzer analyzer = new StandardAnalyzer();

        // Create the index
        Directory index = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config);

        // Create documents with StringField
        Document doc1 = new Document();
        doc1.add(new StringField("id", "12345", Field.Store.YES));
        doc1.add(new TextField("title", "Document 1", Field.Store.YES));
        writer.addDocument(doc1);

        Document doc2 = new Document();
        doc2.add(new StringField("id", "67890", Field.Store.YES));
        doc2.add(new TextField("title", "Document 2", Field.Store.YES));
        writer.addDocument(doc2);

        writer.close();

        // Search the index
        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        // Create a query for the StringField
        QueryParser parser = new QueryParser("title", analyzer);
        Query query = parser.parse("\"Document 1\"");

        // Perform the search
        ScoreDoc[] hits = searcher.search(query, 10).scoreDocs;

        // Display the results
        System.out.println("Results for StringField search:");
        for (ScoreDoc hit : hits) {
            Document foundDoc = searcher.doc(hit.doc);
            System.out.println("ID: " + foundDoc.get("id"));
            System.out.println("Title: " + foundDoc.get("title"));
        }

        reader.close();
        index.close();
    }
}
