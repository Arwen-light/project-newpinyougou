package com.pinyougou.order.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;
import com.pinyougou.order.service.OrderService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbOrder> findAll() {
        return orderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    /**
     * 增加
     */
    @Override
    public void add(TbOrder order) {

        // 从购物车中获取订单列表
        String userId = order.getUserId();
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(userId);
        System.out.println("购物车列表为" + cartList);
        for (Cart cart : cartList) {
            // 创建一个新的订单对象 和 cart 购物车
            TbOrder finalOrder = new TbOrder();
            long orderId = idWorker.nextId();
            String sellerId = cart.getSellerId();
            System.out.println("商家Id" + sellerId);
            finalOrder.setOrderId(orderId);
            finalOrder.setUserId(userId);
            finalOrder.setPaymentType(order.getPaymentType());
            finalOrder.setStatus("1");
            finalOrder.setUpdateTime(new Date());
            finalOrder.setCreateTime(new Date());
            finalOrder.setReceiverAreaName(order.getReceiverAreaName());
            finalOrder.setReceiverMobile(order.getReceiverMobile());
            finalOrder.setReceiver(order.getReceiver());
            finalOrder.setSourceType(order.getSourceType());
            finalOrder.setSellerId(cart.getSellerId());

            // 循环购物车明细，构成订单明细对象存入orderItem 中
            double totalMoney = 0;

            for (TbOrderItem tbOrderItem : cart.getOrderItemList()) {
                // 创建购物车明细对象
                TbOrderItem orderItem = new TbOrderItem();
                long orderItemId = idWorker.nextId();
                // 设置参数

                orderItem.setId(orderItemId);
                orderItem.setItemId(tbOrderItem.getItemId());
                orderItem.setOrderId(orderId);
                orderItem.setSellerId(cart.getSellerId());
                orderItem.setGoodsId(tbOrderItem.getGoodsId());
                orderItem.setPicPath(tbOrderItem.getPicPath());
                orderItem.setNum(tbOrderItem.getNum());
                orderItem.setPrice(tbOrderItem.getPrice());
                orderItem.setTotalFee(tbOrderItem.getTotalFee());
                orderItem.setTitle(tbOrderItem.getTitle());
                totalMoney += tbOrderItem.getTotalFee().doubleValue();

                // 购物车明细列表对象存储
                orderItemMapper.insert(orderItem);
            }
            finalOrder.setPayment(new BigDecimal(totalMoney));
            orderMapper.insert(finalOrder);
        }
        redisTemplate.boundHashOps("cartList").delete(userId);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbOrder order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbOrder findOne(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            orderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();

        if (order != null) {
            if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
                criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
            }
            if (order.getPostFee() != null && order.getPostFee().length() > 0) {
                criteria.andPostFeeLike("%" + order.getPostFee() + "%");
            }
            if (order.getStatus() != null && order.getStatus().length() > 0) {
                criteria.andStatusLike("%" + order.getStatus() + "%");
            }
            if (order.getShippingName() != null && order.getShippingName().length() > 0) {
                criteria.andShippingNameLike("%" + order.getShippingName() + "%");
            }
            if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
                criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
            }
            if (order.getUserId() != null && order.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + order.getUserId() + "%");
            }
            if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
                criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
            }
            if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
                criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
            }
            if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
                criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
            }
            if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
                criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
            }
            if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
            }
            if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
                criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
            }
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + order.getReceiver() + "%");
            }
            if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
                criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
            }
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {
                criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
            }
            if (order.getSellerId() != null && order.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + order.getSellerId() + "%");
            }
        }
        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

}
