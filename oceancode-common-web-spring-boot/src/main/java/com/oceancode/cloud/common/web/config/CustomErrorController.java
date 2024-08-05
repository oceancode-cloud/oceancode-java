package com.oceancode.cloud.common.web.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class CustomErrorController implements ErrorController {

    @Value("${oc.web.error.redirect:/}")
    private String errorHome;

    @GetMapping("/error")
    public String handleError(HttpServletResponse response) {
        return "redirect:" + errorHome;
    }
}
