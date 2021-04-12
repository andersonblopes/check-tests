package com.lm.checktests.model;

import lombok.Data;

import java.util.List;


/**
 * The type Exam result.
 */
@Data
public class ExamResult {

    /**
     * The Student.
     */
    private Student student;

    /**
     * The Student answers.
     */
    private List<Answer> studentAnswers;

    /**
     * The Exam.
     */
    private Exam exam;

    /**
     * The Average.
     */
    private Double average;
}
