package com.pinyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.CartService;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utils.CookieUtil;
import entity.Result;
import org.opensaml.xml.signature.J;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    // 获取cookie 中购物车列表
    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {

        // 尝试获取登录用户的名称
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登陆的用户名是：" + username);

        // 从cookie 中获得购物车列表
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartListString == null || cartListString.equals("")) {
            cartListString = "[]";
        }
        ;


        List<Cart> cartListCookie = JSON.parseArray(cartListString, Cart.class);

        if (username.equals("anonymousUser")) {  // 用户未登录
            return cartListCookie;
        } else {  // 用户已经登录

            // 从Redies 中获得购物车的列表
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(username);

            if (cartListCookie.size() > 0) {
                // 合并cookie 和 redis 中的购物车内容
                cartListFromRedis = cartService.mergeCartList(cartListCookie, cartListFromRedis);
                // 并且将合并后的购物车的重新存放入Redis 中
                System.out.println("并且将合并后的购物车的重新存放入Redis");
                cartService.saveCartListToRedis(username, cartListFromRedis);
                // 清空旧的cookie 中的CartList
                CookieUtil.deleteCookie(request, response, "cartList");
            }

            System.out.println("测试从Redis 中获得" + cartListFromRedis);
            return cartListFromRedis;

        }


    }


    // 添加sku to 购物车

    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num) {

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // 尝试获取登录用户的名称
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登陆的用户名是：" + username);


        try {
            List<Cart> cartList = findCartList();
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);

            if (username.equals("anonymousUser")) {  // 未登录
                // 存储到cookies 中
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList),
                        60 * 60, "UTF-8");
                System.out.println(" 存储到cookies 中" + username);
            } else {  // 已登录
                // 存到到Redis 中
                cartService.saveCartListToRedis(username, cartList);
                System.out.println("存到到Redis 中" + username);
            }
            return new Result(true, "成功添加到购物车");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }

    }

    ;


    @RequestMapping("/loginName")
    public Map showName() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();//登陆人账号
        Map map = new HashMap<>();
        map.put("loginName", name);
        return map;
    }

}
