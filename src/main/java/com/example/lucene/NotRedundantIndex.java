package com.example.lucene;

import java.nio.file.Paths;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class NotRedundantIndex {
    public static void main(String[] args) {
        // 1. 색인에 사용할 Analyzer 정의 (StandardAnalyzer 사용)
    	KeywordAnalyzer analyzer = new KeywordAnalyzer();

        // 2. 인덱스를 저장할 Directory 생성 (파일 시스템 사용, "indexDir" 디렉터리)
        Directory index;
		try {
			index = FSDirectory.open(Paths.get("indexDir"));
			
	        // 3. IndexWriter 설정 및 초기화
	        IndexWriterConfig config = new IndexWriterConfig(analyzer);
	        IndexWriter writer = new IndexWriter(index, config);

	        // 4. 색인할 문서 추가 (중복 검사 포함)
	        addDocWithCheck(writer, index, "Lucene in Action", "C:\\Users\\c\\Documents\\193398817.txt", analyzer);
	        addDocWithCheck(writer, index, "Lucene for Dummies", "C:\\Users\\c\\Documents\\55320055Z.txt", analyzer);
	        addDocWithCheck(writer, index, "Managing Gigabytes", "C:\\Users\\c\\Documents\\55063554A.txt", analyzer);
	        addDocWithCheck(writer, index, "The Art of Computer Science", "C:\\Users\\c\\Documents\\9900333X.txt", analyzer);

	        // 5. ISBN으로 문서 삭제
	        deleteDocByIsbn(writer, "C:\\Users\\c\\Documents\\55320055Z.txt");

	        // 6. 삭제 확인
	        boolean isDeleted = isDocumentDeleted(index, "isbn", "55320055Z", analyzer);
	        if (isDeleted) {
	            System.out.println("It has been successfully deleted.");
	        } else {
	            System.out.println("It still exists.");
	        }

	        // 5. ISBN으로 검색
	        searchByIsbn(index, "C:\\Users\\c\\Documents\\55320055Z.txt");

	        // 모든 문서 출력
	        printAllDocuments(index);

	        writer.close();
			index.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    // 색인할 문서 추가 메소드 (중복 체크 포함)
    private static void addDocWithCheck(IndexWriter writer, Directory index, String title, String isbn, KeywordAnalyzer analyzer) throws Exception {
    	writer.commit();
    	// 이미 해당 ISBN이 있는지 확인하는 쿼리
        Query query = new TermQuery(new Term("isbn", isbn));

        // 인덱스에서 검색을 수행하여 중복 여부 확인
        DirectoryReader reader = DirectoryReader.open(writer);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs results = searcher.search(query, 2);

        if (results.totalHits.value == 0) {
            // 중복되지 않으면 문서 추가
            Document doc = new Document();
            doc.add(new TextField("title", title, Field.Store.YES));
            doc.add(new TextField("isbn", isbn, Field.Store.YES));
            writer.addDocument(doc);
            System.out.println("Added document with ISBN: " + isbn + " , results.totalHits.value : " + results.totalHits.value);
            // 문서 추가 후 즉시 커밋하여 인덱스에 반영
            writer.commit();
        } else {
            // 중복된 ISBN일 경우 메시지 출력
            System.out.println("Duplicate ISBN found, skipping: " + isbn);
        }

        reader.close();
    }

    // ISBN으로 문서를 삭제하는 메소드
    private static void deleteDocByIsbn(IndexWriter writer, String isbn) throws Exception {
        // TermQuery를 이용해 특정 ISBN을 기준으로 문서 삭제
        writer.deleteDocuments(new Term("isbn", isbn));
        writer.commit(); // 실제 삭제가 적용되도록 commit 수행
        System.out.println("Deleted document with ISBN: " + isbn);
    }

    // 삭제 여부 확인 메소드
    private static boolean isDocumentDeleted(Directory index, String field, String value, KeywordAnalyzer analyzer) throws Exception {
        // DirectoryReader를 열어 검색 실행
        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        // 특정 필드와 값에 대해 쿼리 작성 (예: ISBN)
        Query query = new TermQuery(new Term(field, value));

        // 검색 실행
        TopDocs results = searcher.search(query, 1);
        reader.close();

        // 검색 결과가 없으면 문서가 삭제된 것
        return results.totalHits.value == 0;
    }

    // ISBN으로 문서를 검색하는 함수
    private static void searchByIsbn(Directory index, String isbn) throws Exception {
        // DirectoryReader와 IndexSearcher를 통해 검색
        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        // TermQuery를 이용해 특정 ISBN으로 검색
        Query query = new TermQuery(new Term("isbn", isbn));

        // 검색 실행
        TopDocs results = searcher.search(query, 10); // 최대 10개의 결과 반환

        System.out.println("Searching for ISBN: " + isbn);
        if (results.totalHits.value > 0) {
            for (ScoreDoc scoreDoc : results.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println("Found Document - Title: " + doc.get("title") + ", ISBN: " + doc.get("isbn"));
            }
        } else {
            System.out.println("No document found with ISBN: " + isbn);
        }

        reader.close();
    }

    // 인덱스에 있는 모든 문서를 출력하는 함수
    private static void printAllDocuments(Directory index) throws Exception {
        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        // 모든 문서를 검색하는 MatchAllDocsQuery 사용
        Query query = new MatchAllDocsQuery();

        // 인덱스 내 모든 문서 검색
        TopDocs results = searcher.search(query, Integer.MAX_VALUE); // 최대 문서 수 지정

        System.out.println("All documents in the index:");
        for (ScoreDoc scoreDoc : results.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("Title: " + doc.get("title") + ", ISBN: " + doc.get("isbn"));
        }

        reader.close();
    }
}
