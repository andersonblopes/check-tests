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
     * The Status.
     */
    private boolean status = true;

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
        setStatus(true);
        modelAndView.addObject("cardHeader", cardHeader);
        modelAndView.addObject("students", students);
        modelAndView.addObject("exam", exam);
        modelAndView.addObject("status", isStatus());
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
        setStatus(true);
        modelAndView.setViewName("selection-process-view");
        modelAndView.addObject("cardHeader", cardHeader);
        modelAndView.addObject("exam", this.exam == null ? new Exam() : this.exam);
        modelAndView.addObject("status", isStatus());
        modelAndView.addObject("uploadCorrectAnswers", isUploadCorrectAnswers());
        return modelAndView;
    }

    /**
     * Register selection string.
     *
     * @param exam   the exam
     * @param result the result
     * @param model  the model
     * @return the string
     * @throws IOException the io exception
     */
    @PostMapping("/selection")
    public String registerSelection(@Valid @ModelAttribute("exam") Exam exam, BindingResult result, Model model) throws IOException {

        if (result.hasErrors()) {
            cardHeader = "Dados do processo seletivo";
            setStatus(true);
            model.addAttribute("cardHeader", cardHeader);
            model.addAttribute("status", isStatus());
            model.addAttribute("uploadCorrectAnswers", isUploadCorrectAnswers());
            return "selection-process-view";
        }

        if(!mainService.validateFile(exam.getFileUploaded(), Constants.FILE_TEXT_EXTENSION)){
            cardHeader = "Dados do processo seletivo";
            model.addAttribute("cardHeader", cardHeader);
            setStatus(false);
            model.addAttribute("status", isStatus());
            model.addAttribute("uploadCorrectAnswers", isUploadCorrectAnswers());
            String message = "<strong>Falha na importação do arquivo</strong>" +
                    "<br>" +
                    "O arquivo não pode ser vazio ou diferente do formato 'txt'";
            model.addAttribute("message", message);
            return "selection-process-view";
        }

        List<Character> answersFromFile = mainService.extractAnswersFromFile(exam);
        if (answersFromFile.size() != exam.getNumberOfQuestions()) {
            cardHeader = "Dados do processo seletivo";
            model.addAttribute("cardHeader", cardHeader);
            setStatus(false);
            model.addAttribute("status", isStatus());
            model.addAttribute("uploadCorrectAnswers", isUploadCorrectAnswers());
            String message = "<strong>Erro na quantidade de respostas no arquivo.</strong>" +
                    "<br>" +
                    "<strong>Quantidade experada:</strong> " + exam.getNumberOfQuestions() +
                    "<br>" +
                    "<strong>Quantidade no arquivo:</strong> " + answersFromFile.size();
            model.addAttribute("message", message);
            return "selection-process-view";
        }
        this.exam = exam;
        cardHeader = "Dados do processo seletivo";
        model.addAttribute("cardHeader", cardHeader);
        setStatus(true);
        setUploadCorrectAnswers(true);
        model.addAttribute("status", isStatus());
        model.addAttribute("uploadCorrectAnswers", isUploadCorrectAnswers());

        model.addAttribute("cardHeader", cardHeader);
        model.addAttribute("students", students);
        model.addAttribute("exam", exam);
        model.addAttribute("uploadCandidates", isUploadCandidates());
        model.addAttribute("uploadCandidatesAnswers", isUploadCandidatesAnswers());

        return "index";
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
