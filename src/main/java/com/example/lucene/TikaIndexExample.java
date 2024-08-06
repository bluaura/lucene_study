package com.example.lucene;

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
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;

import java.io.FileInputStream;
import java.io.IOException;

public class TikaIndexExample {
    public static void main(String[] args) throws Exception {
        // PDF 파일 경로
        String filePath = "C:\\Users\\c\\Downloads\\2024 학령전 성경학교 자료집.pdf";

        // Tika를 사용하여 PDF 파일에서 텍스트 추출
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        FileInputStream inputStream = new FileInputStream(filePath);
        ParseContext pcontext = new ParseContext();

        // PDF parser
        PDFParser pdfparser = new PDFParser();
        pdfparser.parse(inputStream, handler, metadata, pcontext);

        // 추출된 텍스트
        String fileContent = handler.toString();

        // Lucene 인덱스 생성
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config);

        // 문서 추가
        addDoc(writer, fileContent);
        writer.close();

        // 검색
        String querystr = "your search query";
        Query q = new QueryParser("content", analyzer).parse(querystr);

        // 인덱스에서 검색 수행
        int hitsPerPage = 10;
        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        // 검색 결과 출력
        System.out.println("Found " + hits.length + " hits.");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("content"));
        }

        reader.close();
    }

    private static void addDoc(IndexWriter writer, String content) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("content", content, Field.Store.YES));
        writer.addDocument(doc);
    }
}
