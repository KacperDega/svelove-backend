package com.team.backend.service;

import com.team.backend.model.Enum.Sex;
import com.team.backend.model.Hobby;
import com.team.backend.model.Enum.LikedStatus;
import com.team.backend.model.Match;
import com.team.backend.model.User;
import com.team.backend.model.PendingPair;
import com.team.backend.model.dto.MessageResponseDto;
import com.team.backend.model.mapper.MessageMapper;
import com.team.backend.repository.MatchRepository;
import com.team.backend.repository.MessageRepository;
import com.team.backend.repository.PendingPairRepository;
import com.team.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.team.backend.model.Enum.Preference;

import static com.team.backend.model.Enum.NotificationType.NEW_MATCH;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final PendingPairRepository pendingPairRepository;
    private final PendingPairService pendingPairService;
    private final UserStatsService userStatsService;
    private final NotificationService notificationService;

    @Transactional
    public Match save(Match match) {
        return matchRepository.save(match);
    }

    public List<Match> findByFirstUserId(Long firstUserId) {
        return matchRepository.findByFirstUserId(firstUserId);
    }

    public List<Match> findBySecondUserId(Long secondUserId) {
        return matchRepository.findBySecondUserId(secondUserId);
    }

    public Match findById(Long id) {
        return matchRepository.findById(id).orElse(null);
    }

    public List<Match> getUserMatches(User user) {
        return matchRepository.findAllMatchesForUser(user);
    }

    public List<MessageResponseDto> findMessagesByMatchId(Long matchId) {
        return messageRepository.findByMatchId(matchId).stream()
                .map(MessageMapper::mapToMessageResponse)
                .collect(Collectors.toList());
    }

    public List<User> getPotentialMatches(User user) {

        List<User> filtered = userRepository.findFilteredByAgeAndLocation(
                user.getId(),
                user.getAge_min(),
                user.getAge_max(),
                user.getCity().getId()
        );

        Set<Long> matchedUserIds = matchRepository.findAllMatchesForUser(user).stream()
                .map(match -> {
                    if (match.getFirstUser().getId().equals(user.getId())) {
                        return match.getSecondUser().getId();
                    } else {
                        return match.getFirstUser().getId();
                    }
                })
                .collect(Collectors.toSet());

        Set<Long> likedUserIds = pendingPairRepository.findByFirstUser(user).stream()
                .map(pp -> pp.getSecondUserStatus().getUser().getId())
                .collect(Collectors.toSet());

        List<User> filteredUsers = filtered.stream()
                .filter(candidate -> !matchedUserIds.contains(candidate.getId()))
                .filter(candidate -> !likedUserIds.contains(candidate.getId()))
                .filter(candidate -> areBothCompatible(user, candidate))
                .filter(candidate -> hasCommonHobbies(user, candidate))
                .collect(Collectors.toList());

        if (filteredUsers.size() <= 30) {
            return filteredUsers;
        }

        Collections.shuffle(filteredUsers);
        return filteredUsers.subList(0, 30);
    }

    public void handleSwipeLeft(String login, Long swipedUserId) {
        User swipingUser = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User swipedUser = userRepository.findById(swipedUserId)
                .orElseThrow(() -> new RuntimeException("Swiped user not found"));

        userStatsService.recordLeftSwipe(swipingUser);
    }

    public boolean handleLike(String login, Long likedUserId) {
        User likingUser = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User likedUser = userRepository.findById(likedUserId)
                .orElseThrow(() -> new RuntimeException("Liked user not found"));

        PendingPair pendingPair = pendingPairService.getOrCreatePendingPair(likingUser, likedUser);

        pendingPairService.updatePairStatus(pendingPair, likingUser, LikedStatus.LIKED);

        userStatsService.recordRightSwipe(likingUser);

        if (pendingPairService.bothUsersLiked(pendingPair))
        {
            Match newMatch = new Match(likingUser, likedUser);
            matchRepository.save(newMatch);
            pendingPairService.deletePendingPair(pendingPair);

            notificationService.createNotification(likingUser, NEW_MATCH, "Masz nowy match z " + likedUser.getUsername(), newMatch.getId());
            notificationService.createNotification(likedUser, NEW_MATCH, "Masz nowy match z " + likingUser.getUsername(), newMatch.getId());

            userStatsService.recordMatch(likingUser);
            userStatsService.recordMatch(likedUser);

            return true;
        }

        return false;
    }

    private boolean areBothCompatible(User currentUser, User candidate) {
        boolean userToCandidate = isSexInPreference(currentUser.getSex(), candidate.getPreference());
        boolean candidateToUser = isSexInPreference(candidate.getSex(), currentUser.getPreference());

        return userToCandidate && candidateToUser;
    }

    private boolean isSexInPreference(Sex sex, Preference pref) {
        return switch (pref) {
            case MEN -> sex == Sex.MALE;
            case WOMEN -> sex == Sex.FEMALE;
            case OTHER -> sex == Sex.OTHER;
            case BOTH -> sex == Sex.MALE || sex == Sex.FEMALE || sex == Sex.OTHER;
        };
    }


    private boolean hasCommonHobbies(User user1, User user2) {
        Set<Long> hobbyIds1 = user1.getHobbies().stream().map(Hobby::getId).collect(Collectors.toSet());
        Set<Long> hobbyIds2 = user2.getHobbies().stream().map(Hobby::getId).collect(Collectors.toSet());
        hobbyIds1.retainAll(hobbyIds2);
        return !hobbyIds1.isEmpty();
    }


}

