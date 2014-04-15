package de.innuendo.fileexplorer.message.impl;

import de.innuendo.fileexplorer.message.api.IMessageProvider;

public class DummyMessageProviderImpl implements IMessageProvider {

	@Override
	public String getMessage(String key, Object... par) {
	  return key + ":"+String.valueOf(par);
	}

}
