package cn.itcast.jd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.itcast.jd.po.Result;
import cn.itcast.jd.service.ProductService;

@Controller
public class ProductController {

	@Autowired
	private ProductService service;

	@RequestMapping("/list")
	public String list(String queryString, String catalog_name, String price, Integer page, String sort, Model model)
			throws Exception {
		// 调用service获取搜索结果
		Result result = service.search(queryString, catalog_name, price, page, sort);

		// 将搜索结果放入request域中
		model.addAttribute("result", result);
		// 回显
		model.addAttribute("queryString", queryString);
		model.addAttribute("catalog_name", catalog_name);
		model.addAttribute("price", price);
		model.addAttribute("page", page);
		model.addAttribute("sort", sort);
		return "product_list";
	}
}
