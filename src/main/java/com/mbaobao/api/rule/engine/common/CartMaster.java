package com.mbaobao.api.rule.engine.common;

import java.math.BigDecimal;

public class CartMaster {
	private String		customerId;
	private BigDecimal	amountTotal;
	private BigDecimal	amountPromote;
	private BigDecimal	amountNomal;
	private BigDecimal	amountMonery;
	private BigDecimal	saleOff;
	private BigDecimal	amountPay;
	private boolean		privilegFlag	= false;
	private String		privilegeId;
	private String		privilegeTitle;
	private String		privilegeItemId;
	private String		privilegeItemTitle;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public BigDecimal getAmountTotal() {
		return amountTotal;
	}

	public void setAmountTotal(BigDecimal amountTotal) {
		this.amountTotal = amountTotal;
	}

	public BigDecimal getAmountPromote() {
		return amountPromote;
	}

	public void setAmountPromote(BigDecimal amountPromote) {
		this.amountPromote = amountPromote;
	}

	public BigDecimal getAmountNomal() {
		return amountNomal;
	}

	public void setAmountNomal(BigDecimal amountNomal) {
		this.amountNomal = amountNomal;
	}

	public BigDecimal getAmountMonery() {
		return amountMonery;
	}

	public void setAmountMonery(BigDecimal amountMonery) {
		this.amountMonery = amountMonery;
	}

	public BigDecimal getSaleOff() {
		return saleOff;
	}

	public void setSaleOff(BigDecimal saleOff) {
		this.saleOff = saleOff;
	}

	public BigDecimal getAmountPay() {
		return amountPay;
	}

	public void setAmountPay(BigDecimal amountPay) {
		this.amountPay = amountPay;
	}

	public boolean isPrivilegFlag() {
		return privilegFlag;
	}

	public void setPrivilegFlag(boolean privilegFlag) {
		this.privilegFlag = privilegFlag;
	}

	public String getPrivilegeId() {
		return privilegeId;
	}

	public void setPrivilegeId(String privilegeId) {
		this.privilegeId = privilegeId;
	}

	public String getPrivilegeTitle() {
		return privilegeTitle;
	}

	public void setPrivilegeTitle(String privilegeTitle) {
		this.privilegeTitle = privilegeTitle;
	}

	public String getPrivilegeItemId() {
		return privilegeItemId;
	}

	public void setPrivilegeItemId(String privilegeItemId) {
		this.privilegeItemId = privilegeItemId;
	}

	public String getPrivilegeItemTitle() {
		return privilegeItemTitle;
	}

	public void setPrivilegeItemTitle(String privilegeItemTitle) {
		this.privilegeItemTitle = privilegeItemTitle;
	}
}
