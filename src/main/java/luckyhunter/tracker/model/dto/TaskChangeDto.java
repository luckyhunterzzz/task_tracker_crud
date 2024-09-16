package luckyhunter.tracker.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@ToString
public class TaskChangeDto {
    private String title;
    private String description;
    private LocalDateTime plannedStart;
    private LocalDateTime plannedEnd;
    private String status;

}
