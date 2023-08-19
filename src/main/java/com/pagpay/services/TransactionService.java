package com.pagpay.services;

import com.pagpay.domain.transaction.Transaction;
import com.pagpay.domain.user.User;
import com.pagpay.dtos.TransactionDTO;
import com.pagpay.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository repository;
    @Autowired
    private UserService userService;
    @Autowired
    private RestTemplate restTemplate;

    public void createTransaction(TransactionDTO transactionDTO) throws Exception {
        User sender = this.userService.findById(transactionDTO.senderId());
        User receiver = this.userService.findById(transactionDTO.receiverId());

        userService.validateTransaction(sender, transactionDTO.value());

        if(!isAuthorized()) {
            throw new Exception("Transação não autorizada");
        }

        Transaction transaction = transactionFactory(transactionDTO, sender, receiver);

        sender.setBalance(returnNewAmountAfterTransactionSender(sender, transaction));
        receiver.setBalance(returnNewAmountAfterTransactionReceiver(receiver, transaction));

        this.repository.save(transaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);
    }

    private boolean isAuthorized() {
        ResponseEntity<Map> authorizerResponse = this.restTemplate.getForEntity("https://run.mocky.io/v3/8fafdd68-a090-496f-8c9a-3442cf30dae6", Map.class);

        if(authorizerResponse.getStatusCode() == HttpStatus.OK) {
            String message = (String) authorizerResponse.getBody().get("message");
            return "Autorizado".equalsIgnoreCase(message);
        } else return false;
    }

    private static Transaction transactionFactory(TransactionDTO transactionDTO, User sender, User receiver) {
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDTO.value());
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setTimestamp(LocalDateTime.now());

        return transaction;
    }

    private static BigDecimal returnNewAmountAfterTransactionSender(User sender, Transaction transaction) {
        return sender.getBalance().subtract(transaction.getAmount());
    }

    private static BigDecimal returnNewAmountAfterTransactionReceiver(User receiver, Transaction transaction) {
        return receiver.getBalance().add(transaction.getAmount());
    }
}
