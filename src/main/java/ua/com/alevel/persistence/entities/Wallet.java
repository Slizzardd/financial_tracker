package ua.com.alevel.persistence.entities;

import jakarta.persistence.*;
import ua.com.alevel.persistence.types.AccessLevel;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "wallets")
public class Wallet extends BaseEntity {

    @Column(name = "wallet_name", nullable = false)
    private String name;

    @Column(name = "balance_in_dollar", nullable = false)
    private BigDecimal balance;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transaction> transactions = new HashSet<>();

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WalletAccess> accesses = new HashSet<>();

    public Wallet(String name, String description) {
        this.name = name;
        this.description = description;
        this.balance = BigDecimal.ZERO;
    }

    public Wallet() {
        this.balance = BigDecimal.ZERO;
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            balance = balance.add(amount);
        }
    }

    public boolean withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0 && balance.compareTo(amount) >= 0) {
            balance = balance.subtract(amount);
            return true;
        }
        return false;
    }

    public Set<WalletAccess> getAccesses() {
        return accesses;
    }

    public void addAccess(User user, AccessLevel accessLevel) {
        WalletAccess access = new WalletAccess();
        access.setUser(user);
        access.setWallet(this);
        access.setAccessLevel(accessLevel);
        accesses.add(access);
    }

    public String getOwnerEmail() {
        return accesses.stream()
                .filter(access -> access.getAccessLevel() == AccessLevel.OWNER)
                .map(access -> access.getUser().getEmail())
                .findFirst()
                .orElse(null);
    }

    public void removeAccess(User user) {
        accesses.removeIf(access -> {
            boolean shouldRemove = access.getUser().equals(user) && access.getAccessLevel() != AccessLevel.OWNER;
            if (shouldRemove) {
                access.setWallet(null);
            }
            return shouldRemove;
        });
    }

    public WalletAccess findAccessByEmail(String email) {
        return accesses.stream()
                .filter(access -> access.getUser().getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
