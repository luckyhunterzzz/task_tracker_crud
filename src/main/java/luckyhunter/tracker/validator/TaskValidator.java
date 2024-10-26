package luckyhunter.tracker.validator;

import luckyhunter.tracker.model.dto.TaskChangeDto;
import luckyhunter.tracker.model.dto.TaskDto;
import luckyhunter.tracker.model.enums.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static luckyhunter.tracker.model.enums.Status.*;

public class TaskValidator {
    private static final Pattern TITLE_NAME_PATTERN = Pattern.compile("^[А-ЯЁа-яёA-Za-z0-9 .,!?;:\\-()\\[\\]{}'\"«»]+$");

    private List<String> errors = new ArrayList<>();
    private TaskChangeDto taskChangeDto = new TaskChangeDto();

    public void validateTitle(String name) {
        if (name == null) {
            errors.add("Title can not be NULL");
        } else if (name.length() > 50) {
            errors.add("Title name can not be more than 500 characters");
        } else if (!TITLE_NAME_PATTERN.matcher(name).matches()){
            errors.add("Title must contain only Cyrillic or Latin characters without special symbols.");
        } else {
            taskChangeDto.setTitle(name);
        }
    }

    public void validateDescription(String description) {
        if (description == null) {
            errors.add("Description can not be NULL");
        } else if (description.length() > 500) {
            errors.add("Description name can not be more than 5000 characters");
        } else if (!TITLE_NAME_PATTERN.matcher(description).matches()){
            errors.add("Description must contain only Cyrillic or Latin characters without special symbols.");
        } else {
            taskChangeDto.setDescription(description);
        }
    }

    public void validateStatus(String status) {
        if (status == null) {
            errors.add("Status can not be null");
        } else {
            try {
                Status enumStatus = Status.fromString(status);
                taskChangeDto.setStatus(enumStatus.getStatus());
            } catch (IllegalArgumentException e) {
                errors.add("Status can be only: Not started, In progress, Done");
            }
        }
    }

    public void validatePlannedStartTime(String startTime) {
        LocalDateTime time;

        if (startTime == null) {
            errors.add("The start time can not be null");
        } else {
            try {
                time = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                taskChangeDto.setPlannedStart(time);
            } catch (DateTimeParseException e) {
                errors.add("The start time must be in format: yyyy-MM-dd'T'HH:mm:ss");
            }
        }
    }

    public void validatePlannedEndTime(String endTime) {
        LocalDateTime time;

        if (endTime == null) {
            errors.add("The end time can not be null");
        } else {
            try {
                time = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                if (taskChangeDto.getPlannedStart() != null && time.isBefore(taskChangeDto.getPlannedStart())) {
                    errors.add("The end time can not be before start time");
                }
                taskChangeDto.setPlannedEnd(time);
            } catch (DateTimeParseException e) {
                errors.add("The end time must be in format: yyyy-MM-dd'T'HH:mm:ss");
            }
        }
    }


    public TaskChangeDto getTaskChangeDto() {
        return taskChangeDto;
    }

    public List<String> getErrors() {
        return errors;
    }
}
