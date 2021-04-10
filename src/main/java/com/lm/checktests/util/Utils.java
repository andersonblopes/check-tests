package com.lm.checktests.util;

import com.lm.checktests.model.Exam;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Utils.
 */
public class Utils {

    /**
     * Validate file boolean.
     *
     * @param file the file
     * @return the boolean
     */
    public static boolean validateFile(MultipartFile file) {
        return !file.isEmpty();
    }

    /**
     * Read txt list.
     *
     * @param exam the exam
     * @return the list
     * @throws IOException the io exception
     */
    public static List<Character> readTxt(Exam exam) throws IOException {

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
}
