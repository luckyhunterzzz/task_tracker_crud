package luckyhunter.tracker.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString
public class TagDto {
    private int id;
    private String name;
    private String color;

}
