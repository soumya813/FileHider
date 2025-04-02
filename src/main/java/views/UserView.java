package views;

import dao.DataDAO;
import model.Data;

import java.io.IOException;
import java.sql.SQLException;
import java.io.File;
import java.util.List;
import java.util.Scanner;

public class UserView {
    private String email;
    UserView(String email){
        this.email = email;
    }
    public void home(){
        do{
            System.out.println("Welcome " + this.email);
            System.out.println("Press 1 to show hidden files");
            System.out.println("Press 2 to hide a new file");
            System.out.println("Press 3 to unhide a file");
            System.out.println("Press 0 to exit");

            Scanner sc = new Scanner(System.in);
            int ch = Integer.parseInt(sc.nextLine());
            switch(ch){
                case 1 -> {
                   try{
                       List<Data> files = DataDAO.getAllFiles(this.email);
                       System.out.println("ID - File Name");
                       for(Data file: files){
                           System.out.println(file.getId() + " - " + file.getFileName());
                       }
                   }catch(SQLException e){
                       e.printStackTrace();
                   }
                }
                case 2 -> {
                    System.out.println("Enter the File Path");
                    String path = sc.nextLine();
                    File f = new File(path);
                    Data file = new Data(0,f.getName(), path, this.email);
                    try {
                        DataDAO.hideFile(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                case 3 -> {
                    
                }
                case 0 -> {
                    System.exit(0);
                }
            }
        }while(true);
    }
}
