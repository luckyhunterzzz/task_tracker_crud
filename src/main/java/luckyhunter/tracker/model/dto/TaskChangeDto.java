package luckyhunter.tracker.model.dto;

import java.time.LocalDateTime;

public class TaskChangeDto {
    private String title;
    private String description;
    private LocalDateTime plannedStart;
    private LocalDateTime plannedEnd;
    private String status;

    public TaskChangeDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getPlannedStart() {
        return plannedStart;
    }

    public void setPlannedStart(LocalDateTime plannedStart) {
        this.plannedStart = plannedStart;
    }

    public LocalDateTime getPlannedEnd() {
        return plannedEnd;
    }

    public void setPlannedEnd(LocalDateTime plannedEnd) {
        this.plannedEnd = plannedEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TaskChangeDto{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", plannedStart=" + plannedStart +
                ", plannedEnd=" + plannedEnd +
                ", status='" + status + '\'' +
                '}';
    }
}
