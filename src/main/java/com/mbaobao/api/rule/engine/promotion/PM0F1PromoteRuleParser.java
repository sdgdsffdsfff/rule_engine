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
import com.mbaobao.api.rule.engine.common.CartDetail;
import com.mbaobao.api.rule.engine.common.CartGift;
import com.mbaobao.api.rule.engine.common.CartView;
import com.mbaobao.api.rule.engine.common.DbExecutor;

/**
 * 赠送礼品
 * 
 * @author baiyu
 * 
 */
public class PM0F1PromoteRuleParser extends BasePromoteRuleParser implements
		IPromoteRuleParser {
	private String _productId;
	private String _productTitle;
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
		String standardPriceStr = element.element("StandardPrice").getText();
		String quantityStr = element.element("Quantity").getText();
		String weightStr = element.element("Weight").getText();

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
		List<CartGift> cartGifts = cartView.GetCartGifts();
		if (cartDetails.size() == 0) {
			return;
		}
		for (CartDetail cartDetail : cartDetails) {
			if (this._productIds.contains(cartDetail.getProductId())) {
				if (!cartDetail.isPromoteFlag()) {
					CartGift cartGift = new CartGift();
					cartGift.setProductIds(this._productId);
					cartGift.setTitles(this._productTitle);
					cartGift.setStandardPrice(this._standardPrice);
					cartGift.setWeight(this._weight);
					cartGift.setQuantity(this._quantity);
					cartGift.setActivityId(this.getPromotionId());
					cartGift.setActivityName(this.getPromotionTitle());
					cartGift.setActivityTypeId(this.getPromoteItemId());
					cartGift.setActivityTypeName(this.getPromoteItemTitle());
					cartGifts.add(cartGift);
				}
			}
		}
	}

}
