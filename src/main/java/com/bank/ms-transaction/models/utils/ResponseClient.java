package com.bank.mstransaction.models.utils;

import com.bank.mstransaction.models.documents.Client;
import lombok.Data;

@Data
public class ResponseClient
{
    private Client data;

    private String message;

    private String status;

}
