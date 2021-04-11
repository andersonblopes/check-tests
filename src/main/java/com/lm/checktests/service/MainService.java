package com.lm.checktests.service;

import com.lm.checktests.model.Exam;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Main service.
 */
@Service
public class MainService {

    /**
     * Extract answers from file list.
     *
     * @param exam the exam
     * @return the list
     * @throws IOException the io exception
     */
    public List<Character> extractAnswersFromFile(Exam exam) throws IOException {
        File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + exam.getFileUploaded().getName() + "_" + RandomStringUtils.randomAlphabetic(10));
        exam.getFileUploaded().transferTo(tempFile);

        BufferedReader br = new BufferedReader(new FileReader(tempFile));

        int readIndex;
        List<Character> answersFromFile = new ArrayList<>();
        while ((readIndex = br.read()) != -1) {
            char ch = (char) readIndex;
            answersFromFile.add(ch);
        }

        return answersFromFile;
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
}
