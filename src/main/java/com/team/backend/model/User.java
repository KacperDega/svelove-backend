package com.team.backend.model;

import com.team.backend.model.Enum.Preference;
import com.team.backend.model.Enum.Sex;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
// wszystkie non null pola moga byc wziete do konstruktora UWAGA NOn NULL musi byc z lomboka a nie z jakarty
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NonNull //Non Null z lombok i nie nullable columna
    @Column(nullable = false, unique = true)
    private String username;
    @NonNull
    @Column(nullable = false)
    private String login;
    @NonNull
    @Column(nullable = false)
    private String password;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "sex", nullable = false)
    private Sex sex;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "preference", nullable = false)
    private Preference preference;

    @NonNull
    private String description;

    @NonNull
    private Integer age;

    @NonNull
    private Integer age_min;

    @NonNull
    private Integer age_max;
    @NonNull
    private String localization;


    @NonNull
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "user_hobbies",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "hobby_id")
    )
    private List<Hobby> hobbies;

    @OneToMany(mappedBy = "firstUser")
    private List<Match> matchesAsFirstUser;

    @OneToMany(mappedBy = "secondUser")
    private List<Match> matchesAsSecondUser;

    @OneToMany(mappedBy = "user")
    private List<PairStatus> pairStatuses;

    @ElementCollection
    @OrderColumn(name = "photo_order")
    @Column(name = "photo_url")
    private List<String> photoUrls = new ArrayList<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}