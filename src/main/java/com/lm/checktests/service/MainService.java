package com.lm.checktests.service;

import com.lm.checktests.model.Answer;
import com.lm.checktests.model.Constants;
import com.lm.checktests.model.Exam;
import com.lm.checktests.model.ExamResult;
import com.lm.checktests.model.ExamStatus;
import com.lm.checktests.model.Student;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Main service.
 */
@Slf4j
@Getter
@Setter
@Service
public class MainService {

    /**
     * The Students.
     */
    private List<Student> students;

    /**
     * The Student answers.
     * <p>
     * This map is used to ensure ignore duplicate lines from the file.
     */
    private Map<String, List<Answer>> studentAnswers;

    /**
     * The Unknown student.
     */
    private List<String> unknownStudent;

    /**
     * The Exam result list.
     */
    private List<ExamResult> examResultList;

    /**
     * Extract answers from file exam.
     *
     * @param exam the exam
     * @return the exam
     * @throws IOException the io exception
     */
    public Exam extractAnswersFromFile(Exam exam) throws IOException {

        log.info("Invoking extractAnswersFromFile");
        log.info("Exam: {}", exam.getTitle());

        File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + exam.getFileUploaded().getName() + "_" + RandomStringUtils.randomAlphabetic(10));
        exam.getFileUploaded().transferTo(tempFile);

        BufferedReader br = new BufferedReader(new FileReader(tempFile));

        int readIndex;
        List<Answer> answersFromFile = new ArrayList<>();
        while ((readIndex = br.read()) != -1) {
            char ch = (char) readIndex;
            Answer answer = new Answer();
            answer.setQuestionNumber(answersFromFile.size() + 1);
            answer.setQuestionAnswer(ch);
            answer.setValid(ch != Constants.INVALID_QUESTION);
            answersFromFile.add(answer);
        }

        exam.setAnswers(answersFromFile);

