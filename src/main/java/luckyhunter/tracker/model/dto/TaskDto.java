package luckyhunter.tracker.model.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Data
@ToString
public class TaskDto {
    private int id;
    private String title;
    private String description;
    private String status;
    private LocalDateTime plannedStart;
    private LocalDateTime plannedEnd;
    private List<TagDto> tags;
    private List<CommentDto> comments;
}
