package ua.com.alevel.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.alevel.persistence.entities.User;
import ua.com.alevel.persistence.entities.Wallet;
import ua.com.alevel.persistence.entities.WalletAccess;
import ua.com.alevel.persistence.repositories.WalletRepository;
import ua.com.alevel.persistence.types.AccessLevel;
import ua.com.alevel.services.WalletService;

import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {

    private static final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);
    private final WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Wallet create(Wallet wallet, User owner) {
        logger.info("Creating a new wallet: {} for user: {}", wallet.getName(), owner.getEmail());
        wallet.addAccess(owner, AccessLevel.OWNER);
        Wallet savedWallet = walletRepository.save(wallet);
        logger.info("Wallet '{}' successfully created for owner: {}", savedWallet.getName(), owner.getEmail());
        return savedWallet;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Wallet update(Wallet wallet) {
        logger.info("Updating wallet with ID: {}", wallet.getId());
        Wallet updatedWallet = walletRepository.save(wallet);
        logger.info("Wallet with ID: {} successfully updated", updatedWallet.getId());
        return updatedWallet;
    }

    @Override
    @Transactional(readOnly = true)
    public Wallet findById(Long id) {
        logger.info("Searching for wallet with ID: {}", id);
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Wallet with ID " + id + " not found."));
        logger.info("Wallet found: {}", wallet.getName());
        return wallet;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Wallet> findAll() {
        logger.info("Fetching all wallets.");
        List<Wallet> wallets = walletRepository.findAll();
        logger.info("Fetched {} wallets.", wallets.size());
        return wallets;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Wallet removeWalletAccess(Wallet wallet, User aimUser, WalletAccess existingAccess) {
        logger.info("Removing access for user {} from wallet: {}", aimUser.getEmail(), wallet.getName());
        wallet.removeAccess(aimUser);
        Wallet updatedWallet = walletRepository.save(wallet);
        logger.info("Access for user {} successfully removed from wallet: {}", aimUser.getEmail(), wallet.getName());
        return updatedWallet;
    }

    @Override
    public Wallet changeWalletAccess(Wallet wallet, User aimUser, AccessLevel requestLevel, WalletAccess existingAccess) {
        logger.info("Changing access level for user {} on wallet: {} to {}", aimUser.getEmail(), wallet.getName(), requestLevel);
        if (existingAccess == null) {
            wallet.addAccess(aimUser, requestLevel);
            logger.info("New access level '{}' set for user: {}", requestLevel, aimUser.getEmail());
        } else {
            existingAccess.setAccessLevel(requestLevel);
            logger.info("Access level for user {} updated to: {}", aimUser.getEmail(), requestLevel);
        }
        return walletRepository.save(wallet);
    }
}
