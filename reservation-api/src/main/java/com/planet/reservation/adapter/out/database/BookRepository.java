package com.planet.reservation.adapter.out.database;

import com.planet.reservation.application.ports.out.BookDatabasePort;
import com.planet.reservation.domain.entities.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long>, BookDatabasePort {
}
