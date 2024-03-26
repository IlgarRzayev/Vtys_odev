import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class vtys_odev extends JFrame {

    private Connection connection;
    
    public vtys_odev() {
        initializeDatabaseConnection();
        
        setTitle("User Authentication System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);

        getContentPane().setBackground(new Color(128, 0, 128));


        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(Color.WHITE);
        registerButton.setBackground(Color.ORANGE);
        registerButton.setForeground(Color.BLACK);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login(usernameField.getText(), new String(passwordField.getPassword()));
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        
        add(panel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeDatabaseConnection() {
        try {
            // MySQL bağlantısı için gerekli bilgileri güncelleyin
            String jdbcUrl = "jdbc:mysql://localhost:3306/odev4";
            String username = "root";
            String password = "password;

            //Veritabanına bağlantı oluşturmak için kullanılır. Bağlantı bilgileri URL'de belirtilir.
            connection = DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanına bağlanırken hata oluştu");
            System.exit(1);
        } 
    }

    private void login(String username, String password) {
        try {
            String query = "SELECT * FROM user WHERE username = ? AND password = ?";
            // Parametrelerle çalışan sorguları çalıştırmak için kullanılır
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            //Bir SELECT sorgusunu çalıştırmak ve sonuç kümesini almak için kullanılır.
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int userID = resultSet.getInt("userID");
                JOptionPane.showMessageDialog(this, "Giriş Başarılı");
                openProjectManagementPage(username,userID);
            } else {
                JOptionPane.showMessageDialog(this, "Geçersiz Kullanıcı Adı veya Şifre");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Giriş sırasında hata oluştu");
        }

    }

    
    private void register() {
        JTextField nameField = new JTextField();
        JTextField surnameField = new JTextField();
        JTextField phoneField = new JTextField();

        Object[] message = {
                "Name:", nameField,
                "Surname:", surnameField,
                "Phone:", phoneField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Register", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String username = JOptionPane.showInputDialog(this, "Enter a username:");
            String password = JOptionPane.showInputDialog(this, "Enter a password:");

            String fullName = nameField.getText() + " " + surnameField.getText();
            String phone = phoneField.getText();

            try {
                String insertQuery = "INSERT INTO user (username, password, fullName, phone) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

                insertStatement.setString(1, username);
                insertStatement.setString(2, password);
                insertStatement.setString(3, fullName);
                insertStatement.setString(4, phone);

                insertStatement.executeUpdate();

                JOptionPane.showMessageDialog(this, "Kayıt Başarılı");

                // Yeni kayıt olan kullanıcıya otomatik olarak giriş yap
                //login(username, password);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Kayıt sırasında hata oluştu");
            }
        }
    }

    private void openProjectManagementPage(String username, int userID) {
                dispose();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ProjectManagementGUI projectManagementGUI = new ProjectManagementGUI(username, userID);
                        projectManagementGUI.setVisible(true);
                    }
                });
            }
            


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new vtys_odev();
            }
        });
    }
}

class ProjectManagementGUI extends JFrame {

    private Connection connection;
    //private String currentLoggedInUser;
    private int currentLoggedInUserID;

