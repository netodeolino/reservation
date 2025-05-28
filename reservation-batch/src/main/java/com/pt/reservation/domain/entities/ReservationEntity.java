package com.pt.reservation.domain.entities;

import com.pt.reservation.domain.enums.ReservationStatusEnum;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "reservations")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "reservation", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<ReservationItemEntity> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ReservationStatusEnum status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private LocalDateTime pickedUpAt;

    public ReservationEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<ReservationItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ReservationItemEntity> items) {
        this.items = items;
    }

    public ReservationStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ReservationStatusEnum status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getPickedUpAt() {
        return pickedUpAt;
    }

    public void setPickedUpAt(LocalDateTime pickedUpAt) {
        this.pickedUpAt = pickedUpAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationEntity that = (ReservationEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(user, that.user) && status == that.status && Objects.equals(createdAt, that.createdAt) && Objects.equals(expiresAt, that.expiresAt) && Objects.equals(pickedUpAt, that.pickedUpAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, status, createdAt, expiresAt, pickedUpAt);
    }

    private ReservationEntity(Builder builder) {
        this.user = builder.user;
        this.items = builder.items;
        this.status = builder.status;
        this.createdAt = builder.createdAt;
        this.expiresAt = builder.expiresAt;
        this.pickedUpAt = builder.pickedUpAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UserEntity user;
        private List<ReservationItemEntity> items;
        private ReservationStatusEnum status;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
        private LocalDateTime pickedUpAt;

        public Builder user(UserEntity user) {
            this.user = user;
            return this;
        }

        public Builder items(List<ReservationItemEntity> items) {
            this.items = items;
            return this;
        }

        public Builder status(ReservationStatusEnum status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder pickedUpAt(LocalDateTime pickedUpAt) {
            this.pickedUpAt = pickedUpAt;
            return this;
        }

        public ReservationEntity build() {
            return new ReservationEntity(this);
        }
    }
}
