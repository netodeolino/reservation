package com.planet.reservation.application.ports.in;

import com.planet.reservation.application.exceptions.NotFoundException;
import com.planet.reservation.application.ports.out.database.UserDatabasePort;
import com.planet.reservation.domain.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserUseCaseImplTest {

    @Mock
    private UserDatabasePort userDatabasePort;

    @InjectMocks
    private UserUseCaseImpl userUseCase;

    @Test
    void findByIdShouldReturnUserWhenUserExists() {
        Long userId = 1L;
        String name = "User test";
        String email = "email@email.com";
        UserEntity expectedUser = UserEntity.builder()
                .id(userId)
                .name(name)
                .email(email)
                .build();

        when(userDatabasePort.findById(userId)).thenReturn(Optional.of(expectedUser));

        UserEntity result = userUseCase.findById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());

        verify(userDatabasePort).findById(userId);
    }

    @Test
    void findByIdShouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        Long nonExistentUserId = 999L;

        when(userDatabasePort.findById(nonExistentUserId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userUseCase.findById(nonExistentUserId)
        );

        assertEquals("User not found", exception.getMessage());

        verify(userDatabasePort).findById(nonExistentUserId);
    }
}