    public ProjectManagementGUI(String currentLoggedInUser, int currentLoggedInUserID ) {
        initializeDatabaseConnection();
        this.currentLoggedInUserID = currentLoggedInUserID;

        setTitle("Project Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 200);

        JPanel panel = new JPanel(new GridLayout(2, 5, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Project Buttons
        JButton listProjectsButton = new JButton("Projeleri Listele");
        JButton addProjectButton = new JButton("Yeni proje Ekle");
        JButton viewProjectStatusButton = new JButton("Projenin Durumunu Goster");
        JButton markProjectAsCompletedButton = new JButton("Projenin Durumunu Guncelle");
        JButton markTaskAsCompletedButton = new JButton("Gorevin Durumunu Guncell");
        JButton viewTaskStatusButton = new JButton("Gorevin Durumunu Goster");
        JButton listEmployeesButton = new JButton("Calisanlari listele");
        JButton addEmployeeButton = new JButton("Yeni Calisan Ekle");
        JButton removeEmployeeButton = new JButton("Calisani Sil");
        JButton exitButton = new JButton("Cik");


        // Add action listeners
        listProjectsButton.addActionListener(e -> listProjects());
        addProjectButton.addActionListener(e -> addNewProject());
        viewProjectStatusButton.addActionListener(e -> viewProjectStatus());
        markProjectAsCompletedButton.addActionListener(e -> markProjectAsCompleted());
        markTaskAsCompletedButton.addActionListener(e -> markTaskAsCompleted());
        viewTaskStatusButton.addActionListener(e -> viewTaskStatus());
        listEmployeesButton.addActionListener(e -> listEmployees());
        addEmployeeButton.addActionListener(e -> addEmployee());
        removeEmployeeButton.addActionListener(e -> removeEmployee());
        exitButton.addActionListener(e -> System.exit(0));
        
        panel.add(addProjectButton);
        panel.add(addEmployeeButton);
        panel.add(viewProjectStatusButton);
        panel.add(viewTaskStatusButton);
        panel.add(removeEmployeeButton);
        panel.add(listProjectsButton);
        panel.add(listEmployeesButton);
        panel.add(markProjectAsCompletedButton);
        panel.add(markTaskAsCompletedButton);        
        panel.add(exitButton);

        add(panel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeDatabaseConnection() {
        try {
            // MySQL bağlantısı için gerekli bilgileri güncelleyin
            String jdbcUrl = "jdbc:mysql://localhost:3306/odev4";
            String username = "root";
            String password = "password";

            connection = DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanına bağlanırken hata oluştu");
            System.exit(1);
        } 
    }

   
    private void viewProjectStatus() {
        List<String> userProjects = getUserProjects();
    
        if (userProjects.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Henüz bir proje eklenmemiş. İlk önce bir proje ekleyin.");
        } else {
            String selectedProject = (String) JOptionPane.showInputDialog(
                    this,
                    "Görüntülemek istediğiniz proje:",
                    "Proje Durumu",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    userProjects.toArray(),
                    userProjects.get(0)
            );
    
            if (selectedProject != null) {
                displayProjectStatus(selectedProject);
            }
        }
    }

    private void displayProjectStatus(String projectName) {
        try {
            String selectProjectStatusQuery = "SELECT projectStartDate, projectEndDate, status FROM project WHERE projectName = ?";
            try (PreparedStatement selectProjectStatusStatement = connection.prepareStatement(selectProjectStatusQuery)) {
                selectProjectStatusStatement.setString(1, projectName);
                try (ResultSet projectStatusResultSet = selectProjectStatusStatement.executeQuery()) {
                    if (projectStatusResultSet.next()) {
                        Date startDate = projectStatusResultSet.getDate("projectStartDate");
                        Date endDate = projectStatusResultSet.getDate("projectEndDate");
                        String status = projectStatusResultSet.getString("status");
    
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String formattedStartDate = dateFormat.format(startDate);
                        String formattedEndDate = dateFormat.format(endDate);
    
                        // Eğer proje bitiş tarihi geçmişse ve durum "Tamamlandı" değilse, erteleme seçeneği sun
                        if (endDate.before(new Date(System.currentTimeMillis())) && !status.equals("Tamamlandı")) {
                            int choice = JOptionPane.showConfirmDialog(
                                    this,
                                    "Proje bitiş tarihi geçti, ancak henüz tamamlanmadı. Proje 1 hafta ertelensin mi?",
                                    "Proje Erteleniyor",
                                    JOptionPane.YES_NO_OPTION
                            );
    
                            if (choice == JOptionPane.YES_OPTION) {
                                // Proje durumunu "Ertelendi" olarak güncelle
                                markProjectAsDelayed(projectName);
                            }
                        }
    
                        // Diğer bilgileri görüntüleme işlemini devam ettir
                        String projectStatusMessage = "Proje Adı: " + projectName + "\n"
                                + "Başlangıç Tarihi: " + formattedStartDate + "\n"
                                + "Bitiş Tarihi: " + formattedEndDate + "\n"
                                + "Durum: " + status;
    
                        JOptionPane.showMessageDialog(this, projectStatusMessage, "Proje Durumu", JOptionPane.PLAIN_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Belirtilen proje bulunamadı.");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Proje durumu görüntülenirken hata oluştu.");
        }
    }
    
    private void markProjectAsDelayed(String projectName) {
        try {
            // Proje bitiş tarihini 1 hafta ertelemek için güncelleme sorgusu
            String markProjectAsDelayedQuery = "UPDATE project SET projectEndDate = DATE_ADD(projectEndDate, INTERVAL 1 WEEK) WHERE projectName = ?";
            try (PreparedStatement markProjectAsDelayedStatement = connection.prepareStatement(markProjectAsDelayedQuery)) {
                markProjectAsDelayedStatement.setString(1, projectName);
                int affectedRows = markProjectAsDelayedStatement.executeUpdate();
    
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Proje başarıyla ertelendi: " + projectName);
                } else {
                    JOptionPane.showMessageDialog(this, "Belirtilen proje bulunamadı veya zaten ertelenmiş.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Proje ertelenirken hata oluştu.");
        }
    }
    
    


        /**
     * Kullanıcının seçtiği bir projeyi tamamlanan olarak işaretler.
     */
    private void markProjectAsCompleted() {
        List<String> userProjects = getUserProjects();

        if (userProjects.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Henüz bir proje eklenmemiş. İlk önce bir proje ekleyin.");
        } else {
            // Tamamlanan olarak işaretlemek istediği projeyi seçmesi için bir diyalog kutusu oluştur
            String selectedProject = (String) JOptionPane.showInputDialog(
                    this,
                    "Tamamlanan olarak  işaretlemek istediğiniz proje:",
                    "Proje Tamamlandı",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    userProjects.toArray(),
                    userProjects.get(0)
            );

            if (selectedProject != null) {
                try {
                    // Proje durumunu güncellemek için update sorgusu
                    String markProjectAsCompletedQuery = "UPDATE project SET status = 'Tamamlandı' WHERE projectName = ?";
                    try (PreparedStatement markProjectAsCompletedStatement = connection.prepareStatement(markProjectAsCompletedQuery)) {
                        markProjectAsCompletedStatement.setString(1, selectedProject);
                        int affectedRows = markProjectAsCompletedStatement.executeUpdate();

                        if (affectedRows > 0) {
                            // Başarı durumunda kullanıcıya mesaj göster
                            JOptionPane.showMessageDialog(this, "Proje başarıyla tamamlandı: " + selectedProject);
                        } else {
                            JOptionPane.showMessageDialog(this, "Belirtilen proje bulunamadı veya zaten tamamlanmış.");
                        }
                    }
                } catch (SQLException ex) {
                    // Hata durumunda hata detaylarını yazdır ve kullanıcıya hata mesajı göster
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Proje tamamlanırken hata oluştu.");
                }
            }
        }
    }

    // Kullanıcının seçtiği bir görevi tamamlanan olarak işaretler.
    private void markTaskAsCompleted() {
        List<String> userTasks = getUserTasks();

        if (userTasks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Henüz bir görev eklenmemiş. İlk önce bir görev ekleyin.");
        } else {
            // Tamamlanan olarak işaretlemek istediği görevi seçmesi için bir diyalog kutusu oluştur
            String selectedTask = (String) JOptionPane.showInputDialog(
                    this,
                    "Tamamlanan olarak işaretlemek istediğiniz görev:",
                    "Görev Tamamlandı",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    userTasks.toArray(),
                    userTasks.get(0)
            );

            if (selectedTask != null) {
                try {
                    // Görev durumunu güncellemek için update sorgusu
                    String markTaskAsCompletedQuery = "UPDATE task SET status = 'Tamamlandı' WHERE taskAd = ?";
                    try (PreparedStatement markTaskAsCompletedStatement = connection.prepareStatement(markTaskAsCompletedQuery)) {
                        markTaskAsCompletedStatement.setString(1, selectedTask);
                        int affectedRows = markTaskAsCompletedStatement.executeUpdate();

                        if (affectedRows > 0) {
                            // Başarı durumunda kullanıcıya mesaj göster
                            JOptionPane.showMessageDialog(this, "Görev başarıyla tamamlandı: " + selectedTask);
                        } else {
                            JOptionPane.showMessageDialog(this, "Belirtilen görev bulunamadı veya zaten tamamlanmış.");
                        }
                    }
                } catch (SQLException ex) {
                    // Hata durumunda hata detaylarını yazdır ve kullanıcıya hata mesajı göster
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Görev tamamlanırken hata oluştu.");
                }
            }
        }
    }

    private List<String> getUserTasks() {
        List<String> tasks = new ArrayList<>();

        try {
            String selectQuery = "SELECT taskAd FROM task WHERE employeeID IN (SELECT employeeID FROM employee WHERE userID = ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setInt(1, currentLoggedInUserID);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        tasks.add(resultSet.getString("taskAd"));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return tasks;
    }
    
    private void listProjects() {
        List<String> userProjects = getUserProjects();
        
        if (userProjects.isEmpty()) {
            // Kullanıcının hiç projesi yoksa bilgilendirme mesajı göster ve yeni proje eklemesini iste
            JOptionPane.showMessageDialog(this, "Projeniz yok.\nYeni proje eklemek için tıklayınız");
    
            // Yeni proje eklemek için addNewProject metodunu çağır
            addNewProject();
        } else {
            // Projeler varsa, her bir projeyi listeleyen bir düğme (button) oluştur
            JPanel projectPanel = new JPanel(new GridLayout(userProjects.size(), 1));
            for (String project : userProjects) {
                JButton projectButton = new JButton(project);
                // Her bir düğmeye ActionListener ekleyerek tıklama durumunu kontrol et
                projectButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Tıklanan projenin adını kullanarak addTask metodunu çağır
                        addTask(project);
                    }
                });
                // Projeleri içeren paneli güncelle
                projectPanel.add(projectButton);
            }
    
            // Kullanıcıya projeleri listeleyen pencereyi göster
            JOptionPane.showMessageDialog(this, projectPanel, "User Projects", JOptionPane.PLAIN_MESSAGE);
        }
    }

    private List<String> getUserProjects() {
        List<String> projects = new ArrayList<>();
        
        try  {
            String selectQuery = "SELECT projectName FROM project WHERE userID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setInt(1, currentLoggedInUserID);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        projects.add(resultSet.getString("projectName"));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return projects;
    }

    private void viewTaskStatus() {
        List<String> userTasks = getUserTasks();
    
        if (userTasks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Henüz bir görev eklenmemiş. İlk önce bir görev ekleyin.");
        } else {
            // Kullanıcıya görevleri listeleyen bir pencere göster
            String selectedTask = (String) JOptionPane.showInputDialog(
                    this,
                    "Görüntülemek istediğiniz görevi seçin:",
                    "Görev Durumu",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    userTasks.toArray(),
                    userTasks.get(0)
            );
    
            if (selectedTask != null) {
                // Seçilen görevin detaylarını al ve kullanıcıya göster
                displayTaskStatus(selectedTask);
            }
        }
    }
    
    private void displayTaskStatus(String taskAd) {
        try {
            String selectTaskStatusQuery = "SELECT start_Date, end_date, status FROM task WHERE taskAd = ?";
            try (PreparedStatement selectTaskStatusStatement = connection.prepareStatement(selectTaskStatusQuery)) {
                selectTaskStatusStatement.setString(1, taskAd);
                try (ResultSet taskStatusResultSet = selectTaskStatusStatement.executeQuery()) {
                    if (taskStatusResultSet.next()) {
                        Date startDate = taskStatusResultSet.getDate("start_Date");
                        Date endDate = taskStatusResultSet.getDate("end_date");
                        String status = taskStatusResultSet.getString("status");
    
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String formattedStartDate = dateFormat.format(startDate);
                        String formattedEndDate = dateFormat.format(endDate);
    
                        String taskStatusMessage = "Görev Adı: " + taskAd + "\n"
                                + "Başlangıç Tarihi: " + formattedStartDate + "\n"
                                + "Bitiş Tarihi: " + formattedEndDate + "\n"
                                + "Durum: " + status;
    
                        JOptionPane.showMessageDialog(this, taskStatusMessage, "Görev Durumu", JOptionPane.PLAIN_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Belirtilen görev bulunamadı.");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Görev durumu görüntülenirken hata oluştu.");
        }
    }
    
    private void addTask(String projectName) {
        JTextField taskNameField = new JTextField();
        JTextField startDateField = new JTextField();
        JTextField endDateField = new JTextField();
    
        // Çalışanları seçmek için bir diyalog kutusu oluştur
        List<String> employees = getUserEmployees();
        if (employees.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sisteme kayıtlı çalışan bulunamadı. Önce bir çalışan ekleyin.");
            return;
        }
    
        
        Date currentDate = new Date(System.currentTimeMillis());
    
        String selectedEmployee = (String) JOptionPane.showInputDialog(this, "Çalışanı seçiniz:", "Çalışanlar",
                JOptionPane.QUESTION_MESSAGE, null, employees.toArray(), employees.get(0));
    
        if (selectedEmployee == null) {
            // Eğer çalışan seçilmediyse işlemi iptal et
            return;
        }
    
        Object[] message = {
                "Task Name:", taskNameField,
                "Start Date (yyyy-MM-dd):", startDateField,
                "End Date (yyyy-MM-dd):", endDateField
        };
    
        int option = JOptionPane.showConfirmDialog(this, message, "Add Task to Project: " + projectName, JOptionPane.OK_CANCEL_OPTION);
    
        if (option == JOptionPane.OK_OPTION) {
            String taskName = taskNameField.getText();
            String startDateStr = startDateField.getText();
            String endDateStr = endDateField.getText();
    
            try {
                // Seçilen projenin ID'sini almak için select sorgusu
                String selectProjectQuery = "SELECT projectID FROM project WHERE projectName = ?";
                int projectID;
                try (PreparedStatement selectProjectStatement = connection.prepareStatement(selectProjectQuery)) {
                    selectProjectStatement.setString(1, projectName);
                    try (ResultSet projectResultSet = selectProjectStatement.executeQuery()) {
                        if (!projectResultSet.next()) {
                            JOptionPane.showMessageDialog(this, "Hata: Belirtilen proje bulunamadı.");
                            return;
                        }
                        projectID = projectResultSet.getInt("projectID");
                    }
                }
    
                // Seçilen çalışanın ID'sini almak için select sorgusu
                String selectEmployeeQuery = "SELECT employeeID FROM employee WHERE fullName = ?";
                int employeeID;
                try (PreparedStatement selectEmployeeStatement = connection.prepareStatement(selectEmployeeQuery)) {
                    selectEmployeeStatement.setString(1, selectedEmployee);
                    try (ResultSet employeeResultSet = selectEmployeeStatement.executeQuery()) {
                        if (!employeeResultSet.next()) {
                            JOptionPane.showMessageDialog(this, "Hata: Belirtilen çalışan bulunamadı.");
                            return;
                        }
                        employeeID = employeeResultSet.getInt("employeeID");
                    }
                }
    
                // Yeni görev eklemek için insert sorgusu
                String insertTaskQuery = "INSERT INTO task (taskAd, start_date, end_date, projectID, employeeID, status) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertTaskStatement = connection.prepareStatement(insertTaskQuery)) {
                    insertTaskStatement.setString(1, taskName);
    
                    // String formatındaki tarih bilgilerini java.sql.Date tipine çevirme
                    java.sql.Date startDate = java.sql.Date.valueOf(startDateStr);
                    java.sql.Date endDate = java.sql.Date.valueOf(endDateStr);
    
                    // Tarih kontrolü
                    if (endDate.before(startDate)) {
                        JOptionPane.showMessageDialog(this, "Hata: Görevin bitiş tarihi, başlangıç tarihinden önce olamaz.");
                        return;
                    }
    
                    insertTaskStatement.setDate(2, startDate);
                    insertTaskStatement.setDate(3, endDate);
                    insertTaskStatement.setInt(4, projectID);
                    insertTaskStatement.setInt(5, employeeID);
    
                    // Başlangıç tarihi bugünkü günden sonra ise, durumu "Tamamlandı" olarak ayarla
                    if (startDate.after(currentDate)) {
                        insertTaskStatement.setString(6, "Tamamlanlanacak");
                    } else {
                        insertTaskStatement.setString(6, "Devam Ediyor");
                    }
    
                    insertTaskStatement.executeUpdate();
    
                    JOptionPane.showMessageDialog(this, "Yeni Görev Eklendi: " + taskName + " (Proje: " + projectName + ", Çalışan: " + selectedEmployee + ")");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Görev eklenirken hata oluştu");
            }
        }
    }
    

    // Sistemde kayıtlı çalışanları listeler ve kullanıcının seçtiği çalışana ait projeleri ve görevleri gösterir.

    private void listEmployees() {
        // Sistemde kayıtlı çalışanları al
        List<String> employees = getUserEmployees();
        
        if (employees.isEmpty()) {
            // Eğer çalışan bulunamazsa kullanıcıya bilgi mesajı göster
            JOptionPane.showMessageDialog(this, "Sisteme kayıtlı çalışan bulunamadı.");
        } else {
            // Çalışanları seçmek için bir diyalog kutusu oluştur
            Object selectedEmployee = JOptionPane.showInputDialog(
                    this,
                    "Çalışanı seçiniz:",
                    "Çalışanlar",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    employees.toArray(),
                    employees.get(0)
            );

            if (selectedEmployee != null) {
                // Eğer bir çalışan seçildiyse, seçilen çalışana ait projeleri ve görevleri göster
                showEmployeeProjectsAndTasks(selectedEmployee.toString());
            }
        }
 
    }


    // Seçilen çalışana ait projeleri ve görevleri gösterir.
    
    private void showEmployeeProjectsAndTasks(String selectedEmployee) {
        try {
            // Seçilen çalışanın ID'sini almak için select sorgusu
            String selectEmployeeQuery = "SELECT employeeID FROM employee WHERE fullName = ?";
            int employeeID;
            try (PreparedStatement selectEmployeeStatement = connection.prepareStatement(selectEmployeeQuery)) {
                selectEmployeeStatement.setString(1, selectedEmployee);
                try (ResultSet employeeResultSet = selectEmployeeStatement.executeQuery()) {
                    if (!employeeResultSet.next()) {
                        JOptionPane.showMessageDialog(this, "Hata: Belirtilen çalışan bulunamadı.");
                        return;
                    }
                    employeeID = employeeResultSet.getInt("employeeID");
                }
            }

            // Seçilen çalışana ait projeleri ve görevleri almak için select sorgusu
            String selectProjectsQuery = "SELECT projectID, projectName FROM project WHERE projectID IN " +
                    "(SELECT projectID FROM task WHERE employeeID = ?)";
            List<String> projectsAndTasks = new ArrayList<>();
            try (PreparedStatement selectProjectsStatement = connection.prepareStatement(selectProjectsQuery)) {
                selectProjectsStatement.setInt(1, employeeID);
                try (ResultSet projectsResultSet = selectProjectsStatement.executeQuery()) {
                    while (projectsResultSet.next()) {
                        int projectID = projectsResultSet.getInt("projectID");
                        String projectName = projectsResultSet.getString("projectName");

                        // Seçilen projenin görevlerini al
                        List<String> tasks = getTasksForProjectAndEmployee(projectID, employeeID);

                        // Proje ve görevleri ekrana yaz
                        StringBuilder projectText = new StringBuilder("- " + projectName + "\n");
                        for (String task : tasks) {
                            // Görevin durumunu al
                            String taskStatus = getTaskStatus(projectID, task);

                            // Görev ve durumu ekrana yaz
                            projectText.append("  - ").append(task).append(" (").append(taskStatus).append(")").append("\n");
                        }
                        projectsAndTasks.add(projectText.toString());
                    }
                }
            }

            // Projeleri ve görevleri bir diyalog kutusunda göster
            StringBuilder projectsText = new StringBuilder("Çalışanın Projeleri ve Görevleri:\n");
            for (String projectAndTasks : projectsAndTasks) {
                projectsText.append(projectAndTasks);
            }
            JOptionPane.showMessageDialog(this, projectsText.toString(), "Çalışan Projeleri ve Görevleri", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Çalışan projeleri ve görevleri alınırken hata oluştu");
        }
    }

    // Belirtilen projenin ve çalışanın görevlerini alır.
    
    private List<String> getTasksForProjectAndEmployee(int projectID, int employeeID) {
        List<String> tasks = new ArrayList<>();
        try {
            String selectTasksQuery = "SELECT taskAd FROM task WHERE projectID = ? AND employeeID = ?";
            try (PreparedStatement selectTasksStatement = connection.prepareStatement(selectTasksQuery)) {
                selectTasksStatement.setInt(1, projectID);
                selectTasksStatement.setInt(2, employeeID);
                try (ResultSet tasksResultSet = selectTasksStatement.executeQuery()) {
                    while (tasksResultSet.next()) {
                        tasks.add(tasksResultSet.getString("taskAd"));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Görevler alınırken hata oluştu");
        }
        return tasks;
    }

    // Belirtilen projenin ve çalışanın görevlerini ve durumlarını alır.
    private String getTaskStatus(int projectID, String taskName) {
        String taskStatus = "";
        try {
            String selectTaskStatusQuery = "SELECT status FROM task WHERE projectID = ? AND taskAd = ?";
            try (PreparedStatement selectTaskStatusStatement = connection.prepareStatement(selectTaskStatusQuery)) {
                selectTaskStatusStatement.setInt(1, projectID);
                selectTaskStatusStatement.setString(2, taskName);
                try (ResultSet taskStatusResultSet = selectTaskStatusStatement.executeQuery()) {
                    if (taskStatusResultSet.next()) {
                        taskStatus = taskStatusResultSet.getString("status");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Görev durumu alınırken hata oluştu");
        }
        return taskStatus;
    }

    // Sisteme giriş yapan kullanıcının çalışanlarını alır.
    
    private List<String> getUserEmployees() {
        List<String> employees = new ArrayList<>();

        try  {
            String selectQuery = "SELECT fullName FROM employee WHERE userID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setInt(1, currentLoggedInUserID);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        employees.add(resultSet.getString("fullName"));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return employees;
    }
        
    // Yeni bir çalışan ekler.
    
    private void addEmployee() {
        JTextField nameField = new JTextField();
        JTextField surnameField = new JTextField();

        Object[] message = {
                "Name:", nameField,
                "Surname:", surnameField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Employee", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            // Kullanıcının girdiği bilgiler alınıyor
            String fullName = nameField.getText() + " " + surnameField.getText();

            try {
                String insertQuery = "INSERT INTO employee (fullName, userID) VALUES (?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

                insertStatement.setString(1, fullName);
                insertStatement.setInt(2, currentLoggedInUserID);

                insertStatement.executeUpdate();

                JOptionPane.showMessageDialog(this, "Çalışan eklendi");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Çalışan eklenirken hata oluştu");
            }
        }
    }

    
    private void removeEmployee() {
        try {
            List<String> employees = getUserEmployees();
            if (employees.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Sisteme kayıtlı çalışan bulunamadı.");
                return;
            }
    
            // Çalışanları seçmek için bir diyalog kutusu oluştur
            String selectedEmployee = (String) JOptionPane.showInputDialog(
                    this,
                    "Silinecek çalışanı seçiniz:",
                    "Çalışanları Sil",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    employees.toArray(),
                    employees.get(0)
            );
    
            if (selectedEmployee == null) {
                // Eğer çalışan seçilmediyse işlemi iptal et
                return;
            }
    
            // Birden fazla SQL ifadesi gerçekleştireceğimiz için bir işlem başlat
            connection.setAutoCommit(false);
    
            try {
                // İlk olarak, çalışana ait görevleri sil
                String deleteTasksQuery = "DELETE FROM task WHERE employeeID IN (SELECT employeeID FROM employee WHERE fullName = ?)";
                try (PreparedStatement deleteTasksStatement = connection.prepareStatement(deleteTasksQuery)) {
                    deleteTasksStatement.setString(1, selectedEmployee);
                    deleteTasksStatement.executeUpdate();
                }
    
                // Şimdi, çalışanı sil
                String deleteEmployeeQuery = "DELETE FROM employee WHERE fullName = ?";
                try (PreparedStatement deleteEmployeeStatement = connection.prepareStatement(deleteEmployeeQuery)) {
                    deleteEmployeeStatement.setString(1, selectedEmployee);
                    int affectedRows = deleteEmployeeStatement.executeUpdate();
    
                    if (affectedRows > 0) {
                        // Her şey başarılı ise işlemi onayla
                        connection.commit();
                        JOptionPane.showMessageDialog(this, "Çalışan başarıyla silindi: " + selectedEmployee);
                    } else {
                        // Eğer çalışan bulunamadıysa işlemi geri al
                        connection.rollback();
                        JOptionPane.showMessageDialog(this, "Silinecek çalışan bulunamadı: " + selectedEmployee);
                    }
                }
            } catch (SQLException ex) {
                // Herhangi bir istisna durumunda işlemi geri al
                connection.rollback();
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Çalışan silinirken hata oluştu");
            } finally {
                // İşlem sonrasında otomatik işlemi tekrar etmeye izin ver
                connection.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Çalışan silinirken hata oluştu");
        }
    }

    

        private void addNewProject() {
            JTextField nameField = new JTextField();
            JTextField startField = new JTextField();
            JTextField endField = new JTextField();

            Object[] message = {
                "Project Name: ", nameField,
                "Project Start Date", startField,
                "Project End Date", endField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Proje Ekle", JOptionPane.OK_CANCEL_OPTION);

            if(option == JOptionPane.OK_OPTION){

                String projectName = nameField.getText();
                String startDateStr = startField.getText();
                String endDateStr = endField.getText();

                try {
                // String formatındaki tarih bilgilerini java.sql.Date tipine çevirme
                java.sql.Date projectStartDate = java.sql.Date.valueOf(startDateStr);
                java.sql.Date projectEndDate = java.sql.Date.valueOf(endDateStr);

                // SQL sorgusu için PreparedStatement oluşturma
                String insertProject = "INSERT INTO project (projectName, projectStartDate, projectEndDate, userID) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertProject);

                // Parametreleri set etme
                insertStatement.setString(1, projectName);
                insertStatement.setDate(2, projectStartDate);
                insertStatement.setDate(3, projectEndDate);
                insertStatement.setInt(4, currentLoggedInUserID);

                // SQL sorgusunu çalıştırma
                insertStatement.executeUpdate();

                JOptionPane.showMessageDialog(this, "Proje Başarıyla Eklendi");
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Proje eklenirken hata oluştu");
                }
            }  
        }
} 