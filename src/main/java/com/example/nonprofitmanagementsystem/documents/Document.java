package com.example.nonprofitmanagementsystem.documents;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.time.LocalDateTime;

public abstract class Document {
    private int documentId;
    private LocalDateTime createdDate;
    private String content;
    private String filePath;

    public Document(int documentId) {
        this.documentId = documentId;
        this.createdDate = LocalDateTime.now();
    }

    public int getDocumentId() {return documentId;}
    public LocalDateTime getCreatedDate() {return createdDate;}
    public String getContent() {return content;}

    public boolean saveDocument(String filePath) {
        try {
            refreshContent();
            Files.writeString(Path.of(filePath), content);
            this.filePath = filePath;
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    public String getFilePath() {return filePath;}
    public abstract String generateContent();
    public void refreshContent() {this.content = generateContent();}
    public abstract String getDocumentType();


}
