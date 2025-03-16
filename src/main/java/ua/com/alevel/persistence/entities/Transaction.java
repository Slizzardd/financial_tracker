package ua.com.alevel.persistence.entities;

import jakarta.persistence.*;
import ua.com.alevel.persistence.types.TransactionType;

@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private Double amount;
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    public Transaction() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}
