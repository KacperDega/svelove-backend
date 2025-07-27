package com.team.backend.model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.team.backend.model.Enum.HobbyEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "hobby")
public class Hobby
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private HobbyEnum name;

    public String getHobbyName() {
        return name.getDisplayName();
    }

    @JsonCreator
    public static Hobby fromString(String hobbyName) {
        Hobby hobby = new Hobby();
        hobby.setName(HobbyEnum.fromDisplayName(hobbyName));
        return hobby;
    }

    @JsonValue
    public String toJson() {
        return name.toString();
    }
}
