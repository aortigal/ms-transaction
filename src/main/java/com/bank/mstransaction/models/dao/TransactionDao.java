package com.bank.mstransaction.models.dao;

import com.bank.mstransaction.models.documents.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TransactionDao extends ReactiveMongoRepository<Transaction, String>
{
}
