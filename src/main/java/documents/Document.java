package documents;

import java.time.LocalDateTime;

public abstract class Document {
    private int documentId;
    private LocalDateTime createdDate;
    private String content;
    private String filePath;

    public Document(int documentId) {this.documentId = documentId;}
    public int getDocumentId() {return documentId;}
    public LocalDateTime getCreatedDate() {return createdDate;}
    public String getContent() {return content;}

    public boolean saveDocument(String filePath) {
        this.filePath = filePath;
        return true;
    }
    public String getFilePath() {return filePath;}
    public abstract String generateContent();
    public void refreshContent() {this.content = generateContent();}
    public abstract String getDocumentType();


}
