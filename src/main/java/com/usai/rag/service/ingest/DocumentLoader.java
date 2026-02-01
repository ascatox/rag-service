package com.usai.rag.service.ingest;

import com.usai.rag.model.DocumentInput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class DocumentLoader {

    private final PdfTextExtractor pdfTextExtractor;

    public DocumentLoader(PdfTextExtractor pdfTextExtractor) {
        this.pdfTextExtractor = pdfTextExtractor;
    }

    public List<DocumentInput> load(Path root, Predicate<Path> include, Predicate<Path> exclude) throws IOException {
        List<DocumentInput> docs = new ArrayList<>();
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!attrs.isRegularFile()) {
                    return FileVisitResult.CONTINUE;
                }
                if (exclude != null && exclude.test(file)) {
                    return FileVisitResult.CONTINUE;
                }
                if (include != null && !include.test(file)) {
                    return FileVisitResult.CONTINUE;
                }

                String contentType = Files.probeContentType(file);
                String content;
                if (isPdf(file, contentType)) {
                    content = pdfTextExtractor.extract(file);
                } else {
                    content = Files.readString(file, StandardCharsets.UTF_8);
                }
                Instant lastModified = attrs.lastModifiedTime().toInstant();
                docs.add(new DocumentInput(file, contentType, lastModified, content));
                return FileVisitResult.CONTINUE;
            }
        });
        return docs;
    }

    private boolean isPdf(Path path, String contentType) {
        if (contentType == null) {
            return path.toString().toLowerCase().endsWith(".pdf");
        }
        return contentType.toLowerCase().contains("pdf");
    }
}
