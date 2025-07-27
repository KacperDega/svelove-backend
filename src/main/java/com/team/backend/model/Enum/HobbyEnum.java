package com.team.backend.model.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum HobbyEnum {
    // Sztuka i kreatywność
    DRAWING("Drawing"),
    PAINTING("Painting"),
    PHOTOGRAPHY("Photography"),
    WRITING("Writing"),
    DIY("DIY / Crafts"),
    PLAYING_INSTRUMENT("Playing an Instrument"),
    DANCING("Dancing"),
    SINGING("Singing"),
    GRAPHIC_DESIGN("Graphic Design"),

    // Sport i aktywność fizyczna
    GYM("Gym"),
    RUNNING("Running"),
    CYCLING("Cycling"),
    SWIMMING("Swimming"),
    YOGA("Yoga"),
    TEAM_SPORTS("Team Sports"),
    CLIMBING("Climbing"),
    MARTIAL_ARTS("Martial Arts"),
    FITNESS("Fitness"),
    HIKING("Hiking"),

    // Na świeżym powietrzu i podróże
    CAMPING("Camping"),
    FISHING("Fishing"),
    GARDENING("Gardening"),
    TRAVELING("Traveling"),
    CITY_EXPLORATION("City Exploration"),
    STARGAZING("Stargazing"),
    PICNICS("Picnics"),

    // Kultura i rozrywka
    READING("Reading"),
    MOVIES("Movies"),
    BOARD_GAMES("Board Games"),
    VIDEO_GAMES("Video Games"),
    THEATER("Theater"),
    MUSIC("Listening to Music"),
    ANIME("Anime / Manga"),
    COOKING("Cooking"),
    LANGUAGE_LEARNING("Learning Languages"),

    // Intelektualne i technologiczne
    PROGRAMMING("Programming"),
    SCIENCE("Science"),
    PUZZLES("Logic Puzzles"),
    MODEL_BUILDING("Model Building"),
    PODCASTS("Podcasts"),
    ONLINE_LEARNING("Online Learning");

    private final String displayName;

    HobbyEnum(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    private static final Map<String, HobbyEnum> DISPLAY_NAME_MAP = new HashMap<>();

    static {
        for (HobbyEnum hobby : HobbyEnum.values()) {
            DISPLAY_NAME_MAP.put(hobby.getDisplayName().toLowerCase(), hobby);
        }
    }

    @JsonCreator
    public static HobbyEnum fromDisplayName(String value) {
        if (value == null) return null;

        HobbyEnum hobby = DISPLAY_NAME_MAP.get(value.toLowerCase());
        if (hobby == null) {
            throw new IllegalArgumentException("nieznane hobby: " + value);
        }
        return hobby;
    }


}
