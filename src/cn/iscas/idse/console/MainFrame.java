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
/**
 * swing程序最小化至系统托盘
 * @author HarryHuang
 */
public class MainFrame {
	
	
	public void startUP() throws Exception{
		//初始化系统参数
		SystemConfiguration.init();
			    
		//启动系统并最小化到托盘
	    TrayIcon trayIcon = null;
		if (SystemTray.isSupported()){ // 判断系统是否支持系统托盘
		
			SystemTray tray = SystemTray.getSystemTray(); // 创建系统托盘
			Image image = Toolkit.getDefaultToolkit().getImage(SystemConfiguration.rootPath + "WEB-INF/pictures/icon.png");// 载入图片,这里要写你的图标路径哦
		   
			ActionListener listener = new ActionListener() {
				 public void actionPerformed(ActionEvent e) {
					 //判断当前系统是否支持Java AWT Desktop扩展
					 if(java.awt.Desktop.isDesktopSupported()){
				            try {
				                //创建一个URI实例
				                java.net.URI uri = java.net.URI.create("http://localhost:8080"); 
				                //获取当前系统桌面扩展
				                java.awt.Desktop dp = java.awt.Desktop.getDesktop();
				                //判断系统桌面是否支持要执行的功能
				                if(dp.isSupported(java.awt.Desktop.Action.BROWSE)){
				                    //获取系统默认浏览器打开链接
				                    dp.browse(uri);    
				                }
				            } catch(java.lang.NullPointerException e1){
				                //此为uri为空时抛出异常
				            } catch (java.io.IOException e1) {
				                //此为无法获取系统默认浏览器
				            }             
				        }
				 }
		   };
		   // 创建弹出菜单
		   PopupMenu popup = new PopupMenu();
		   
		   
		   
		   //主界面选项
		   MenuItem mainFrameItem = new MenuItem("open search home");
		   mainFrameItem.addActionListener(listener);
		   
		   //打开设置界面
		   MenuItem configItem = new MenuItem("setting");
		   configItem.addActionListener(new ActionListener() {
				 public void actionPerformed(ActionEvent e) {
					 //判断当前系统是否支持Java AWT Desktop扩展
					 if(java.awt.Desktop.isDesktopSupported()){
				            try {
				                //创建一个URI实例
				                java.net.URI uri = java.net.URI.create("http://localhost:8080/config.jsp"); 
				                //获取当前系统桌面扩展
				                java.awt.Desktop dp = java.awt.Desktop.getDesktop();
				                //判断系统桌面是否支持要执行的功能
				                if(dp.isSupported(java.awt.Desktop.Action.BROWSE)){
				                    //获取系统默认浏览器打开链接
				                    dp.browse(uri);    
				                }
				            } catch(java.lang.NullPointerException e1){
				                //此为uri为空时抛出异常
				            } catch (java.io.IOException e1) {
				                //此为无法获取系统默认浏览器
				            }             
				        }
				 }
		   });
		   
		   //索引选项
		   MenuItem indexItem = new MenuItem("start index");
		   
		   //索引更新选项
		   MenuItem indexUpdateItem = new MenuItem("update index");
//		   indexUpdateItem.setEnabled(false);
		   
		   //构建pageRankGraph(或Knowledge Graph)选项
		   MenuItem pageRankGrapItem = new MenuItem("build knowledge graph");
//		   pageRankGrapItem.setEnabled(false);
		   
		   //退出程序选项
		   MenuItem exitItem = new MenuItem("exit");
		   exitItem.addActionListener(new ActionListener() {
			   public void actionPerformed(ActionEvent e) {
//				   if (JOptionPane.showConfirmDialog(null, "确定退出系统") == 0) 
				   System.exit(0);
			   }
		   });
		   
		   popup.add(mainFrameItem);
		   popup.add(configItem);
		   popup.add(indexItem);
		   popup.add(indexUpdateItem);
		   popup.add(pageRankGrapItem);
		   popup.add(exitItem);
		   
		   trayIcon = new TrayIcon(image, "IDSE", popup);// 创建trayIcon
		   trayIcon.addActionListener(listener);	//添加双击托盘图标时的响应事件
		   
		   indexItem.addActionListener(new IndexItemActionListener(trayIcon, indexItem, indexUpdateItem, pageRankGrapItem));
		   indexUpdateItem.addActionListener(new IndexUpdateItemActionListener(trayIcon, indexUpdateItem, pageRankGrapItem));
		   pageRankGrapItem.addActionListener(new KnowLedgeGraphBuildItemActionListener(trayIcon, indexUpdateItem, pageRankGrapItem));
		   
		   try {
			   tray.add(trayIcon);
		   } catch (AWTException e1) {
			   e1.printStackTrace();
		   }
		   
		   
		}
		//启动内嵌jetty服务器
		ServerIDSE server = new ServerIDSE();
	    server.startUP();
	}
	
