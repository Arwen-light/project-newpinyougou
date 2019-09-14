package cn.sms.producer;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Demo {

    @RequestMapping("info")
    public String demoHelloworld(){
        System.out.println("Helloworld");
        return "String: HelloWorld";
    }

}
