/*
 * Copyright (C) 2007 by
 * 
 * 	Xuan-Hieu Phan
 *	hieuxuan@ecei.tohoku.ac.jp or pxhieu@gmail.com
 * 	Graduate School of Information Sciences
 * 	Tohoku University
 * 
 *  Cam-Tu Nguyen
 *  ncamtu@gmail.com
 *  College of Technology
 *  Vietnam National University, Hanoi
 *
 * JGibbsLDA is a free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JGibbsLDA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JGibbsLDA; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package jgibblda;

import org.kohsuke.args4j.*;

import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.rank.topic.TopicFile;

public class LDA {
	
	private LDACmdOption option = null;
	private Estimator estimator = null;
	
	public LDA(){
		option = new LDACmdOption();
		estimator = new Estimator();
		option.est = true;
		option.inf = false;
		option.alpha = 0.01;
		option.beta = 0.01;
		option.K = 100;
		option.niters = SystemConfiguration.LDAIteration;
		option.savestep = 500;
		option.twords = 20;
		option.dir = SystemConfiguration.LDAPath;
		
	}
	
	public static void main(String args[]){
		
		LDA lda = new LDA();
//		lda.getTopicDistribuiton("00000000");
		lda.generateTopicModel("00000000");
		
		
	}
	
	public double[][] getTopicDistribuiton(String datafile){
		option.dfile = datafile;
		estimator.init(option);
		double[][] topic = estimator.estimateTopic();
		return topic;
	}
	
	public void generateTopicModel(String datafile){
		option.dfile = datafile;
		estimator.init(option);
		estimator.estimate();
	}
	
	public static void showHelp(CmdLineParser parser){
		System.out.println("LDA [options ...] [arguments...]");
		parser.printUsage(System.out);
	}
	
}
