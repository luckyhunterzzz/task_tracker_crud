package luckyhunter.tracker.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString
public class CommentDto {
    private int id;
    private String title;
    private String description;

}