        return exam;
    }

    /**
     * Validate file boolean.
     *
     * @param file      the file
     * @param extension the extension
     * @return the boolean
     */
    public boolean validateFile(MultipartFile file, String extension) {
        log.info("Invoking validateFile");
        log.info("File: {}", file.getName());
        log.info("Extension: {}", extension);

        return !file.isEmpty() && getFileExtension(file.getOriginalFilename()).equalsIgnoreCase(extension);
    }

    /**
     * Gets file extension.
     *
     * @param filename the filename
     * @return the file extension
     */
    private String getFileExtension(String filename) {
        log.info("Invoking getFileExtension");
        log.info("Filename: {}", filename);

        if (filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    /**
     * Parse students from file list.
     *
     * @param file the file
     * @return the list
     * @throws IOException the io exception
     */
    public List<Student> parseStudentsFromFile(MultipartFile file) throws IOException {
        log.info("Invoking parseStudentsFromFile");
        log.info("Filename: {}", file.getName());

        // TODO: save students to database

        // parse CSV file to create a list of `Student` objects
        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));

        // create csv bean reader
        CsvToBean<Student> csvToBean = new CsvToBeanBuilder(reader).withType(Student.class)
                .withIgnoreLeadingWhiteSpace(true).withSeparator(';').build();

        // convert `CsvToBean` object to list of students
        students = csvToBean.parse();

        return students;
    }

    /**
     * Extract student answers from file.
     *
     * @param file the file
     * @param exam the exam
     * @throws IOException the io exception
     */
    public void extractStudentAnswersFromFile(MultipartFile file, Exam exam) throws IOException {
        log.info("Invoking extractStudentAnswersFromFile");
        log.info("Filename: {}", file.getName());
        log.info("Exam: {}", exam.getTitle());

        List<String> lines = new ArrayList<>();
        unknownStudent = new ArrayList<>();

        File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + "_" + RandomStringUtils.randomAlphabetic(10));

        file.transferTo(tempFile);

        if (tempFile.exists()) {
            lines = Files.readAllLines(tempFile.toPath(),
                    Charset.defaultCharset());
        }

        if (!lines.isEmpty()) {
            studentAnswers = new HashMap<>();
            for (String line : lines) {
                String[] res = line.split("\n");
                String inscription = res[0].substring(0, Constants.DIGITS_AMOUNT_FROM_STUDENT_INSCRIPTION);
                String answerStr = res[0].substring(Constants.DIGITS_AMOUNT_FROM_STUDENT_INSCRIPTION);

                if (!studentExists(inscription)) {
                    unknownStudent.add(inscription);
                }
                studentAnswers.put(inscription, extractAnswersFromString(answerStr, exam.getNumberOfQuestions()));
            }
        }
    }

    /**
     * Student exists boolean.
     *
     * @param inscription the inscription
     * @return the boolean
     */
    private boolean studentExists(String inscription) {
        log.info("Invoking studentExists");
        log.info("Inscription: {}", inscription);

        for (Student s : students) {
            if (s.getMatricula().equalsIgnoreCase(inscription)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extract answers from string list.
     *
     * @param answersLine         the answers line
     * @param amountAnswersToRead the amount answers to read
     * @return the list
     */
    private List<Answer> extractAnswersFromString(String answersLine, int amountAnswersToRead) {
        log.info("Invoking extractAnswersFromString");

        List<Answer> answersStudent = new ArrayList<>();
        for (int i = 0; i < amountAnswersToRead; i++) {
            char ch = answersLine.charAt(i);
            Answer answer = new Answer();
            answer.setQuestionNumber(i + 1);
            answer.setQuestionAnswer(ch);
            answersStudent.add(answer);
        }
        return answersStudent;
    }

    /**
     * Process result list.
     *
     * @param exam the exam
     * @return the list
     */
    public List<ExamResult> processResult(Exam exam) {
        log.info("Invoking processResult");
        log.info("Exam: {}", exam.getTitle());

        examResultList = new ArrayList<>();
        if (studentAnswers != null) {
            for (Map.Entry<String, List<Answer>> studentAnswers : studentAnswers.entrySet()) {
                ExamResult examResult = new ExamResult();
                examResult.setExam(exam);
                examResult.setStudent(recoverStudentByMatricula(studentAnswers.getKey()));
                examResult.setStudentAnswers(studentAnswers.getValue());
                examResult.setAverage(calcAverageFromStudent(examResult));
                examResult.setStatus(ExamStatus.NOT_CLASSIFIED);
                examResultList.add(examResult);
            }
        }

        sortExamResults(examResultList, exam);

        return examResultList;
    }

    /**
     * Sort exam results.
     *
     * @param examResultList the exam result list
     * @param exam           the exam
     */
    private void sortExamResults(List<ExamResult> examResultList, Exam exam) {
        log.info("Invoking sortExamResults");
        log.info("Exam: {}", exam.getTitle());

        // Order by average
        Collections.sort(examResultList, Collections.reverseOrder());

        for (int i = 0; i < examResultList.size(); i++) {
            examResultList.get(i).setPosition(i + 1);
            if (i < exam.getNumberOfApproved()) {
                examResultList.get(i).setStatus(ExamStatus.APPROVED);
            } else if (i >= exam.getNumberOfApproved() && i < (exam.getNumberOfApproved() + exam.getNumberWaitQueue())) {
                examResultList.get(i).setStatus(ExamStatus.WAITING);
            }
        }

    }

    /**
     * Calc average from student double.
     *
     * @param examResult the exam result
     * @return the double
     */
    private Double calcAverageFromStudent(ExamResult examResult) {
        log.info("Invoking calcAverageFromStudent");

        Integer amountStudentCorrectAnswers = 0;
        List<Answer> rightAnswers = examResult.getExam().getAnswers();
        List<Answer> studentAnswers = examResult.getStudentAnswers();
        for (int i = 0; i < examResult.getExam().getNumberOfQuestions(); i++) {
            if (rightAnswers.get(i).equals(studentAnswers.get(i))) {
                amountStudentCorrectAnswers += 1;
            } else {
                studentAnswers.get(i).setValid(false);
            }
        }

        Double average = amountStudentCorrectAnswers.doubleValue() * 100 / examResult.getExam().getNumberOfQuestions().doubleValue();

        return average;
    }

    /**
     * Recover student by matricula student.
     *
     * @param studentMatricula the student matricula
     * @return the student
     */
    private Student recoverStudentByMatricula(String studentMatricula) {
        log.info("Invoking recoverStudentByMatricula");
        log.info("Student Matricula: {}", studentMatricula);

        Student studentFound = null;
        for (Student student : students) {
            String matricula = String.valueOf(student.getMatricula());
            if (matricula.equalsIgnoreCase(studentMatricula)) {
                studentFound = student;
                break;
            }
        }
        return studentFound;
    }


    /**
     * Gets exam result from list.
     *
     * @param inscription    the inscription
     * @param examResultList the exam result list
     * @return the exam result from list
     */
    public ExamResult getExamResultFromList(String inscription, List<ExamResult> examResultList) {
        log.info("Invoking getExamResultFromList");
        log.info("Inscription: {}", inscription);

        ExamResult examResultFound = null;

        if (examResultList != null && !examResultList.isEmpty()) {
            for (ExamResult examResult : examResultList) {
                if (examResult.getStudent().getInscricao().equalsIgnoreCase(inscription)) {
                    examResultFound = examResult;
                    break;
                }
            }
        }

        return examResultFound;
    }

}
