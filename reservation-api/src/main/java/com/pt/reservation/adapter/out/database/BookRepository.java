package com.pt.reservation.adapter.out.database;

import com.pt.reservation.application.ports.out.database.BookDatabasePort;
import com.pt.reservation.domain.entities.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long>, BookDatabasePort {
}
