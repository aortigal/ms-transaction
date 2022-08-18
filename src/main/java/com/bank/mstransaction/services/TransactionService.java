package com.bank.mstransaction.services;

import com.bank.mstransaction.handler.ResponseHandler;
import com.bank.mstransaction.models.documents.Transaction;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Mono<ResponseHandler> findAll();

    Mono<ResponseHandler> find(String id);

    Mono<ResponseHandler> create(String type, Transaction tran);

    Mono<ResponseHandler> update(String id, Transaction act);

    Mono<ResponseHandler> delete(String id);

    Mono<ResponseHandler> findByIdClient(String idClient);

    Mono<ResponseHandler> getBalance(String idClient);
}
