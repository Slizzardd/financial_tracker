package ua.com.alevel.persistence.repositories;

import org.springframework.stereotype.Repository;
import ua.com.alevel.persistence.entities.Transaction;

@Repository
public interface TransactionRepository extends BaseRepository<Transaction> {
}
