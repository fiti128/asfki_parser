package rw.asfki.dao.impl;

import java.io.IOException;

import rw.asfki.error.ErrorManager;

public class Db2LoadDaoClpUtf8Impl extends Db2LoadDaoClpImpl {
	
	private static final String UTF8_CODEPAGE = "1208";

	private Db2LoadDaoClpUtf8Impl(ErrorManager errorManager) throws IOException {
		super(errorManager);

	}

	public static Db2LoadDaoClpUtf8Impl getInstance(ErrorManager errorManager) throws IOException {
		return new Db2LoadDaoClpUtf8Impl(errorManager);
	}
	
	@Override
	protected String getCodepage() {
		return UTF8_CODEPAGE;
	}
}
