package service;

import dao.UserDAO;
import model.User;

import java.sql.SQLException;

public class UserService {
    public static Integer saveUser(User user){
        try{
            if(UserDAO.isExists(user.getEmail())){
                return 1; // User already exists
            }else{
                int result = UserDAO.saveUser(user);
                return result > 0 ? 0 : 2; // 0 = success, 2 = failed to save
            }
        }catch(SQLException ex){
            ex.printStackTrace();
            return 2; // Error occurred
        }
    }
}
