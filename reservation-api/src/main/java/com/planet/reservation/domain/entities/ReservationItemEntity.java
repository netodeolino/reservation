package com.planet.reservation.domain.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "reservation_items")
public class ReservationItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationEntity reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @Column(nullable = false)
    private int quantity;

    public ReservationItemEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReservationEntity getReservation() {
        return reservation;
    }

    public void setReservation(ReservationEntity reservation) {
        this.reservation = reservation;
    }

    public BookEntity getBook() {
        return book;
    }

    public void setBook(BookEntity book) {
        this.book = book;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationItemEntity that = (ReservationItemEntity) o;
        return quantity == that.quantity && Objects.equals(id, that.id) && Objects.equals(reservation, that.reservation) && Objects.equals(book, that.book);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reservation, book, quantity);
    }

    private ReservationItemEntity(Builder builder) {
        this.id = builder.id;
        this.reservation = builder.reservation;
        this.book = builder.book;
        this.quantity = builder.quantity;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private ReservationEntity reservation;
        private BookEntity book;
        private int quantity;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder reservation(ReservationEntity reservation) {
            this.reservation = reservation;
            return this;
        }

        public Builder book(BookEntity book) {
            this.book = book;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public ReservationItemEntity build() {
            return new ReservationItemEntity(this);
        }
    }
}
