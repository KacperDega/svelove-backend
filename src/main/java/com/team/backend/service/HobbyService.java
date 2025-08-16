package com.team.backend.service;

import com.team.backend.model.Hobby;
import com.team.backend.repository.HobbyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class HobbyService {

    private final HobbyRepository hobbyRepository;

    public List<Hobby> getAllHobbies() {
        return hobbyRepository.findAll();
    }

    public List<Hobby> getHobbiesByIdList(List<Long> idList) {
        List<Hobby> result = new ArrayList<>();

        for (Long id : idList) {
            hobbyRepository.findById(id).ifPresent(result::add);
        }

        return result;
    }
}
