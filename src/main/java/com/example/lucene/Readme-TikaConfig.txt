tika config (tika-config.xml)

<?xml version="1.0" encoding="UTF-8"?>
<properties>
    <parsers>
        <!-- TesseractOCRParser를 명시적으로 사용 -->
        <parser class="org.apache.tika.parser.ocr.TesseractOCRParser">
            <!-- Tesseract 경로 설정 (운영체제에 맞게 경로 수정) -->
            <tesseractPath>/usr/local/bin/tesseract</tesseractPath> <!-- 예시 경로, 운영체제에 맞게 수정 -->
            <language>eng+kor</language> <!-- OCR에 사용할 언어 설정 -->
            <minFileSizeToOcr>0</minFileSizeToOcr> <!-- OCR을 수행할 최소 파일 크기 설정 -->
            <maxFileSizeToOcr>10485760</maxFileSizeToOcr> <!-- OCR을 수행할 최대 파일 크기 설정 -->
        </parser>
    </parsers>
</properties>

config - source file

            InputStream configStream = new FileInputStream("path/to/tika-config.xml");
            TikaConfig tikaConfig = new TikaConfig(configStream);

            // AutoDetectParser 설정
            AutoDetectParser parser = new AutoDetectParser(tikaConfig);
            ParseContext parseContext = new ParseContext();
