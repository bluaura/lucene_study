package com.example.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SortingExample2 {
    public static void main(String[] args) throws IOException, ParseException, ParseException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // 문서 추가
        addDoc(writer, "Lucene in Action", "978-1933988177", dateFormat.parse("2022-01-01 10:00:00"));
        addDoc(writer, "Lucene for Dummies", "978-1234567890", dateFormat.parse("2022-01-02 12:00:00"));
        addDoc(writer, "Lucene Cookbook in Action", "978-9876543210", dateFormat.parse("2022-01-03 14:00:00"));
        addDoc(writer, "Lucene Good", "978-6547843210", dateFormat.parse("2022-01-04 14:00:00"));
        writer.close();

        // 검색 예제
        searchAndSortWithDate(index, analyzer, "Lucene", dateFormat.parse("2022-01-01 00:00:00"), dateFormat.parse("2022-01-04 23:59:59"));
        System.out.println("===================================");
        searchAndSort(index, analyzer, "Lucene");
    }

    private static void addDoc(IndexWriter w, String title, String isbn, Date date) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        // 날짜를 long 값으로 변환하여 저장합니다.
        doc.add(new NumericDocValuesField("date", date.getTime())); // 정렬용 필드
        doc.add(new LongPoint("date", date.getTime())); // 범위 검색용 필드
        doc.add(new StoredField("date", date.getTime())); // 검색 결과에서 날짜 표시용 필드
        w.addDocument(doc);
    }

    private static void searchAndSortWithDate(Directory index, StandardAnalyzer analyzer, String titleQuery, Date startDate, Date endDate) throws IOException, ParseException {
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        // 날짜 범위를 long 값으로 변환합니다.
        long start = startDate.getTime();
        long end = endDate.getTime();

        // 제목 쿼리를 생성합니다.
        QueryParser parser = new QueryParser("title", analyzer);
        Query titleQueryObj;
		try {
			titleQueryObj = parser.parse(titleQuery);
	        // 날짜 범위 쿼리를 생성합니다.
	        Query dateRangeQuery = LongPoint.newRangeQuery("date", start, end);


	        BooleanQuery booleanQuery = new BooleanQuery.Builder()
	                .add(titleQueryObj, BooleanClause.Occur.MUST)
	                .add(dateRangeQuery, BooleanClause.Occur.MUST)
	                .build();

	        // 날짜 필드로 정렬(내림차순)
	        Sort sort = new Sort(new SortField("date", SortField.Type.LONG, true));
	        TopDocs docs = searcher.search(booleanQuery, 10, sort);

	        System.out.println("Sorted by date (newest first):");
	        for (ScoreDoc sd : docs.scoreDocs) {
	            Document d = searcher.doc(sd.doc);
	            System.out.println("Title: " + d.get("title") + ", Date: " + new Date(d.getField("date").numericValue().longValue()));
	        }
		} catch (org.apache.lucene.queryparser.classic.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        reader.close();
    }

    private static void searchAndSort(Directory index, StandardAnalyzer analyzer, String titleQuery) throws IOException, ParseException {
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        // 제목 쿼리를 생성합니다.
        QueryParser parser = new QueryParser("title", analyzer);
        Query titleQueryObj;
		try {
			titleQueryObj = parser.parse(titleQuery);

	        // 날짜 필드로 정렬(내림차순)
	        Sort sort = new Sort(new SortField("date", SortField.Type.LONG, true));
	        TopDocs docs = searcher.search(titleQueryObj, 10, sort);

	        System.out.println("Sorted by date (newest first):");
	        for (ScoreDoc sd : docs.scoreDocs) {
	            Document d = searcher.doc(sd.doc);
	            System.out.println("Title: " + d.get("title"));
	        }
		} catch (org.apache.lucene.queryparser.classic.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        reader.close();
    }
}
