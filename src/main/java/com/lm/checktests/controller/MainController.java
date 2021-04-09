package com.lm.checktests.controller;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

/**
 * The type Main controller.
 */
@Controller
@Data
@Slf4j
public class MainController {

    private String cardHeader;

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
        modelAndView.addObject("cardHeader", cardHeader);
        return modelAndView;
    }

    /**
     * Selection model and view.
     *
     * @param modelAndView the model and view
     * @return the model and view
     */
    @GetMapping("/selection")
    public ModelAndView selection(ModelAndView modelAndView){
        modelAndView.setViewName("selection-process-view");
        cardHeader = "Dados do processo seletivo";
        modelAndView.addObject("cardHeader", cardHeader);
        return modelAndView;
    }

    /**
     * Candidates model and view.
     *
     * @param modelAndView the model and view
     * @return the model and view
     */
    @GetMapping("/candidates")
    public ModelAndView candidates(ModelAndView modelAndView){
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
    public ModelAndView result(ModelAndView modelAndView){
        modelAndView.setViewName("result-view");
        cardHeader = "O resultado do processo seletivo";
        modelAndView.addObject("cardHeader", cardHeader);
        return modelAndView;
    }

}
