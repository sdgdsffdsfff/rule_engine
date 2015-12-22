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
import com.mbaobao.api.rule.engine.common.CartView;
import com.mbaobao.api.rule.engine.common.DbExecutor;

/**
 * 立减
 * 
 * @author baiyu
 * 
 */
public class PM0A1PromoteRuleParser extends BasePromoteRuleParser implements
		IPromoteRuleParser {

	private BigDecimal _saleOff;
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
		String saleOffStr = element.element("SaleOff").getText();
		this._saleOff = BigDecimal.valueOf(Double.valueOf(saleOffStr));

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

		if (cartDetails.size() == 0) {
			return;
		}
		for (CartDetail cartDetail : cartDetails) {
			if (this._productIds.contains(cartDetail.getProductId())) {
				if (!cartDetail.isPromoteFlag()) {
					cartDetail.setSalePrice(cartDetail.getStandardPrice()
							.subtract(this._saleOff));
					cartDetail.setPromotionId(this.getPromotionId());
					cartDetail.setPromotionTitle(this.getPromotionTitle());
					cartDetail.setPromoteItemId(this.getPromoteItemId());
					cartDetail.setPromoteItemTitle(this.getPromoteItemTitle());
					cartDetail.setPromoteFlag(true);
				}
			}
		}
	}

}
