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
            "FROM Event e " +
            "WHERE (e.initiator.id IN :users OR :users IS NULL) " +
            "AND (e.state IN :states OR :states IS NULL) " +
            "AND (e.category.id IN :categories OR :categories IS NULL) " +
            "AND (e.eventDate > :start OR :start IS NULL) " +
            "AND (e.eventDate < :end OR :end IS NULL)")
    List<Event> findAllByParam(@Param("users") List<Long> users,
                               @Param("states") List<State> states,
                               @Param("categories") List<Long> categories,
                               @Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end,
                               Pageable pageable);

    boolean existsByIdAndInitiatorId(long eventId, long userId);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE (:text IS NULL OR (LOWER(e.annotation) LIKE CONCAT('%', LOWER(:text), '%') OR LOWER(e.description) LIKE CONCAT('%', LOWER(:text), '%'))) " +
            "AND (e.category.id IN :categories OR :categories IS NULL) " +
            "AND (e.paid = :paid OR :paid IS NULL) " +
            "AND (e.eventDate > :rangeStart OR :rangeStart IS NULL) " +
            "AND (e.eventDate < :rangeEnd OR :rangeEnd IS NULL) " +
            "AND (:onlyAvailable = false OR (e.participantLimit > (SELECT COUNT(r) FROM Request AS r WHERE e.id = r.event.id AND e.participantLimit > 0))) " +
            "GROUP BY e.id " +
            "ORDER BY :sort")
    List<Event> findAllEvents(@Param("text") String text,
                              @Param("categories") List<Long> categories,
                              @Param("paid") Boolean paid,
                              @Param("rangeStart") LocalDateTime rangeStart,
                              @Param("rangeEnd") LocalDateTime rangeEnd,
                              @Param("onlyAvailable") Boolean onlyAvailable,
                              @Param("sort") String sort,
                              Pageable pageable);

    boolean existsByCategoryId(long catId);
}
