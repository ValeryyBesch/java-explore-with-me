package ru.practicum.main.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.event.status.State;
import ru.practicum.main.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventMainServiceRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(long eventId, long userId);

    @Query("SELECT e " +
            "FROM ru.practicum.main.event.model.Event e " +
            "WHERE (e.initiator.id IN ?1 OR ?1 IS null) " +
            "AND (e.state IN ?2 OR ?2 IS null) " +
            "AND (e.category.id IN ?3 OR ?3 IS null) " +
            "AND (e.eventDate > ?4 OR ?4 IS null) " +
            "AND (e.eventDate < ?5 OR ?5 IS null) ")
    List<Event> findAllByParam(List<Long> users, List<State> states, List<Long> categories,
                               LocalDateTime start, LocalDateTime end, Pageable pageable);

    boolean existsByIdAndInitiatorId(long eventId, long userId);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE " +
            "(:text IS null OR (lower(e.annotation) LIKE concat('%', lower(:text), '%')) OR (lower(e.description) LIKE concat('%', lower(:text), '%'))) " +
            "AND (:categories IS null OR e.category.id IN :categories) " +
            "AND (:paid IS null OR e.paid = :paid) " +
            "AND (:rangeStart IS null OR e.eventDate > :rangeStart) " +
            "AND (:rangeEnd IS null OR e.eventDate < :rangeEnd) " +
            "AND (:onlyAvailable = false OR (:onlyAvailable = true AND (e.participantLimit > (SELECT count(r) FROM Request r WHERE r.event.id = e.id))) OR (e.participantLimit > 0)) " +
            "ORDER BY " +
            "CASE " +
            "WHEN :sort = 'title' THEN e.title " +
            "WHEN :sort = 'date' THEN e.eventDate " +
            "ELSE e.id " +
            "END")
    List<Event> findAllEvents(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") Boolean onlyAvailable,
            @Param("sort") String sort,
            Pageable pageable);

    boolean existsByCategoryId(long catId);
}
