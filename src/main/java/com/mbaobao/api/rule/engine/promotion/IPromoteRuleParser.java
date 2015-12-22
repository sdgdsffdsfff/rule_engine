package com.mbaobao.api.rule.engine.promotion;

import com.mbaobao.api.rule.engine.common.CartView;

public interface IPromoteRuleParser {
	 void SetBaseInfo(String promotionId, String promotionTitle, String promoteItemId, String promoteItemTitle);            
     void SetRule(String ruleXmlStr);
     void Parse(CartView cartView);
     String getPromotionId();
     String getPromotionTitle();
     String getPromoteItemId();        
     String getPromoteItemTitle();
}
