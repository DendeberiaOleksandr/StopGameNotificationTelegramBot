import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.Properties;

public class DatabaseService {

    private DatabaseService() {

    }

    private static final DatabaseService databaseService = new DatabaseService();

    public static final DatabaseService getInstance() {
        return databaseService;
    }

    public void init() {
        try {
            Class.forName("com.mysql.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection connection = getConnection()) {
                System.out.println("Successfully connected to database!");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addSubscriber(Long userId, boolean status) {
        try {
            Statement statement = getConnection().createStatement();
            statement.execute("INSERT INTO subscriber (user_id, status) VALUES (" + userId + "," + status + ");");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getSubscribers() throws IOException, SQLException {
        Statement statement = getConnection().createStatement();
        ResultSet resultSet = null;
        if (statement.execute("SELECT * FROM subscriber;")) {
            resultSet = statement.executeQuery("SELECT * FROM subscriber;");
        }
        return resultSet;
    }

    public boolean isSubscriberExist(Long userId) {
        boolean exist = false;

        try {
            ResultSet resultSet = getSubscribers();
            if (resultSet != null) {
                while (resultSet.next()) {
                    if (resultSet.getLong("user_id") == userId) {
                        exist = true;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exist;
    }

    private Connection getConnection() throws SQLException, IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

        return DriverManager.getConnection(url, username, password);
    }

    public void subscribe(Long userId){
            try {
                Statement statement = getConnection().createStatement();
                String sql = "UPDATE subscriber SET status=true WHERE user_id="+userId+";";
                if(statement.execute(sql)){
                    statement.executeQuery(sql);
                }
            } catch (SQLException sqlException){
                sqlException.printStackTrace();
            }
            catch (IOException ioException){
                ioException.printStackTrace();
            }
    }

    public boolean checkSubscription(Long userId){
        boolean isSubscribed = true;
        try {
            ResultSet resultSet = getSubscribers();
                while (resultSet.next()){
                    if(resultSet.getLong("user_id") == userId){
                        if(!resultSet.getBoolean("status")){
                            isSubscribed = false;
                        }
                        break;
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isSubscribed;
    }

    public void unsubscribe(Long userId){
            try {
                Statement statement = getConnection().createStatement();
                String sql = "UPDATE subscriber SET status=false WHERE user_id="+userId+";";
                if(statement.execute(sql)){
                    statement.executeQuery(sql);
                }
            } catch (SQLException sqlException){
                sqlException.printStackTrace();
            }
            catch (IOException ioException){
                ioException.printStackTrace();
            }
        }
    }
