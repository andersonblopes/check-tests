package com.lm.checktests.controller;

import com.lm.checktests.model.Constants;
import com.lm.checktests.model.Exam;
import com.lm.checktests.model.Student;
import com.lm.checktests.service.MainService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Main controller.
 */
@Controller
@Data
@Slf4j
public class MainController {

    /**
     * The Card header.
     */
    private String cardHeader;

    /**
     * The Status errors.
     */
    private boolean statusErrors;

    /**
     * The Upload candidates.
     */
    private boolean uploadCandidates;

    /**
     * The Upload correct answers.
     */
    private boolean uploadCorrectAnswers;

    /**
     * The Upload candidates.
     */
    private boolean uploadCandidatesAnswers;

    /**
     * The Students.
     */
    private List<Student> students = new ArrayList<>();

    /**
     * The Exam.
     */
    private Exam exam;

    /**
     * The Main service.
     */
    @Autowired
    private MainService mainService;

    /**
     * Index model and view.
     *
     * @param modelAndView the model and view
     * @return the model and view
     */
    @GetMapping("/")
    public ModelAndView index(ModelAndView modelAndView) {
        modelAndView.setViewName("index");
        cardHeader = "Bem vindo(a) ao 'check-tests'!";
        setStatusErrors(false);
        modelAndView.addObject("cardHeader", cardHeader);
        modelAndView.addObject("students", students);
        modelAndView.addObject("exam", exam);
        modelAndView.addObject("statusErrors", isStatusErrors());
        modelAndView.addObject("uploadCandidates", isUploadCandidates());
        modelAndView.addObject("uploadCandidatesAnswers", isUploadCandidatesAnswers());
        modelAndView.addObject("uploadCorrectAnswers", isUploadCorrectAnswers());

        return modelAndView;
    }

    /**
     * Restart model and view.
     *
     * @param modelAndView the model and view
     * @return the model and view
     */
    @GetMapping("/restart")
    public ModelAndView restart(ModelAndView modelAndView) {
        modelAndView.setViewName("index");
        setStatusErrors(false);
        students = new ArrayList<>();
        setUploadCandidates(false);
        setUploadCorrectAnswers(false);
        setUploadCandidatesAnswers(false);
        exam = null;
        modelAndView.addObject("students", students);
        modelAndView.addObject("exam", exam);
        modelAndView.addObject("statusErrors", isStatusErrors());
        modelAndView.addObject("uploadCandidates", isUploadCandidates());
        modelAndView.addObject("uploadCandidatesAnswers", isUploadCandidatesAnswers());
        modelAndView.addObject("uploadCorrectAnswers", isUploadCorrectAnswers());

        return modelAndView;
    }

    /**
     * Selection model and view.
     *
     * @param modelAndView the model and view
     * @return the model and view
     */
    @GetMapping("/selection")
    public ModelAndView selection(ModelAndView modelAndView) {
        cardHeader = "Dados do processo seletivo";
        setStatusErrors(false);
        modelAndView.setViewName("selection-process-view");
        modelAndView.addObject("cardHeader", cardHeader);
        modelAndView.addObject("exam", this.exam == null ? new Exam() : this.exam);
        modelAndView.addObject("statusErrors", isStatusErrors());
        modelAndView.addObject("uploadCorrectAnswers", isUploadCorrectAnswers());
        return modelAndView;
    }

