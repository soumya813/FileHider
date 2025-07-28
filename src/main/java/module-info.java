module FileHider {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.sql;
    requires java.mail;
    requires java.desktop;

    opens org.example.filehider to javafx.graphics;
    exports org.example.filehider;

    opens views.fx to javafx.fxml;
    exports views.fx;
    
    exports model;
    exports dao;
    exports service;
}
