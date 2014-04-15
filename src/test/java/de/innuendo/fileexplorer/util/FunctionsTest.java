package de.innuendo.fileexplorer.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;


public class FunctionsTest {
	@Test
	public void replaceEnvImplTestOK() throws Exception {
		Map<String,String> m = new HashMap<String,String>(){{this.put("HOME","home");}};
		String[][] data = new String[][] {
			new String[] {
					"my $HOME is my castle","my home is my castle"
			},
			new String[] {
					"my $HOME$ is my castle","my home$ is my castle"
			},
			new String[] {
					"my $$HOME$ is my castle","my $home$ is my castle"
			},
			new String[] {
					"my HOME is my castle","my HOME is my castle"
			}
		};
		for (String[] d : data) {
			String val = d[0];
			String exp = d[1];
			String res = Functions.replaceEnvImpl(val, m);
			Assert.assertEquals(exp, res);
		}
	}
}
