package com.lm.checktests.model;

import lombok.Data;

import java.util.List;


/**
 * The type Exam result.
 */
@Data
public class ExamResult implements Comparable<ExamResult> {

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

    /**
     * Compare to int.
     *
     * @param result the result
     * @return the int
     */
    @Override
    public int compareTo(ExamResult result) {
        return getAverage().compareTo(result.getAverage());
    }
}
