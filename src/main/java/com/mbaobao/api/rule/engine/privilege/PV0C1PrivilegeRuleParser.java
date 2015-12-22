package com.mbaobao.api.rule.engine.privilege;

import java.math.BigDecimal;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mbaobao.api.rule.engine.common.CartBarter;
import com.mbaobao.api.rule.engine.common.CartMaster;
import com.mbaobao.api.rule.engine.common.CartView;
import com.mbaobao.api.rule.engine.common.CommonUtils;

/**
 * 换购商品
 * 
 * @author baiyu
 * 
 */
public class PV0C1PrivilegeRuleParser extends BasePrivilegeRuleParser implements
		IPrivilegeRuleParser {

	private String _productIds;
	private String _productTitles;
	public BigDecimal _salePrice;
	public BigDecimal _standardPrice;
	public int _quantity;
	public BigDecimal _weight;
	private String _isAmountTotal;
	private BigDecimal _minValue;
	private BigDecimal _maxValue;

	@Override
	public void SetRule(String ruleXmlStr) {
		Document document = null;
		try {
			document = DocumentHelper.parseText(ruleXmlStr);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element element = document.getRootElement().element("RuleDefine");

		this._isAmountTotal = element.element("IsAmountTotal").getText();
		String MinValue = element.element("MinValue").getText();
		String MaxValue = element.element("MaxValue").getText();
		this._minValue = BigDecimal.valueOf(Double.valueOf(MinValue));
		this._maxValue = BigDecimal.valueOf(Double.valueOf(MaxValue));
		this._productIds = element.element("ProductIds").getText();
		this._productTitles = element.element("ProductTitles").getText();

		String salePriceStr = element.element("SalePrice").getText();

		String standardPriceStr = element.element("StandardPrice").getText();

		String quantityStr = element.element("Quantity").getText();

		String weightStr = element.element("Weight").getText();

		this._salePrice = BigDecimal.valueOf(Double.valueOf(salePriceStr));
		this._standardPrice = BigDecimal.valueOf(Double
				.valueOf(standardPriceStr));
		this._quantity = Integer.valueOf(quantityStr);
		this._weight = BigDecimal.valueOf(Double.valueOf(weightStr));
	}

	@Override
	public void Parse(CartView cartView) {
		CartMaster cartMaster = cartView.GetCartMaster();
		List<CartBarter> cartBarters = cartView.GetCartBarters();
		if (cartMaster.getAmountMonery().compareTo(BigDecimal.valueOf(0.00)) == 0) {
			return;
		}
		if (this._isAmountTotal.compareTo("0") == 0) {
			if (CommonUtils.compareBigDecimal(cartMaster.getAmountNomal(),
					this._minValue, this._maxValue)) {
				if (!cartMaster.isPrivilegFlag()) {
					cartBarters.add(getCartBarter());
				}
			}
		} else {
			if (CommonUtils.compareBigDecimal(cartMaster.getAmountTotal(),
					this._minValue, this._maxValue)) {
				if (!cartMaster.isPrivilegFlag()) {
					cartBarters.add(getCartBarter());
				}
			}
		}
	}

	private CartBarter getCartBarter() {
		CartBarter cartBarter = new CartBarter();
		cartBarter.setProductIds(this._productIds);
		cartBarter.setTitles(this._productTitles);
		cartBarter.setSalePrice(this._salePrice);
		cartBarter.setStandardPrice(this._standardPrice);
		cartBarter.setWeight(this._weight);
		cartBarter.setQuantity(this._quantity);
		cartBarter.setActivityId(this.getPrivilegeId());
		cartBarter.setActivityName(this.getPrivilegeTitle());
		cartBarter.setActivityTypeId(this.getPrivilegeItemId());
		cartBarter.setActivityTypeName(this.getPrivilegeItemTitle());
		return cartBarter;
	}
}
