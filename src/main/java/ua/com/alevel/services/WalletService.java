package ua.com.alevel.services;

import ua.com.alevel.persistence.entities.User;
import ua.com.alevel.persistence.entities.Wallet;
import ua.com.alevel.persistence.entities.WalletAccess;
import ua.com.alevel.persistence.types.AccessLevel;

import java.util.List;

public interface WalletService extends BaseService<WalletAccess> {

    Wallet create(Wallet wallet, User owner);

    Wallet update(Wallet wallet);

    Wallet findById(Long id);

    List<Wallet> findAll();

    Wallet removeWalletAccess(Wallet wallet, User aimUser, WalletAccess existingAccess);

    Wallet changeWalletAccess(Wallet wallet, User aimUser, AccessLevel requestLevel, WalletAccess existingAccess);
}
