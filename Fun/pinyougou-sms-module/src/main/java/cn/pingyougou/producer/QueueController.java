package cn.pingyougou.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessageOperations;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class QueueController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @RequestMapping("/sendsms")
    public void sendSms(){

        Map map=new HashMap<>();

        map.put("mobile", "13653514922");
        map.put("template_code", "SMS_174025566");
        map.put("sign_name", "艾文平台");
        map.put("param", "{\"code\":\"888888\"}");

        jmsMessagingTemplate.convertAndSend("sms",map);
    }


}
