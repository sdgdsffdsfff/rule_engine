package com.mbaobao.api.rule.engine.common;

import java.math.BigDecimal;

public class CommonUtils {
	public static boolean compareBigDecimal(BigDecimal a, BigDecimal b, BigDecimal c) {
		if ((a.compareTo(b) == 0 || a.compareTo(b) == 1) &&
			(a.compareTo(c) == 0 || a.compareTo(c) == -1)) {
			return true;
		} else {
			return false;
		}
	}
}
