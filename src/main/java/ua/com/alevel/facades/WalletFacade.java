package ua.com.alevel.facades;

import org.apache.juli.logging.Log;
import ua.com.alevel.web.dto.requests.WalletAccessDeleteRequestDto;
import ua.com.alevel.web.dto.requests.WalletAccessRequestDto;
import ua.com.alevel.web.dto.requests.WalletRequestDto;
import ua.com.alevel.web.dto.responses.WalletResponseDto;

public interface WalletFacade extends BaseFacade<WalletRequestDto, WalletResponseDto> {

    WalletResponseDto create(WalletRequestDto req);

    WalletResponseDto changeWalletAccess(WalletAccessRequestDto req, Long walletId);

    WalletResponseDto removeWalletAccess(WalletAccessDeleteRequestDto req, Long walletId);
}
