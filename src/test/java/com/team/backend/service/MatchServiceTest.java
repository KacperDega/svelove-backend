package com.team.backend.service;

import com.team.backend.model.Enum.LikedStatus;
import com.team.backend.model.Enum.Preference;
import com.team.backend.model.Enum.Sex;
import com.team.backend.model.Hobby;
import com.team.backend.model.Match;
import com.team.backend.model.PendingPair;
import com.team.backend.model.User;
import com.team.backend.repository.MatchRepository;
import com.team.backend.repository.PendingPairRepository;
import com.team.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock private MatchRepository matchRepository;
    @Mock private UserRepository userRepository;
    @Mock private PendingPairRepository pendingPairRepository;
    @Mock private PendingPairService pendingPairService;

    @InjectMocks private MatchService matchService;

    private User user;
    private User candidate1, candidate2;
    private User likingUser;
    private User likedUser;
    private PendingPair pendingPair;

    @BeforeEach
    void setup() {
        Hobby hobby = new Hobby();
        hobby.setId(1L);

        user = User.of("user", "login", "pass", Sex.MALE, Preference.WOMEN,
                "Description for user", 25, 20, 30, "Lublin", List.of(hobby));
        user.setId(1L);

        likingUser = User.of("liker", "likerLogin", "pass", Sex.MALE, Preference.WOMEN,
                "Description for liker", 28, 22, 35, "Lublin", List.of(hobby));
        likingUser.setId(1L);

        likedUser = User.of("liked", "likedLogin", "pass", Sex.FEMALE, Preference.MEN,
                "Description for liked", 24, 20, 30, "Lublin", List.of(hobby));
        likedUser.setId(2L);

        pendingPair = new PendingPair();

        user.setHobbies(List.of(hobby));

        candidate1 = User.of("c1", "c1login", "pass", Sex.FEMALE, Preference.MEN,
                "Description for c1", 26, 22, 32, "Lublin", List.of(hobby));
        candidate1.setId(2L);

        candidate2 = User.of("c2", "c2login", "pass", Sex.FEMALE, Preference.MEN,
                "Description for c2", 27, 21, 33, "Lublin", List.of());
        candidate2.setId(3L);
    }



    @Test
    void handleLike_shouldThrow_whenLikingUserNotFound() {
        when(userRepository.findByLogin("unknownLogin")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            matchService.handleLike("unknownLogin", 2L);
        });
    }

    @Test
    void shouldReturnOnlyCompatiblePotentialMatches() {
        List<User> filteredUsers = List.of(candidate1, candidate2);
        when(userRepository.findFilteredByAgeAndLocation(user.getId(), user.getAge_min(), user.getAge_max(), user.getLocalization()))
                .thenReturn(filteredUsers);

        when(matchRepository.findAllMatchesForUser(user)).thenReturn(List.of());
        when(pendingPairRepository.findByFirstUser(user)).thenReturn(List.of());


        List<User> potentialMatches = matchService.getPotentialMatches(user);


        assertEquals(1, potentialMatches.size());
        assertTrue(potentialMatches.contains(candidate1));
        assertFalse(potentialMatches.contains(candidate2)); // brak wspolnych hobby
    }

    @Test
    void handleLike_shouldCreateMatchAndDeletePendingPair_whenBothLiked() {
        when(userRepository.findByLogin("likerLogin")).thenReturn(Optional.of(likingUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(likedUser));
        when(pendingPairService.getOrCreatePendingPair(likingUser, likedUser)).thenReturn(pendingPair);

        when(pendingPairService.bothUsersLiked(pendingPair)).thenReturn(true);

        boolean result = matchService.handleLike("likerLogin", 2L);

        assertTrue(result);
        verify(pendingPairService).updatePairStatus(pendingPair, likingUser, LikedStatus.LIKED);
        verify(matchRepository).save(any(Match.class));
        verify(pendingPairService).deletePendingPair(pendingPair);
    }

    @Test
    void handleLike_shouldNotCreateMatch_whenOnlyOneSideLiked() {
        when(userRepository.findByLogin("likerLogin")).thenReturn(Optional.of(likingUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(likedUser));
        when(pendingPairService.getOrCreatePendingPair(likingUser, likedUser)).thenReturn(pendingPair);

        when(pendingPairService.bothUsersLiked(pendingPair)).thenReturn(false);

        boolean result = matchService.handleLike("likerLogin", 2L);

        assertFalse(result);
        verify(pendingPairService).updatePairStatus(pendingPair, likingUser, LikedStatus.LIKED);
        verify(matchRepository, never()).save(any());
        verify(pendingPairService, never()).deletePendingPair(any());
    }
}

