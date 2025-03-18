package ua.com.alevel.web.dto.requests;

import java.math.BigDecimal;

public class WalletRequestDto extends BaseRequestDto {

    private String name;

    private String description;

    private BigDecimal balance;

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
}
