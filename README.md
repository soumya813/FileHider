# FileHider - Secure File Management System

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21.0.2-blue.svg)](https://openjfx.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-green.svg)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A robust desktop application for secure file hiding and management with user authentication, email-based OTP verification, and encrypted file storage.

## 🚀 Features

### Core Functionality
- **Secure File Hiding**: Hide sensitive files with encrypted storage in MySQL database
- **User Authentication**: Email-based login with OTP verification
- **File Management**: View, hide, and unhide files with detailed metadata
- **Real-time Updates**: Dynamic file list updates with progress indicators
- **Cross-platform**: JavaFX-based GUI for Windows, macOS, and Linux

### Security Features
- **Email OTP Verification**: Two-factor authentication via email
- **Encrypted Storage**: Files stored as encrypted binary data in database
- **Session Management**: Secure user sessions with automatic logout
- **Input Validation**: Comprehensive email and file validation

### User Interface
- **Modern GUI**: Clean, responsive JavaFX interface
- **Intuitive Navigation**: Easy-to-use welcome screen and main dashboard
- **File Table View**: Sortable table with file details and actions
- **Progress Indicators**: Visual feedback for file operations
- **Status Notifications**: Real-time status updates and error handling

## 🏗️ Architecture

### Technology Stack
- **Backend**: Java 21 with JavaFX for GUI
- **Database**: MySQL 8.0+ with JDBC connectivity
- **Email Service**: JavaMail API with Gmail SMTP
- **Build Tool**: Apache Maven
- **Architecture Pattern**: MVC (Model-View-Controller)

### Project Structure
```
FileHider/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── org/example/filehider/
│   │   │   │   └── FileHiderApp.java          # Main application entry point
│   │   │   ├── model/
│   │   │   │   ├── User.java                  # User entity model
│   │   │   │   └── Data.java                  # File data model
│   │   │   ├── views/
│   │   │   │   ├── fx/
│   │   │   │   │   ├── WelcomeController.java # Welcome screen controller
│   │   │   │   │   └── MainController.java    # Main application controller
│   │   │   │   ├── Welcome.java               # Welcome view
│   │   │   │   └── UserView.java              # User view
│   │   │   ├── service/
│   │   │   │   ├── SendOTPService.java        # Email OTP service
│   │   │   │   ├── GenerateOTP.java           # OTP generation utility
│   │   │   │   └── UserService.java           # User business logic
│   │   │   ├── dao/
│   │   │   │   ├── UserDAO.java               # User data access
│   │   │   │   └── DataDAO.java               # File data access
│   │   │   └── db/
│   │   │       └── MyConnection.java          # Database connection
│   │   └── resources/
│   │       ├── fxml/
│   │       │   ├── welcome.fxml               # Welcome screen layout
│   │       │   └── main.fxml                  # Main application layout
│   │       └── css/
│   │           └── styles.css                 # Application styling
│   └── test/                                  # Test files
├── target/                                    # Compiled classes
├── pom.xml                                    # Maven configuration
├── run-app.bat                               # Windows launcher script
└── README.md                                 # This file
```

## 📋 Prerequisites

### System Requirements
- **Java**: OpenJDK 21 or Oracle JDK 21
- **MySQL**: MySQL Server 8.0 or higher
- **Maven**: Apache Maven 3.6 or higher
- **Email Account**: Gmail account for OTP service

### Database Setup
1. Install MySQL Server
2. Create a new database:
   ```sql
   CREATE DATABASE filehider;
   USE filehider;
   ```
3. Create required tables:
   ```sql
   CREATE TABLE users (
       id INT AUTO_INCREMENT PRIMARY KEY,
       name VARCHAR(255) NOT NULL,
       email VARCHAR(255) UNIQUE NOT NULL
   );
   
   CREATE TABLE data (
       id INT AUTO_INCREMENT PRIMARY KEY,
       name VARCHAR(255) NOT NULL,
       path VARCHAR(500) NOT NULL,
       email VARCHAR(255) NOT NULL,
       bin_data LONGTEXT,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );
   ```

## 🛠️ Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/FileHider.git
cd FileHider
```

### 2. Configure Database Connection
Edit `src/main/java/db/MyConnection.java`:
```java
connection = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/filehider?useSSL=false",
    "your_username",
    "your_password"
);
```

### 3. Configure Email Service
Edit `src/main/java/service/SendOTPService.java`:
```java
String from = "your-email@gmail.com";
// Update the password with your Gmail app password
return new PasswordAuthentication(from, "your-app-password");
```

### 4. Build the Project
```bash
mvn clean compile
```

### 5. Run the Application

#### Option 1: Using Maven
```bash
mvn javafx:run
```

#### Option 2: Using Batch File (Windows)
```bash
run-app.bat
```

#### Option 3: Manual Java Command
```bash
java --module-path "path/to/javafx-sdk" \
     --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics \
     -cp "target/classes:path/to/dependencies" \
     org.example.filehider.FileHiderApp
