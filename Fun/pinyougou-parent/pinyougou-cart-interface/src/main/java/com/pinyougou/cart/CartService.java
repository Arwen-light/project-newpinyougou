package com.pinyougou.cart;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

public interface CartService {

    /**
     * 添加商品到购物车
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num );

    /**
     *  根据小key 获取存储的hash 存存储的值
     * @param username
     * @return
     */

    public List<Cart> findCartListFromRedis(String username);

    /**
     *  根据用户名和购物车列表数据存入redies 中
     * @param username
     * @param cartList
     * @return
     */
    public void saveCartListToRedis ( String username, List<Cart> cartList );


    /**
     *  合并cookie and Redis 中的购物车数据
     * @param carListCookie
     * @param cartListRedis
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> carListCookie,List<Cart> cartListRedis);

}
