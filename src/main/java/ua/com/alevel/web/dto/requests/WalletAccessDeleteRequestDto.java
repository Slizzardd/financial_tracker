package ua.com.alevel.web.dto.requests;

public class WalletAccessDeleteRequestDto extends BaseRequestDto {

    private String userEmail;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
