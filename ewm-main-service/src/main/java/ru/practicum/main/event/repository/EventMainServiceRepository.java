package ru.practicum.main.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Импорт аннотации Param
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
            "WHERE (e.initiator.id IN :users OR :users IS null) " + // Заменяем ?1 на :users и добавляем аннотацию Param
            "AND (e.state IN :states OR :states IS null) " + // Заменяем ?2 на :states и добавляем аннотацию Param
            "AND (e.category.id IN :categories OR :categories IS null) " + // Заменяем ?3 на :categories и добавляем аннотацию Param
            "AND (e.eventDate > :start OR :start IS null) " +
            "AND (e.eventDate < :end OR :end IS null) ")
    List<Event> findAllByParam(@Param("users") List<Long> users, @Param("states") List<State> states,
                               @Param("categories") List<Long> categories, @Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end, Pageable pageable); // Используем аннотацию Param для каждого аргумента

    boolean existsByIdAndInitiatorId(long eventId, long userId);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE ((:text IS null) OR ((lower(e.annotation) LIKE concat('%', lower(:text), '%')) OR (lower(e.description) LIKE concat('%', lower(:text), '%')))) " +
            "AND (e.category.id IN :categories OR :categories IS null) " +
            "AND (e.paid = :paid OR :paid IS null) " +
            "AND (e.eventDate > :rangeStart OR :rangeStart IS null) AND (e.eventDate < :rangeEnd OR :rangeEnd IS null) " +
            "AND (:onlyAvailable = false OR (e.participantLimit > (SELECT count(*) FROM Request AS r WHERE e.id = r.event.id)) OR (e.participantLimit > 0 )) ")
    List<Event> findAllEvents(@Param("text") String text, @Param("categories") List<Long> categories,
                              @Param("paid") Boolean paid, @Param("rangeStart") LocalDateTime rangeStart,
                              @Param("rangeEnd") LocalDateTime rangeEnd, @Param("onlyAvailable") Boolean onlyAvailable,
                              @Param("sort") String sort, Pageable pageable); // Используем аннотацию Param для каждого аргумента

    boolean existsByCategoryId(long catId);
}
