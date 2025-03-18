package ua.com.alevel.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.alevel.facades.WalletFacade;
import ua.com.alevel.web.dto.requests.WalletAccessDeleteRequestDto;
import ua.com.alevel.web.dto.requests.WalletAccessRequestDto;
import ua.com.alevel.web.dto.requests.WalletRequestDto;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletRestController {

    private final WalletFacade walletFacade;

    public WalletRestController(WalletFacade walletFacade) {
        this.walletFacade = walletFacade;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody WalletRequestDto walletRequestDto) {
        return ResponseEntity.ok(walletFacade.create(walletRequestDto));
    }

    @PostMapping("/add-access/{walletId}")
    public ResponseEntity<?> addWalletAccess(@PathVariable Long walletId, @RequestBody WalletAccessRequestDto requestDto) {
        return ResponseEntity.ok(walletFacade.changeWalletAccess(requestDto, walletId));
    }

    @PostMapping("/remove-access/{walletId}")
    public ResponseEntity<?> removeWalletAccess(@PathVariable Long walletId, @RequestBody WalletAccessDeleteRequestDto requestDto) {
        return ResponseEntity.ok(walletFacade.removeWalletAccess(requestDto, walletId));
    }
}
