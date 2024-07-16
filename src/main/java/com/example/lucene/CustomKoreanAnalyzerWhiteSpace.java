package com.example.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;

public class CustomKoreanAnalyzerWhiteSpace extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        // 기본 KoreanAnalyzer의 토크나이저를 사용합니다.
        Tokenizer source = new StandardTokenizer();
        
        // KoreanAnalyzer의 필터를 추가합니다.
        KoreanAnalyzer koreanAnalyzer = new KoreanAnalyzer();
        TokenStream koreanFilter = new KoreanAnalyzer().tokenStream(fieldName, source);

        // 띄어쓰기 기반의 WhitespaceAnalyzer 필터를 추가합니다.
        TokenStream whitespaceFilter = new WhitespaceAnalyzer().tokenStream(fieldName, koreanFilter);

        return new TokenStreamComponents(source, whitespaceFilter);
    }
}
