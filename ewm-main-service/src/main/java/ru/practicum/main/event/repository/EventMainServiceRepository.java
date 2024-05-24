package ru.practicum.main.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main.event.status.State;
import ru.practicum.main.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventMainServiceRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(long eventId, long userId);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE (e.initiator.id IN :users OR :users IS null) " +
            "AND (e.state IN :states OR :states IS null) " +
            "AND (e.category.id IN :categories OR :categories IS null) " +
            "AND (e.eventDate > :start OR :start IS null) " +
            "AND (e.eventDate < :end OR :end IS null) ")
    List<Event> findAllByParam(List<Long> users, List<State> states, List<Long> categories,
                               LocalDateTime start, LocalDateTime end, Pageable pageable);

    boolean existsByIdAndInitiatorId(long eventId, long userId);

    @Query("SELECT e " +
            "FROM Event e " +
            "LEFT JOIN Request r ON e.id = r.event.id " +
            "WHERE ((:text IS null) OR (lower(e.annotation) LIKE concat('%', lower(:text), '%') OR lower(e.description) LIKE concat('%', lower(:text), '%'))) " +
            "AND (e.category.id IN :categories OR :categories IS null) " +
            "AND (e.paid = :paid OR :paid IS null) " +
            "AND (e.eventDate > :rangeStart OR :rangeStart IS null) " +
            "AND (e.eventDate < :rangeEnd OR :rangeEnd IS null) " +
            "AND (:onlyAvailable = false OR (e.participantLimit > COUNT(r) AND e.participantLimit > 0)) " +
            "GROUP BY e.id " +
            "ORDER BY :sort")
    List<Event> findAllEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                              LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Pageable pageable);

    boolean existsByCategoryId(long catId);
}
