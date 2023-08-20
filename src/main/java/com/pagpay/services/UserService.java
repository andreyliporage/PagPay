package com.pagpay.services;

import com.pagpay.domain.user.User;
import com.pagpay.domain.user.UserType;
import com.pagpay.dtos.UserDTO;
import com.pagpay.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
        return sender.getBalance().compareTo(value) >= 0;
    }

    public User findById(Long id) {
        return this.repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    public void saveUser(User user) {
        this.repository.save(user);
    }

    public User createUser(UserDTO userDTO) {
        validateEmail(userDTO);
        validateDocument(userDTO);
        User newUser = new User(userDTO);
        this.saveUser(newUser);
        return newUser;
    }

    public List<User> getAll() {
        return this.repository.findAll();
    }

    private void validateEmail(UserDTO userDTO) {
        Optional<User> user = this.repository.findUserByEmail(userDTO.email());
        if(user.isPresent()) {
            throw new DataIntegrityViolationException("E-mail já cadastrado");
        }
    }

    private void validateDocument(UserDTO userDTO) {
        Optional<User> user = this.repository.findUserByDocument(userDTO.document());
        if (user.isPresent()) {
            throw new DataIntegrityViolationException("Documento já cadastrado");
        }
    }
}
