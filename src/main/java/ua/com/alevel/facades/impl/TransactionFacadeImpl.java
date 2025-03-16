package ua.com.alevel.facades.impl;

import org.springframework.stereotype.Service;
import ua.com.alevel.facades.TransactionFacade;
import ua.com.alevel.services.TransactionService;

@Service
public class TransactionFacadeImpl implements TransactionFacade {

    private final TransactionService transactionService;

    public TransactionFacadeImpl(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
