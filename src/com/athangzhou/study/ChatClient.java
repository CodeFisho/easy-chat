package com.athangzhou.study;

import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * @Author:asus
 * @Date: 2019/12/23 20:33
 * @Description:
 */
public class ChatClient extends Frame {
    private Socket ss=null;
    private DataOutputStream dataOutputStream=null;
    private DataInputStream dataInputStream=null;
    private boolean beConnected=false;
    TextField textField=new TextField();
    TextArea textArea=new TextArea();
    private Thread thread=new Thread(new Receive());
    public static void main(String[] args) {
        new ChatClient().launchFrame();
    }

    public void launchFrame(){
        setLocation(300,400);
        setSize(300,300);
        add(textField,BorderLayout.SOUTH);
        add(textArea,BorderLayout.NORTH);
        pack();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disConnect();
                System.exit(0);
            }
        });
        textField.addActionListener(new TFListener());
        setVisible(true);
        connect();
        thread.start();
    }

    public void connect(){
        try {
            ss=new Socket("127.0.0.1",8888);
            dataOutputStream=new DataOutputStream(ss.getOutputStream());
            dataInputStream=new DataInputStream(ss.getInputStream());
            beConnected=true;
System.out.println("connected!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disConnect(){
        try {
            dataOutputStream.close();
            dataInputStream.close();
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

   /*     try {
            beConnected=false;
            thread.join();//关闭线程时优先考虑使用join方法
        } catch (InterruptedException e){
            e.printStackTrace();
        } finally {
            try {
                dataOutputStream.close();
                dataInputStream.close();
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }*/

    }

    private class TFListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String info=textField.getText().trim();
            //textArea.setText(info);
            try {
                dataOutputStream.writeUTF(info);
                dataOutputStream.flush();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            textField.setText("");

        }
    }

    private class Receive implements Runnable{

        @Override
        public void run() {
            try {
                while(beConnected){
                    String str=dataInputStream.readUTF();
                    textArea.setText(textArea.getText()+str+"\n");
                }
            } catch (SocketException e){
                System.out.println("程序结束运行！");
            } catch(EOFException e){
                System.out.println("程序结束运行！");
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
