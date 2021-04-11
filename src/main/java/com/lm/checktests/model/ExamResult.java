package com.lm.checktests.model;

import lombok.Data;

import java.util.List;


/**
 * The type Exam result.
 */
@Data
public class ExamResult {

    private Student student;

    private List<Answer> studentAnswers;

    private Exam exam;

    private Double average;
}