    /**
     * Register selection string.
     *
     * @param exam         the exam
     * @param result       the result
     * @param modelAndView the model and view
     * @return the string
     * @throws IOException the io exception
     */
    @PostMapping("/selection")
    public ModelAndView registerSelection(@Valid @ModelAttribute("exam") Exam exam, BindingResult result, ModelAndView modelAndView) throws IOException {

        StringBuilder message = new StringBuilder("");
        modelAndView.setViewName("selection-process-view");
        cardHeader = "Dados do processo seletivo";
        modelAndView.addObject("cardHeader", cardHeader);
        setStatusErrors(false);
        setUploadCorrectAnswers(true);

        if (result.hasErrors()) {
            setStatusErrors(true);
            setUploadCorrectAnswers(false);
            message.append("<strong>Campos obrigatórios devem ser preenchidos.</strong>");
        }

        if(!mainService.validateFile(exam.getFileUploaded(), Constants.FILE_TEXT_EXTENSION)){
            setStatusErrors(true);
            setUploadCorrectAnswers(false);
            message.append("<strong>Falha na importação do arquivo</strong>");
            message.append("<br>");
            message.append("O arquivo não pode ser vazio ou diferente do formato 'txt'");
            message.append("<br>");
        }

        exam = mainService.extractAnswersFromFile(exam);

        if (exam.getNumberOfQuestions() != null && exam.getAnswers() != null && exam.getAnswers().size() != exam.getNumberOfQuestions()) {
            setStatusErrors(true);
            setUploadCorrectAnswers(false);
            message.append("<strong>Erro na quantidade de respostas no arquivo.</strong>");
            message.append("<br>");
            message.append("<strong>Quantidade experada: </strong> " + exam.getNumberOfQuestions());
            message.append("<br>");
            message.append("<strong>Quantidade no arquivo: </strong> " + exam.getAnswers().size());
            message.append("<br>");
        }

        this.exam = exam;
        modelAndView.addObject("message", message);
        modelAndView.addObject("statusErrors", isStatusErrors());
        modelAndView.addObject("uploadCorrectAnswers", isUploadCorrectAnswers());
        modelAndView.addObject("exam", exam);

        return modelAndView;
    }

    /**
     * Candidates model and view.
     *
     * @param modelAndView the model and view
     * @return the model and view
     */
    @GetMapping("/candidates")
    public ModelAndView candidates(ModelAndView modelAndView) {
        modelAndView.setViewName("candidates-view");
        cardHeader = "Os Candidatos inscritos no processo seletivo";
        modelAndView.addObject("cardHeader", cardHeader);
        modelAndView.addObject("statusErrors", isStatusErrors());
        modelAndView.addObject("uploadCandidates", isUploadCandidates());
        modelAndView.addObject("students", students);
        return modelAndView;
    }

    /**
     * Upload candidates model and view.
     *
     * @param file         the file
     * @param modelAndView the model and view
     * @return the model and view
     */
    @PostMapping("/candidates")
    public ModelAndView uploadCandidates(@RequestParam("file") MultipartFile file, ModelAndView modelAndView) {

        modelAndView.setViewName("candidates-view");
        cardHeader = "Os Candidatos inscritos no processo seletivo";
        modelAndView.addObject("cardHeader", cardHeader);
        String message = "";
        setStatusErrors(false);
        setUploadCandidates(true);

        if(!mainService.validateFile(file, Constants.FILE_CSV_EXTENSION)){
            setStatusErrors(true);
            setUploadCandidates(false);
            message = "<strong>Falha na importação do arquivo</strong>" +
                    "<br>" +
                    "O arquivo não pode ser vazio ou diferente do formato 'csv'";

        }else{

            try {

                this.students = mainService.parseStudentsFromFile(file);

                setStatusErrors(false);
                setUploadCandidates(true);
                modelAndView.addObject("students", students);

            } catch (Exception ex) {

                message = "Ocorreu um erro durante o processamento do arquivo CSV.";
                modelAndView.addObject("message", message);
                setStatusErrors(true);
                modelAndView.addObject("statusErrors", isStatusErrors());
            }
        }

        modelAndView.addObject("statusErrors", isStatusErrors());
        modelAndView.addObject("uploadCandidates", isUploadCandidates());
        modelAndView.addObject("message", message);

        return modelAndView;
    }

    /**
     * Result model and view.
     *
     * @param modelAndView the model and view
     * @return the model and view
     */
    @GetMapping("/result")
    public ModelAndView result(ModelAndView modelAndView) {
        modelAndView.setViewName("result-view");
        cardHeader = "O resultado do processo seletivo";
        modelAndView.addObject("cardHeader", cardHeader);
        return modelAndView;
    }

}
