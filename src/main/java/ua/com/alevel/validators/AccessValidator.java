package ua.com.alevel.validators;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ua.com.alevel.exceptions.SelfAccessModificationException;
import ua.com.alevel.persistence.entities.Wallet;

@Component
public class AccessValidator {

    public void validateSelfAccessChange(String userEmail) {
        if (userEmail.equals(getAuthenticatedEmail())) {
            throw new SelfAccessModificationException("You cannot change your own access level.");
        }
    }

    public void validateOwnerAccessChange(String accessLevel) {
        if ("OWNER".equalsIgnoreCase(accessLevel)) {
            throw new IllegalArgumentException("Access level OWNER cannot be assigned manually.");
        }
    }

    public void validateOwnership(Wallet wallet, String userEmail) {
        if (!wallet.getOwnerEmail().equals(userEmail)) {
            throw new SecurityException("Only the wallet owner can change access rights.");
        }
    }

    private String getAuthenticatedEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
