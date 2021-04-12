package com.lm.checktests.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.List;

/**
 * The type Exam.
 */
@Data
@Entity
public class Exam {

    /**
     * The Id.
     */
    @Id
    private Long id;

    /**
     * The Title.
     */
    @NotBlank(message = "O título é obrigatório")
    private String title;

    /**
     * The Number of questions.
     */
    @NotNull(message = "Número de questões é obrigatório")
    @Positive
    private Integer numberOfQuestions;

    /**
     * The Number of approved.
     */
    @NotNull(message = "Número de vagas é obrigatório")
    @Positive
    private Integer numberOfApproved;

    /**
     * The Number wait queue.
     */
    @PositiveOrZero
    private Integer numberWaitQueue = 0;

    /**
     * The Date of exam.
     */
    @NotNull(message = "Date of exam is mandatory")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfExam;

    /**
     * The File uploaded.
     */
    @Transient
    private MultipartFile fileUploaded;

    /**
     * The Answers.
     */
    @OneToMany
    private List<Answer> answers;

}
