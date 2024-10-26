package luckyhunter.tracker.validator;

import luckyhunter.tracker.model.dto.TagDto;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TagValidator {
    private static final Pattern TITLE_NAME_PATTERN = Pattern.compile("^[А-ЯЁа-яёA-Za-z0-9 .,!?;:\\-()\\[\\]{}'\"«»]+$");
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");

    private List<String> errors = new ArrayList<>();
    private TagDto tagDto = new TagDto();

    public void validateName(String name) {
        if (name == null) {
            errors.add("Tag name can not be NULL");
        } else if (name.length() > 15) {
            errors.add("Tag name can not be more than 15 characters");
        } else if (!TITLE_NAME_PATTERN.matcher(name).matches()){
            errors.add("Tag name must contain only Cyrillic or Latin characters without special symbols.");
        } else {
            tagDto.setName(name);
        }
    }

    public void validateHexColor(String color) {
        if (color == null) {
            errors.add("Tag color can not be NULL");
        } else if (!HEX_COLOR_PATTERN.matcher(color).matches()){
            errors.add("Tag color must contain only HEX code.");
        } else {
            tagDto.setColor(color);
        }
    }

    public List<String> getErrors() {
        return errors;
    }

    public TagDto getTagDto() {
        return tagDto;
    }
}
