package cn.iscas.idse.index.segmentation;

/**
 * Filter the punctuation from the input string. 
 * @author Harry Huang
 *
 */
public class PunctuationFilter {
	/**
	 * remove the punctuation that is contained in the term string.
	 * @param term
	 * @return
	 */
	public static String removePunctuation(String term){
		return term.replaceAll("[\\pP+~$`^=|<>�����ޣ�������������]", "").trim();
	}
	
	public static void main(String args[]){
		String s = "`~!@#$%^&*()_+-=[]\\{}|:\";',./<>?/*-+����������磥�ޣ��������ߣ������������ۣݣܣ���������������������������~����@#��%����&*��������+-={}|��������������������������/*-+�����������������������������������������ܣ���������������������������������";
		System.out.println(s.replaceAll("\\p{Punct}", ""));       
        System.out.println(s.replaceAll("[\\pP+~$`^=|<>�����ޣ�������������]", ""));       
        System.out.println(s.replaceAll("[\\p{P}+~$`^=|<>�����ޣ�������������]", "")); 
	}

}
