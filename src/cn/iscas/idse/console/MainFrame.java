package cn.iscas.idse.console;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.EdgedBalloonStyle;

import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.index.Index;
import cn.iscas.idse.rank.MatrixWriter;
import cn.iscas.idse.rank.PersonalRank;
import cn.iscas.idse.rank.topic.InputDataForLDA;
/**
 * swing������С����ϵͳ����
 * @author HarryHuang
 */
public class MainFrame {
	
	
	public void startUP() throws Exception{
		//��ʼ��ϵͳ����
		SystemConfiguration.init();
			    
		//����ϵͳ����С��������
	    TrayIcon trayIcon = null;
		if (SystemTray.isSupported()){ // �ж�ϵͳ�Ƿ�֧��ϵͳ����
		
			SystemTray tray = SystemTray.getSystemTray(); // ����ϵͳ����
			Image image = Toolkit.getDefaultToolkit().getImage(SystemConfiguration.rootPath + "WEB-INF/pictures/icon.png");// ����ͼƬ,����Ҫд���ͼ��·��Ŷ
		   
			ActionListener listener = new ActionListener() {
				 public void actionPerformed(ActionEvent e) {
					 //�жϵ�ǰϵͳ�Ƿ�֧��Java AWT Desktop��չ
					 if(java.awt.Desktop.isDesktopSupported()){
				            try {
				                //����һ��URIʵ��
				                java.net.URI uri = java.net.URI.create("http://localhost:8080"); 
				                //��ȡ��ǰϵͳ������չ
				                java.awt.Desktop dp = java.awt.Desktop.getDesktop();
				                //�ж�ϵͳ�����Ƿ�֧��Ҫִ�еĹ���
				                if(dp.isSupported(java.awt.Desktop.Action.BROWSE)){
				                    //��ȡϵͳĬ�������������
				                    dp.browse(uri);    
				                }
				            } catch(java.lang.NullPointerException e1){
				                //��ΪuriΪ��ʱ�׳��쳣
				            } catch (java.io.IOException e1) {
				                //��Ϊ�޷���ȡϵͳĬ�������
				            }             
				        }
				 }
		   };
		   // ���������˵�
		   PopupMenu popup = new PopupMenu();
		   
		   
		   
		   //������ѡ��
		   MenuItem mainFrameItem = new MenuItem("open search home");
		   mainFrameItem.addActionListener(listener);
		   
		   //�����ý���
		   MenuItem configItem = new MenuItem("setting");
		   configItem.addActionListener(new ActionListener() {
				 public void actionPerformed(ActionEvent e) {
					 //�жϵ�ǰϵͳ�Ƿ�֧��Java AWT Desktop��չ
					 if(java.awt.Desktop.isDesktopSupported()){
				            try {
				                //����һ��URIʵ��
				                java.net.URI uri = java.net.URI.create("http://localhost:8080/config.jsp"); 
				                //��ȡ��ǰϵͳ������չ
				                java.awt.Desktop dp = java.awt.Desktop.getDesktop();
				                //�ж�ϵͳ�����Ƿ�֧��Ҫִ�еĹ���
				                if(dp.isSupported(java.awt.Desktop.Action.BROWSE)){
				                    //��ȡϵͳĬ�������������
				                    dp.browse(uri);    
				                }
				            } catch(java.lang.NullPointerException e1){
				                //��ΪuriΪ��ʱ�׳��쳣
				            } catch (java.io.IOException e1) {
				                //��Ϊ�޷���ȡϵͳĬ�������
				            }             
				        }
				 }
		   });
		   
		   //����ѡ��
		   MenuItem indexItem = new MenuItem("start index");
		   MenuItem indexItemWithoutGraph = new MenuItem("start index (without Graph Build)");
		   
		   //��������ѡ��
		   MenuItem indexUpdateItem = new MenuItem("update index");
		   MenuItem indexUpdateItemWithoutGraph = new MenuItem("update index (without Graph Build)");
		   
		   //����LDA��������
		   MenuItem LDADataGeneratorItem = new MenuItem("generate LDA data");
		   LDADataGeneratorItem.addActionListener(new ActionListener() {
			   public void actionPerformed(ActionEvent e) {
				   InputDataForLDA LDADataBuilder = new InputDataForLDA(100000);
				   LDADataBuilder.executeFormat();
				   LDADataBuilder.saveWordListBuffer();
			   }
		   });
		   
		   //����pageRankGraph(��Knowledge Graph)ѡ��
		   MenuItem pageRankGrapItem = new MenuItem("build knowledge graph");
		   
		   //ִ��pageRank����
		   MenuItem pageRankIterationItem = new MenuItem("iterate PageRank");
		   pageRankIterationItem.addActionListener(new ActionListener() {
			   public void actionPerformed(ActionEvent e) {
				   PersonalRank pageRankRunner = new PersonalRank();
				   pageRankRunner.run();
			   }
		   });
		   
		   //�˳�����ѡ��
		   MenuItem exitItem = new MenuItem("exit");
		   exitItem.addActionListener(new ActionListener() {
			   public void actionPerformed(ActionEvent e) {
//				   if (JOptionPane.showConfirmDialog(null, "ȷ���˳�ϵͳ") == 0) 
				   System.exit(0);
			   }
		   });
		   
		   popup.add(mainFrameItem);
		   popup.add(configItem);
		   popup.add(indexItem);
		   popup.add(indexItemWithoutGraph);
		   popup.add(indexUpdateItem);
		   popup.add(indexUpdateItemWithoutGraph);
		   popup.add(LDADataGeneratorItem);
		   popup.add(pageRankGrapItem);
		   popup.add(pageRankIterationItem);
		   popup.add(exitItem);
		   
		   trayIcon = new TrayIcon(image, "IDSE", popup);// ����trayIcon
		   trayIcon.addActionListener(listener);	//���˫������ͼ��ʱ����Ӧ�¼�
		   
		   indexItem.addActionListener(new IndexItemActionListener(trayIcon));
		   indexUpdateItem.addActionListener(new IndexUpdateItemActionListener(trayIcon));
		   pageRankGrapItem.addActionListener(new KnowLedgeGraphBuildItemActionListener(trayIcon));
		   indexItemWithoutGraph.addActionListener(new IndexItemWithoutGraphActionListener(trayIcon));
		   indexUpdateItemWithoutGraph.addActionListener(new IndexUpdateItemWithoutGraphActionListener(trayIcon));
		   
		   try {
			   tray.add(trayIcon);
		   } catch (AWTException e1) {
			   e1.printStackTrace();
		   }
		   
		   
		}
		//������Ƕjetty������
		ServerIDSE server = new ServerIDSE();
	    server.startUP();
	}
	
