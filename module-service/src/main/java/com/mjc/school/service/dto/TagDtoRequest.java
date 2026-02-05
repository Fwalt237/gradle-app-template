package com.mjc.school.service.dto;

import com.mjc.school.service.validator.constraint.NotNull;
import com.mjc.school.service.validator.constraint.Size;
import io.swagger.v3.oas.annotations.media.Schema;


public record TagDtoRequest(
    @NotNull
    @Size(min = 3, max = 15)
    String name) {
}
