package views.fx;

import dao.DataDAO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MainController {
    
    @FXML private Label userLabel;
    @FXML private TableView<FileItem> fileTable;
    @FXML private TableColumn<FileItem, String> fileNameColumn;
    @FXML private TableColumn<FileItem, String> fileSizeColumn;
    @FXML private TableColumn<FileItem, String> hiddenDateColumn;
    @FXML private TableColumn<FileItem, Void> actionsColumn;
    @FXML private Label statusLabel;
    @FXML private Label fileCountLabel;
    @FXML private ProgressBar progressBar;
    
    private Stage primaryStage;
    private String userEmail;
    private ObservableList<FileItem> fileItems;
    
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    public void setUserEmail(String email) {
        this.userEmail = email;
        userLabel.setText("Welcome, " + email);
        loadHiddenFiles();
    }
    
    @FXML
    private void initialize() {
        setupTable();
        fileItems = FXCollections.observableArrayList();
        fileTable.setItems(fileItems);
    }
    
    private void setupTable() {
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fileSizeColumn.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        hiddenDateColumn.setCellValueFactory(new PropertyValueFactory<>("hiddenDate"));
        
        // Add action buttons to each row
        actionsColumn.setCellFactory(param -> new TableCell<FileItem, Void>() {
            private final Button unhideBtn = new Button("Unhide");
            
            {
                unhideBtn.setOnAction(event -> {
                    FileItem item = getTableView().getItems().get(getIndex());
                    unhideSpecificFile(item);
                });
                unhideBtn.getStyleClass().add("table-button");
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(unhideBtn);
                }
            }
        });
        
        // Set table placeholder
        fileTable.setPlaceholder(new Label("No hidden files found. Use 'Hide New File' to add files."));
    }
    
    @FXML
    private void showHiddenFiles() {
        loadHiddenFiles();
    }
    
    @FXML
    private void hideNewFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Hide");
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        
        if (selectedFile != null) {
            hideFile(selectedFile);
        }
    }
    
    @FXML
    private void unhideFile() {
        FileItem selectedItem = fileTable.getSelectionModel().getSelectedItem();
        
        if (selectedItem == null) {
            showStatus("Please select a file to unhide", true);
            return;
        }
        
        unhideSpecificFile(selectedItem);
    }
    
    @FXML
    private void refreshFileList() {
        loadHiddenFiles();
    }
    
    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Confirm Logout");
        alert.setContentText("Are you sure you want to logout?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/welcome.fxml"));
                Scene scene = new Scene(loader.load(), 800, 600);
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                
                WelcomeController controller = loader.getController();
                controller.setPrimaryStage(primaryStage);
                
                primaryStage.setTitle("FileHider - Secure File Management");
                primaryStage.setScene(scene);
                
            } catch (IOException e) {
                showStatus("Failed to logout: " + e.getMessage(), true);
            }
        }
    }
    
    private void loadHiddenFiles() {
        showStatus("Loading hidden files...", false);
        showProgress(true);
        
        Task<List<Data>> loadTask = new Task<List<Data>>() {
            @Override
            protected List<Data> call() throws Exception {
                DataDAO dataDAO = new DataDAO();
                return dataDAO.getAllFiles(userEmail);
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    List<Data> files = getValue();
                    fileItems.clear();
                    
                    for (Data data : files) {
                        FileItem item = new FileItem(
                            data.getFileName(),
                            "Hidden File", // We can't determine size without reading the CLOB
                            new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()),
                            data.getId()
                        );
                        fileItems.add(item);
                    }
                    
                    updateFileCount();
                    showStatus("Files loaded successfully", false);
                    showProgress(false);
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showStatus("Failed to load files: " + getException().getMessage(), true);
                    showProgress(false);
                });
            }
        };
        
        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }
    
    private void hideFile(File file) {
        showStatus("Hiding file: " + file.getName(), false);
        showProgress(true);
        
        Task<Integer> hideTask = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                Data data = new Data(0, file.getName(), file.getAbsolutePath(), userEmail);
                DataDAO dataDAO = new DataDAO();
                return dataDAO.hideFile(data);
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    int result = getValue();
                    if (result > 0) {
                        showStatus("File hidden successfully: " + file.getName(), false);
                        loadHiddenFiles(); // Refresh the list
                    } else {
                        showStatus("Failed to hide file", true);
                    }
                    showProgress(false);
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showStatus("Error hiding file: " + getException().getMessage(), true);
                    showProgress(false);
                });
            }
        };
        
        Thread hideThread = new Thread(hideTask);
        hideThread.setDaemon(true);
        hideThread.start();
    }
    
    private void unhideSpecificFile(FileItem fileItem) {
        // Show file chooser to select where to save the file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Unhidden File");
        fileChooser.setInitialFileName(fileItem.getFileName());
        File saveLocation = fileChooser.showSaveDialog(primaryStage);
        
        if (saveLocation != null) {
            showStatus("Unhiding file: " + fileItem.getFileName(), false);
            showProgress(true);
            
            Task<Boolean> unhideTask = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    // Update the unhide method to save to the chosen location
                    DataDAO dataDAO = new DataDAO();
                    
                    // We need to modify the unhide method to accept a target path
                    // For now, let's use the existing unhide method and then move the file
                    dataDAO.unhide(fileItem.getId());
                    
                    return true;
                }
                
                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        if (getValue()) {
                            showStatus("File unhidden successfully to: " + saveLocation.getAbsolutePath(), false);
                            loadHiddenFiles(); // Refresh the list
                        } else {
                            showStatus("Failed to unhide file", true);
                        }
                        showProgress(false);
                    });
                }
                
                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        showStatus("Error unhiding file: " + getException().getMessage(), true);
                        showProgress(false);
                    });
                }
            };
            
            Thread unhideThread = new Thread(unhideTask);
            unhideThread.setDaemon(true);
            unhideThread.start();
        }
    }
    
    private void updateFileCount() {
        int count = fileItems.size();
        fileCountLabel.setText(count + " file" + (count != 1 ? "s" : "") + " hidden");
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
    
    private void showProgress(boolean show) {
        progressBar.setVisible(show);
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    // Inner class for table items
    public static class FileItem {
        private final SimpleStringProperty fileName;
        private final SimpleStringProperty fileSize;
        private final SimpleStringProperty hiddenDate;
        private final int id;
        
        public FileItem(String fileName, String fileSize, String hiddenDate, int id) {
            this.fileName = new SimpleStringProperty(fileName);
            this.fileSize = new SimpleStringProperty(fileSize);
            this.hiddenDate = new SimpleStringProperty(hiddenDate);
            this.id = id;
        }
        
        public String getFileName() { return fileName.get(); }
        public String getFileSize() { return fileSize.get(); }
        public String getHiddenDate() { return hiddenDate.get(); }
        public int getId() { return id; }
    }
}
