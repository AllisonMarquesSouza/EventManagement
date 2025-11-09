package com.br.eventmanagement.repositories;

import com.br.eventmanagement.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, UUID> {
    List<Registration> findAllByUserId(UUID userId);
    List<Registration> findAllByEventId(UUID eventId);
    Optional<Registration> findByUserIdAndEventId(UUID userId, UUID eventId);
    boolean existsRegistrationByUserIdAndEventId(UUID userId, UUID eventId);
    void deleteByUserIdAndEventId(UUID userId, UUID eventId);
    void deleteAllByEventId(UUID eventId);
}
