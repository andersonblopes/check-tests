package com.lm.checktests.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The type Student.
 */
@Data
@Entity
public class Student {

    /**
     * The Id.
     */
    @Id
    private Long id;

    /**
     * The Inscricao.
     */
    @CsvBindByName
    private String inscricao;

    /**
     * The Matricula.
     */
    @CsvBindByName
    private String matricula;

    /**
     * The Graduacao.
     */
    @CsvBindByName
    private String graduacao;

    /**
     * The Nome.
     */
    @CsvBindByName
    private String nome;

    /**
     * The Obm.
     */
    @CsvBindByName
    private String obm;

    /**
     * The Email.
     */
    @CsvBindByName
    private String email;

    /**
     * The Telefone.
     */
    @CsvBindByName
    private String telefone;

    /**
     * The Nota.
     */
    @CsvBindByName
    private Integer nota;

    /**
     * The Nota 2.
     */
    @CsvBindByName
    private String nota2;

    /**
     * The Situacao.
     */
    @CsvBindByName
    private String situacao;

}
