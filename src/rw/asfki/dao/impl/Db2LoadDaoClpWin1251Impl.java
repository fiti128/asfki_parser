package rw.asfki.dao.impl;

import java.io.IOException;

import rw.asfki.error.ErrorManager;

public class Db2LoadDaoClpWin1251Impl extends Db2LoadDaoClpImpl {
	private static final String WIN1251_CODEPAGE = "1251";
	
	private Db2LoadDaoClpWin1251Impl(ErrorManager errorManager)
			throws IOException {
		super(errorManager);
	}
	public static Db2LoadDaoClpWin1251Impl getInstance(ErrorManager errorManager) throws IOException {
		return new Db2LoadDaoClpWin1251Impl(errorManager);
	}
	@Override
	protected String getCodepage() {
		return WIN1251_CODEPAGE;
	}
}
