package com.pagpay.services;

import com.pagpay.domain.user.User;
import com.pagpay.domain.user.UserType;
import com.pagpay.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public void validateTransaction(User sender, BigDecimal value) throws Exception {
        if(UserService.isUserSellerType(sender)) {
            throw new Exception("Usuário não autorizado a fazer transações");
        }

        if (!UserService.hasEnoughAmount(sender, value)) {
            throw new Exception("Saldo insuficiente para transação desejada");
        }
    }

    private static boolean isUserSellerType(User sender) {
        return sender.getUserType() == UserType.SELLER;
    }

    private static boolean hasEnoughAmount(User sender, BigDecimal value) {
        return sender.getBalance().compareTo(value) > 0;
    }

    public User findById(Long id) throws Exception {
        return this.repository.findById(id).orElseThrow(() -> new Exception("Usuário não encontrado"));
    }

    public void saveUser(User user) {
        this.repository.save(user);
    }
}
