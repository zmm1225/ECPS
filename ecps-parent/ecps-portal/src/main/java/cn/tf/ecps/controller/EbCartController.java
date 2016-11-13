package cn.tf.ecps.controller;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;



import cn.tf.ecps.po.EbCart;
import cn.tf.ecps.po.EbSku;
import cn.tf.ecps.service.EbCartService;
import cn.tf.ecps.service.EbSkuService;

@Controller
@RequestMapping("/cart")
public class EbCartController {
	
	@Autowired
	private EbSkuService skuService;
	@Autowired
	private EbCartService cartService;
	
	//查询购物车
	@RequestMapping("/listCart.do")
	public String listCart(HttpServletRequest request,HttpServletResponse response,Model model){
		List<EbCart> cartList = cartService.selectCart(request, response);
		model.addAttribute("cartList",cartList);
		Integer itemNum=0;
		BigDecimal totalPrice = new BigDecimal(0);
		for (EbCart cart : cartList) {
			//计算商品数量
			itemNum = cart.getQuantity() + itemNum;
			//计算商品总金额，不会损失精度
			totalPrice = totalPrice.add(cart.getSku().getSkuPrice().multiply(new BigDecimal(cart.getQuantity())));
		}
		model.addAttribute("itemNum", itemNum);
		model.addAttribute("totalPrice", totalPrice);
		
		return "shop/car";	
	}
	
	//判断库存
	@RequestMapping("/validStock.do")
	public void validStock(Long skuId,Integer quantity,PrintWriter out){
		
		String result="yes";
		EbSku sku = skuService.getSkuById(skuId);
		if(sku.getStockInventory()<quantity){
			result="no";
		}
		out.write(result);
	}
	
	//添加到购物车
	@RequestMapping("/addCart.do")
	public void addCart(Long skuId,Integer quantity,HttpServletRequest  request,HttpServletResponse response,PrintWriter out){
		cartService.addCart(skuId, quantity, request, response);
		
		out.write("success");
	}
	

}