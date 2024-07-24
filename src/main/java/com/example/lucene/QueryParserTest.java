package com.example.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

public class QueryParserTest {
    public static void main(String[] args) throws Exception {
        // Analyzer 생성
        Analyzer analyzer = new StandardAnalyzer();

        // QueryParser 초기화
        QueryParser parser = new QueryParser("fieldName", analyzer);

        // 쿼리 파싱
        Query query = parser.parse("some search text");

        // 결과 쿼리 출력
        System.out.println(query.toString());
    }
}
