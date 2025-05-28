package com.pt.reservation.adapter.out.database;

import com.pt.reservation.domain.entities.BookEntity;
import com.pt.reservation.domain.entities.ReservationEntity;
import com.pt.reservation.domain.entities.ReservationItemEntity;
import com.pt.reservation.domain.entities.UserEntity;
import com.pt.reservation.domain.enums.ReservationStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ReservationItemRepositoryTest {
    @Autowired
    private ReservationItemRepository reservationItemRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    private ReservationEntity reservationEntity;
    private UserEntity userEntity;
    private BookEntity bookEntity;

    @BeforeEach
    void setUp() {
        bookEntity = BookEntity.builder()
                .title("Test book")
                .author("Test author")
                .isbn("Test ISBN")
                .totalCopies(10)
                .totalAvailable(10)
                .version(1)
                .build();
        bookEntity = ((CrudRepository<BookEntity, Long>)bookRepository).save(bookEntity);

        userEntity = UserEntity.builder()
                .name("Test name")
                .email("test@email.com")
                .build();
        userEntity = ((CrudRepository<UserEntity, Long>)userRepository).save(userEntity);

        reservationEntity = ReservationEntity.builder()
                .user(userEntity)
                .status(ReservationStatusEnum.PENDING)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(2))
                .items(Collections.emptyList())
                .build();
        reservationEntity = ((CrudRepository<ReservationEntity, Long>)reservationRepository).save(reservationEntity);
    }

    @Test
    void countActiveBooksByUserShouldReturnCorrectQuantity() {
        ReservationItemEntity reservationItem = ReservationItemEntity.builder()
                .reservation(reservationEntity)
                .book(bookEntity)
                .quantity(3)
                .build();

        reservationItemRepository.save(reservationItem);

        int count = reservationItemRepository.countActiveBooksByUser(reservationEntity.getUser().getId());
        assertEquals(3, count);
    }

    @Test
    void countReservedBooksByBookShouldReturnCorrectQuantity() {
        ReservationItemEntity reservationItem = ReservationItemEntity.builder()
                .reservation(reservationEntity)
                .book(bookEntity)
                .quantity(4)
                .build();

        reservationItemRepository.save(reservationItem);

        int count = reservationItemRepository.countReservedBooksByBook(bookEntity.getId());
        assertEquals(4, count);
    }
}
