package com.pagpay.controllers;

import com.pagpay.domain.transaction.Transaction;
import com.pagpay.dtos.TransactionDTO;
import com.pagpay.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService service;

    @PostMapping
    public ResponseEntity<Transaction> post(@RequestBody TransactionDTO transactionDTO) throws Exception {
        Transaction newTransaction = service.createTransaction(transactionDTO);
        return new ResponseEntity<>(newTransaction, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAll() {
        List<Transaction> transactions = this.service.getAll();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }
}
