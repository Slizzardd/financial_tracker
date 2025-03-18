package ua.com.alevel.web.dto.responses;

import ua.com.alevel.persistence.entities.Wallet;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public class WalletResponseDto extends BaseResponseDto {

    private String name;

    private String description;

    private BigDecimal balance;

    private Instant created;

    private Instant updated;

    private Set<UsersAccessDto> users;

    public WalletResponseDto(Wallet wallet) {
        this.name = wallet.getName();
        this.description = wallet.getDescription();
        this.balance = wallet.getBalance();
        this.created = wallet.getCreated();
        this.updated = wallet.getUpdated();
        this.users = wallet.getAccesses().stream()
                .map(access -> new UsersAccessDto(
                        access.getUser().getEmail(),
                        access.getAccessLevel().name(),
                        access.getUpdated()
                ))
                .collect(Collectors.toSet());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return updated;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public Set<UsersAccessDto> getUsers() {
        return users;
    }

    public void setUsers(Set<UsersAccessDto> users) {
        this.users = users;
    }
}

class UsersAccessDto {

    private String email;

    private Instant lastUpdated;

    private String accessLevel;

    public UsersAccessDto(String email, String accessLevel, Instant lastUpdated) {
        this.email = email;
        this.lastUpdated = lastUpdated;
        this.accessLevel = accessLevel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }
}
