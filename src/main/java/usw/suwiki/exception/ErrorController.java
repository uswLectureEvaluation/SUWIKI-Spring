package usw.suwiki.exception;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping(value = {"/"}, method= {RequestMethod.GET, RequestMethod.POST})
    public String handleError() {
        return "/index.html";
    }
}