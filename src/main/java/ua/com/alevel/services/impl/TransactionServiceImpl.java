package ua.com.alevel.services.impl;

import org.springframework.stereotype.Service;
import ua.com.alevel.persistence.repositories.TransactionRepository;
import ua.com.alevel.services.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
}
