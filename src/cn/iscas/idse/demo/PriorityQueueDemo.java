package cn.iscas.idse.demo;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;

import cn.iscas.idse.search.entity.Score;
//public class PriorityQueueDemo
//{
//    public static void main(String[] args)
//    {
//    	PriorityQueueDemo a = new PriorityQueueDemo();
//        Comparator<Double> comparator =  a.new StringLengthComparator();
//        PriorityQueue<Double> queue = 
//            new PriorityQueue<Double>(1000,comparator);
//        Random random = new Random();
//		for(int i=0; i<100; i++)
//			queue.add(random.nextDouble());
//        while (queue.size() != 0)
//        {
//            System.out.println(queue.remove());
//        }
//    }
//    
//    public class StringLengthComparator implements Comparator<Double>
//    {
//        @Override
//        public int compare(Double x, Double y)
//        {
//            // Assume neither string is null. Real code should
//            // probably be more robust
//            if (x < y)
//            {
//                return -1;
//            }
//            if (x > y)
//            {
//                return 1;
//            }
//            return 0;
//        }
//    }
//}

public class PriorityQueueDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PriorityQueue<Score> scoreQueue = new PriorityQueue<Score>(100, new Comparator<Score>(){
			public int compare(final Score o1,final Score o2) 
	        {
	            double r = o1.getScore() - o2.getScore();
	            return r<0 ? 1 : -1;
	        }
		});
		
		PriorityQueue<Integer> scoreQueue2 = new PriorityQueue<Integer>(100);
		
		Random random = new Random();
		for(int i=0; i<100; i++)
			scoreQueue.add(new Score(i, random.nextFloat()));
		
		while(scoreQueue.size() != 0){
			Score score = scoreQueue.poll();
			System.out.println(score.getScore() + "\t" + score.getDocID());
		}
		
	}
	
}
