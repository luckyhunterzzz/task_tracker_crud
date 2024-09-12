package luckyhunter.tracker.validator;

import luckyhunter.tracker.model.dto.ClientToCreateDto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ClientValidator {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[А-ЯЁа-яёA-Za-z\\-\\s']+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$");
    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");




    private List<String> errors = new ArrayList<>();
    private ClientToCreateDto clientToCreateDto = new ClientToCreateDto();

    public void validateFirstName(String firstName) {
        if (firstName == null) {
            errors.add("First name can not be NULL");
        } else if (firstName.length() > 20) {
            errors.add("First name can not be more than 20 characters");
        } else if (!NAME_PATTERN.matcher(firstName).matches()){
            errors.add("First name must contain only Cyrillic or Latin characters without special symbols.");
        } else {
            clientToCreateDto.setFirstName(firstName);
        }
    }

    public void validateLastName(String lastName) {
        if (lastName == null) {
            errors.add("Last name can not be NULL");
        } else if (lastName.length() > 20) {
            errors.add("Last name can not be more than 20 characters");
        } else if (!NAME_PATTERN.matcher(lastName).matches()){
            errors.add("Last name must contain only Cyrillic or Latin characters without special symbols.");
        } else {
            clientToCreateDto.setLastName(lastName);
        }
    }
    public void validateEmail(String email) {
        if (email == null) {
            errors.add("Email can not be NULL");
        } else if (email.length() > 50) {
            errors.add("Email can not be more than 50 characters");
        } else if (!EMAIL_PATTERN.matcher(email).matches()){
            errors.add("Email must be correct.");
        } else {
            clientToCreateDto.setEmail(email);
        }
    }
    public void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            errors.add("Phone number can not be NULL");
        } else if (!PHONE_PATTERN.matcher(phoneNumber).matches()){
            errors.add("Phone number must correcct.");
        } else {
            clientToCreateDto.setPhoneNumber(phoneNumber);
        }
    }
    public void validateDateOfBirth(String dateOfBirth) {
        LocalDate date;

        if (dateOfBirth == null) {
            errors.add("The date of birth cannot be null");
        } else {
            try {
                date = LocalDate.parse(dateOfBirth, DateTimeFormatter.ISO_LOCAL_DATE);
                clientToCreateDto.setBirthDate(date.toString());
            } catch (DateTimeParseException e) {
                errors.add("The date of birth must be in format: yyyy-MM-dd");
            }
        }
    }
    public void validateLogin(String login) {
        if (login == null) {
            errors.add("Login can not be NULL");
        } else if (login.length() > 20) {
            errors.add("Login name can not be more than 20 characters");
        } else if (!LOGIN_PATTERN.matcher(login).matches()){
            errors.add("Login must contain only Latin characters without special symbols.");
        } else {
            clientToCreateDto.setLogin(login);
        }
    }
    public void validatePassword(String password) {
        if (password == null) {
            errors.add("Password can not be NULL");
        } else if (password.length() > 20) {
            errors.add("Password can not be more than 20 characters");
        } else if (!LOGIN_PATTERN.matcher(password).matches()){
            errors.add("Password must contain only Latin characters, digits without special symbols.");
        } else {
            clientToCreateDto.setPassword(password);
        }
    }

    public List<String> getErrors() {
        return errors;
    }

    public ClientToCreateDto getClientToCreateDto() {
        return clientToCreateDto;
    }
}
