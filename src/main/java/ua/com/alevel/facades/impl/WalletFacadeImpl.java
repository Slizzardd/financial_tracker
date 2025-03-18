package ua.com.alevel.facades.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ua.com.alevel.facades.WalletFacade;
import ua.com.alevel.persistence.entities.User;
import ua.com.alevel.persistence.entities.Wallet;
import ua.com.alevel.persistence.entities.WalletAccess;
import ua.com.alevel.persistence.types.AccessLevel;
import ua.com.alevel.services.UserService;
import ua.com.alevel.services.WalletService;
import ua.com.alevel.validators.AccessValidator;
import ua.com.alevel.web.dto.requests.WalletAccessDeleteRequestDto;
import ua.com.alevel.web.dto.requests.WalletAccessRequestDto;
import ua.com.alevel.web.dto.requests.WalletRequestDto;
import ua.com.alevel.web.dto.responses.WalletResponseDto;

@Service
public class WalletFacadeImpl implements WalletFacade {

    private static final Logger logger = LoggerFactory.getLogger(WalletFacadeImpl.class);

    private final WalletService walletService;
    private final UserService userService;
    private final AccessValidator accessValidator;

    public WalletFacadeImpl(WalletService walletService, UserService userService, AccessValidator accessValidator) {
        this.walletService = walletService;
        this.userService = userService;
        this.accessValidator = accessValidator;
    }

    @Override
    public WalletResponseDto create(WalletRequestDto request) {
        String authenticatedEmail = getAuthenticatedEmail();
        logger.info("Creating a new wallet '{}' for user '{}'", request.getName(), authenticatedEmail);

        User owner = userService.findByEmail(authenticatedEmail);
        Wallet wallet = new Wallet(request.getName(), request.getDescription());
        WalletResponseDto response = new WalletResponseDto(walletService.create(wallet, owner));

        logger.info("Wallet '{}' successfully created for user '{}'", request.getName(), authenticatedEmail);
        return response;
    }

    @Override
    public WalletResponseDto changeWalletAccess(WalletAccessRequestDto request, Long walletId) {
        String authenticatedEmail = getAuthenticatedEmail();
        logger.info("User '{}' requests to change access for user '{}' on wallet with ID '{}'",
                authenticatedEmail, request.getUserEmail(), walletId);

        Wallet wallet = walletService.findById(walletId);
        logger.debug("Validating access change permissions for user '{}' on wallet '{}'", authenticatedEmail, wallet.getName());
        accessValidator.validateOwnership(wallet, authenticatedEmail);
        accessValidator.validateSelfAccessChange(request.getUserEmail());
        accessValidator.validateOwnerAccessChange(request.getAccessLevel());

        User user = userService.findByEmail(request.getUserEmail());
        AccessLevel requestedLevel = AccessLevel.valueOf(request.getAccessLevel().toUpperCase());
        WalletAccess existingAccess = wallet.findAccessByEmail(request.getUserEmail());

        WalletResponseDto response = new WalletResponseDto(walletService.changeWalletAccess(wallet, user, requestedLevel, existingAccess));
        logger.info("Access for user '{}' on wallet '{}' successfully changed to '{}'", user.getEmail(), wallet.getName(), requestedLevel);
        return response;
    }

    @Override
    public WalletResponseDto removeWalletAccess(WalletAccessDeleteRequestDto request, Long walletId) {
        String authenticatedEmail = getAuthenticatedEmail();
        logger.info("User '{}' requests to remove access for user '{}' from wallet with ID '{}'",
                authenticatedEmail, request.getUserEmail(), walletId);

        Wallet wallet = walletService.findById(walletId);
        logger.debug("Validating access removal permissions for user '{}' on wallet '{}'", authenticatedEmail, wallet.getName());
        accessValidator.validateOwnership(wallet, authenticatedEmail);
        accessValidator.validateSelfAccessChange(request.getUserEmail());

        User user = userService.findByEmail(request.getUserEmail());
        WalletAccess existingAccess = wallet.findAccessByEmail(request.getUserEmail());

        WalletResponseDto response = new WalletResponseDto(walletService.removeWalletAccess(wallet, user, existingAccess));
        logger.info("Access for user '{}' successfully removed from wallet '{}'", user.getEmail(), wallet.getName());
        return response;
    }

    private String getAuthenticatedEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
