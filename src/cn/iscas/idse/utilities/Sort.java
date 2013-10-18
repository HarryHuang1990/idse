package cn.iscas.idse.utilities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


public class Sort {
	
//	@SuppressWarnings({ "unchecked", "rawtypes"})
//	static public List<Feature> sortFeatures(List<Feature>list){
//		//����������������������
//        java.util.Collections.sort(list,new Comparator() 
//        {
//            public int compare(final Object o1,final Object o2) 
//            {
//                final Feature m1 = (Feature) o1;
//                final Feature m2 = (Feature) o2;
//                float r = m1.getIndex() - m2.getIndex();
//                if (r>0)
//                {
//                    return 1;
//                } 
//                else 
//                {
//                    return -1;
//                }
//            }
//        });
//        return list;
//	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static public List<Map.Entry<String, Integer>> sortStringIntegerMapDesc(List<Map.Entry<String, Integer>> list){
		java.util.Collections.sort(list,new Comparator() 
        {
            public int compare(final Object o1,final Object o2) 
            {
                final Map.Entry<String, Integer> m1 = (Map.Entry<String, Integer>) o1;
                final Map.Entry<String, Integer> m2 = (Map.Entry<String, Integer>) o2;
                double r = m1.getValue() - m2.getValue();
                if (r<0)
                {
                    return 1;
                }
                else 
                {
                    return -1;
                }
            }
        });
        return list;
	}
}
