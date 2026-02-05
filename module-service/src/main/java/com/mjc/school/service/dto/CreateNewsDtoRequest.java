package com.mjc.school.service.dto;

import com.mjc.school.service.validator.constraint.NotNull;
import com.mjc.school.service.validator.constraint.Size;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public record CreateNewsDtoRequest(
    @NotNull
    @Size(min = 5, max = 30)
    String title,

    @NotNull
    @Size(min = 5, max = 255)
    String content,

    @NotNull
    String author,

    List<String> tags,

    List<Long> commentsIds
) {
     public CreateNewsDtoRequest {
         if (tags == null) {
             tags = new ArrayList<>();
         }
         if (commentsIds == null) {
            commentsIds = new ArrayList<>();
         }
     }
}
