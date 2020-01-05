package com.athangzhou.study;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author:asus
 * @Date: 2019/12/24 21:40
 * @Description:
 */
public class ChatServer {
    private static boolean started=false;
    private ServerSocket ss;
    private List<Client> clients=new ArrayList<Client>();

    public static void main(String[] args) {
        new ChatServer().start();
    }

    public void start(){
        try {
            ss = new ServerSocket(8888);
        }catch (BindException e){
            System.out.println("端口使用中!");
            System.out.println("请关闭正在使用的程序后再试");
            System.exit(0);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        try {
            started=true;
            while(started){
                Socket socket=null;
                socket=ss.accept();
                System.out.println("a client connect!");
                Client client=new Client(socket);
                new Thread(client).start();
                clients.add(client);
            }
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class Client implements Runnable{
        private Socket socket;
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;
        private boolean canReceive;

        public void send(String info){
            try {
                dataOutputStream.writeUTF(info);
            } catch (IOException e) {
                clients.remove(this);
                System.out.println("对方退出");
            }
        }
        public Client(Socket socket){
            this.socket=socket;
            try {
                dataInputStream=new DataInputStream(socket.getInputStream());
                dataOutputStream=new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            canReceive=true;
        }
        @Override
        public void run() {
            Client client=null;
            try {
                while(canReceive){
                    String getInfo=dataInputStream.readUTF();
                    System.out.println(getInfo);
                    for(int i=0;i<clients.size();i++) {
                        client=clients.get(i);
                        client.send(getInfo);
                    }
                }
            } catch (EOFException e) {
                System.out.println("走了");
            }catch(SocketException e){
                System.out.println("人间值得，未来可期");
            } catch (IOException e){
                e.printStackTrace();
            }finally {
                try {
                    if(dataOutputStream!=null){dataOutputStream.close();}
                    if(dataInputStream!=null) {dataInputStream.close();}
                    if(socket!=null) {
                        socket.close();
                        socket=null;
                    }
                }catch (IOException ex){
                    ex.printStackTrace();
                }

            }

        }
    }

}
