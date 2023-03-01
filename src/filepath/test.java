package filepath;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class test {
//	static String condition = null;
//	static String MainCond = "";
//	static StringBuffer andOp =new StringBuffer();
//	static String orOp = "";
//	static String result="";
//	static int andIndex;
	static String resultStr;
	public static String readCidlFileLineByLine_component(String line) throws IOException {
		resultStr = "";
				
				if (line.contains("when")) {
					processWhenCondition(line);			
				}

		return resultStr;
	}

	private static void processWhenCondition(String line) {
		// TODO Auto-generated method stub
		String andOPStr = "";
		String[] stringArray = line.split("when");
		String str = stringArray[1].replace("{", "").replace("FEAT_","").replace("Feat_","").replace("feat_","")
				.replace("thirdParty", "").replace(" (condition) */", "");
		String splitStr[] = str.split("&&");
		int ct = 1;
		for (String andOp : splitStr) {
			if (str.contains("&&") && str.contains("||")) {
				if (andOp.contains("||")) {
					String splitOROp[] = andOp.split("\\|\\|");
					int ctOp = 1;
					for (String subOROp : splitOROp) {
						if (subOROp.contains("!") && !subOROp.contains("!=")) {
							if (subOROp.contains("(")) {
								subOROp = subOROp.replace(subOROp, "(NOT" + subOROp.trim() + ") ");
								subOROp = subOROp.replace("!", "");
							} else {
								subOROp = subOROp.replace(subOROp, "NOT(" + subOROp.trim() + ") ");
								subOROp = subOROp.replace("!", "");
							}
						}
						if (subOROp.contains("!=")) {
							if (subOROp.contains("(")) {
								String notOp = " (NOT" + subOROp.trim() + ") ";
								subOROp = notOp.replace("!=", "__");
							} else {
								String notOp = " NOT(" + subOROp.trim() + ") ";
								subOROp = notOp.replace("!=", "__");
							}
						}
						if (ctOp < splitOROp.length)
							andOPStr = andOPStr.concat(subOROp) + "OR ";
						else
							andOPStr = andOPStr.concat(subOROp);
						ctOp++;
					}
				}

				else if (andOp.contains("!") && !andOp.contains("!=")) {
					if (andOp.contains("(")) {
						andOp = andOp.replace(andOp, "(NOT" + andOp.trim() + ") ");
						andOp = andOp.replace("!", "");
					} else {
						andOp = andOp.replace(andOp, "NOT(" + andOp.trim() + ") ");
						andOp = andOp.replace("!", "");
					}
				}
				if (andOp.contains("!=")) {
					if (andOp.contains("(")) {
						String notOp = " (NOT" + andOp.trim() + ") ";
						andOp = notOp.replace("!=", "__");
					} else {
						String notOp = " NOT(" + andOp.trim() + ") ";
						andOp = notOp.replace("!=", "__");
					}
				}
			}
			if (ct < splitStr.length) {
				if (andOp.contains("||")) {
					andOp = andOp.replace(andOp, "");
					andOPStr = andOPStr.concat(andOp) + "AND ";
				} else if (andOp.contains("!") && !andOp.contains("!=")) {
					if (andOp.contains("(")) {
						andOp = andOp.replace(andOp, "(NOT" + andOp.trim() + ") ");
						andOp = andOp.replace("!", "");
						andOPStr = andOPStr.concat(andOp) + "AND ";
					} else {
						andOp = andOp.replace(andOp, "NOT(" + andOp.trim() + ") ");
						andOp = andOp.replace("!", "");
						andOPStr = andOPStr.concat(andOp) + "AND ";
					}

				} else if (andOp.contains("!=")) {
					if (andOp.contains("(")) {
						String notOp = " (NOT" + andOp.trim() + ") ";
						andOp = notOp.replace("!=", "__");
						andOPStr = andOPStr.concat(andOp) + "AND ";
					} else {
						String notOp = " NOT(" + andOp.trim() + ") ";
						andOp = notOp.replace("!=", "__");
						andOPStr = andOPStr.concat(andOp) + "AND ";
					}
				} else
					andOPStr = andOPStr.concat(andOp) + "AND ";
			} else {
				if (andOp.contains("||")) {
					andOp = andOp.replace(andOp, "");
					andOPStr = andOPStr.concat(andOp);
				} else if (andOp.contains("!") && !andOp.contains("!=")) {
					if (andOp.contains("(")) {
						andOp = andOp.replace(andOp, "(NOT" + andOp.trim() + ") ");
						andOp = andOp.replace("!", "");
						andOPStr = andOPStr.concat(andOp);
					} else {
						andOp = andOp.replace(andOp, "NOT(" + andOp.trim() + ") ");
						andOp = andOp.replace("!", "");
						andOPStr = andOPStr.concat(andOp);
					}

				} else if (andOp.contains("!=")) {
					if (andOp.contains("(")) {
						String notOp = " (NOT" + andOp.trim() + ") ";
						andOp = notOp.replace("!=", "__");
						andOPStr = andOPStr.concat(andOp);
					} else {
						String notOp = " NOT(" + andOp.trim() + ") ";
						andOp = notOp.replace("!=", "__");
						andOPStr = andOPStr.concat(andOp);
					}
				} else
					andOPStr = andOPStr.concat(andOp);
			}
			ct++;
		}

		ct = 1;
		String splitStrOR[] = andOPStr.split("\\|\\|");
		for (String oROp : splitStrOR) {
			if (str.contains("||")) {
				if (oROp.contains("!") && !oROp.contains("!=")) {
					if (oROp.contains("(")) {
						oROp = oROp.replace(oROp, "(NOT" + oROp.trim() + ") ");
						oROp = oROp.replace("!", "");
					} else {
						oROp = oROp.replace(oROp, "NOT(" + oROp.trim() + ") ");
						oROp = oROp.replace("!", "");
					}
				}
				if (oROp.contains("!=")) {
					if (oROp.contains("(")) {
						String notOp = " (NOT" + oROp.trim() + ")";
						oROp = notOp.replace("!=", "__");
					} else {
						String notOp = " NOT(" + oROp.trim() + ")";
						oROp = notOp.replace("!=", "__");
					}

				}
			}
			if (ct < splitStrOR.length)
				resultStr = resultStr.concat(oROp) + "OR ";
			else
				resultStr = resultStr.concat(oROp);
			ct++;
		}

		if (!str.contains("&&") && !str.contains("||")) {
			if (str.contains("!") && !str.contains("!=")) {
				if (str.contains("(")) {
					str = str.replace(str, "(NOT" + str.trim() + ") ");
					str = str.replace("!", "");
					resultStr = str;
				} else {
					str = str.replace(str, "NOT(" + str.trim() + ") ");
					str = str.replace("!", "");
					resultStr = str;
				}
			}
			if (str.contains("!=")) {
				if (str.contains("(")) {
					String notOp = " (NOT" + str.trim() + ") ";
					str = notOp.replace("!=", "__");
					resultStr = str;
				} else {
					String notOp = " NOT(" + str.trim() + ") ";
					str = notOp.replace("!=", "__");
					resultStr = str;
				}
			}

		}
		if (str.contains("||") && !str.contains("&&")) {
			resultStr = "";
			String[] splitOr = str.split("\\|\\|");
			int counter_oR = 1;
			for (String oRCondi : splitOr) {
				if (oRCondi.contains("!") && !oRCondi.contains("!=")) {
					if (oRCondi.contains("(")) {
						oRCondi = oRCondi.replace(oRCondi, "(NOT" + oRCondi.trim() + ") ");
						oRCondi = oRCondi.replace("!", "");

					} else {
						oRCondi = oRCondi.replace(oRCondi, "NOT(" + oRCondi.trim() + ") ");
						oRCondi = oRCondi.replace("!", "");

					}
				}
				if (oRCondi.contains("!=")) {
					if (oRCondi.contains("(")) {
						String notOp = " (NOT" + oRCondi.trim() + ") ";
						oRCondi = notOp.replace("!=", "__");

					} else {
						String notOp = " NOT(" + oRCondi.trim() + ") ";
						oRCondi = notOp.replace("!=", "__");

					}
				}
				if (counter_oR < splitOr.length) {
					resultStr = resultStr.concat(oRCondi) + "OR ";
				} else {
					resultStr = resultStr.concat(oRCondi);

				}
				counter_oR++;
			}
		}
	}

	public static void main(String[] args) throws IOException {
		
//	    String line="when ((FEAT_CFG_SYSTEM_TYPE__cHBE) || (FEAT_CFG_SW_ACTSRV_SERVICES_IV!=_ACT_TEST__cAVAILABLE) && cond ";
		String line="when (FEAT_AUTOSAR_NVRAM ) || ((!FEAT_AUTOSAR_NVRAM) && (AUTOSAR_MEMSTACK == cEEP_NVM_WRAPPER))";
//		String line="when (CFG_ACTUATOR_MCI_AVAILABLE)&&(!CFG_ACTUATOR_LPF_AVAILABLE) || (CFG_ACTUATOR_PUMP_AVAILABLE) && (CFG_SYSsTEM_TYPE != cHBE)";
//		String line=" when (condition) */";
		String when = readCidlFileLineByLine_component(line);
        if(when !="") {
	    System.out.println(when);
        }
        else {
        	System.out.println("error");
        }
	}
}