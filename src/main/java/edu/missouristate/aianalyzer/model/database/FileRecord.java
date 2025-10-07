package edu.missouristate.aianalyzer.model.database;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "files") // This ensures it maps to the 'files' table from DataTable.sql
public class FileRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String path;

    private String parentPath;
    private long sizeBytes;
    private long mtimeUnix;
    private Long ctimeUnix;
    private long lastScannedUnix;
    private String contentHash;
    private String kind;
    private String typeLabel;
    private Double typeLabelConfidence;
    private String typeLabelSource;
    private Long typeLabelUpdatedUnix;
    private String ext;

    // These columns were added via 'ALTER TABLE' in the old DatabaseManager
    private String aiSafety;
    @Column(length = 1024) // It's good practice to define a length for potentially long text fields
    private String aiResponse;
}