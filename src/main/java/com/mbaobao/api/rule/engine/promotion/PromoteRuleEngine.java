package com.mbaobao.api.rule.engine.promotion;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mbaobao.api.rule.engine.common.CartBarter;
import com.mbaobao.api.rule.engine.common.CartDetail;
import com.mbaobao.api.rule.engine.common.CartMaster;
import com.mbaobao.api.rule.engine.common.CartView;
import com.mbaobao.api.rule.engine.common.DbExecutor;

public class PromoteRuleEngine {
	private volatile static PromoteRuleEngine _MeObj;

	public static PromoteRuleEngine create() {
		if (_MeObj == null) {
			synchronized (PromoteRuleEngine.class) {
				if (_MeObj == null) {
					_MeObj = new PromoteRuleEngine();
				}
			}
		}
		return _MeObj;
	}

	private List<IPromoteRuleParser> _promoteRuleParsers;
	private String _updateDateTime;

	public PromoteRuleEngine() {
		this._promoteRuleParsers = new ArrayList<IPromoteRuleParser>();
		LoadInfo();
	}

	public String GetUpdateDateTime() {
		return this._updateDateTime;
	}

	private void LoadInfo() {
		Connection conn = DbExecutor.getConnection();
		try {
			String sqlStrPromoteConfig = "SELECT MValue,UpdateDateTime FROM MMKeyValue WHERE MKey='PromoteConfig'";
			ResultSet rsPromoteConfig = DbExecutor.read(conn,
					sqlStrPromoteConfig);
			String promoteConfig = null;
			if (rsPromoteConfig.next()) {
				this._updateDateTime = rsPromoteConfig
						.getString("UpdateDateTime");
				promoteConfig = "'"
						+ rsPromoteConfig.getString("MValue").replace(";",
								"','") + "'";
			}

			String sqlStrPromoteItem = "SELECT * FROM PromoteItem WHERE PromoteItemId in ("
					+ promoteConfig + ") AND CheckFlag=1";
			ResultSet rsPromoteItem = DbExecutor.read(conn, sqlStrPromoteItem);

			List<String> listPromotionId = new ArrayList<String>();
			StringBuilder sbPromotionId = new StringBuilder();

			while (rsPromoteItem.next()) {
				String strFlg = rsPromoteItem.getString("PromotionId").trim();
				if (!listPromotionId.contains(strFlg)) {
					listPromotionId.add(strFlg);
					if (!sbPromotionId.toString().equals("")) {
						sbPromotionId.append(",");
					}
					sbPromotionId.append("'" + strFlg + "'");
				}
			}

			rsPromoteItem.beforeFirst();

			if (!sbPromotionId.toString().equals("")) {
				String sqlStrPromote = "SELECT * FROM Promotion WHERE PromotionId in ("
						+ sbPromotionId.toString() + ") AND Enabled=1";
				ResultSet rsPromotion = DbExecutor.read(conn, sqlStrPromote);

				while (rsPromoteItem.next()) {
					// 促销类型编号
					String promotionId = rsPromoteItem.getString("PromotionId");
					// 促销类型标题
					String promotionTitle = this.GetTitleFromPromotionTable(
							rsPromotion, promotionId);
					// 促销应用项编号
					String promoteItemId = rsPromoteItem
							.getString("PromoteItemId");
					// 促销应用项标题
					String promoteItemTitle = rsPromoteItem
							.getString("PromoteItemTitle");
					// XmlData
					String promoteXmlData = rsPromoteItem
							.getString("PromoteXmlData");
					//
					String implClassType = GetImplClassTypeFromPromotionTable(
							rsPromotion, promotionId);

					Class o = Class.forName(implClassType);
					IPromoteRuleParser promoteRuleParser = (IPromoteRuleParser) o
							.newInstance();

					promoteRuleParser.SetBaseInfo(promotionId, promotionTitle,
							promoteItemId, promoteItemTitle);
					promoteRuleParser.SetRule(promoteXmlData);

					this._promoteRuleParsers.add(promoteRuleParser);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbExecutor.close(conn, null, null);
		}
	}

	private String GetTitleFromPromotionTable(ResultSet rs, String privilegeId) {
		try {
			rs.beforeFirst();
			while (rs.next()) {
				if (privilegeId.compareTo(rs.getString("PromotionId")) == 0) {
					return rs.getString("PromotionTitle");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private String GetImplClassTypeFromPromotionTable(ResultSet rs,
			String privilegeId) {
		try {
			rs.beforeFirst();
			while (rs.next()) {
				if (privilegeId.compareTo(rs.getString("PromotionId")) == 0) {
					return rs.getString("ImplClassType");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 拆分CartData 到 CartView
	 * 
	 * @param cartXmlStr
	 * @return
	 */
	public CartView SplitCartDetail(String cartXmlStr) {
		CartView cartView = new CartView();

		Document document = null;
		try {
			document = DocumentHelper.parseText(cartXmlStr);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element element = document.getRootElement().element("CartMaster");

		CartMaster cartMaster = new CartMaster();
		cartMaster.setCustomerId(element.attributeValue("CustomerId"));
		cartView.SetCartMaster(cartMaster);

		List detailNodes = document
				.selectNodes("/CartData/CartDetails/CartDetail");
		Iterator it = detailNodes.iterator();
		while (it.hasNext()) {
			Element detailNode = (Element) it.next();
			String productId = detailNode.attributeValue("ProductId").trim();
			String title = detailNode.attributeValue("Title");
			String weight = detailNode.attributeValue("Weight");
			String standardPriceStr = detailNode
					.attributeValue("StandardPrice");
			String quantityStr = detailNode.attributeValue("Quantity");

			int quantity = Integer.valueOf(quantityStr);

			for (int i = 0; i < quantity; i++) {
				CartDetail cartDetail = new CartDetail();
				cartDetail.setProductId(productId);
				cartDetail.setTitle(title);
				cartDetail
						.setWeight(BigDecimal.valueOf(Double.valueOf(weight)));
				cartDetail.setStandardPrice(BigDecimal.valueOf(Double
						.valueOf(standardPriceStr)));
				cartDetail.setSalePrice(cartDetail.getStandardPrice());
				cartView.AddDetail(cartDetail);
			}
		}
		return cartView;
	}

	public String ApplyParse(String xmlStr) {
		CartView cartViewFlg = this.SplitCartDetail(xmlStr);
		for (IPromoteRuleParser promoteRuleParser : this._promoteRuleParsers) {
			promoteRuleParser.Parse(cartViewFlg);
		}

		Map<String, CartDetail> _productNum = new HashMap<String, CartDetail>();

		for (CartDetail item : cartViewFlg.GetCartDetails()) {
			String strFlag = item.getProductId() + ","
					+ item.getPromoteItemId();

			if (_productNum.containsKey(strFlag)) {
				CartDetail cartDetail = _productNum.get(strFlag);
				cartDetail.setQuantity(cartDetail.getQuantity() + 1);
				_productNum.put(strFlag, cartDetail);
			} else {
				_productNum.put(strFlag, item);
			}
		}

		Document doc = DocumentHelper.createDocument();

		Element nodeCartView = doc.addElement("CartView");

		Element nodeCartMaster = nodeCartView.addElement("CartMaster");

		Element nodeCartDetails = nodeCartView.addElement("CartDetails");

		BigDecimal amountTotal = new BigDecimal(0);// AmountPromote+AmountNomal
		BigDecimal amountPromote = new BigDecimal(0);// 有过促销的商品金额
		BigDecimal amountNomal = new BigDecimal(0);// 未有过促销的商品金额

		BigDecimal amount = new BigDecimal(0);

		for (CartDetail cartDetail : _productNum.values()) {
			Element nodeCartDetail = nodeCartDetails.addElement("CartDetail");
			nodeCartDetail.addAttribute("ProductId", cartDetail.getProductId());
			nodeCartDetail.addAttribute("Title", cartDetail.getTitle());
			nodeCartDetail.addAttribute("Weight", cartDetail.getWeight()
					.toString());
			nodeCartDetail.addAttribute("StandardPrice", cartDetail
					.getStandardPrice().toString());
			nodeCartDetail.addAttribute("Quantity",
					String.valueOf(cartDetail.getQuantity()));
			nodeCartDetail.addAttribute("SalePrice", cartDetail.getSalePrice()
					.toString());

			if (cartDetail.isPromoteFlag()) {
				nodeCartDetail.addAttribute("PromotionId",
						cartDetail.getPromotionId());
				nodeCartDetail.addAttribute("PromotionTitle",
						cartDetail.getPromotionTitle());
				nodeCartDetail.addAttribute("PromoteItemId",
						cartDetail.getPromoteItemId());
				nodeCartDetail.addAttribute("PromoteItemTitle",
						cartDetail.getPromoteItemTitle());

				amountPromote = amountPromote.add(cartDetail.getSalePrice()
						.multiply(new BigDecimal(cartDetail.getQuantity())));
			} else {
				amountNomal = amountNomal.add(cartDetail.getSalePrice()
						.multiply(new BigDecimal(cartDetail.getQuantity())));
			}
			amount = amount.add(cartDetail.getStandardPrice().multiply(
					new BigDecimal(cartDetail.getQuantity())));
		}

		amountTotal = amountPromote.add(amountNomal);

		CartMaster cartMaster = cartViewFlg.GetCartMaster();

		nodeCartMaster.addAttribute("CustomerId", cartMaster.getCustomerId());
		nodeCartMaster.addAttribute("AmountTotal", amountTotal.toString());
		nodeCartMaster.addAttribute("AmountPromote", amountPromote.toString());
		nodeCartMaster.addAttribute("AmountNomal", amountNomal.toString());
		nodeCartMaster.addAttribute("AmountMoney", amount.toString());

		// 订单优惠换购
		Element nodeCartBarters = nodeCartView.addElement("CartBarters");

		BigDecimal cartBarterSum = new BigDecimal(0);
		for (CartBarter item : cartViewFlg.GetCartBarters()) {
			Element nodeCartBarter = nodeCartBarters.addElement("CartBarter");
			nodeCartBarter.addAttribute("ProductId", item.getProductIds());
			nodeCartBarter.addAttribute("Title", item.getTitles());
			nodeCartBarter.addAttribute("Weight", item.getWeight().toString());
			nodeCartBarter.addAttribute("StandardPrice", item
					.getStandardPrice().toString());
			nodeCartBarter.addAttribute("Quantity",
					String.valueOf(item.getQuantity()));
			nodeCartBarter.addAttribute("SalePrice", item.getSalePrice()
					.toString());
			nodeCartBarter.addAttribute("ActivityTypeId",
					item.getActivityTypeId());
			nodeCartBarter.addAttribute("ActivityTypeName",
					item.getActivityTypeName());
			nodeCartBarter.addAttribute("ActivityId", item.getActivityId());
			nodeCartBarter.addAttribute("ActivityName", item.getActivityName());
			cartBarterSum = cartBarterSum.add(item.getSalePrice());
		}
		nodeCartMaster
				.addAttribute("CartBarterMoney", cartBarterSum.toString());

		return doc.asXML();
	}
}
