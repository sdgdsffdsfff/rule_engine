package com.mbaobao.api.rule.engine.promotion;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mbaobao.api.rule.engine.common.CartBarter;
import com.mbaobao.api.rule.engine.common.CartDetail;
import com.mbaobao.api.rule.engine.common.CartView;
import com.mbaobao.api.rule.engine.common.DbExecutor;

/**
 * 换购商品
 * 
 * @author baiyu
 * 
 */
public class PM0C1PromoteRuleParser extends BasePromoteRuleParser implements
		IPromoteRuleParser {
	private String _productId;
	private String _productTitle;
	public BigDecimal _salePrice;
	public BigDecimal _standardPrice;
	public int _quantity;
	public BigDecimal _weight;
	public List<String> _productIds;

	@Override
	public void SetRule(String ruleXmlStr) {
		_productIds = new ArrayList<String>();
		Document document = null;
		try {
			document = DocumentHelper.parseText(ruleXmlStr);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element element = document.getRootElement().element("RuleDefine");

		String pMCategoryId = element.element("PMCategoryId").getText();
		this._productId = element.element("ProductIds").getText();
		this._productTitle = element.element("ProductTitles").getText();
		String salePriceStr = element.element("SalePrice").getText();
		String standardPriceStr = element.element("StandardPrice").getText();
		String quantityStr = element.element("Quantity").getText();
		String weightStr = element.element("Weight").getText();

		this._salePrice = BigDecimal.valueOf(Double.valueOf(salePriceStr));
		this._standardPrice = BigDecimal.valueOf(Double
				.valueOf(standardPriceStr));
		this._quantity = Integer.valueOf(quantityStr);
		this._weight = BigDecimal.valueOf(Double.valueOf(weightStr));

		pMCategoryId = "'" + pMCategoryId.replace(";", "','") + "'";

		String sqlStr = "SELECT ProductId FROM PromoteCategoryRefProduct WHERE PromoteCategoryId IN ("
				+ pMCategoryId + ")";

		Connection conn = DbExecutor.getConnection();
		ResultSet rs = DbExecutor.read(conn, sqlStr);

		try {
			while (rs.next()) {
				this._productIds.add(rs.getString("ProductId"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DbExecutor.close(conn, null, rs);
		}
	}

	@Override
	public void Parse(CartView cartView) {
		List<CartDetail> cartDetails = cartView.GetNormalCartDetails();
		List<CartBarter> cartBarters = cartView.GetCartBarters();
		if (cartDetails.size() == 0) {
			return;
		}
		for (CartDetail cartDetail : cartDetails) {
			if (this._productIds.contains(cartDetail.getProductId())) {
				if (!cartDetail.isPromoteFlag()) {
					CartBarter cartBarter = new CartBarter();
					cartBarter.setProductIds(this._productId);
					cartBarter.setTitles(this._productTitle);
					cartBarter.setSalePrice(this._salePrice);
					cartBarter.setStandardPrice(this._standardPrice);
					cartBarter.setWeight(this._weight);
					cartBarter.setQuantity(this._quantity);
					cartBarter.setActivityId(this.getPromotionId());
					cartBarter.setActivityName(this.getPromotionTitle());
					cartBarter.setActivityTypeId(this.getPromoteItemId());
					cartBarter.setActivityTypeName(this.getPromoteItemTitle());
					cartBarters.add(cartBarter);
				}
			}
		}
	}

}
