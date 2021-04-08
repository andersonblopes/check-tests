package com.lm.checktests.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The type Main controller.
 */
@Controller
@Getter
@Setter
@Slf4j
public class MainController {

    /**
     * Index string.
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }
}
