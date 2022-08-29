package com.bank.mstransaction.services.impl;

import com.bank.mstransaction.handler.ResponseHandler;
import com.bank.mstransaction.models.dao.TransactionDao;
import com.bank.mstransaction.models.documents.Transaction;
import com.bank.mstransaction.services.ActiveService;
import com.bank.mstransaction.services.ClientService;
import com.bank.mstransaction.services.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionDao dao;

    @Autowired
    private ActiveService activeService;
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Autowired
    private ClientService clientService;

    @Override
    public Mono<ResponseHandler> findAll() {
        log.info("[INI] findAll Transaction");
        return dao.findAll()
                .doOnNext(transaction -> log.info(transaction.toString()))
                .collectList()
                .map(transactions -> new ResponseHandler("Done", HttpStatus.OK, transactions))
                .onErrorResume(error -> Mono.just(new ResponseHandler(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(new ResponseHandler("No Content", HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] findAll Transaction"));
    }

    @Override
    public Mono<ResponseHandler> find(String id) {
        log.info("[INI] find Transaction");
        return dao.findById(id)
                .doOnNext(transaction -> log.info(transaction.toString()))
                .map(transaction -> new ResponseHandler("Done", HttpStatus.OK, transaction))
                .onErrorResume(error -> Mono.just(new ResponseHandler(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(new ResponseHandler("No Content", HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] find Transaction"));
    }

    @Override
    public Mono<ResponseHandler> create(String type, Transaction tran) {
        log.info("[INI] create Transaction");

        String typeName = "";
        if(type.equals("3")){
            typeName = "PERSONAL";
        }else if(type.equals("4")){
            typeName = "COMPANY";
        }

        String finalTypeName = typeName;
        return activeService.findByCode(tran.getActiveId())
                .doOnNext(transaction -> log.info(transaction.toString())).
                flatMap(responseActive -> {
                    if(responseActive.getData()==null){
                        return Mono.just(new ResponseHandler("Does not have active", HttpStatus.BAD_REQUEST, null));
                    }

                    return clientService.findByCode(tran.getClientId())
                            .doOnNext(transaction -> log.info(transaction.toString()))
                            .flatMap(responseClient -> {
                                if(responseClient.getData() == null){
                                    return Mono.just(new ResponseHandler("Does not have client", HttpStatus.BAD_REQUEST, null));
                                }

                                if(!finalTypeName.equals(responseClient.getData().getType())){
                                    return Mono.just(new ResponseHandler("The Active is not enabled for the client", HttpStatus.BAD_REQUEST, null));
                                }
                                tran.setDateRegister(LocalDateTime.now());
                                return dao.save(tran)
                                        .doOnNext(transaction -> log.info(transaction.toString()))
                                        .map(transaction -> new ResponseHandler("Done", HttpStatus.OK, transaction)                )
                                        .onErrorResume(error -> Mono.just(new ResponseHandler(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                                        ;
                            })
                            .switchIfEmpty(Mono.just(new ResponseHandler("Client No Content", HttpStatus.BAD_REQUEST, null)));

                })
                .switchIfEmpty(Mono.just(new ResponseHandler("Active No Content", HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] create Transaction"));

    }

    @Override
    public Mono<ResponseHandler> update(String id, Transaction act) {
        log.info("[INI] update Transaction");
        return dao.existsById(id).flatMap(check -> {
            if (check){
                act.setDateUpdate(LocalDateTime.now());
                return dao.save(act)
                        .doOnNext(transaction -> log.info(transaction.toString()))
                        .map(transaction -> new ResponseHandler("Done", HttpStatus.OK, transaction)                )
                        .onErrorResume(error -> Mono.just(new ResponseHandler(error.getMessage(), HttpStatus.BAD_REQUEST, null)));
            }
            else
                return Mono.just(new ResponseHandler("Not found", HttpStatus.NOT_FOUND, null));

        }).doFinally(fin -> log.info("[END] update Transaction"));
    }

    @Override
    public Mono<ResponseHandler> delete(String id) {
        log.info("[INI] delete Transaction");
        return dao.existsById(id).flatMap(check -> {
            if (check)
                return dao.deleteById(id).then(Mono.just(new ResponseHandler("Done", HttpStatus.OK, null)));
            else
                return Mono.just(new ResponseHandler("Not found", HttpStatus.NOT_FOUND, null));
        }).doFinally(fin -> log.info("[END] delete Transaction"));
    }

    @Override
    public Mono<ResponseHandler> findByIdClient(String idClient) {
        log.info("[INI] findByIdClient Transaction");
        return dao.findAll()
                .filter(transaction ->
                        transaction.getClientId().equals(idClient)
                )
                .collectList()
                .doOnNext(transaction -> log.info(transaction.toString()))
                .map(movements -> new ResponseHandler("Done", HttpStatus.OK, movements))
                .onErrorResume(error -> Mono.just(new ResponseHandler(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(new ResponseHandler("No Content", HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] findByIdClient transaction"));
    }

    @Override
    public Mono<ResponseHandler> getBalance(String idClient) {
        log.info("[INI] getBalance transaction");
        log.info(idClient);
        AtomicReference<Float> balance = new AtomicReference<>((float) 0);
        return dao.findAll()
                .doOnNext(transaction -> {
                    if(transaction.getClientId().equals(idClient)) {
                        balance.set(balance.get() + transaction.getAmount());
                        log.info(transaction.toString());
                    }
                })
                .collectList()
                .map(movements -> new ResponseHandler("Done", HttpStatus.OK, balance.get()))
                .onErrorResume(error -> Mono.just(new ResponseHandler(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(new ResponseHandler("No Content", HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] getBalance transaction"));
    }
}
