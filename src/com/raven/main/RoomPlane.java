package com.raven.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import client.GameClient;
import util.GameRoomUtil;

public class RoomPlane extends JPanel{
	
	private static final long serialVersionUID = 1L;
	Room room;
	int rectwidth = 180;
	int rectheight = 60;
	int indexpage = 0;
	int lastpage = 0;
	int currpage = 0;
	public static MouseThing mous;
	public boolean hide =false;
	// 坑爹 的boolean不初始化就是空指针！！！
	Boolean mousedown = false;
	int lasty = 150;
	Map<String,String> nameMap = new HashMap<String,String>();
	/**
	 * p  是鼠标记录移动的最后一个位置的点
	 */
	Point p = new Point();

	int Y;
	/**
	 * 这个参数非常重要！因为它涉及到重绘画面的鼠标点击事件
	 */
	public static List<String> hasplayer = new ArrayList<String>();
	
	public RoomPlane() {
		
		mous = new MouseThing(this);
		addMouseListener(mous);
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				p = e.getPoint();
				
				repaint();	
			}
			
		});
		
	}
	
	public void paint(Graphics g) {
		//super.paint(g);
		//setBackground(Color.white);
		nameMap.clear();
		BufferedImage bi = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_BGR);
		Graphics2D g2 = bi.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);  
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_DEFAULT); 
		g2.drawImage(MyPlane.bgImg.getImage(), 0, 0,  800, 800, MyPlane.bgImg.getImageObserver());
		g2.setColor(Color.green);
		int index = 0;
		
		if(GameClient.MSG==null||GameClient.MSG.equals("")) {
			g2.setFont(new Font("黑体", Font.BOLD, 30));
			g2.setColor(Color.PINK);
			g2.drawString("亲爱的："+BeginWindow.username+",当前暂无房间~", 150, 100);
		}else {
			
			g2.setFont(new Font("楷体", Font.PLAIN, 22));
			
			g2.drawString("亲爱的："+BeginWindow.username+",以下是当前服务器房间列表~：", 150, 40);
			g2.setColor(Color.black);
			g2.drawString("当前是第："+(currpage+1)+"页,共有"+lastpage+"页。", 220, 750);
			List<String> m = Arrays.asList(GameClient.MSG.split("&"));
			//5个房间为一页
			int last = m.size()%5;
					
			if(last==0) {
				lastpage = m.size()/5;
			}else {
				lastpage = m.size()/5+1;
			}
			
			for(int i = 0;i<5;i++) {
				if(i+(currpage*5)>m.size()-1)break;
				String[] plays = m.get(i+(currpage*5)).split(",");
				
				String play1 = plays[0];
				//因为房间有可能只有房主 所以这里默认为null
				String play2 ="null";
				if(plays.length==2) {
					play2 = plays[1];
				}

				index++;
				Y = 80*index;
				g2.setFont(new Font("楷体", Font.BOLD, 25));
				g2.drawImage(Room.man.getImage(), 100, Y, Room.man.getImageObserver());
				g2.setColor(Color.red);
				g2.drawString(play1, 100, Y+60);
				g2.drawImage(Room.chessWhite.getImage(), 250, Y+20, 40,40,this);
				if(!play2.equals("null")) {
					g2.drawImage(Room.women.getImage(), 400, Y, 40,40,this);
					g2.drawString(play2, 400, Y+60);
					hasplayer.add("1");
				}else {
					g2.drawString("加入该对局", 400, Y+40);
					GameRoomUtil.writeString(p, mousedown, "加入该对局", g2, 400, Y+40, 200, 60);
					hasplayer.add("0");
				}
				g2.setColor(Color.BLUE);
				g2.drawLine(0, Y+70, this.getWidth(), Y+70);
				nameMap.put(""+(index-1), play1);
			}
			lasty = 100*(index+1);
			g2.setColor(Color.red);
			g2.drawString("上一页", 100, 700);
			g2.drawString("下一页", 500, 700);
			GameRoomUtil.writeString(p, mousedown, "上一页", g2, 100, 700, rectwidth, rectheight);
			GameRoomUtil.writeString(p, mousedown, "下一页", g2, 500, 700, rectwidth, rectheight);
		}
		
		g2.setColor(Color.PINK);
		g2.drawString("创建房间", 100, lasty);
		g2.drawString("点击刷新", 500, lasty);
		GameRoomUtil.writeString(p, mousedown, "创建房间", g2, 100, lasty, rectwidth, rectheight);
		GameRoomUtil.writeString(p, mousedown, "点击刷新", g2, 500, lasty, 200, 60);
		g.drawImage(bi, 0,0,this);
		/**
		 * 注意了 这个鼠标监听事件一定要卸载paint里面  为什么 不直接写在对象的构造器里？因为这样子
		 * 每次刷新一次界面都会重新创建一个监听器  为什么要这么做？ 因为房间信息是动态变化的  必须这样  不然 刷新新增的房间
		 *  点击效果怎么实现 以及 房间销毁 取消点击事件   
		 *  	更好的不是放在paint方法里 而是放入 刷新房间点击事件里 如果房间信息改变 那么重新构造新的监听器 这样写 太不好了 
		 */
		
		
		
	}
}