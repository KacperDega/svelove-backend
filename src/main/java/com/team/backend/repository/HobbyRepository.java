package com.team.backend.repository;

import com.team.backend.model.Enum.HobbyEnum;
import com.team.backend.model.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HobbyRepository extends JpaRepository<Hobby, Long> {

    List<Hobby> findByName(HobbyEnum name);

    //Optional<Hobby> findByHobbyName(String hobbyName2);

//    Optional<Hobby> findByName(com.team.backend.model.Enum.Hobby hobbyName);

    //List<Hobby> findByHobbyNameContainingIgnoreCase(String hobbyName, Limit limit);
}
