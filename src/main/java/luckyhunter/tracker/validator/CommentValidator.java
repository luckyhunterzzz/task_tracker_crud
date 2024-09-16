package luckyhunter.tracker.validator;

import luckyhunter.tracker.model.dto.CommentDto;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CommentValidator {
    private static final Pattern TITLE_NAME_PATTERN = Pattern.compile("^[А-ЯЁа-яёA-Za-z0-9 .,!?;:\\-()\\[\\]{}'\"«»]+$");

    private List<String> errors = new ArrayList<>();
    private CommentDto commentDto = new CommentDto();

    public void validateTitle(String name) {
        if (name == null) {
            errors.add("Title can not be NULL");
        } else if (name.length() > 50) {
            errors.add("Title name can not be more than 50 characters");
        } else if (!TITLE_NAME_PATTERN.matcher(name).matches()){
            errors.add("Title must contain only Cyrillic or Latin characters without special symbols.");
        } else {
            commentDto.setTitle(name);
        }
    }

    public void validateDescription(String description) {
        if (description == null) {
            errors.add("Description can not be NULL");
        } else if (description.length() > 500) {
            errors.add("Description name can not be more than 500 characters");
        } else if (!TITLE_NAME_PATTERN.matcher(description).matches()){
            errors.add("Description must contain only Cyrillic or Latin characters without special symbols.");
        } else {
            commentDto.setDescription(description);
        }
    }

    public List<String> getErrors() {
        return errors;
    }

    public CommentDto getCommentDto() {
        return commentDto;
    }
}
