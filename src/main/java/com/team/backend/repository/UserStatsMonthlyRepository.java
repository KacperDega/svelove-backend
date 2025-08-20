package com.team.backend.repository;

import com.team.backend.model.User;
import com.team.backend.model.UserStatsMonthly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStatsMonthlyRepository extends JpaRepository<UserStatsMonthly, Long> {

    Optional<UserStatsMonthly> findByUserAndYearAndMonth(User user, int year, int month);

    List<UserStatsMonthly> findAllByUser(User user);

    List<UserStatsMonthly> findAllByUserAndYear(User user, int year);
}

