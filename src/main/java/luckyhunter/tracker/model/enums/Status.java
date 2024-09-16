package luckyhunter.tracker.model.enums;

public enum Status {
    NOT_STARTED ("Not started"),
    IN_PROGRESS ("In progress"),
    DONE ("Done");

    private String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static Status fromString(String text) {
        for (Status s : Status.values()) {
            if (s.status.equalsIgnoreCase(text)) {
                return s;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
