package com.example.account.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.account.aop.AccountLock;
import com.example.account.dto.CancelBalance;
import com.example.account.dto.QueryTransaction;
import com.example.account.dto.UseBalance;
import com.example.account.exception.AccountException;
import com.example.account.service.TransactionService;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/{transactionId}")
    public QueryTransaction.QtResponse queryTransaction(
            @PathVariable String transactionId
    ) {
        return QueryTransaction.QtResponse.from(
                transactionService.queryTransactionId(transactionId)
        );
    }

    @PostMapping("/use")
    @AccountLock
    public UseBalance.UbResponse useBalance(
            @Valid @RequestBody UseBalance.UbRequest request
    ) throws InterruptedException {
        try {
            return UseBalance.UbResponse.fromDto(transactionService.useBalance(
                    request.getUserId(),
                    request.getAccountNumber(),
                    request.getAmount()));
        } catch (AccountException e) {
            transactionService.saveAndFailedUseTransaction(
                    TransactionType.USE,
                    TransactionResultType.F,
                    request.getAccountNumber(),
                    request.getAmount());

            throw e;
        }
    }

    @DeleteMapping("/cancel")
    @AccountLock
    public CancelBalance.CbResponse cancelBalance(
            @Valid @RequestBody CancelBalance.CbRequest request
    ) throws InterruptedException {
        try {
            return CancelBalance.CbResponse.fromDto(
                    transactionService.cancelBalance(
                            request.getTransactionId(),
                            request.getAccountNumber(),
                            request.getAmount())
            );
        } catch(AccountException e) {
            transactionService.saveAndFailedUseTransaction(
                    TransactionType.CANCEL,
                    TransactionResultType.F,
                    request.getAccountNumber(),
                    request.getAmount());
            throw e;
        }
    }
}
