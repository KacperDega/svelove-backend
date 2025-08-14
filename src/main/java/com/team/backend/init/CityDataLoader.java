package com.team.backend.init;

import com.team.backend.model.City;
import com.team.backend.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CityDataLoader implements CommandLineRunner {

    private final CityRepository cityRepository;

    @Override
    public void run(String... args) throws Exception {
        if (cityRepository.count() == 0) {
            List<String> lines = Files.readAllLines(Path.of("src/main/resources/polish_cities.csv"), StandardCharsets.UTF_8);

            List<City> cities = lines.stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(name -> {
                        City city = new City();
                        city.setName(name);
                        return city;
                    })
                    .toList();

            cityRepository.saveAll(cities);
            System.out.println("Loaded " + cities.size() + " cities.");
        }
    }
}

