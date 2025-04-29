package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mpa {

    @Positive
    protected int id;

    @NotBlank
    protected String name;
}