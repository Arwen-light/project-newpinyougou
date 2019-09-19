package com.pinyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.utils.IdWorker;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private OrderService orderService;


    /**
     * 生成二维码
     *
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        //获取当前用户
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

          //     1.测试调用服务层微信统一下单api ，返回需要的数据
            IdWorker idworker = new IdWorker();
            return weixinPayService.createNative(String.valueOf(idworker.nextId()), "1");


          /*  //  2.到redis查询支付日志，调用服务层微信统一下单api ，返回需要的数据
            TbPayLog payLog = orderService.searchPayLogFromRedis(userId);
            if (payLog != null) {
                //IdWorker idworker = new IdWorker();
                return weixinPayService.createNative(payLog.getOutTradeNo(), String.valueOf(payLog.getTotalFee()));
            } else {
                return new HashMap();
            }*/
    }


    /**
     * 查询支付状态
     *
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        Result result = null;

        // 获取系统但前得时间  微信支付超时（5分钟内没有支付，认定支付超时）

        Long start = System.currentTimeMillis();

        while (true) {
            //调用查询接口
            Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no);
            if (map == null) {//出错
                result = new Result(false, "支付出错");
                break;
            }
            if (map.get("trade_state").equals("SUCCESS")) {//如果成功
                result = new Result(true, "支付成功");

                //修改订单状态
                orderService.updateOrderStatus(out_trade_no, map.get("transaction_id"));

                break;
            }
            try {
                Thread.sleep(3000);//间隔三秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 获取系统每次执行后，当时的时间戳
            Long end = System.currentTimeMillis();

            if ((end - start) >= 5 * 60 * 1000) {
                System.out.println("5分钟内没有支付，系统此次订单超时");
                result = new Result(false, "二维码支付超时");
                break;
            }

        }

        return result;
    }
}
