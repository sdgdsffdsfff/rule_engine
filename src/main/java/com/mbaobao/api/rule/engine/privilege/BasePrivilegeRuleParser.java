package com.mbaobao.api.rule.engine.privilege;

public class BasePrivilegeRuleParser {
	private String	privilegeId;
	private String	privilegeTitle;
	private String	privilegeItemId;
	private String	privilegeItemTitle;

	public void SetBaseInfo(String privilegeId, String privilegeTitle, String privilegeItemId,
			String privilegeItemTitle) {
		this.privilegeId = privilegeId;
		this.privilegeTitle = privilegeTitle;
		this.privilegeItemId = privilegeItemId;
		this.privilegeItemTitle = privilegeItemTitle;
	}

	public String getPrivilegeId() {
		return privilegeId;
	}

	public String getPrivilegeTitle() {
		return privilegeTitle;
	}

	public String getPrivilegeItemId() {
		return privilegeItemId;
	}

	public String getPrivilegeItemTitle() {
		return privilegeItemTitle;
	}
}
