package com.mbaobao.api.rule.engine.common;

import java.math.BigDecimal;
import java.util.UUID;

public class CartDetail {
	private String detailId;
	private String productId;
    private String title;
    private BigDecimal weight;
    private BigDecimal standardPrice;
    private int quantity = 1;
    private BigDecimal salePrice;
    private boolean promoteFlag = false;
    private String saleOff;
    private String promotionId;
    private String promotionTitle;
    private String promoteItemId;
    private String promoteItemTitle;

    public CartDetail()
    {
        this.detailId =UUID.randomUUID().toString().replace("-", "");
    }

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public BigDecimal getStandardPrice() {
		return standardPrice;
	}

	public void setStandardPrice(BigDecimal standardPrice) {
		this.standardPrice = standardPrice;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(BigDecimal salePrice) {
		this.salePrice = salePrice;
	}

	public boolean isPromoteFlag() {
		return promoteFlag;
	}

	public void setPromoteFlag(boolean promoteFlag) {
		this.promoteFlag = promoteFlag;
	}

	public String getSaleOff() {
		return saleOff;
	}

	public void setSaleOff(String saleOff) {
		this.saleOff = saleOff;
	}

	public String getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(String promotionId) {
		this.promotionId = promotionId;
	}

	public String getPromotionTitle() {
		return promotionTitle;
	}

	public void setPromotionTitle(String promotionTitle) {
		this.promotionTitle = promotionTitle;
	}

	public String getPromoteItemId() {
		return promoteItemId;
	}

	public void setPromoteItemId(String promoteItemId) {
		this.promoteItemId = promoteItemId;
	}

	public String getPromoteItemTitle() {
		return promoteItemTitle;
	}

	public void setPromoteItemTitle(String promoteItemTitle) {
		this.promoteItemTitle = promoteItemTitle;
	}

    
}
