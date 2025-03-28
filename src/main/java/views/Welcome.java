package views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Welcome {
    public void welcomeScreen(){
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Welcome to the Application");
        System.out.println("Presss 1 to Login");
        System.out.println("Presss 2 to SignUp");
        System.out.println("Presss 0 to Exit");
        int choice = 0;
        try{
            choice = Integer.parseInt(br.readLine());
        }catch(IOException ex){
            ex.printStackTrace();
        }
        switch(choice){
            case 1 -> login();
            case 2 -> signUp();
            case 0 -> System.exit(0);
        }
    }

    private void signUp() {
    }

    private void login() {
    }
}
