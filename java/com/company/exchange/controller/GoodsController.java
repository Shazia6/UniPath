package com.company.exchange.controller;

import com.company.exchange.pojo.*;
import com.company.exchange.service.*;
import com.company.exchange.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
@RequestMapping(value = "/goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private CatelogService catelogService;
    @Autowired
    private UserService userService;
    @Resource
    private PurseService purseService;

    @Autowired
    private CommentService commentService;
    @Autowired
    private PhoneService phoneService;

    /**

     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/homeGoods")
    public ModelAndView homeGoods() throws Exception {
        ModelAndView modelAndView = new ModelAndView();

        int catelogSize = 8;

        int goodsSize = 6;

        List<Goods> goodsList = null;
        List<GoodsExtend> goodsAndImage = null;
        List<User> userList = null;


        userList = userService.getUserOrderByDate(goodsSize);
        goodsList = goodsService.getGoodsOrderByDate(goodsSize);
        goodsAndImage = new ArrayList<GoodsExtend>();
        for (int j = 0; j < goodsList.size(); j++) {

            GoodsExtend goodsExtend = new GoodsExtend();
            Goods goods = goodsList.get(j);

            User user = userList.get(j);

            List<Image> images = imageService.getImagesByGoodsPrimaryKey(goods.getId());
            goodsExtend.setGoods(goods);
            goodsExtend.setImages(images);
            goodsExtend.setNum(commentService.getCommentCount(goods.getId()));
            goodsExtend.setPhone(phoneService.getUserPhone(user.getId()));
            goodsAndImage.add(j, goodsExtend);
        }
        String key0 = "catelogGoods";
        modelAndView.addObject(key0, goodsAndImage);


        for (int i = 1; i <= catelogSize; i++) {
            goodsList = goodsService.getGoodsByCatelogOrderByDate(i, goodsSize);
            goodsAndImage = new ArrayList<GoodsExtend>();
            for (int j = 0; j < goodsList.size(); j++) {

                GoodsExtend goodsExtend = new GoodsExtend();
                Goods goods = goodsList.get(j);
                List<Image> images = imageService.getImagesByGoodsPrimaryKey(goods.getId());
                goodsExtend.setGoods(goods);
                goodsExtend.setImages(images);
                goodsAndImage.add(j, goodsExtend);
                goodsExtend.setNum(commentService.getCommentCount(goods.getId()));
            }
            String key = "catelog" + "Goods" + i;
            modelAndView.addObject(key, goodsAndImage);
        }
        modelAndView.setViewName("goods/homeGoods");
        return modelAndView;
    }


    @RequestMapping(value = "/search")
    public ModelAndView searchGoods(@RequestParam(value = "str", required = false) String str) throws Exception {
        List<Goods> goodsList = goodsService.searchGoods(str, str);
        List<GoodsExtend> goodsExtendList = new ArrayList<GoodsExtend>();
        for (int i = 0; i < goodsList.size(); i++) {
            GoodsExtend goodsExtend = new GoodsExtend();
            Goods goods = goodsList.get(i);
            List<Image> imageList = imageService.getImagesByGoodsPrimaryKey(goods.getId());
            goodsExtend.setGoods(goods);
            goodsExtend.setImages(imageList);
            goodsExtendList.add(i, goodsExtend);
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goodsExtendList", goodsExtendList);
        modelAndView.addObject("search", str);
        modelAndView.setViewName("/goods/searchGoods");
        return modelAndView;
    }


    @RequestMapping(value = "/catelog")
    public ModelAndView homeGoods(HttpServletRequest request, @RequestParam(value = "str", required = false) String str)
            throws Exception {
        ModelAndView modelAndView = new ModelAndView();

        int goodsSize = 12;
        List<Goods> goodsList = null;
        List<GoodsExtend> goodsAndImage = null;

        goodsList = goodsService.getGoodsByStr(goodsSize, str, str);
        goodsAndImage = new ArrayList<GoodsExtend>();
        for (int j = 0; j < goodsList.size(); j++) {

            GoodsExtend goodsExtend = new GoodsExtend();
            Goods goods = goodsList.get(j);
            List<Image> images = imageService.getImagesByGoodsPrimaryKey(goods.getId());
            goodsExtend.setGoods(goods);
            goodsExtend.setImages(images);
            goodsAndImage.add(j, goodsExtend);
        }
        modelAndView.addObject("goodsExtendList", goodsAndImage);
        modelAndView.addObject("search", str);
        modelAndView.setViewName("/goods/catelogGoods");
        return modelAndView;
    }


    @RequestMapping(value = "/catelog/{id}")
    public ModelAndView catelogGoods(HttpServletRequest request, @PathVariable("id") Integer id,
                                     @RequestParam(value = "str", required = false) String str) throws Exception {
        List<Goods> goodsList = goodsService.getGoodsByCatelog(id, str, str);
        Catelog catelog = catelogService.selectByPrimaryKey(id);
        List<GoodsExtend> goodsExtendList = new ArrayList<GoodsExtend>();
        for (int i = 0; i < goodsList.size(); i++) {
            GoodsExtend goodsExtend = new GoodsExtend();
            Goods goods = goodsList.get(i);
            List<Image> imageList = imageService.getImagesByGoodsPrimaryKey(goods.getId());
            goodsExtend.setGoods(goods);
            goodsExtend.setImages(imageList);
            goodsExtendList.add(i, goodsExtend);
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goodsExtendList", goodsExtendList);
        modelAndView.addObject("catelog", catelog);
        modelAndView.addObject("search", str);
        modelAndView.setViewName("/goods/catelogGoods");
        return modelAndView;
    }


    @RequestMapping(value = "/goodsId/{id}")
    public ModelAndView getGoodsById(HttpServletRequest request, @PathVariable("id") Integer id,
                                     @RequestParam(value = "str", required = false) String str) throws Exception {
        Goods goods = goodsService.getGoodsByPrimaryKey(id);
        User seller = userService.selectByPrimaryKey(goods.getUserId());
        Catelog catelog = catelogService.selectByPrimaryKey(goods.getCatelogId());
        GoodsExtend goodsExtend = new GoodsExtend();
        List<Image> imageList = imageService.getImagesByGoodsPrimaryKey(id);
        CommentExtend CommentExtend = goodsService.selectCommentsByGoodsId(id);
        goodsExtend.setGoods(goods);
        goodsExtend.setImages(imageList);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("CommentExtend", CommentExtend);
        modelAndView.addObject("goodsExtend", goodsExtend);
        modelAndView.addObject("seller", seller);
        modelAndView.addObject("search", str);
        modelAndView.addObject("catelog", catelog);
        modelAndView.setViewName("/goods/detailGoods");
        return modelAndView;

    }


    @RequestMapping(value = "/addComments", method = RequestMethod.POST)
    public void deleteFocus(HttpServletRequest request, Comments comments) {
        User cur_user = (User) request.getSession().getAttribute("cur_user");
        comments.setUser(cur_user);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date createAt = new Date();
        comments.setCreateAt(sdf.format(createAt));
        goodsService.addComments(comments);

    }


    @RequestMapping(value = "/editGoods/{id}")
    public ModelAndView editGoods(HttpServletRequest request, @PathVariable("id") Integer id) throws Exception {
        User cur_user = (User) request.getSession().getAttribute("cur_user");
        Goods goods = goodsService.getGoodsByPrimaryKey(id);
        List<Image> imageList = imageService.getImagesByGoodsPrimaryKey(id);
        GoodsExtend goodsExtend = new GoodsExtend();
        goodsExtend.setGoods(goods);
        goodsExtend.setImages(imageList);
        ModelAndView modelAndView = new ModelAndView();
        Integer userId = cur_user.getId();
        Purse myPurse = purseService.getPurseByUserId(userId);
        modelAndView.addObject("myPurse", myPurse);
        // 将商品信息添加到model
        modelAndView.addObject("goodsExtend", goodsExtend);
        modelAndView.setViewName("/goods/editGoods");
        return modelAndView;
    }


    @RequestMapping(value = "/editGoodsSubmit")
    public String editGoodsSubmit(HttpServletRequest request, Goods goods) throws Exception {
        User cur_user = (User) request.getSession().getAttribute("cur_user");
        goods.setUserId(cur_user.getId());
        String polish_time = DateUtil.getNowDay();
        goods.setPolishTime(polish_time);
        goods.setStatus(1);
        goodsService.updateGoodsByPrimaryKeyWithBLOBs(goods.getId(), goods);
        return "redirect:/user/allGoods";
    }


    @RequestMapping(value = "/offGoods")
    public ModelAndView offGoods() throws Exception {

        return null;
    }


    @RequestMapping(value = "/deleteGoods/{id}")
    public String deleteGoods(HttpServletRequest request, @PathVariable("id") Integer id) throws Exception {
        Goods goods = goodsService.getGoodsByPrimaryKey(id);

        User cur_user = (User) request.getSession().getAttribute("cur_user");
        goods.setUserId(cur_user.getId());
        int number = cur_user.getGoodsNum();
        Integer calelog_id = goods.getCatelogId();
        Catelog catelog = catelogService.selectByPrimaryKey(calelog_id);
        catelogService.updateCatelogNum(calelog_id, catelog.getNumber() - 1);
        userService.updateGoodsNum(cur_user.getId(), number - 1);
        cur_user.setGoodsNum(number - 1);
        request.getSession().setAttribute("cur_user", cur_user);
        //imageService.deleteImagesByGoodsPrimaryKey(id);
        goodsService.deleteGoodsByPrimaryKey(id);
        return "redirect:/user/allGoods";
    }


    @RequestMapping(value = "/publishGoods")
    public ModelAndView publishGoods(HttpServletRequest request) {

        User cur_user = (User) request.getSession().getAttribute("cur_user");
        // if (cur_user == null) {
        // return "/goods/homeGoods";
        // } else {
        Integer userId = cur_user.getId();
        Purse myPurse = purseService.getPurseByUserId(userId);
        ModelAndView mv = new ModelAndView();
        mv.addObject("myPurse", myPurse);
        mv.setViewName("/goods/pubGoods");
        return mv;
    }


    @RequestMapping(value = "/publishGoodsSubmit")
    public String publishGoodsSubmit(HttpServletRequest request, Goods goods, MultipartFile myfile)
            throws Exception {
        String filename = uploadFile(myfile);

        User cur_user = (User) request.getSession().getAttribute("cur_user");
        goods.setUserId(cur_user.getId());
        goodsService.addGood(goods, 10);

        int goodsId = goods.getId();
        Image ima = new Image();
        ima.setImgUrl(filename);
        ima.setGoodsId(goodsId);
        imageService.insert(ima);

        int number = cur_user.getGoodsNum();
        Integer calelog_id = goods.getCatelogId();
        Catelog catelog = catelogService.selectByPrimaryKey(calelog_id);
        catelogService.updateCatelogNum(calelog_id, catelog.getNumber() + 1);
        userService.updateGoodsNum(cur_user.getId(), number + 1);
        cur_user.setGoodsNum(number + 1);
        request.getSession().setAttribute("cur_user", cur_user);// 修改session值
        return "redirect:/user/allGoods";
    }


    private String uploadFile(MultipartFile myfile)
            throws IllegalStateException, IOException {

        String oldFileName = myfile.getOriginalFilename();

        String file_path = System.getProperty("user.dir") + "/src/main/webapp/upload";

        if (myfile != null && oldFileName != null && oldFileName.length() > 0) {
            String type = oldFileName.substring(oldFileName.lastIndexOf("."));

            String newFileName = UUID.randomUUID() + type;
            if (type.equalsIgnoreCase(".jpg") || type.equalsIgnoreCase(".jpeg") ||
                    type.equalsIgnoreCase(".png") || type.equalsIgnoreCase(".gif")) {

                File newFile = new File(file_path + "/" + newFileName);

                myfile.transferTo(newFile);

                return newFileName;
            } else {
                throw new RuntimeException("Wrong type");
            }
        } else {
            throw new RuntimeException("please upload");
        }
    }


    @ResponseBody
    @RequestMapping(value = "/uploadFile")
    public Map<String, Object> uploadFile(HttpSession session, MultipartFile myfile)
            throws IllegalStateException, IOException {

        String oldFileName = myfile.getOriginalFilename();

        String file_path = session.getServletContext().getRealPath("upload");
        // System.out.println("file_path:"+file_path);

        if (myfile != null && oldFileName != null && oldFileName.length() > 0) {

            String newFileName = UUID.randomUUID() + oldFileName.substring(oldFileName.lastIndexOf("."));

            File newFile = new File(file_path + "/" + newFileName);

            myfile.transferTo(newFile);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("success", "success");
            map.put("imgUrl", newFileName);
            return map;
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("error", "pic is illigel");
            return map;
        }
    }


    @RequestMapping(value = "/buyId/{id}")
    public ModelAndView getGoodsdetailById(HttpServletRequest request, @PathVariable("id") Integer id)
            throws Exception {
        Goods goods = goodsService.getGoodsByPrimaryKey(id);
        GoodsExtend goodsExtend = new GoodsExtend();
        List<Image> imageList = imageService.getImagesByGoodsPrimaryKey(id);
        goodsExtend.setGoods(goods);
        goodsExtend.setImages(imageList);
        User cur_user = (User) request.getSession().getAttribute("cur_user");
        Integer userId = cur_user.getId();
        Purse myPurse = purseService.getPurseByUserId(userId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goodsExtend", goodsExtend);
        modelAndView.addObject("myPurse", myPurse);
        modelAndView.setViewName("/user/pay");
        return modelAndView;
    }

}