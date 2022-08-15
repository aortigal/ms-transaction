package com.bank.mstransaction.models.documents;

import com.bank.mstransaction.models.utils.Audit;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Data
@Document(collection = "transactions")
public class Transaction extends Audit
{
    @Id
    private String id;
    @NotNull(message = "activeId must not be null")
    private String activeId;
    @NotNull(message = "clientId must not be null")
    private String clientId;
    @NotNull(message = "creditId must not be null")
    private String creditId;
    @NotNull(message = "mont must not be null")
    private float mont;
    private String seller;
    private String ruc;

}
