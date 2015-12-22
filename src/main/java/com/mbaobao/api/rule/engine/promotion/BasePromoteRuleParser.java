package com.mbaobao.api.rule.engine.promotion;

public class BasePromoteRuleParser {
	private String promotionId;
	private String promotionTitle;
	private String promoteItemId;
	private String promoteItemTitle;

    public void SetBaseInfo(String promotionId, String promotionTitle, String promoteItemId, String promoteItemTitle)
    {
        this.promotionId = promotionId;
        this.promotionTitle = promotionTitle;
        this.promoteItemId = promoteItemId;
        this.promoteItemTitle = promoteItemTitle;
    }

	public String getPromotionId() {
		return promotionId;
	}

	public String getPromotionTitle() {
		return promotionTitle;
	}

	public String getPromoteItemId() {
		return promoteItemId;
	}

	public String getPromoteItemTitle() {
		return promoteItemTitle;
	}
}