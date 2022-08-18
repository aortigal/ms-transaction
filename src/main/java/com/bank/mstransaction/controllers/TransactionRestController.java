package com.bank.mstransaction.controllers;

import com.bank.mstransaction.handler.ResponseHandler;
import com.bank.mstransaction.models.documents.Transaction;
import com.bank.mstransaction.services.TransactionService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/transaction")
public class TransactionRestController
{
    @Autowired
    private TransactionService transactionService;
    @GetMapping
    public Mono<ResponseHandler> findAll() {
        return transactionService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseHandler> find(@PathVariable String id) {
        return transactionService.find(id);
    }

    @PostMapping("{type}")
    @CircuitBreaker(name="active", fallbackMethod = "fallBackActive")
    public Mono<ResponseHandler> create(@PathVariable("type") String type,@Valid @RequestBody Transaction tran) {
        return transactionService.create(type,tran);

    }

    @PutMapping("/{id}")
    public Mono<ResponseHandler> update(@PathVariable("id") String id, @RequestBody Transaction act) {
        return transactionService.update(id, act);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseHandler> delete(@PathVariable("id") String id) {
        return transactionService.delete(id);
    }

    @GetMapping("/clientTransactions/{idClient}")
    public Mono<ResponseHandler> findByIdClient(@PathVariable String idClient) {
        return transactionService.findByIdClient(idClient);
    }

    @GetMapping("/balance/{idClient}")
    public Mono<ResponseHandler> getBalance(@PathVariable("idClient") String idClient) {
        return transactionService.getBalance(idClient);
    }

    public Mono<ResponseHandler> fallBackActive(RuntimeException runtimeException){
        return Mono.just(new ResponseHandler("Microservicio externo no responde", HttpStatus.BAD_REQUEST,runtimeException.getMessage()));
    }
}
