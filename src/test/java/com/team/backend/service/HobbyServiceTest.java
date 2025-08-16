package com.team.backend.service;

import com.team.backend.model.Hobby;
import com.team.backend.repository.HobbyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HobbyServiceTest {

    @Mock
    private HobbyRepository hobbyRepository;

    @InjectMocks
    private HobbyService hobbyService;

    private Hobby hobby1;
    private Hobby hobby2;

    @BeforeEach
    void setUp() {
        hobby1 = new Hobby("reading", "Reading");
        hobby1.setId(1L);

        hobby2 = new Hobby("swimming", "Swimming");
        hobby2.setId(2L);
    }

    @Test
    void getHobbiesByIdList_shouldReturnHobbiesForValidIds() {
        when(hobbyRepository.findById(1L)).thenReturn(Optional.of(hobby1));
        when(hobbyRepository.findById(2L)).thenReturn(Optional.of(hobby2));
        when(hobbyRepository.findById(3L)).thenReturn(Optional.empty());

        List<Long> ids = List.of(1L, 2L, 3L);

        List<Hobby> result = hobbyService.getHobbiesByIdList(ids);

        assertEquals(2, result.size());
        assertTrue(result.contains(hobby1));
        assertTrue(result.contains(hobby2));
    }

    @Test
    void getHobbiesByIdList_shouldReturnEmptyListForEmptyInput() {
        List<Hobby> result = hobbyService.getHobbiesByIdList(Collections.emptyList());

        assertTrue(result.isEmpty());
        verifyNoInteractions(hobbyRepository);
    }

//    @Test
//    void searchHobbiesByKeyword_shouldReturnMatchingHobbies() {
//        String keyword = "art";
//        int limit = 2;
//        List<Hobby> hobbies = List.of(hobby1, hobby2);
//
//        when(hobbyRepository.findByHobbyNameContainingIgnoreCase(eq(keyword), any()))
//                .thenReturn(hobbies);
//
//        List<Hobby> result = hobbyService.searchHobbiesByKeyword(keyword, limit);
//
//        assertEquals(2, result.size());
//        assertEquals(hobby1, result.get(0));
//        assertEquals(hobby2, result.get(1));
//    }
//
//    @Test
//    void searchHobbiesByKeyword_shouldReturnEmptyListWhenNoMatch() {
//        String keyword = "xyz";
//        int limit = 3;
//
//        when(hobbyRepository.findByHobbyNameContainingIgnoreCase(eq(keyword), any()))
//                .thenReturn(Collections.emptyList());
//
//        List<Hobby> result = hobbyService.searchHobbiesByKeyword(keyword, limit);
//
//        assertTrue(result.isEmpty());
//    }
}

