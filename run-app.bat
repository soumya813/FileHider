@echo off
cd /d "%USERPROFILE%\IdeaProjects\FileHider"

echo Starting FileHider Desktop Application...

java ^
  --module-path "%USERPROFILE%\.m2\repository\org\openjfx\javafx-controls\21.0.2\javafx-controls-21.0.2.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-fxml\21.0.2\javafx-fxml-21.0.2.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-base\21.0.2\javafx-base-21.0.2.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-graphics\21.0.2\javafx-graphics-21.0.2.jar" ^
  --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics ^
  -cp "target/classes;%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\8.0.33\mysql-connector-j-8.0.33.jar;%USERPROFILE%\.m2\repository\com\sun\mail\javax.mail\1.6.2\javax.mail-1.6.2.jar;%USERPROFILE%\.m2\repository\javax\activation\activation\1.1\activation-1.1.jar;%USERPROFILE%\.m2\repository\com\google\protobuf\protobuf-java\3.21.9\protobuf-java-3.21.9.jar" ^
  org.example.filehider.FileHiderApp

pause
