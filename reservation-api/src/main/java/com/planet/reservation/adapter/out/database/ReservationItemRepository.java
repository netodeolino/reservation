package com.planet.reservation.adapter.out.database;

import com.planet.reservation.application.ports.out.database.ReservationItemDatabasePort;
import com.planet.reservation.domain.entities.ReservationItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationItemRepository extends JpaRepository<ReservationItemEntity, Long>, ReservationItemDatabasePort {
    @Query("""
        SELECT COALESCE(SUM(ri.quantity), 0) FROM ReservationItemEntity ri
        INNER JOIN ri.reservation r
        WHERE r.user.id = :userId AND r.status IN ('PENDING', 'CONFIRMED')
    """)
    int countActiveBooksByUser(@Param("userId") Long userId);

    @Query("""
        SELECT COALESCE(SUM(ri.quantity), 0) FROM ReservationItemEntity ri
        INNER JOIN ri.reservation r
        WHERE ri.book.id = :bookId AND r.status IN ('PENDING', 'CONFIRMED')
    """)
    int countReservedBooksByBook(@Param("bookId") Long bookId);
}
