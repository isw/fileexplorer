package de.innuendo.fileexplorer.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Functions {
	private static Pattern PATTERN = Pattern.compile("\\$(\\w+)");
	public static final String replaceEnv (String val) {		
		Map<String,String> env = System.getenv();
		return replaceEnvImpl(val, env);
	}
	
	static final String replaceEnvImpl (String val, Map<String,String> replace) {
		Matcher matcher = PATTERN.matcher(val);
		StringBuilder res = new StringBuilder();
		int i=0;
		while (matcher.find()) {
			res.append(val.substring(i, matcher.start()));
			String key = matcher.group(1);
			if (replace.containsKey(key))
				res.append(replace.get(key));
			else
				res.append(key);
		    i = matcher.end();
		}
		res.append(val.substring(i, val.length()));
		return res.toString();
		
	}
}
