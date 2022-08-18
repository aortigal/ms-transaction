package com.bank.mstransaction.services.impl;

import com.bank.mstransaction.models.utils.ResponseClient;
import com.bank.mstransaction.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    WebClient webClient;

    @Override
    public Mono<ResponseClient> findByCode(String id)
    {
        return webClient.get()
                .uri("/api/client/"+ id)
                .retrieve()
                .bodyToMono(ResponseClient.class);
    }
}
