package luckyhunter.tracker.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString
public class ClientToCreateDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String birthDate;
    private String login;
    private String password;

}
