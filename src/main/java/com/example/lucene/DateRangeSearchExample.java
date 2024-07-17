package com.example.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateRangeSearchExample {

    public static void main(String[] args) throws IOException, ParseException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config);

        // 날짜 형식을 지정합니다.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 문서 인덱싱
        addDoc(writer, "Lucene in Action", "978-1933988177", dateFormat.parse("2022-01-01 10:00:00"));
        addDoc(writer, "Lucene for Dummies", "978-1234567890", dateFormat.parse("2022-01-02 12:00:00"));
        addDoc(writer, "Lucene Cookbook", "978-9876543210", dateFormat.parse("2022-01-03 14:00:00"));
        writer.close();

        // 검색 예제
        search(index, analyzer, dateFormat.parse("2022-01-01 00:00:00"), dateFormat.parse("2022-01-02 23:59:59"));
    }

    private static void addDoc(IndexWriter w, String title, String isbn, Date date) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        // 날짜를 long 값으로 변환하여 저장합니다.
        doc.add(new LongPoint("date", date.getTime()));
        doc.add(new StoredField("date", date.getTime()));
        w.addDocument(doc);
    }

    private static void search(Directory index, StandardAnalyzer analyzer, Date startDate, Date endDate) throws IOException {
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        // 날짜 범위를 long 값으로 변환합니다.
        long start = startDate.getTime();
        long end = endDate.getTime();

        // 날짜 범위 쿼리를 생성합니다.
        Query query = LongPoint.newRangeQuery("date", start, end);

        TopDocs docs = searcher.search(query, 10);
        ScoreDoc[] hits = docs.scoreDocs;

        System.out.println("Found " + hits.length + " hits:");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("title") + " (ISBN: " + d.get("isbn") + ", Date: " + new Date(d.getField("date").numericValue().longValue()) + ")");
        }

        reader.close();
    }
}
