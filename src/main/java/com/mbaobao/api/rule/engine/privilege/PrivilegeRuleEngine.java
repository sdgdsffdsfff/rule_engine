package com.mbaobao.api.rule.engine.privilege;

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
import com.mbaobao.api.rule.engine.common.CartMaster;
import com.mbaobao.api.rule.engine.common.CartView;
import com.mbaobao.api.rule.engine.common.DbExecutor;

public class PrivilegeRuleEngine {
	private volatile static PrivilegeRuleEngine	_MeObj;

	public static PrivilegeRuleEngine create() {
		if (_MeObj == null) {
			synchronized (PrivilegeRuleEngine.class) {
				if (_MeObj == null) {
					_MeObj = new PrivilegeRuleEngine();
				}
			}
		}
		return _MeObj;
	}

	private List<IPrivilegeRuleParser>	_privilegeRuleParsers;
	public String						_updateDateTime;

	public PrivilegeRuleEngine() {
		this._privilegeRuleParsers = new ArrayList<IPrivilegeRuleParser>();
		LoadInfo();
	}

	public String GetUpdateDateTime() {
		return this._updateDateTime;
	}

	private void LoadInfo() {
		Connection conn = DbExecutor.getConnection();
		try {
			// 读取订单优惠项
			String sqlStrPrivilegeConfig = "SELECT MValue,UpdateDateTime FROM MMKeyValue WHERE MKey='PrivilegeConfig'";
			ResultSet rsPrivilegeConfig = DbExecutor.read(conn, sqlStrPrivilegeConfig);
			String privilegeConfig = "";
			if (rsPrivilegeConfig.next()) {
				this._updateDateTime = rsPrivilegeConfig.getString("UpdateDateTime");
				privilegeConfig ="'" + rsPrivilegeConfig.getString("MValue").replace(";", "','") + "'";
			}

			String sqlStrPrivilegeItem = "SELECT * FROM PrivilegeItem WHERE PrivilegeItemId in ("
					+ privilegeConfig + ") AND CheckFlag=1";

			ResultSet rsPrivilegeItem = DbExecutor.read(conn, sqlStrPrivilegeItem);

			List<String> listPrivilegeId = new ArrayList<String>();
			StringBuilder sbPrivilegeId = new StringBuilder();

			while (rsPrivilegeItem.next()) {
				String strFlg = rsPrivilegeConfig.getString("PrivilegeId").trim();
				if (!listPrivilegeId.contains(strFlg)) {
					listPrivilegeId.add(strFlg);
					if (!sbPrivilegeId.toString().equals("")) {
						sbPrivilegeId.append(",");
					}
					sbPrivilegeId.append("'" + strFlg + "'");
				}
			}

			rsPrivilegeItem.beforeFirst();

			if (!sbPrivilegeId.toString().equals("")) {

				String sqlStrPromote = "SELECT * FROM Privilege WHERE PrivilegeId in ("
						+ sbPrivilegeId.toString() + ") AND Enabled=1";
				ResultSet rsPrivilege = DbExecutor.read(conn, sqlStrPromote);

				while (rsPrivilegeItem.next()) {
					// 订单优惠类型编号
					String privilegeId = rsPrivilegeItem.getString("PrivilegeId");
					// 订单优惠类型标题
					String privilegeTitle = this
							.GetTitleFromPrivilegeTable(rsPrivilege, privilegeId);
					// 订单优惠应用项编号
					String privilegeItemId = rsPrivilegeItem.getString("PrivilegeItemId");
					// 订单优惠应用项标题
					String privilegeItemTitle = rsPrivilegeItem.getString("PrivilegeItemTitle");
					// XmlData
					String privilegeXmlData = rsPrivilegeItem.getString("PrivilegeXmlData");
					//
					String implClassType = GetImplClassTypeFromPrivilegeTable(rsPrivilege,
							privilegeId);

					Class o = Class.forName(implClassType);
					IPrivilegeRuleParser privilegeRuleParser = (IPrivilegeRuleParser) o
							.newInstance();

					privilegeRuleParser.SetBaseInfo(privilegeId, privilegeTitle, privilegeItemId,
							privilegeItemTitle);
					privilegeRuleParser.SetRule(privilegeXmlData);

					this._privilegeRuleParsers.add(privilegeRuleParser);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbExecutor.close(conn, null, null);
		}
	}

	private String GetTitleFromPrivilegeTable(ResultSet rs, String privilegeId) {
		try {
			rs.beforeFirst();
			while (rs.next()) {
				if (privilegeId.compareTo(rs.getString("PrivilegeId")) == 0) {
					return rs.getString("PrivilegeTitle");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private String GetImplClassTypeFromPrivilegeTable(ResultSet rs, String privilegeId) {
		try {
			rs.beforeFirst();
			while (rs.next()) {
				if (privilegeId.compareTo(rs.getString("PrivilegeId")) == 0) {
					return rs.getString("ImplClassType");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public CartView SplitCartDetail(String cartXmlStr) {
		CartView cartView = new CartView();

		Document document = null;
		try {
			document = DocumentHelper.parseText(cartXmlStr);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element element = document.getRootElement().element("CartMaster");

		String amountTotalStr = element.attributeValue("AmountTotal");
		BigDecimal amountTotal = BigDecimal.valueOf(Double.valueOf(amountTotalStr));

		String amountPromoteStr = element.attributeValue("AmountPromote");
		BigDecimal amountPromote = BigDecimal.valueOf(Double.valueOf(amountPromoteStr));

		String amountNomalStr = element.attributeValue("AmountNomal");
		BigDecimal amountNomal = BigDecimal.valueOf(Double.valueOf(amountNomalStr));

		String amountMoneryStr = element.attributeValue("AmountMoney");
		BigDecimal amountMonery = BigDecimal.valueOf(Double.valueOf(amountMoneryStr));

		CartMaster cartMaster = new CartMaster();
		cartMaster.setAmountTotal(amountTotal);
		cartMaster.setAmountPromote(amountPromote);
		cartMaster.setAmountNomal(amountNomal);
		cartMaster.setAmountMonery(amountMonery);

		cartView.SetCartMaster(cartMaster);
		return cartView;
	}

	public String ApplyParse(String xmlStr) {
		CartView cartView = this.SplitCartDetail(xmlStr);
		for (IPrivilegeRuleParser privilegeRuleParser : this._privilegeRuleParsers) {
			privilegeRuleParser.Parse(cartView);
		}

		CartMaster cartMaster = cartView.GetCartMaster();

		Document document = null;
		try {
			document = DocumentHelper.parseText(xmlStr);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element element = document.getRootElement().element("CartMaster");

		if (cartMaster.isPrivilegFlag()) {
			element.addAttribute("SaleOff", cartMaster.getSaleOff().toString());
			element.addAttribute("AmountPay", cartMaster.getAmountPay().toString());
			element.addAttribute("PrivilegeId", cartMaster.getPrivilegeId());
			element.addAttribute("PrivilegeTitle", cartMaster.getPrivilegeTitle());
			element.addAttribute("PrivilegeItemId", cartMaster.getPrivilegeItemId());
			element.addAttribute("PrivilegeItemTitle", cartMaster.getPrivilegeItemTitle());
		} else {
			element.addAttribute("AmountPay", element.attributeValue("AmountTotal"));
		}

		String cartBarterMoneyStr = element.attributeValue("CartBarterMoney");
		BigDecimal cartBarterSum = BigDecimal.valueOf(Double.valueOf(cartBarterMoneyStr));

		Element nodeCartBarters = document.getRootElement().element("CartBarters");

		for (CartBarter item : cartView.GetCartBarters()) {
			Element nodeCartBarter = nodeCartBarters.addElement("CartBarter");
			nodeCartBarter.addAttribute("ProductId", item.getProductIds());
			nodeCartBarter.addAttribute("Title", item.getTitles());
			nodeCartBarter.addAttribute("Weight", item.getWeight().toString());
			nodeCartBarter.addAttribute("StandardPrice", item.getStandardPrice().toString());
			nodeCartBarter.addAttribute("Quantity", String.valueOf(item.getQuantity()));
			nodeCartBarter.addAttribute("SalePrice", item.getSalePrice().toString());
			nodeCartBarter.addAttribute("ActivityTypeId", item.getActivityTypeId());
			nodeCartBarter.addAttribute("ActivityTypeName", item.getActivityTypeName());
			nodeCartBarter.addAttribute("ActivityId", item.getActivityId());
			nodeCartBarter.addAttribute("ActivityName", item.getActivityName());
			cartBarterSum = cartBarterSum.add(item.getSalePrice());
		}
		element.addAttribute("CartBarterMoney", cartBarterSum.toString());

		return document.asXML();
	}
}
