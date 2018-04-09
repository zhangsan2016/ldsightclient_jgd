package com.ldgd.em.funsdk.support.config;

public interface JsonListener {
	String getSendMsg();

	boolean onParse(String json);
}
