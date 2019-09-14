package com.pinyougou.page.service.impl;


import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.misc.Contended;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

@Component
public class PageListener  implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {

        ObjectMessage objectMessage = (ObjectMessage)message;
        try {
            Long[] ids = (Long[]) objectMessage.getObject();
            for (Long id : ids) {
                boolean result = itemPageService.genItemHtml(id);
                System.out.println("静态页面生成是否成功：？"+result);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
