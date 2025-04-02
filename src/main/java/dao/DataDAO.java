package dao;

import db.MyConnection;
import model.Data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataDAO {
    public static List<Data> getAllFiles(String email) throws SQLException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement("select * from data where email = ?");
        ps.setString(1,email);
        ResultSet rs = ps.executeQuery();
        List<Data> files = new ArrayList<>();
        while(rs.next()){
            int id = rs.getInt(1);
            String name = rs.getString(2);
            String path = rs.getString(3);
            files.add(new Data(id, name, path));
        }
        return files;
    }
    public static int hideFile(Data file) throws SQLException, IOException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement("insert into data(name,path,email,bin_data) values(?,?,?,?)");
        ps.setString(1,file.getFileName());
        ps.setString(2,file.getPath());
        ps.setString(3,file.getEmail());
        File f = new File(file.getPath());
        FileReader fr = new FileReader(f);
        ps.setCharacterStream(4,fr,f.length());
        int ans = ps.executeUpdate();
        fr.close();
        f.delete();
        return ans;
    }
}
