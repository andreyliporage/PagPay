package com.pagpay.services;

import com.pagpay.domain.user.User;
import com.pagpay.domain.user.UserType;
import com.pagpay.dtos.UserDTO;
import com.pagpay.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserService service;
    @Mock
    UserRepository repository;

    UserDTO userDTO;

    User user;

    @BeforeEach
    public void setup() {
        userDTO = new UserDTO("Andrey", "Matos", "19007884571", BigDecimal.TEN, "andreydev.io@gmail.com", "abc098", UserType.COMMON);
        user = new User(userDTO);
    }

    @Test
    void shouldFindUser() {
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));
        User userResponse = service.findById(user.getId());

        assertEquals(Optional.of(user).get(), userResponse);
        verify(repository).findById(user.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldThrowEntityNotFindExceptionWhenUserNotExist() {
        final EntityNotFoundException e = assertThrowsExactly(EntityNotFoundException.class, () -> {
           service.findById(user.getId());
        });

        assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("Usuário não encontrado"));
    }

    @Test
    void shouldCreateUserSuccess() {
        when(repository.save(user)).thenReturn(user);
        User newUser = service.createUser(userDTO);

        assertThat(newUser, notNullValue());
        assertEquals(user.getUserType(), newUser.getUserType());
        verify(repository).save(user);
    }

    @Test
    void shouldThrowDataIntegrityViolationExceptionBecauseEmailAlreadyExist() {
        when(repository.save(user)).thenThrow(new DataIntegrityViolationException("E-mail já cadastrado"));
        final DataIntegrityViolationException e = assertThrowsExactly(DataIntegrityViolationException.class, () -> {
            service.createUser(userDTO);
        });

        assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("E-mail já cadastrado"));
        verify(repository).save(user);
    }

    @Test
    void shouldThrowDataIntegrityViolationExceptionBecauseDocumentAlreadyExist() {
        when(repository.save(user)).thenThrow(new DataIntegrityViolationException("Documento já cadastrado"));
        final DataIntegrityViolationException e = assertThrowsExactly(DataIntegrityViolationException.class, () -> {
            service.createUser(userDTO);
        });

        assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("Documento já cadastrado"));
        verify(repository).save(user);
    }

    @Test
    void shouldReturnAllUsers() {
        when(repository.findAll()).thenReturn(Collections.singletonList(user));
        List<User> users = service.getAll();

        assertEquals(Collections.singletonList(user), users);
        assertEquals(Collections.singletonList(user).size(), 1);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }
}
