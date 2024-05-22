package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatServiceRepository extends JpaRepository<Stat, Long> {

    @Query("select new ru.practicum.dto.StatResponseDto(stat.ip, stat.uri, count(distinct stat.ip)) " +
            "from Stat as stat " +
            "where stat.timestamp between :start and :end " +
            "group by stat.ip, stat.uri " +
            "order by count(distinct stat.ip) desc")
    List<StatResponseDto> findAllByTimestampBetweenStartAndEndWithUniqueIp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.dto.StatResponseDto(stat.ip, stat.uri, count(stat.ip)) " +
            "from Stat as stat " +
            "where stat.timestamp between :start and :end " +
            "group by stat.ip, stat.uri " +
            "order by count(stat.ip) desc ")
    List<StatResponseDto> findAllByTimestampBetweenStartAndEndWhereIpNotUnique(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.dto.StatResponseDto(stat.ip, stat.uri, count(distinct stat.ip)) " +
            "from Stat as stat " +
            "where stat.timestamp between :start and :end and stat.uri in :uris " +
            "group by stat.ip, stat.uri " +
            "order by count(distinct stat.ip) desc ")
    List<StatResponseDto> findAllByTimestampBetweenStartAndEndWithUrisUniqueIp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris") List<String> uris);

    @Query("select new ru.practicum.dto.StatResponseDto(stat.ip, stat.uri, count(stat.ip)) " +
            "from Stat as stat " +
            "where stat.timestamp between :start and :end and stat.uri in :uris " +
            "group by stat.ip, stat.uri " +
            "order by count(stat.ip) desc ")
    List<StatResponseDto> findAllByTimestampBetweenStartAndEndWithUrisIpNotUnique(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris") List<String> uris);

}
