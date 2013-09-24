import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

/**
 * Question 2 / 2 (Amazon Campus（9）: MM-Chess)

There is an interesting game called MM-Chess. The size of the board is 1*N, every grid has a score (non-negative).  The first grid is the start and the Nth grid is the end. The game requires players to control the chess starting from the starting point to the end.

There're four types of cards in the game and the total number is M. Each type of card are labeled one integer number in [1,4]. After using a card with number x on it, the chess will move x steps forward. Each time, player will choose one unused card to move the chess forward, and each card can only be used once. In the game, the chess gains the score at the starting point automatically.  When the chess arrives at a new grid, it also gets the score on that point. The goal of the game is to get the most score.

Input:

                The first line contains two integers N (the size of board) and M (the number of the cards).

                The second line contains N integers, meaning the scores on the the board (the i-th integer corresponds to the score on the i-th grid).

                The third line contains M integers, meaning the numbers on the M cards.

                The sum of the number of M cards equals to N-1.

                You can assume that 1 <= N <= 350, 1 <= M <= 120, and that the number of cards are less than 40 for each kind.

Output:

                One integer. The most score that player can get.

 

Sample Input 1

4 2

1 2 1 2

1 2

Sample Output 1

5

Given two cards with number 1 and number 2 each, we have two choices: path one is 1 -> 2 -> 2, path two is 1 -> 1 -> 2. The maximum score is 5, which is the output.

 

Sample Input 2

5 3

1 2 1 2 1

1 2 1

Sample Output 2

6

Given three cards (one can move 2 steps, two can move 1 steps), we have three choices: path one is 1 -> 2 -> 1 -> 1, path two is 1 -> 2 -> 2 -> 1, path three is 1 -> 1 -> 2 -> 1. The maximum score is 6, which is the output.

 * @author Administrator
 *
 */


public class MM_Cheese {
	
	private static int maxscore_ = 0;
	static int a = 0;
	static int b = 0;
	
	public static void p(int N, int M, int[]scores, List<Integer>steps){
		int offset = 0;
		int sum = scores[0];

		aa(sum, offset, scores, steps);
	}
	
	public static void aa(int sum, int offset, int[]scores, List<Integer>steps){
		if(steps.size()==0){
			a++;
			if(sum>maxscore_)
				maxscore_ = sum;
			return;
		}
		for(int i=0; i<steps.size(); i++){
			int a = sum + scores[offset + steps.get(i)];
			List<Integer>steps_ = new ArrayList<Integer>(steps);
			steps_.remove(i);
//			System.out.println("("+offset+","+steps.get(i)+"," + a +")");
			aa(a, (offset + steps.get(i)), scores, steps_);
		}
	}
	
	/**
	 * MM_Chess
	 * @param N		棋谱长
	 * @param M		步长数组长度
	 * @param scores	棋谱得分数组
	 * @param steps		步长数组
	 * @author Harry Huang
	 */
	public static void MM_Chess(int N, int M, int[]scores, List<Integer>steps){
		
		//统计步长分布
		Map<Integer, Integer> stepMap = new HashMap<Integer, Integer>();
		for(Integer step : steps){
			if(stepMap.containsKey(step))
				stepMap.put(step, stepMap.get(step)+1);
			else
				stepMap.put(step, 1);
		}
		
		//动归搜全局最优解
		int maxResult = getMAX_X(N-1, scores, stepMap);
		System.out.println("动态规划答案：" + maxResult);
		
	}
	
	/**
	 * 求MAX(N)
	 * @param N		当前尾格索引	
	 * @param scores	得分数组
	 * @param stepMap	当前步长分布
	 * @return	从第1格到第N格的最大得分
	 * @author Harry Huang
	 */
	public static int getMAX_X(int N, int[]scores, Map<Integer, Integer> stepMap){
		//递归结束条件，返回到了第1格
		if(N==0){
			return scores[0];
		}
		
		int maxResult = 0;
		for(Entry<Integer, Integer> entry : stepMap.entrySet()){
			if(entry.getValue() != 0){	//判断是否还有该步长的牌
				//还有牌的话，可以继续向前递归，更新步长分布
				Map<Integer, Integer> newMap = new HashMap<Integer, Integer>(stepMap);
				newMap.put(entry.getKey(), entry.getValue()-1);
				//动归
				int result = getMAX_X(N-entry.getKey(), scores, newMap);
				//判断取max{MAX[N-1],MAX[N-2],MAX[N-3],MAX[N-4]}
				if((scores[N]+result) > maxResult){
					maxResult = scores[N]+result;
				}
			}
		}
		return maxResult;
	}
	
	public static void main(String args[]){
		/**
		 * 	18 7
			1 4 5 4 5 1 2 8 3 10 1 2 3 4 6 9 1 3
			3 2 1 4 1 3 3
		 */
		int N = 4,M = 2;
		int line = 0;
		Scanner str = new Scanner(System.in);
		String[] pars=str.nextLine().split(" ");
		N = Integer.parseInt(pars[0]);
		M = Integer.parseInt(pars[1]);
		int[] scores = new int[N];
		int[] steps = new int[M];
		
		str = new Scanner(System.in);
		pars=str.nextLine().split("[ ]");
		for(int i=0; i<N; i++){
			scores[i] = Integer.parseInt(pars[i]);
		}
		
		str = new Scanner(System.in);
		pars=str.nextLine().split("[ ]");
		List<Integer> sList = new ArrayList<Integer>();
		List<Integer> cList = new ArrayList<Integer>();
		
		
		for(int i=0; i<M; i++){
			steps[i] = Integer.parseInt(pars[i]);
			sList.add(Integer.parseInt(pars[i]));
		}
		
		//全排列
		p(N, M, scores, sList);
		System.out.println("标准答案(暴力解法)："+maxscore_);
		//动归
		MM_Chess(N, M, scores, sList);
//		System.out.println("排列="+b);

		
		
//		int currentOffset = 0;
//		int result = scores[0];
//		for(int i=0; i<M; i++){
//			//iter
//			int maxscore = 0;
//			for(int index = 0; index<sList.size(); index++){
//				if(scores[currentOffset + sList.get(index)] > maxscore){
//					cList.clear();
//					cList.add(index);
//					maxscore = scores[currentOffset + sList.get(index)];
//				}
//				else if(scores[currentOffset + sList.get(index)] == maxscore){
//					cList.add(index);
//					maxscore = scores[currentOffset + sList.get(index)];
//				}
//			}
//			
//			result += maxscore;
//			
//			int stepIndex = 0;
//			if(cList.size() > 1){
//				int minIndex = 0;
//				int minStop = sList.get(cList.get(0));
//				for(int k=1;k<cList.size();k++){
//					if(sList.get(cList.get(k))<minStop)
//						minIndex = k;
//				}
//				stepIndex = minIndex;
//			}
//			currentOffset += sList.get(cList.get(stepIndex));
//			System.out.println("ddd="+cList.get(stepIndex));
//			sList.remove((0+cList.get(stepIndex)));
//			System.out.println(sList.toString());
//		}
//		System.out.println(result);
	}
}
