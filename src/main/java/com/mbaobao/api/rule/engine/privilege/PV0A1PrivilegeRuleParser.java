package com.mbaobao.api.rule.engine.privilege;

import java.math.BigDecimal;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mbaobao.api.rule.engine.common.CartMaster;
import com.mbaobao.api.rule.engine.common.CartView;
import com.mbaobao.api.rule.engine.common.CommonUtils;

/**
 * 满减
 * 
 * @author baiyu
 * 
 */
public class PV0A1PrivilegeRuleParser extends BasePrivilegeRuleParser implements
		IPrivilegeRuleParser {

	private BigDecimal _saleOff;
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
		String saleOffStr = element.element("SaleOff").getText();
		this._minValue = BigDecimal.valueOf(Double.valueOf(MinValue));
		this._maxValue = BigDecimal.valueOf(Double.valueOf(MaxValue));
		this._saleOff = BigDecimal.valueOf(Double.valueOf(saleOffStr));
	}

	@Override
	public void Parse(CartView cartView) {
		boolean isSet = false;
		CartMaster cartMaster = cartView.GetCartMaster();
		if (cartMaster.getAmountMonery().compareTo(BigDecimal.valueOf(0.00)) == 0) {
			return;
		}
		if (this._isAmountTotal.compareTo("0") == 0) {
			if (CommonUtils.compareBigDecimal(cartMaster.getAmountNomal(),
					this._minValue, this._maxValue)) {
				if (!cartMaster.isPrivilegFlag()) {
					cartMaster.setAmountPay(cartMaster.getAmountTotal()
							.subtract(this._saleOff));
					isSet = true;
				}
			}
		} else {
			if (CommonUtils.compareBigDecimal(cartMaster.getAmountTotal(),
					this._minValue, this._maxValue)) {
				if (!cartMaster.isPrivilegFlag()) {
					cartMaster.setAmountPay(cartMaster.getAmountTotal()
							.subtract(this._saleOff));
					isSet = true;
				}
			}
		}

		if (isSet) {
			cartMaster.setPrivilegFlag(true);
			cartMaster.setSaleOff(this._saleOff);
			cartMaster.setPrivilegeId(this.getPrivilegeId());
			cartMaster.setPrivilegeTitle(this.getPrivilegeTitle());
			cartMaster.setPrivilegeItemId(this.getPrivilegeItemId());
			cartMaster.setPrivilegeItemTitle(this.getPrivilegeItemTitle());
		}
	}
}
