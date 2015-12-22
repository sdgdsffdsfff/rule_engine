package com.mbaobao.api.rule.engine.common;

import java.util.ArrayList;
import java.util.List;

public class CartView {
	private CartMaster			_cartMaster;
	private List<CartDetail>	_cartDetails;
	private List<CartBarter>	_cartBarters;
	private List<CartGift>		_cartGifts;

	public CartView() {
		this._cartDetails = new ArrayList<CartDetail>();
		this._cartBarters = new ArrayList<CartBarter>();
		this._cartGifts = new ArrayList<CartGift>();
	}
	
	public void AddCartGift(CartGift cartGift) {
		this._cartGifts.add(cartGift);
	}

	public boolean ContainsCartGift(CartGift cartGift) {
		return this._cartGifts.contains(cartGift);
	}

	public List<CartGift> GetCartGifts() {
		return this._cartGifts;
	}

	public void AddCartBarter(CartBarter cartBarter) {
		this._cartBarters.add(cartBarter);
	}

	public boolean ContainsCartBarter(CartBarter cartBarter) {
		return this._cartBarters.contains(cartBarter);
	}

	public List<CartBarter> GetCartBarters() {
		return this._cartBarters;
	}

	public void AddDetail(CartDetail cartDetail) {
		this._cartDetails.add(cartDetail);
	}

	public boolean ContainsDetail(CartDetail cartDetail) {
		return this._cartDetails.contains(cartDetail);
	}

	public List<CartDetail> GetCartDetails() {
		return this._cartDetails;
	}

	public List<CartDetail> GetNormalCartDetails() {
		List<CartDetail> cartDetails = new ArrayList<CartDetail>();
		for (CartDetail cartDetail : this._cartDetails) {
			if (!cartDetail.isPromoteFlag()) {
				cartDetails.add(cartDetail);
			}
		}
		return cartDetails;
	}

	public void SetCartMaster(CartMaster cartMaster) {
		this._cartMaster = cartMaster;
	}

	public CartMaster GetCartMaster() {
		return this._cartMaster;
	}
}
