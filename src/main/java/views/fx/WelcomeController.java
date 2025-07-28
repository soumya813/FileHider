package views.fx;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import model.User;
import service.GenerateOTP;
import service.SendOTPService;
import service.UserService;
import dao.UserDAO;

import java.io.IOException;
import java.sql.SQLException;

public class WelcomeController {
    
    @FXML private VBox welcomeButtons;
    @FXML private VBox loginForm;
    @FXML private VBox signupForm;
    @FXML private VBox otpForm;
    
    @FXML private TextField loginEmailField;
    @FXML private TextField signupNameField;
    @FXML private TextField signupEmailField;
    @FXML private TextField otpField;
    @FXML private Label statusLabel;
    @FXML private Label otpInfoLabel;
    
    private Stage primaryStage;
    private String currentEmail;
    private String currentOTP;
    private String currentUsername;
    private boolean isLoginFlow; // true for login, false for signup
    
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    @FXML
    private void initialize() {
        // Initialize the welcome screen
        showWelcome();
    }
    
    @FXML
    private void showWelcome() {
        hideAllForms();
        welcomeButtons.setVisible(true);
        clearFields();
        clearStatus();
    }
    
    @FXML
    private void showLogin() {
        hideAllForms();
        loginForm.setVisible(true);
        clearStatus();
    }
    
    @FXML
    private void showSignup() {
        hideAllForms();
        signupForm.setVisible(true);
        clearStatus();
    }
    
    @FXML
    private void handleLogin() {
        String email = loginEmailField.getText().trim();
        
        if (email.isEmpty()) {
            showStatus("Please enter your email", true);
            return;
        }
        
        if (!isValidEmail(email)) {
            showStatus("Please enter a valid email address", true);
            return;
        }
        
        // Check if user exists
        try {
            if (!UserDAO.isExists(email)) {
                showStatus("User not found. Please sign up first.", true);
                return;
            }
        } catch (SQLException e) {
            showStatus("Database error: " + e.getMessage(), true);
            return;
        }
        
        currentEmail = email;
        isLoginFlow = true;
        sendOTP();
    }
    
    @FXML
    private void handleSignup() {
        String username = signupNameField.getText().trim();
        String email = signupEmailField.getText().trim();
        
        if (username.isEmpty() || email.isEmpty()) {
            showStatus("Please fill in all fields", true);
            return;
        }
        
        if (!isValidEmail(email)) {
            showStatus("Please enter a valid email address", true);
            return;
        }
        
        // Check if user already exists
        try {
            if (UserDAO.isExists(email)) {
                showStatus("User already exists. Please login instead.", true);
                return;
            }
        } catch (SQLException e) {
            showStatus("Database error: " + e.getMessage(), true);
            return;
        }
        
        currentEmail = email;
        currentUsername = username;
        isLoginFlow = false;
        sendOTP();
    }
    
    private void sendOTP() {
        showStatus("Sending OTP...", false);
        
        // Generate OTP
        currentOTP = GenerateOTP.getOTP();
        
        // Send OTP in background thread
        Task<Void> sendOTPTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                SendOTPService.sendOTP(currentEmail, currentOTP);
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    hideAllForms();
                    otpForm.setVisible(true);
                    otpInfoLabel.setText("OTP has been sent to " + currentEmail);
                    showStatus("OTP sent successfully!", false);
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showStatus("Failed to send OTP. Please try again.", true);
                });
            }
        };
        
        Thread otpThread = new Thread(sendOTPTask);
        otpThread.setDaemon(true);
        otpThread.start();
    }
    
    @FXML
    private void handleOTPVerification() {
        String enteredOTP = otpField.getText().trim();
        
        if (enteredOTP.isEmpty()) {
            showStatus("Please enter the OTP", true);
            return;
        }
        
        if (!enteredOTP.equals(currentOTP)) {
            showStatus("Invalid OTP. Please try again.", true);
            return;
        }
        
        if (isLoginFlow) {
            // Login successful
            openMainApplication();
        } else {
            // Complete signup process
            completeSignup();
        }
    }
    
    private void completeSignup() {
        showStatus("Creating account...", false);
        
        Task<Integer> signupTask = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                User user = new User(currentUsername, currentEmail);
                return UserService.saveUser(user);
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    int response = getValue();
                    switch (response) {
                        case 0 -> {
                            showStatus("Account created successfully!", false);
                            // Wait a moment then open main application
                            Task<Void> delayTask = new Task<Void>() {
                                @Override
                                protected Void call() throws Exception {
                                    Thread.sleep(1500);
                                    return null;
                                }
                                
                                @Override
                                protected void succeeded() {
                                    Platform.runLater(() -> openMainApplication());
                                }
                            };
                            Thread delayThread = new Thread(delayTask);
                            delayThread.setDaemon(true);
                            delayThread.start();
                        }
                        case 1 -> showStatus("User already exists", true);
                        default -> showStatus("Failed to create account", true);
                    }
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showStatus("Failed to create account: " + getException().getMessage(), true);
                });
            }
        };
        
        Thread signupThread = new Thread(signupTask);
        signupThread.setDaemon(true);
        signupThread.start();
    }
    
    private void openMainApplication() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            MainController controller = loader.getController();
            controller.setUserEmail(currentEmail);
            controller.setPrimaryStage(primaryStage);
            
            primaryStage.setTitle("FileHider - " + currentEmail);
            primaryStage.setScene(scene);
            
        } catch (IOException e) {
            showStatus("Failed to load main application: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }
    
    @FXML
    private void exitApplication() {
        Platform.exit();
    }
    
    private void hideAllForms() {
        welcomeButtons.setVisible(false);
        loginForm.setVisible(false);
        signupForm.setVisible(false);
        otpForm.setVisible(false);
    }
    
    private void clearFields() {
        loginEmailField.clear();
        signupNameField.clear();
        signupEmailField.clear();
        otpField.clear();
    }
    
    private void clearStatus() {
        statusLabel.setText("");
        statusLabel.getStyleClass().removeAll("status-success", "status-error");
    }
    
    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("status-success", "status-error");
        if (isError) {
            statusLabel.getStyleClass().add("status-error");
        } else {
            statusLabel.getStyleClass().add("status-success");
        }
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }
}
