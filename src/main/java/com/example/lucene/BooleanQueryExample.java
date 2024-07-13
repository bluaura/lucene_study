/**
 * 
 */
package com.example.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

/**
 * 
 */
public class BooleanQueryExample {
    public static void main(String[] args) throws Exception {
        // Create the analyzer
        Analyzer analyzer = new StandardAnalyzer();

        // Create the index
        Directory index = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config);

        // Create documents with TextField
        Document doc1 = new Document();
        doc1.add(new TextField("title", "Document 1", Field.Store.YES));
        doc1.add(new TextField("content", "This is the content of document 1", Field.Store.YES));
        writer.addDocument(doc1);

        Document doc2 = new Document();
        doc2.add(new TextField("title", "Another Document", Field.Store.YES));
        doc2.add(new TextField("content", "This is the content of another document", Field.Store.YES));
        writer.addDocument(doc2);

        Document doc3 = new Document();
        doc2.add(new TextField("title", "the third Document", Field.Store.YES));
        doc2.add(new TextField("content", "This is the boolean query document", Field.Store.YES));
        writer.addDocument(doc2);

        Document doc4 = new Document();
        doc2.add(new TextField("title", "the fourth Document", Field.Store.YES));
        doc2.add(new TextField("content", "This is the new document", Field.Store.YES));
        writer.addDocument(doc2);

        writer.close();

        // Search the index
        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        // Create a QueryParser for both fields
        QueryParser titleParser = new QueryParser("title", analyzer);
        QueryParser contentParser = new QueryParser("content", analyzer);

        // Create queries for each field
        Query titleQuery = titleParser.parse("\"Document 1\"");
        Query contentQuery = contentParser.parse("\"content of document 1\"");

        // Combine queries using BooleanQuery
        BooleanQuery combinedQuery = new BooleanQuery.Builder()
                .add(titleQuery, BooleanClause.Occur.MUST)
                .add(contentQuery, BooleanClause.Occur.MUST)
                .build();

        // Perform the search
        ScoreDoc[] hits = searcher.search(combinedQuery, 10).scoreDocs;

        // Display the results
        System.out.println("Results for combined field query:");
        for (ScoreDoc hit : hits) {
            Document foundDoc = searcher.doc(hit.doc);
            System.out.println("Title: " + foundDoc.get("title"));
            System.out.println("Content: " + foundDoc.get("content"));
        }

        reader.close();
        index.close();
    }
}
