package com.lm.checktests.service;

import com.lm.checktests.model.Answer;
import com.lm.checktests.model.Constants;
import com.lm.checktests.model.Exam;
import com.lm.checktests.model.Student;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Main service.
 */
@Slf4j
@Service
public class MainService {

    /**
     * The Student answers.
     */
    private Map<String, List<Answer>> studentAnswers;

    /**
     * Extract answers from file list.
     *
     * @param exam the exam
     * @return the list
     * @throws IOException the io exception
     */
    public Exam extractAnswersFromFile(Exam exam) throws IOException {
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
        return !file.isEmpty() && getFileExtension(file.getOriginalFilename()).equalsIgnoreCase(extension);
    }

    /**
     * Gets file extension.
     *
     * @param filename the filename
     * @return the file extension
     */
    private String getFileExtension(String filename) {
        if (filename.contains("."))
            return filename.substring(filename.lastIndexOf(".") + 1);
        else
            return "";
    }

    /**
     * Parse students from file list.
     *
     * @param file the file
     * @return the list
     * @throws IOException the io exception
     */
    public List<Student> parseStudentsFromFile(MultipartFile file) throws IOException {
        // TODO: save students to database

        // parse CSV file to create a list of `Student` objects
        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));

        // create csv bean reader
        CsvToBean<Student> csvToBean = new CsvToBeanBuilder(reader).withType(Student.class)
                .withIgnoreLeadingWhiteSpace(true).withSeparator(';').build();

        // convert `CsvToBean` object to list of students
        List<Student> students = csvToBean.parse();

        return students;
    }

    /**
     * Extract student answers from file.
     *
     * @param file the file
     * @throws IOException the io exception
     */
    public void extractStudentAnswersFromFile(MultipartFile file) throws IOException {
        List<String> lines = new ArrayList<>();

        File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + "_" + RandomStringUtils.randomAlphabetic(10));

        file.transferTo(tempFile);

        if(tempFile.exists()){
            lines = Files.readAllLines(tempFile.toPath(),
                    Charset.defaultCharset());
        }

        if(!lines.isEmpty()){
            studentAnswers = new HashMap<>();
            for(String line : lines){
                String [] res = line.split("\n");
                String inscription = res[0].substring(0, Constants.DIGITS_AMOUNT_FROM_STUDENT_INSCRIPTION);
                String answerStr = res[0].substring(Constants.DIGITS_AMOUNT_FROM_STUDENT_INSCRIPTION);
                studentAnswers.put(inscription, extractAnswersFromString(answerStr));
            }
        }
    }

    /**
     * Extract answers from string list.
     *
     * @param answersLine the answers line
     * @return the list
     */
    private List<Answer> extractAnswersFromString(String answersLine){
        List<Answer> answersStudent = new ArrayList<>();
        for(int i = 0; i < answersLine.length(); i++) {
            char ch = (char) answersLine.charAt(i);;
            Answer answer = new Answer();
            answer.setQuestionNumber(i + 1);
            answer.setQuestionAnswer(ch);
            answersStudent.add(answer);
        }
        return answersStudent;
    }

}
