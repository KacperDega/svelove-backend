package com.team.backend.init;

import com.team.backend.model.Hobby;
import com.team.backend.repository.HobbyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HobbyDataLoader implements CommandLineRunner {

    private final HobbyRepository hobbyRepository;

    @Override
    public void run(String... args) throws Exception {
        if (hobbyRepository.count() == 0) {
            List<String> lines = Files.readAllLines(Path.of("src/main/resources/hobbies.csv"), StandardCharsets.UTF_8);

            List<Hobby> hobbies = lines.stream()
                    .skip(1)
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(line -> {
                        String[] parts = line.split(";");
                        if (parts.length != 2) {
                            throw new IllegalArgumentException("Invalid line in hobbies.csv: " + line);
                        }
                        Hobby hobby = new Hobby();
                        hobby.setName(parts[0].trim());
                        hobby.setLabel(parts[1].trim());
                        return hobby;
                    })
                    .toList();

            hobbyRepository.saveAll(hobbies);
            System.out.println("Loaded " + hobbies.size() + " hobbies.");
        }
    }
}

