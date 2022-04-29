import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import  java.util.*;

public class SaoLei implements ActionListener {
    JFrame frame=new JFrame("扫雷");
    //ImageIcon bannerIcon=new ImageIcon("banner.jpg");//按钮是图片
    //ImageIcon guessIcon=new ImageIcon("爆炸.png");//按钮是图片
    ImageIcon flagIcon=new ImageIcon("旗帜 (3).jpg");
    ImageIcon bombIcon=new ImageIcon("bomb.jpg");
    ImageIcon bannerIcon=new ImageIcon("banner.jpg");
    JButton bannerBtn=new JButton("扫雷");


    //数据结构
    int ROW=20;
    int COL=20;
    int[][] data=new int[ROW][COL];//存储格子数据(是否是雷,周围雷的数量)
    JButton [][] btns=new JButton[ROW][COL];//按钮
    int LEICOUNT=10;//雷数量
    int LEICODE=-1;//表示雷
    int unopened=ROW*COL;//没开
    int opened=0;//已开
    int sec=0;//时钟计数
    JLabel label1=new JLabel("待开"+unopened);
    JLabel label2=new JLabel("已开"+opened);
    JLabel label3=new JLabel("用时"+sec+"s");
    Timer timer=new Timer(1000,this);

    public SaoLei(){
        frame.setSize(600,700);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        setHeader();

        addLei();

        setButtons();

        timer.start();
        frame.setVisible(true);
    }

    private void addLei(){
        Random rand=new Random();
        for(int i=0;i<LEICOUNT;){
            int r=rand.nextInt(ROW);
            int c=rand.nextInt(COL);
            if(data[r][c]!=LEICODE){
                data[r][c]=LEICODE;
                i++;
            }
        }

        //计算周边雷的数量
        for(int i=0;i<ROW;i++){
            for(int j=0;j<COL;j++){

                //如果是雷，跳出本次循环
                if(data[i][j]==LEICODE){
                    continue;
                }

                //如果不是雷，判断周围几个雷
                int tempCount=0;

                //第一个&&左右两边防止数组越界,第二个&&右边按 从左到右 从上到下遍历,判断是否是雷
                if (i > 0 && j > 0 && data[i-1][j-1]==LEICODE) tempCount++;
                if (i > 0 && data[i-1][j]==LEICODE) tempCount++;
                if (i > 0 && j < 19 && data[i-1][j+1]==LEICODE) tempCount++;
                if (j > 0 && data[i][j-1]==LEICODE) tempCount++;
                if (j < 19 && data[i][j+1]==LEICODE) tempCount++;
                if (i < 19 && j > 0 && data[i+1][j-1]==LEICODE) tempCount++;
                if (i <19 && data[i+1][j]==LEICODE) tempCount++;
                if (i < 19 && j < 19 && data[i+1][j+1]==LEICODE) tempCount++;

                data[i][j]=tempCount;
            }
        }

    }

    private void setButtons(){
        Container con=new Container();
        con.setLayout(new GridLayout(ROW,COL));

        for(int i=0;i<ROW;i++){
            for(int j=0;j<COL;j++){
                JButton btn=new JButton();
                btn.setIcon(bannerIcon);
                btn.addActionListener(this);
                btn.setMargin(new Insets(0,0,0,0));
                con.add(btn);
                btns[i][j]=btn;
            }
        }

        frame.add(con,BorderLayout.CENTER);
    }

    private void setHeader(){
        JPanel panel=new JPanel(new GridBagLayout());

        GridBagConstraints c1=new GridBagConstraints(0,0,3,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0);
        panel.add(bannerBtn,c1);
        bannerBtn.addActionListener(this);

        label1.setOpaque(true);
        label1.setBackground(Color.white);
        label1.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        label2.setOpaque(true);
        label2.setBackground(Color.white);
        label2.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        label3.setOpaque(true);
        label3.setBackground(Color.white);
        label3.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        bannerBtn.setOpaque(true);
        bannerBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        bannerBtn.setBackground(Color.WHITE);

        GridBagConstraints c2=new GridBagConstraints(0,1,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0);
        panel.add(label1,c2);
        GridBagConstraints c3=new GridBagConstraints(1,1,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0);
        panel.add(label2,c3);
        GridBagConstraints c4=new GridBagConstraints(2,1,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0);
        panel.add(label3,c4);

        frame.add(panel,BorderLayout.NORTH);//将panel面板用BorderLayout.NORTH方式布局在框架上
    }

