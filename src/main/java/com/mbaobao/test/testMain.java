package com.mbaobao.test;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Response;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.mbaobao.api.rule.engine.common.DbExecutor;
import com.mbaobao.api.rule.engine.privilege.PV0A1PrivilegeRuleParser;
import com.mbaobao.api.rule.engine.privilege.PrivilegeRuleEngine;
import com.mbaobao.api.rule.engine.promotion.PromoteRuleEngine;

public class testMain {
	public static void main(String[] args) {
		String xmlStr = "<CartData>"
				+ "<CartMaster CustomerId=\"sss\" CartDateTime=\"ddssdds\"></CartMaster>"
				+ "<CartDetails>"
				+ "<CartDetail ProductId=\"1203065502 \" Title=\"斜跨\" Weight=\"0.39\" StandardPrice=\"23.00\" Quantity=\"1\"></CartDetail>"
				+ "<CartDetail ProductId=\"1009123456\" Title=\"手提\" Weight=\"0.69\" StandardPrice=\"56.00\" Quantity=\"2\"></CartDetail>"
				+ "</CartDetails>"
				+ "</CartData>";

		Connection conn = DbExecutor.getConnection();
		try {
			// 商品促销
			String sqlStrPromoteConfig = "SELECT UpdateDateTime FROM MMKeyValue WHERE MKey='PromoteConfig'";
			ResultSet rsPromote = DbExecutor.read(conn, sqlStrPromoteConfig);
			String promoteUpDateTime = null;
			if (rsPromote.next()) {
				promoteUpDateTime = rsPromote.getString("UpdateDateTime");
			}

			PromoteRuleEngine promoteRuleEngirne = PromoteRuleEngine.create();

			if (promoteRuleEngirne.GetUpdateDateTime().compareTo(promoteUpDateTime) != 0) {
				promoteRuleEngirne = new PromoteRuleEngine();
			}
			xmlStr = promoteRuleEngirne.ApplyParse(xmlStr);

			// ----------------------------------------------------------------

			// 订单优惠
			String sqlStrPrivilegeConfig = "SELECT UpdateDateTime FROM MMKeyValue WHERE MKey='PrivilegeConfig'";
			ResultSet rsPrivilege = DbExecutor.read(conn, sqlStrPrivilegeConfig);

			String privilegeUpDateTime = null;

			if (rsPrivilege.next()) {
				privilegeUpDateTime = rsPrivilege.getString("UpdateDateTime");
			}

			PrivilegeRuleEngine privilegeRuleEngine = PrivilegeRuleEngine.create();

			if (privilegeRuleEngine.GetUpdateDateTime().compareTo(privilegeUpDateTime) != 0) {
				privilegeRuleEngine = new PrivilegeRuleEngine();
			}
			xmlStr = privilegeRuleEngine.ApplyParse(xmlStr);

			System.out.println(xmlStr);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbExecutor.close(conn, null, null);
		}
	}
}
