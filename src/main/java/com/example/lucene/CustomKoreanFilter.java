package com.example.lucene;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

public class CustomKoreanFilter extends TokenFilter {
    private final CharTermAttribute charTermAttribute = addAttribute(CharTermAttribute.class);

    protected CustomKoreanFilter(TokenStream input) {
        super(input);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            char[] termBuffer = charTermAttribute.buffer();
            int termLength = charTermAttribute.length();
            for (int i = 0; i < termLength; i++) {
                if (termBuffer[i] == '(' || termBuffer[i] == ')' || termBuffer[i] == '[' || termBuffer[i] == ']') {
                    // 특수 문자를 처리할 수 있는 논리를 추가하세요.
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
