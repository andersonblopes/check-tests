package com.lm.checktests.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The type Answer.
 */
@Data
@Entity
public class Answer {

    /**
     * The Id.
     */
    @Id
    private Long id;

    /**
     * The Question number.
     */
    private String questionNumber;

    /**
     * The Question answer.
     */
    private String questionAnswer;
}