    public static void main(String args []){
        SaoLei saolei=new SaoLei();
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() instanceof Timer){
            sec++;
            label3.setText("用时"+sec+"s");
            timer.start();
            return;
        }

        JButton btn=(JButton)e.getSource();
        if(btn.equals(bannerBtn)){
            restart();
            return;
        }

        for(int i=0;i<ROW;i++){
            for(int j=0;j<COL;j++){
                if(btn.equals(btns[i][j])){
                    if(data[i][j]==LEICODE){
                        lose();//踩到雷，失败
                    }
                    else{
                        openCell(i,j);//开格子
                        checkWin();
                    }
                    return;
                }
            }
        }
    }

    //重开
    private void restart(){
        /*
        给数据清零，给按钮恢复状态，重启时钟
         */

        //恢复按钮
        for(int i=0;i<ROW;i++){
            for(int j=0;j<COL;j++){
                data[i][j]=0;
                btns[i][j].setBackground(Color.GRAY);
                btns[i][j].setEnabled(true);
                btns[i][j].setText("");
                btns[i][j].setIcon(bannerIcon);
            }
        }
        bannerBtn.setText("扫雷");

        //恢复状态栏
        unopened=ROW*COL;
        opened=0;
        label1.setText("待开"+unopened);
        label2.setText("已开"+opened);
        sec=0;
        label3.setText("用时"+sec+"s");

        addLei();

        timer.start();
    }

    //检查是否赢了
    private void checkWin(){
        int count=0;
        for(int i=0;i<ROW;i++){
            for(int j=0;j<COL;j++){
                if(btns[i][j].isEnabled()) count++;//计算没打开的格子
            }
        }
        if(count==LEICOUNT){
            timer.stop();
            for(int i=0;i<ROW;i++){
                for(int j=0;j<COL;j++){
                    if(btns[i][j].isEnabled()){
                        btns[i][j].setIcon(flagIcon);
                    }
                }
            }
            JDialog d=new JDialog(frame,"来自香蕉君的祝贺",true);//有模式的对话框
            JLabel j=new JLabel(new ImageIcon("香蕉君.gif"));
            Container c=d.getContentPane();
            c.setLayout(new FlowLayout(FlowLayout.RIGHT));
            c.add(j);
            d.setSize(250,250);
            d.setVisible(true);
        }

    }

    //输了
    private void lose(){
        timer.stop();
        bannerBtn.setText("你输了！点击重开");

        for(int i=0;i<ROW;i++){
            for(int j=0;j<COL;j++){
                if(btns[i][j].isEnabled()){
                    JButton btn=btns[i][j];

                    //把雷显示出来，并把所有格子打开
                    if(data[i][j]==LEICODE){
                        btn.setEnabled(false);
                        btn.setIcon(bombIcon);
                        btn.setDisabledIcon(bombIcon);
                    }else{
                        btn.setIcon(null);
                        btn.setEnabled(false);
                        btn.setOpaque(true);
                        btn.setBackground(Color.YELLOW);//因为输掉打开的格子
                        btn.setText(data[i][j]+"");
                    }
                }
            }
        }

        JOptionPane.showMessageDialog(frame,"你输了！\n点击上方按钮重新开始");

    }

    private void openCell(int i,int j){
        JButton btn=btns[i][j];
        if(!btn.isEnabled()) return;//开过，返回

        btn.setIcon(null);
        btn.setEnabled(false);
        btn.setOpaque(true);
        btn.setBackground(Color.GRAY);
        btn.setText(data[i][j]+"");
        addOpenCount();

        //递归开格子
        if(data[i][j]==0){
            if (i > 0 && j > 0 && data[i-1][j-1]==0) openCell(i-1,j-1);
            if (i > 0 && data[i-1][j]==0) openCell(i-1,j);
            if (i > 0 && j < 19 && data[i-1][j+1]==0) openCell(i-1,j+1);
            if (j > 0 && data[i][j-1]==0) openCell(i,j-1);
            if (j < 19 && data[i][j+1]==0) openCell(i,j+1);
            if (i < 19 && j > 0 && data[i+1][j-1]==0) openCell(i+1,j-1);
            if (i <19 && data[i+1][j]==0) openCell(i+1,j);
            if (i < 19 && j < 19 && data[i+1][j+1]==0) openCell(i+1,j+1);
        }
    }

    private void addOpenCount(){
        opened++;
        unopened--;
        label1.setText("待开"+unopened);
        label2.setText("已开"+opened);
    }
}
