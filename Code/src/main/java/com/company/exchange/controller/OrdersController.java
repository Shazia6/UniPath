package com.company.exchange.controller;

import com.company.exchange.pojo.*;
import com.company.exchange.service.GoodsService;
import com.company.exchange.service.OrdersService;
import com.company.exchange.service.PurseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
@RequestMapping(value = "/orders")
public class OrdersController {

    @Resource
    private OrdersService ordersService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private PurseService purseService;

    ModelAndView mv = new ModelAndView();


    @RequestMapping(value = "/myOrders")
    public ModelAndView orders(HttpServletRequest request) {
        User cur_user = (User) request.getSession().getAttribute("cur_user");
        Integer user_id = cur_user.getId();
        int currentPage=1;
        if(null==request.getParameter("currentPage")||"".equals(request.getParameter("currentPage"))){
            currentPage=1;
        }else{
            currentPage=Integer.valueOf(request.getParameter("currentPage"));
        }

        int pageNum=3;
        if(null==request.getParameter("pageNum")||"".equals(request.getParameter("pageNum"))){
            pageNum=3;
        }else{
            pageNum=Integer.valueOf(request.getParameter("pageNum"));
        }



        //List<Orders> ordersList1 = new ArrayList<Orders>();
        //List<Orders> ordersList2 = new ArrayList<Orders>();
//        ordersList1 = ordersService.getOrdersByUserId(user_id);
       PageInfo<Orders> buyPageOrdersInfo =ordersService.getPageOrdersByUserId(user_id,currentPage,pageNum);
        PageInfo<Orders> sellPageOrdersInfo =ordersService.getSellerPageOrdersByUserId(user_id,currentPage,pageNum);
       // ordersList2 = ordersService.getOrdersByUserAndGoods(user_id);
        Purse myPurse = purseService.getPurseByUserId(user_id);
        //mv.addObject("ordersOfSell", ordersList2);
        //mv.addObject("orders", ordersList1);
        mv.addObject("pageOrdersInfo", buyPageOrdersInfo);
        mv.addObject("sellPageOrdersInfo", sellPageOrdersInfo);
        mv.addObject("myPurse", myPurse);
        mv.setViewName("/user/orders");
        return mv;
    }


    @RequestMapping(value = "/addOrders")
    public String addorders(HttpServletRequest request, Orders orders) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        User cur_user = (User) request.getSession().getAttribute("cur_user");
        Integer user_id = cur_user.getId();
        orders.setUserId(user_id);
        orders.setOrderDate(sdf.format(d));
        Goods goods = new Goods();
        goods.setStatus(0);
        goods.setId(orders.getGoodsId());
        goodsService.updateGoodsByGoodsId(goods);
        ordersService.addOrders(orders);
        Float balance = orders.getOrderPrice();
        purseService.updatePurseOfdel(user_id, balance);
        return "redirect:/orders/myOrders";
    }

    @RequestMapping(value = "/deliver/{orderNum}")
    public String deliver(HttpServletRequest request, @PathVariable("orderNum") Integer orderNum) {
        ordersService.deliverByOrderNum(orderNum);
        return "redirect:/orders/myOrders";
    }


    @RequestMapping(value = "/receipt")
    public String receipt(HttpServletRequest request) {
        Integer orderNum = Integer.parseInt(request.getParameter("orderNum"));
        Float balance = Float.parseFloat(request.getParameter("orderPrice"));
        Integer goodsId = Integer.parseInt(request.getParameter("goodsId"));
        Integer userId = goodsService.getGoodsById(goodsId).getUserId();
        ordersService.receiptByOrderNum(orderNum);
        purseService.updatePurseByuserId(userId, balance);

        return "redirect:/orders/myOrders";
    }

}
