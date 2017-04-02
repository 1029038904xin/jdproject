package cn.itcast.jd.service;

import cn.itcast.jd.po.Result;

public interface ProductService {

	public Result search(String queryString, String catalog_name, String price, Integer page, String sort) throws Exception;
}