	public static void main(String args[]) throws Exception {
		MainFrame mainFrame = new MainFrame();
		mainFrame.startUP();
	}
}



/**
 * ���������ļ���
 * @author dell
 *
 */
class IndexItemWithoutGraphActionListener implements ActionListener{
	private TrayIcon trayIcon = null;
	
	public IndexItemWithoutGraphActionListener(TrayIcon trayIcon){
		this.trayIcon = trayIcon;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.trayIcon.setToolTip("IDSE | Indexing...");
		// index
		Index index = new Index();
		index.createIndex();
		this.trayIcon.setToolTip("IDSE | Indexing DONE.");
	}
}

class IndexItemActionListener implements ActionListener{
	private TrayIcon trayIcon = null;
	
	public IndexItemActionListener(TrayIcon trayIcon){
		this.trayIcon = trayIcon;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.trayIcon.setToolTip("IDSE | Indexing...");
		// index
		Index index = new Index();
		index.createIndex();
		// knowledge graph building
		MatrixWriter graphBuilder = new MatrixWriter();
		graphBuilder.run();
		PersonalRank pageRankRunner = new PersonalRank();
		pageRankRunner.run();
		this.trayIcon.setToolTip("IDSE | Indexing DONE.");
	}
}

class IndexUpdateItemWithoutGraphActionListener implements ActionListener{
	private TrayIcon trayIcon = null;
	
	public IndexUpdateItemWithoutGraphActionListener(TrayIcon trayIcon){
		this.trayIcon = trayIcon;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.trayIcon.setToolTip("IDSE | Updating Index...");
		// index 
		Index index = new Index();
		index.updateIndex();
		this.trayIcon.setToolTip("IDSE | Updating Index DONE.");
	}
}

class IndexUpdateItemActionListener implements ActionListener{
	private TrayIcon trayIcon = null;
	
	public IndexUpdateItemActionListener(TrayIcon trayIcon){
		this.trayIcon = trayIcon;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.trayIcon.setToolTip("IDSE | Updating Index...");
		// index 
		Index index = new Index();
		index.updateIndex();
		// knowledge graph building
		MatrixWriter graphBuilder = new MatrixWriter();
		graphBuilder.run();
		PersonalRank pageRankRunner = new PersonalRank();
		pageRankRunner.run();
		this.trayIcon.setToolTip("IDSE | Updating Index DONE.");
	}
}


class KnowLedgeGraphBuildItemActionListener implements ActionListener{
	private TrayIcon trayIcon = null;
	
	public KnowLedgeGraphBuildItemActionListener(TrayIcon trayIcon){
		this.trayIcon = trayIcon;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.trayIcon.setToolTip("IDSE | Building Knowledge Graph...");
		// knowledge graph building
		MatrixWriter graphBuilder = new MatrixWriter();
		graphBuilder.run();
		PersonalRank pageRankRunner = new PersonalRank();
		pageRankRunner.run();
		this.trayIcon.setToolTip("IDSE | Building Knowledge Graph DONE.");
	}
}
