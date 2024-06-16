package com.oceancode.cloud.common.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
public class CustomErrorController implements ErrorController {

    @Value("${oc.web.error.redirect:/}")
    private String errorHome;

    @GetMapping("/error")
    public String handleError(HttpServletResponse response) {
        return "redirect:" + errorHome;
    }
}
