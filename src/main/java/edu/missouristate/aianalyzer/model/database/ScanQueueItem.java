package edu.missouristate.aianalyzer.model.database;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "scan_queue")
public class ScanQueueItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String kind;

    private long notBeforeUnix;
    private int attempts;
}
