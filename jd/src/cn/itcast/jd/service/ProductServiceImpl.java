package cn.itcast.jd.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.itcast.jd.po.Product;
import cn.itcast.jd.po.Result;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private HttpSolrServer httpSolrServer;

	@Override
	public Result search(String queryString, String catalog_name, String price, Integer page, String sort)
			throws Exception {
		// 创建查询对象
		SolrQuery query = new SolrQuery();

		// 设置查询条件
		if (StringUtils.isEmpty(queryString)) {
			query.setQuery("*:*");
		} else {
			query.setQuery(queryString);
		}

		// 设置默认域
		query.set("df", "product_keywords");

		// 设置分类过滤条件
		if (StringUtils.isNotEmpty(catalog_name)) {
			query.addFilterQuery("product_catalog_name:" + catalog_name);
		}

		// 设置价格区间过滤条件 价格区间0-9
		if (StringUtils.isNotEmpty(price)) {
			String[] arr = price.split("-");
			// 价格区间查询语法product_price:[1 TO 10]
			query.addFilterQuery("product_price:[" + arr[0] + " TO " + arr[1] + "]");
		}

		// 设置排序
		if ("1".equals(sort)) {
			query.setSort("product_price", ORDER.desc);
		} else {
			query.setSort("product_price", ORDER.asc);

		}

		// 设置分页
		if (page == null) {
			page = 1;
		}
		query.setStart((page - 1) * 20);
		query.setRows(20);

		// 设置高亮
		query.setHighlight(true);
		query.addHighlightField("product_name");
		query.setHighlightSimplePre("<font style='color:red'>");
		query.setHighlightSimplePost("</font>");

		// 执行搜索
		QueryResponse response = httpSolrServer.query(query);

		// 获取搜索结果
		SolrDocumentList results = response.getResults();

		// 获取搜索结果总数
		long recordCount = results.getNumFound();

		// 遍历搜索结果

		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();

		List<Product> productList = new ArrayList<>();
		Product product;
		for (SolrDocument solrDocument : results) {
			product = new Product();
			product.setPid(solrDocument.get("id").toString());
			// 高亮处理
			List<String> list = highlighting.get(solrDocument.get("id")).get("product_name");
			if (list != null && list.size() > 0) {
				product.setName(list.get(0));
			} else {
				product.setName(solrDocument.get("product_name").toString());
			}
			product.setPrice(solrDocument.get("product_price").toString());
			product.setPicture(solrDocument.get("product_picture").toString());

			productList.add(product);
		}

		// 封装返回结果
		Result result = new Result();
		result.setCurPage(page);
		result.setProductList(productList);
		result.setRecordCount(recordCount);

		// 计算总页数
		Integer pageCount = (int) (recordCount / 20);
		if (recordCount % 20 > 0) {
			pageCount++;
		}

		result.setPageCount(pageCount);

		return result;
	}

}
