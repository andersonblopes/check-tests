package com.lm.checktests.controller;

import com.lm.checktests.model.Constants;
import com.lm.checktests.model.Exam;
import com.lm.checktests.model.ExamResult;
import com.lm.checktests.model.Student;
import com.lm.checktests.service.MainService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
     * The Upload candidates answers.
     */
    private boolean uploadCandidatesAnswers;

    /**
     * The Process done.
     */
    private boolean processDone;

    /**
     * The Students.
     */
    private List<Student> students = new ArrayList<>();

    /**
     * The Exam result list.
     */
    private List<ExamResult> examResultList = new ArrayList<>();

    /**
     * The Exam.
     */
    private Exam exam;

    /**
     * The Message.
     */
    private String message = "";

    /**
     * The Main service.
     */
    @Autowired
    private MainService mainService;

    /**
     * The Exam result.
     */
    private ExamResult examResult;

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

        modelAndView = prepareModelAndView(modelAndView);

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
        exam = null;
        examResult = null;
        students = new ArrayList<>();
        examResultList = new ArrayList<>();
        setStatusErrors(false);
        setUploadCorrectAnswers(false);
        setUploadCandidates(false);
        setUploadCandidatesAnswers(false);
        setProcessDone(false);

        modelAndView = prepareModelAndView(modelAndView);

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
        modelAndView.setViewName("selection-process-view");
        cardHeader = "Dados do processo seletivo";
        setStatusErrors(false);
        exam = exam == null ? new Exam() : exam;

        modelAndView = prepareModelAndView(modelAndView);

        return modelAndView;
    }

    /**
     * Register selection model and view.
     *
     * @param exam         the exam
     * @param result       the result
     * @param modelAndView the model and view
     * @return the model and view
     * @throws IOException the io exception
     */
    @PostMapping("/selection")
    public ModelAndView registerSelection(@Valid @ModelAttribute("exam") Exam exam, BindingResult result, ModelAndView modelAndView) throws IOException {

        StringBuilder message = new StringBuilder();
        modelAndView.setViewName("selection-process-view");
        cardHeader = "Dados do processo seletivo";
        setStatusErrors(false);
        setUploadCorrectAnswers(true);

        if (result.hasErrors()) {
            setStatusErrors(true);
            setUploadCorrectAnswers(false);
            message.append("<strong>Campos obrigatórios devem ser preenchidos.</strong>");
        }

        if (!mainService.validateFile(exam.getFileUploaded(), Constants.FILE_TEXT_EXTENSION)) {
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

        this.message = message.toString();
        this.exam = exam;

        modelAndView = prepareModelAndView(modelAndView);

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
        cardHeader = "Candidatos inscritos no concurso";
        modelAndView = prepareModelAndView(modelAndView);

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
        cardHeader = "Candidatos inscritos no concurso";
        setStatusErrors(false);
        setUploadCandidates(true);

        if (!mainService.validateFile(file, Constants.FILE_CSV_EXTENSION)) {
            setStatusErrors(true);
            setUploadCandidates(false);
            message = "<strong>Falha na importação do arquivo</strong>" +
                    "<br>" +
                    "O arquivo não pode ser vazio ou diferente do formato 'csv'";

        } else {
            try {
                students = mainService.parseStudentsFromFile(file);
                setStatusErrors(false);
                setUploadCandidates(true);

            } catch (Exception ex) {
                message = "Ocorreu um erro durante o processamento do arquivo CSV.";
                setStatusErrors(true);
            }
        }

        modelAndView = prepareModelAndView(modelAndView);

        return modelAndView;
    }

    /**
     * Upload candidate answers model and view.
     *
     * @param file         the file
     * @param modelAndView the model and view
     * @return the model and view
     */
    @PostMapping("/candidates-answers")
    public ModelAndView uploadCandidateAnswers(@RequestParam("file") MultipartFile file, ModelAndView modelAndView) {
        modelAndView.setViewName("index");
        cardHeader = "Apuração das respostas dos candidatos";
        setUploadCandidatesAnswers(true);
        setStatusErrors(false);

        if (!mainService.validateFile(file, Constants.FILE_TEXT_EXTENSION)) {
            setStatusErrors(true);
            setUploadCandidatesAnswers(false);
            message = "<strong>Falha na importação do arquivo</strong>" +
                    "<br>" +
                    "O arquivo não pode ser vazio ou diferente do formato 'txt'";
        } else {
            try {

                mainService.extractStudentAnswersFromFile(file, exam);

            } catch (Exception e) {
                message = "Ocorreu um erro durante o processamento do arquivo TXT.";
                modelAndView.addObject("message", message);
                setStatusErrors(true);
                setUploadCandidatesAnswers(false);
                modelAndView.addObject("statusErrors", isStatusErrors());
            }
        }

        modelAndView = prepareModelAndView(modelAndView);
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
        cardHeader = "Resultado do Concurso: " + exam.getTitle().toUpperCase();
        modelAndView = prepareModelAndView(modelAndView);

        return modelAndView;
    }

    /**
     * Process result model and view.
     *
     * @param modelAndView the model and view
     * @return the model and view
     */
    @PostMapping("/result")
    public ModelAndView processResult(ModelAndView modelAndView) {

        modelAndView.setViewName("result-view");
        cardHeader = "Resultado do Concurso: " + exam.getTitle().toUpperCase();
        examResultList = mainService.processResult(exam);
        setProcessDone(true);
        modelAndView = prepareModelAndView(modelAndView);

        return modelAndView;
    }

    /**
     * Show result candidate details model and view.
     *
     * @param inscription  the inscription
     * @param modelAndView the model and view
     * @return the model and view
     */
    @GetMapping("/result/candidate/{inscription}")
    public ModelAndView showResultCandidateDetails(@PathVariable("inscription") String inscription, ModelAndView modelAndView) {
        modelAndView.setViewName("result-detail-view");

        examResult = mainService.getExamResultFromList(inscription, examResultList);

        cardHeader = "Detalhes da prova do candidato: " + examResult.getStudent().getInscricao();

        modelAndView = prepareModelAndView(modelAndView);

        return modelAndView;
    }

    /**
     * Upload candidate answers model and view.
     *
     * @param modelAndView the model and view
     * @return the model and view
     */
    @GetMapping("/candidates-answers")
    public ModelAndView uploadCandidateAnswers(ModelAndView modelAndView) {
        modelAndView.setViewName("upload-candidates-answers-view");
        cardHeader = "Apuração das respostas dos candidatos";
        modelAndView = prepareModelAndView(modelAndView);

        return modelAndView;
    }

    /**
     * Prepare model and view model and view.
     *
     * @param modelAndView the model and view
     * @return the model and view
     */
    private ModelAndView prepareModelAndView(ModelAndView modelAndView) {

        modelAndView.addObject("cardHeader", getCardHeader());
        modelAndView.addObject("uploadCandidatesAnswers", isUploadCandidatesAnswers());
        modelAndView.addObject("exam", getExam());
        modelAndView.addObject("statusErrors", isStatusErrors());
        modelAndView.addObject("message", getMessage());
        modelAndView.addObject("results", getExamResultList());
        modelAndView.addObject("students", getStudents());
        modelAndView.addObject("uploadCandidates", isUploadCandidates());
        modelAndView.addObject("uploadCorrectAnswers", isUploadCorrectAnswers());
        modelAndView.addObject("processDone", isProcessDone());
        modelAndView.addObject("examResult", getExamResult());


        return modelAndView;
    }

}
