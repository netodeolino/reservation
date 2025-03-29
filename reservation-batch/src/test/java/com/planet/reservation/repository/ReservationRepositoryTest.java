package com.planet.reservation.repository;

import com.planet.reservation.domain.entities.ReservationEntity;
import com.planet.reservation.domain.entities.UserEntity;
import com.planet.reservation.domain.enums.ReservationStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ReservationRepositoryTest {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        UserEntity userEntity = UserEntity.builder()
                .name("Test name")
                .email("test@email.com")
                .build();
        userEntity = userRepository.save(userEntity);

        ReservationEntity activeReservation = ReservationEntity.builder()
                .user(userEntity)
                .status(ReservationStatusEnum.PENDING)
                .createdAt(now.minusDays(5))
                .expiresAt(now.minusDays(3))
                .pickedUpAt(null)
                .build();

        ReservationEntity alreadyExpired = ReservationEntity.builder()
                .user(userEntity)
                .status(ReservationStatusEnum.EXPIRED)
                .createdAt(now.minusDays(6))
                .expiresAt(now.minusDays(4))
                .pickedUpAt(null)
                .build();

        ReservationEntity pickedUpReservation = ReservationEntity.builder()
                .user(userEntity)
                .status(ReservationStatusEnum.CONFIRMED)
                .createdAt(now.minusDays(5))
                .expiresAt(now.minusDays(3))
                .pickedUpAt(now.minusDays(2))
                .build();

        reservationRepository.saveAll(List.of(activeReservation, alreadyExpired, pickedUpReservation));
    }

    @Test
    @Transactional
    @Rollback
    void markReservationsAsExpiredShouldUpdateCorrectly() {
        LocalDateTime expirationThreshold = LocalDateTime.now().minusDays(4);

        int updatedCount = reservationRepository.markReservationsAsExpired(expirationThreshold);

        assertEquals(1, updatedCount, "Only one reservation should be expired");

        testEntityManager.flush();
        testEntityManager.clear();

        List<ReservationEntity> expiredReservations = reservationRepository.findAll();
        long expiredCount = expiredReservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatusEnum.EXPIRED)
                .count();

        assertEquals(2, expiredCount, "Now must have two expired reservations saved");
    }
}
