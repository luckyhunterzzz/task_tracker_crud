package luckyhunter.tracker.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@ToString
@Entity
@Table(name = "Tag")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "color_code")
    private String color;

    @JsonBackReference
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<Task> tasks;

}
