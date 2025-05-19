package com.finance.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final Properties config;
    private Connection mysqlConnection;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private DatabaseService databaseService;

    private DatabaseManager() {
        this.config = loadConfig();
        initializeDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties dosyası bulunamadı!");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("config.properties okunamadı: " + e.getMessage());
        }
        return props;
    }

    private void initializeDatabase() {
        String dbType = config.getProperty("database.type", "mysql");
        if ("mysql".equalsIgnoreCase(dbType)) {
            initializeMySQL();
        } else if ("mongodb".equalsIgnoreCase(dbType)) {
            initializeMongoDB();
        } else {
            throw new RuntimeException("Desteklenmeyen veritabanı tipi: " + dbType);
        }
    }

    private void initializeMySQL() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = String.format("jdbc:mysql://%s:%s/",
                    config.getProperty("database.host", "localhost"),
                    config.getProperty("database.port", "3306"));
            mysqlConnection = DriverManager.getConnection(
                    url,
                    config.getProperty("database.username", "root"),
                    config.getProperty("database.password", "")
            );
            try (Statement stmt = mysqlConnection.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + config.getProperty("database.name", "finance_manager"));
            }
            mysqlConnection = DriverManager.getConnection(
                    url + config.getProperty("database.name", "finance_manager"),
                    config.getProperty("database.username", "root"),
                    config.getProperty("database.password", "")
            );
            createMySQLTables();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("MySQL başlatılamadı: " + e.getMessage());
        }
    }

    private void initializeMongoDB() {
        try {
            String connectionString = String.format("mongodb://%s:%s",
                    config.getProperty("database.host", "localhost"),
                    config.getProperty("database.port", "27017"));
            mongoClient = MongoClients.create(connectionString);
            mongoDatabase = mongoClient.getDatabase(config.getProperty("database.name", "finance_manager"));
            createMongoDBCollections();
        } catch (Exception e) {
            throw new RuntimeException("MongoDB başlatılamadı: " + e.getMessage());
        }
    }

    private void createMongoDBCollections() {
        // Gelir kategorileri
        mongoDatabase.getCollection("gelir_kategorileri").createIndex(
                new Document("ad", 1),
                new IndexOptions().unique(true)
        );

        // Gider kategorileri
        mongoDatabase.getCollection("gider_kategorileri").createIndex(
                new Document("ad", 1),
                new IndexOptions().unique(true)
        );
    }

    private void createMySQLTables() {
        try (Statement stmt = mysqlConnection.createStatement()) {
            // Tasarruf hedefleri tablosu
            stmt.execute("CREATE TABLE IF NOT EXISTS tasarruf_hedefleri (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "ad VARCHAR(100) NOT NULL," +
                    "hedef_tutar DECIMAL(10,2) NOT NULL," +
                    "birikim_tutar DECIMAL(10,2) DEFAULT 0.00," +
                    "bitis_tarihi DATE," +
                    "olusturma_tarihi DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // Notlar tablosu
            stmt.execute("CREATE TABLE IF NOT EXISTS notlar (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "baslik VARCHAR(100) NOT NULL," +
                    "icerik TEXT," +
                    "olusturma_tarihi DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // Gelir kategorileri tablosu
            stmt.execute("CREATE TABLE IF NOT EXISTS gelir_kategorileri (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "ad VARCHAR(50) NOT NULL," +
                    "olusturma_tarihi DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // Gider kategorileri tablosu
            stmt.execute("CREATE TABLE IF NOT EXISTS gider_kategorileri (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "ad VARCHAR(50) NOT NULL," +
                    "olusturma_tarihi DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // Gelirler tablosu
            stmt.execute("CREATE TABLE IF NOT EXISTS gelirler (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "kategori_id INT," +
                    "tutar DECIMAL(10,2) NOT NULL," +
                    "aciklama TEXT," +
                    "tarih DATE NOT NULL," +
                    "olusturma_tarihi DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (kategori_id) REFERENCES gelir_kategorileri(id)" +
                    ")");

            // Giderler tablosu
            stmt.execute("CREATE TABLE IF NOT EXISTS giderler (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "kategori_id INT," +
                    "tutar DECIMAL(10,2) NOT NULL," +
                    "aciklama TEXT," +
                    "tarih DATE NOT NULL," +
                    "olusturma_tarihi DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (kategori_id) REFERENCES gider_kategorileri(id)" +
                    ")");

            // Bütçe tablosu
            stmt.execute("CREATE TABLE IF NOT EXISTS butce (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "ay INT NOT NULL," +
                    "yil INT NOT NULL," +
                    "tutar DECIMAL(10,2) NOT NULL," +
                    "olusturma_tarihi DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getMySQLConnection() {
        try {
            if (mysqlConnection == null || mysqlConnection.isClosed()) {
                String url = String.format("jdbc:mysql://%s:%s/%s",
                        config.getProperty("database.host", "localhost"),
                        config.getProperty("database.port", "3306"),
                        config.getProperty("database.name", "finance_manager"));
                mysqlConnection = DriverManager.getConnection(
                        url,
                        config.getProperty("database.username", "root"),
                        config.getProperty("database.password", "")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Bağlantı kontrolü sırasında hata oluştu: " + e.getMessage());
        }
        return mysqlConnection;
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public void closeConnections() {
        try {
            if (mysqlConnection != null && !mysqlConnection.isClosed()) {
                mysqlConnection.close();
            }
            if (mongoClient != null) {
                mongoClient.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DatabaseService getDatabaseService() {
        if (databaseService == null) {
            String dbType = config.getProperty("database.type", "mysql");
            if ("mongodb".equalsIgnoreCase(dbType)) {
                databaseService = new MongoDBDatabaseService();
            } else {
                databaseService = new MySQLDatabaseService();
            }
        }
        return databaseService;
    }
} 