	public static void main(String args[]) throws Exception {
		MainFrame mainFrame = new MainFrame();
		mainFrame.startUP();
	}
}



/**
 * 启动索引的监听
 * @author dell
 *
 */
class IndexItemActionListener implements ActionListener{
	private TrayIcon trayIcon = null;
	private MenuItem indexItem = null;
	private MenuItem indexUpdateItem = null;
	private MenuItem pageRankGrapItem = null;
	
	public IndexItemActionListener(TrayIcon trayIcon, MenuItem indexItem, MenuItem indexUpdateItem, MenuItem pageRankGrapItem){
		this.trayIcon = trayIcon;
		this.indexItem = indexItem;
		this.indexUpdateItem = indexUpdateItem;
		this.pageRankGrapItem = pageRankGrapItem;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.trayIcon.setToolTip("IDSE | Indexing...");
		this.indexItem.setEnabled(false);
		this.indexItem.setName("start index (indexing...)");
		// index
		Index index = new Index();
		index.createIndex();
		// knowledge graph building
		MatrixWriter graphBuilder = new MatrixWriter();
		graphBuilder.run();
		PersonalRank pageRankRunner = new PersonalRank();
		pageRankRunner.run();
		this.trayIcon.setToolTip("IDSE | Indexing DONE.");
		this.indexItem.setName("start index (DONE.)");
		this.indexUpdateItem.setEnabled(true);
		this.pageRankGrapItem.setEnabled(true);
	}
}



class IndexUpdateItemActionListener implements ActionListener{
	private TrayIcon trayIcon = null;
	private MenuItem indexUpdateItem = null;
	private MenuItem pageRankGrapItem = null;
	
	public IndexUpdateItemActionListener(TrayIcon trayIcon, MenuItem indexUpdateItem, MenuItem pageRankGrapItem){
		this.trayIcon = trayIcon;
		this.indexUpdateItem = indexUpdateItem;
		this.pageRankGrapItem = pageRankGrapItem;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.trayIcon.setToolTip("IDSE | Updating Index...");
		this.indexUpdateItem.setEnabled(false);
		this.pageRankGrapItem.setEnabled(false);
		this.indexUpdateItem.setName("update index (updating...)");
		// index 
		Index index = new Index();
		index.updateIndex();
		// knowledge graph building
		MatrixWriter graphBuilder = new MatrixWriter();
		graphBuilder.run();
		PersonalRank pageRankRunner = new PersonalRank();
		pageRankRunner.run();
		this.trayIcon.setToolTip("IDSE | Updating Index DONE.");
		this.indexUpdateItem.setName("update index (DONE.)");
		this.indexUpdateItem.setEnabled(true);
		this.pageRankGrapItem.setEnabled(true);
		
	}
}


class KnowLedgeGraphBuildItemActionListener implements ActionListener{
	private TrayIcon trayIcon = null;
	private MenuItem indexUpdateItem = null;
	private MenuItem pageRankGrapItem = null;
	
	public KnowLedgeGraphBuildItemActionListener(TrayIcon trayIcon, MenuItem indexUpdateItem, MenuItem pageRankGrapItem){
		this.trayIcon = trayIcon;
		this.indexUpdateItem = indexUpdateItem;
		this.pageRankGrapItem = pageRankGrapItem;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.trayIcon.setToolTip("IDSE | Building Knowledge Graph...");
		this.indexUpdateItem.setEnabled(false);
		this.pageRankGrapItem.setEnabled(false);
		this.pageRankGrapItem.setName("build knowledge graph (building...)");
		// knowledge graph building
		MatrixWriter graphBuilder = new MatrixWriter();
		graphBuilder.run();
		PersonalRank pageRankRunner = new PersonalRank();
		pageRankRunner.run();
		this.trayIcon.setToolTip("IDSE | Building Knowledge Graph DONE.");
		this.pageRankGrapItem.setName("build knowledge graph (DONE.)");
		this.indexUpdateItem.setEnabled(true);
		this.pageRankGrapItem.setEnabled(true);	
	}
}
