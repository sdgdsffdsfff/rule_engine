package com.mbaobao.api.rule.engine.privilege;

import com.mbaobao.api.rule.engine.common.CartView;

public interface IPrivilegeRuleParser {
	void SetBaseInfo(String privilegeId, String privilegeTitle, String privilegeItemId, String privilegeItemTitle);
    void SetRule(String ruleXmlStr);
    void Parse(CartView cartView);
    String getPrivilegeId();
    String getPrivilegeTitle();
    String getPrivilegeItemId();
    String getPrivilegeItemTitle();
}
