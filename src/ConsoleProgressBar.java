import java.util.*;
import java.awt.*;
import java.awt.event.*;



public class ConsoleProgressBar
{

public static TextField txt1=new TextField(30);
public static TextField txt2=new TextField(30);
public static TextField txt3=new TextField(30);



public static void main(String args[])
{
resnew t1 =  new resnew();
event_move t2 =  new event_move();
t1.start();
t2.start();


Panel p1=new Panel();

p1.setLayout(new GridLayout(3,1,0,10));

p1.add(txt1);
p1.add(txt2);
p1.add(txt3);


Frame f = new Frame();
f.add(p1);
f.setLayout(new FlowLayout());
f.setSize(400,300);
f.setVisible(true);
}
}
class resnew extends Thread
{
public void run()
{
while(true)
{
try {
sleep(2000);
ConsoleProgressBar.txt1.setText("OK1");
}
catch(InterruptedException e)
{
}
}
}
}
class event_move extends Thread
{

public void run()
{
while(true)
{
try {
sleep(2000);
ConsoleProgressBar.txt2.setText("OK2");
}
catch(InterruptedException e)
{
}
}
}
}