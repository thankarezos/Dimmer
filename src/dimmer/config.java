/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dimmer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.util.Properties;

/**
 *
 * @author User
 */
public class config {
    private static String user;
    private static String pass;
    private static int port;
    
    public static String getUser(){
        return user;
    }
    
    public static String getPass(){
        return pass;
    }
    
    public static int getPort(){
        return port;
    }
    
    public static void getCredentials(){
        File configFile = new File("cache/config.properties");
 
        try {
            FileReader reader = new FileReader(configFile);
            Properties props = new Properties();
            props.load(reader);

            user = props.getProperty("user");
            pass = props.getProperty("pass");

            
            reader.close();
        } catch (FileNotFoundException ex) {
            // file does not exist
        } catch (IOException ex) {
            // I/O error
        }
    }
    
    public static void setUser(String User){
        
        
        user = User;
    }
     public static void setPass(String Pass){
        
        
        pass = Pass;
    }
      public static void setPort(int Port){
        
        
        port = Port;
    }
     
    
}

