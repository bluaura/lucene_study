package com.example.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.QueryBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LuceneDateRangeSearchExample {
    public static void main(String[] args) throws Exception {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = new ByteBuffersDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config);

        // 문서 추가
        addDoc(writer, "Lucene in Action", "193398817", "2023-01-01");
        addDoc(writer, "Lucene for Dummies", "55320055Z", "2024-05-15");
        addDoc(writer, "Managing Gigabytes", "55063554A", "2022-11-23");
        addDoc(writer, "The Art of Computer Science", "9900333X", "2023-03-12");
        writer.close();

        // 날짜 범위 설정
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long startDate = dateFormat.parse("2023-01-01").getTime();
        long endDate = dateFormat.parse("2023-12-31").getTime();

        // 검색
        searchByDateRange(index, startDate, endDate);
    }

    private static void addDoc(IndexWriter w, String title, String isbn, String date) throws IOException, ParseException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new TextField("isbn", isbn, Field.Store.YES));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = dateFormat.parse(date);
        long dateInMillis = parsedDate.getTime();

        // LongPoint는 인덱스에 숫자형 값을 저장합니다.
        doc.add(new LongPoint("publishDate", dateInMillis));
        // StoredField는 원본 값을 저장하여 나중에 검색 결과로 반환할 수 있게 합니다.
        doc.add(new StoredField("publishDate", dateInMillis));

        w.addDocument(doc);
    }

    private static void searchByDateRange(Directory index, long startDate, long endDate) throws IOException {
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        // 날짜 범위 쿼리
        Query dateRangeQuery = LongPoint.newRangeQuery("publishDate", startDate, endDate);

        // 기본 검색어와 결합할 경우
        QueryBuilder queryBuilder = new QueryBuilder(new StandardAnalyzer());
        Query titleQuery = queryBuilder.createBooleanQuery("title", "Lucene");

        BooleanQuery finalQuery = new BooleanQuery.Builder()
                .add(dateRangeQuery, BooleanClause.Occur.MUST)
                .add(titleQuery, BooleanClause.Occur.MUST)
                .build();

        int hitsPerPage = 10;
        TopDocs docs = searcher.search(finalQuery, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        System.out.println("Found " + hits.length + " hits.");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            long dateInMillis = d.getField("publishDate").numericValue().longValue();
            String date = dateFormat.format(new Date(dateInMillis));
            System.out.println((i + 1) + ". " + d.get("title") + " (ISBN: " + d.get("isbn") + ", Date: " + date + ")");
        }

        reader.close();
    }
}
