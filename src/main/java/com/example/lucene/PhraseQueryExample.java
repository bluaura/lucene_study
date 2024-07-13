package com.example.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

public class PhraseQueryExample {
    public static void main(String[] args) throws Exception {
        // Create the analyzer
        Analyzer analyzer = new StandardAnalyzer();

        // Create the index
        Directory index = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config);

        // Create documents with TextField
        Document doc1 = new Document();
        doc1.add(new TextField("content", "This is a test document", Field.Store.YES));
        writer.addDocument(doc1);

        Document doc2 = new Document();
        doc2.add(new TextField("content", "Another document for testing", Field.Store.YES));
        writer.addDocument(doc2);

        writer.close();

        // Search the index
        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        // Create a PhraseQuery for "test document"
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.add(new Term("content", "test"));
        builder.add(new Term("content", "document"));
        PhraseQuery phraseQuery = builder.build();

        // Perform the search
        ScoreDoc[] hits = searcher.search(phraseQuery, 10).scoreDocs;

        // Display the results
        System.out.println("Results for PhraseQuery search:");
        for (ScoreDoc hit : hits) {
            Document foundDoc = searcher.doc(hit.doc);
            System.out.println("Content: " + foundDoc.get("content"));
        }

        reader.close();
        index.close();
    }
}
