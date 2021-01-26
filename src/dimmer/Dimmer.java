/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dimmer;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

/**
 *
 * @author User
 */
public class Dimmer {
    
    private static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private static GraphicsDevice[] gs = ge.getScreenDevices();
    private static JFrame frames[] = new JFrame[gs.length];
    private static JCheckBoxMenuItem displays[] = new JCheckBoxMenuItem[gs.length];
    private static boolean enabled = false;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, AWTException {
        config.getCredentials();
        HttpServer server = server(9123,config.getUser(),config.getPass());
        tray();
        read();
        
    }
    public static void write() throws IOException{
        BufferedWriter outputWriter = null;
        outputWriter = new BufferedWriter(new FileWriter("cache/devices"));        
        for(int i=0;i<displays.length;i++){
                if(displays[i].isSelected()){
                    outputWriter.write(Integer.toString(i));
                    outputWriter.newLine();
                }
        }
        outputWriter.flush();  
        outputWriter.close();
    }
    
    public static void read() throws IOException{
        File myObj = new File("cache/devices");
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
          String data = myReader.nextLine();
          displays[Integer.parseInt(data)].setSelected(true);
        }
        myReader.close();
    }
    
    public static void tray() throws IOException, AWTException{
        
        if(!SystemTray.isSupported()){
            System.out.println("System tray is not supported !!! ");
            return ;
        }
        //get the systemTray of the system
        SystemTray systemTray = SystemTray.getSystemTray();
        
        InputStream input =  Thread.currentThread().getContextClassLoader().getResourceAsStream("icon.png");
        Image image = ImageIO.read(input);
        JPopupMenu popup = new JPopupMenu();
        
        for (int i=0;i<gs.length;i++){
            displays[i] = new JCheckBoxMenuItem();
            int disID = i+1;
            displays[i].setText("Display " + disID);
            popup.add(displays[i]);
            displays[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    write();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            });
            
        }
        popup.add(new JSeparator());
        JMenuItem exit = new JMenuItem();
        exit.setText("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        popup.add(exit);
        JTrayIcon Jtray = new JTrayIcon(image,"Dimmer");
        

        Jtray.setImageAutoSize(true);
        Jtray.setJPopupMenu(popup);
        systemTray.add(Jtray);
        Jtray.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                undim();
                dim();
            }
        }
        });
        
        
        Jtray.addMouseListener(new MouseAdapter()
        {
            public void mouseReleased(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    popup.setLocation(e.getX(), e.getY()-100);
                    popup.setInvoker(popup);
                    popup.setVisible(true);
                }
            }
        });
       
       Jtray.addMouseListener(new MouseAdapter()
        {
            public void mouseReleased(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    popup.setLocation(e.getX(), e.getY()-100);
                    popup.setInvoker(popup);
                    popup.setVisible(true);
                }
            }
        });
        
    }
    
    public static void dim(){
        int unselected = 0;
        for(int i=0;i<displays.length;i++){
                if(!displays[i].isSelected()){
                    unselected++;
                }
        }
        if (unselected == displays.length){
            return;
        }
        
        System.out.println("Passed");
        if (!enabled){
            JPopupMenu popup = new JPopupMenu();
            for(int i=0;i<displays.length;i++){
                if(displays[i].isSelected()){
                    System.out.println(i);
                    frames[i] = new JFrame(gs[i].getDefaultConfiguration());
                    frames[i].setUndecorated(true);
                    frames[i].setAlwaysOnTop(true);
                    frames[i].getContentPane().setBackground(Color.BLACK);
                    frames[i].setExtendedState(Frame.MAXIMIZED_BOTH);
                    frames[i].setVisible(true);
                    frames[i].addMouseListener(new MouseAdapter()
                    {
                        public void mouseReleased(MouseEvent e)
                        {
                            if (e.isPopupTrigger())
                            {
                                Point p = MouseInfo.getPointerInfo().getLocation();
                                int x = p.x;
                                int y = p.y;
                                popup.setLocation(x, y);
                                popup.setInvoker(popup);
                                popup.setVisible(true);
                            }
                        }
                    });

                }
            }


            JMenuItem item = new JMenuItem();
            item.setText("Undim");
            item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undim();
                
            }
        });
            popup.add(item);
            enabled = true;
        }
        
    }
    
    public static void undim(){
        if (enabled){
            
            for(int i=0;i<displays.length;i++){
                    if (!(frames[i] == null)){
                        frames[i].setVisible(false);
                    }
            }
            enabled = false;
        }
    }
    
    public static HttpServer server(int p,String user,String pass) throws IOException{
        HttpServer server = HttpServer.create(new InetSocketAddress(p), 0);
        server.setExecutor(null); // creates a default executor
        HttpContext on = server.createContext("/ON", new RootHandler(server));
        HttpContext off = server.createContext("/OFF", new offHandler(server));

        on.setAuthenticator(new BasicAuthenticator("get") {
            @Override
            public boolean checkCredentials(String user, String pwd) {
                return user.equals(user) && pwd.equals(pass);
            }
        });
        off.setAuthenticator(new BasicAuthenticator("get") {
            @Override
            public boolean checkCredentials(String user, String pwd) {
                return user.equals(user) && pwd.equals(pass);
            }
        });
        server.start();
        return server;
    }
    
    static class RootHandler implements HttpHandler {
        HttpServer server;
        
        RootHandler(HttpServer s){
            server = s;
            
        }
        @Override
        public void handle(HttpExchange he) throws IOException {
            String response = "TEST";
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
            undim();
            dim();
        }
    }
    
    static class offHandler implements HttpHandler {
        HttpServer server;
        
        offHandler (HttpServer s){
            server = s;
            
        }
        @Override
        public void handle(HttpExchange he) throws IOException {
            
            String response = "OFF";
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
            undim();
        }
    }
}
