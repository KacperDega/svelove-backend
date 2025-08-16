package com.team.backend.repository;

import com.team.backend.model.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);

    Optional<User> findByLogin(@NonNull String login);

    void deleteByLogin(@NonNull String login);

    Optional<User> findByUsername(@NonNull String username);

    User save(User user);

    void deleteById(Long id);

    @Query("SELECT u FROM User u " +
            "WHERE u.id <> :currentUserId " +
            "AND u.age BETWEEN :ageMin AND :ageMax " +
            "AND u.city.id = :cityId")
    List<User> findFilteredByAgeAndLocation(@Param("currentUserId") Long currentUserId,
                                            @Param("ageMin") int ageMin,
                                            @Param("ageMax") int ageMax,
                                            @Param("cityId") Long cityId);

}
