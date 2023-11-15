package org.jasonf;

import org.jasonf.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author jasonf
 * @Date 2023/11/15
 * @Description
 */

@RestController
@RequestMapping("XQ")
public class HelloController {
    @Reference
    private Hello obj;

    @GetMapping("hello")
    public String hello() {
        return obj.greet("XQ");
    }
}