```

## 🎯 Usage Guide

### First Time Setup
1. **Launch Application**: Run the application using one of the methods above
2. **Sign Up**: Click "Sign Up" and enter your name and email
3. **Verify Email**: Check your email for OTP and enter it in the application
4. **Login**: Use your email to log in to the application

### File Management
1. **Hide Files**: Click "Hide New File" to select and hide files
2. **View Files**: All hidden files are displayed in the main table
3. **Unhide Files**: Click "Unhide" button next to any file to restore it
4. **Refresh**: Use "Refresh" button to update the file list

### Security Features
- **OTP Verification**: Every login requires email OTP verification
- **Session Management**: Automatic logout for security
- **File Encryption**: Files are stored encrypted in the database

## 🔧 Configuration

### Database Configuration
- **Host**: `localhost:3306`
- **Database**: `filehider`
- **SSL**: Disabled for local development

### Email Configuration
- **SMTP Server**: `smtp.gmail.com`
- **Port**: `465`
- **Security**: SSL/TLS enabled
- **Authentication**: Gmail app password required

### Application Settings
- **Window Size**: 800x600 (minimum 600x400)
- **File Size Limit**: No explicit limit (limited by database)
- **Supported File Types**: All file types

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Manual Testing Checklist
- [ ] User registration with email verification
- [ ] User login with OTP
- [ ] File hiding functionality
- [ ] File unhiding functionality
- [ ] File list refresh
- [ ] Error handling for invalid inputs
- [ ] Database connection stability
- [ ] Email service reliability

## 🚨 Troubleshooting

### Common Issues

#### Database Connection Error
```
Error: Connection refused
Solution: Ensure MySQL server is running and credentials are correct
```

#### Email OTP Not Received
```
Error: OTP not sent
Solution: Check Gmail app password and SMTP settings
```

#### JavaFX Runtime Error
```
Error: JavaFX runtime components are missing
Solution: Ensure JavaFX dependencies are properly configured in pom.xml
```

#### File Permission Error
```
Error: Access denied when hiding/unhiding files
Solution: Run application with appropriate file system permissions
```

### Debug Mode
Enable debug logging by setting system properties:
```bash
java -Djavafx.debug=true -Dmail.debug=true org.example.filehider.FileHiderApp
```

## 🔒 Security Considerations

### Data Protection
- Files are stored as encrypted binary data
- Database connections use prepared statements
- User passwords are not stored (OTP-based authentication)

### Email Security
- Uses Gmail app passwords (not regular passwords)
- SSL/TLS encryption for email transmission
- OTP expiration (implemented in service layer)

### File System Security
- Original files are deleted after hiding
- File paths are validated before processing
- Temporary files are properly cleaned up

## 📈 Performance Optimization

### Database Optimization
- Indexed email fields for faster queries
- Prepared statements for query optimization
- Connection pooling for better resource management

### Memory Management
- Streaming file operations for large files
- Proper resource cleanup in DAO layer
- Background task execution for UI responsiveness

## 🤝 Contributing

### Development Setup
1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Make your changes and test thoroughly
4. Commit your changes: `git commit -m 'Add feature'`
5. Push to the branch: `git push origin feature-name`
6. Submit a pull request

### Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Maintain consistent indentation

### Testing Requirements
- Add unit tests for new features
- Test database operations
- Verify email functionality
- Test UI components

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Soumya Srivastav** - [YourGitHub](https://github.com/soumya813)

## 🙏 Acknowledgments

- JavaFX team for the excellent GUI framework
- MySQL team for the robust database system
- JavaMail API for email functionality
- Maven community for build automation

## 📞 Support

For support and questions:
- Create an issue on GitHub

---

**Note**: This application is designed for educational and personal use. For production deployment, additional security measures and proper infrastructure setup are recommended. 