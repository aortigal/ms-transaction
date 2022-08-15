package com.bank.mstransaction.services;

import com.bank.mstransaction.models.utils.ResponseClient;
import reactor.core.publisher.Mono;

public interface ClientService {
    Mono<ResponseClient> findByCode(String id);
}
