package com.smart_school_management_system.smart_school_2026.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "note_purchases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotePurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "amount_paid", nullable = false)
    private Double amountPaid;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PurchaseStatus status = PurchaseStatus.COMPLETED;

    @PrePersist
    protected void onCreate() {
        purchaseDate = LocalDateTime.now();
    }
}