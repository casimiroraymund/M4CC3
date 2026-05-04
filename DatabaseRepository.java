import java.sql.*;
import java.util.UUID;

public class DatabaseRepository {
    private static final String DB_URL = "jdbc:sqlite:sports_academy.db";

    public void createUserTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "role TEXT NOT NULL," +
                "sport_type TEXT," +
                "balance REAL DEFAULT 0.0)";
        executeUpdate(sql);
    }

    public void createSessionTables() {
        String sessionSql = "CREATE TABLE IF NOT EXISTS sessions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "label TEXT NOT NULL," +
                "date TEXT NOT NULL," +
                "coach TEXT NOT NULL," +
                "slots INTEGER NOT NULL," +
                "description TEXT," +
                "price REAL DEFAULT 500.0)";

        String reservationSql = "CREATE TABLE IF NOT EXISTS reservations (" +
                "reservationID TEXT PRIMARY KEY," +
                "sessionID INTEGER NOT NULL," +
                "athleteEmail TEXT NOT NULL," +
                "status TEXT NOT NULL," +
                "FOREIGN KEY(sessionID) REFERENCES sessions(id))";

        String paymentSql = "CREATE TABLE IF NOT EXISTS payments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "transactionID TEXT NOT NULL," +
                "athleteEmail TEXT NOT NULL," +
                "amount REAL NOT NULL," +
                "finalAmount REAL NOT NULL," +
                "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        executeUpdate(sessionSql);
        executeUpdate(reservationSql);
        executeUpdate(paymentSql);
    }

    // --- User Operations ---

    public void registerUser(String name, String email, String password, String role, String sportType) {
        String sql = "INSERT INTO users(name, email, password, role, sport_type) VALUES(?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, role);
            pstmt.setString(5, sportType);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public User loginUser(String email, String password) {
        String sql = "SELECT name, email, role, sport_type FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("name"), rs.getString("email"),
                        rs.getString("role"), rs.getString("sport_type"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public User getUserProfile(String email) {
        String sql = "SELECT name, email, role, sport_type FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("name"), rs.getString("email"),
                        rs.getString("role"), rs.getString("sport_type"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            return pstmt.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    // --- Balance & Payment Operations ---

    public double getUserBalance(String email) {
        String sql = "SELECT balance FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble("balance");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    public void updateUserBalance(String email, double newBalance) {
        String sql = "UPDATE users SET balance = ? WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void recordPayment(String txId, String email, double amount, double finalAmt) {
        String sql = "INSERT INTO payments(transactionID, athleteEmail, amount, finalAmount) VALUES(?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, txId); pstmt.setString(2, email);
            pstmt.setDouble(3, amount); pstmt.setDouble(4, finalAmt);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- Session & Reservation Operations ---

    public void createTrainingSession(String label, String date, String coach, int slots, String desc, double price) {
        String sql = "INSERT INTO sessions(label, date, coach, slots, description, price) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, label); pstmt.setString(2, date); pstmt.setString(3, coach);
            pstmt.setInt(4, slots); pstmt.setString(5, desc); pstmt.setDouble(6, price);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean bookTrainingSession(int sessionID, String athleteEmail) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String updateSql = "UPDATE sessions SET slots = slots - 1 WHERE id = ? AND slots > 0";
            try (PreparedStatement up = conn.prepareStatement(updateSql)) {
                up.setInt(1, sessionID);
                if (up.executeUpdate() == 0) return false;
            }
            String resID = UUID.randomUUID().toString();
            String insSql = "INSERT INTO reservations(reservationID, sessionID, athleteEmail, status) VALUES(?,?,?,?)";
            try (PreparedStatement ins = conn.prepareStatement(insSql)) {
                ins.setString(1, resID); ins.setInt(2, sessionID);
                ins.setString(3, athleteEmail); ins.setString(4, "Confirmed");
                ins.executeUpdate();
            }
            return true;
        } catch (SQLException e) { return false; }
    }

    public void cancelReservation(String resID, String email) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            double paidAmount = 0;
            String paySql = "SELECT finalAmount FROM payments WHERE athleteEmail = ? ORDER BY date DESC LIMIT 1";
            try (PreparedStatement pst = conn.prepareStatement(paySql)) {
                pst.setString(1, email);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) paidAmount = rs.getDouble("finalAmount");
            }
            String delSql = "DELETE FROM reservations WHERE reservationID = ? AND athleteEmail = ?";
            try (PreparedStatement delPst = conn.prepareStatement(delSql)) {
                delPst.setString(1, resID); delPst.setString(2, email);
                if (delPst.executeUpdate() > 0) {
                    double refund = paidAmount * 0.8;
                    updateUserBalance(email, getUserBalance(email) + refund);
                    recordPayment("REFUND-" + UUID.randomUUID(), email, 0, -refund);
                    System.out.println("Refund processed: PHP " + refund + " added to wallet.");
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void viewAllTrainingSessions() {
        String sql = "SELECT * FROM sessions";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + " | " + rs.getString("label") +
                        " | Date: " + rs.getString("date") + " | Price: PHP " + rs.getDouble("price"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void viewMyReservations(String email) {
        String sql = "SELECT r.reservationID, s.label FROM reservations r JOIN sessions s ON r.sessionID = s.id WHERE r.athleteEmail = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("Res ID: " + rs.getString("reservationID") + " | Session: " + rs.getString("label"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public double getSessionPrice(int sessionID) {
        String sql = "SELECT price FROM sessions WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sessionID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble("price");
        } catch (SQLException e) { e.printStackTrace(); }
        return 500.0;
    }

    public void showIncomeStatement() {
        String sql = "SELECT SUM(finalAmount) as total_rev FROM payments";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nAcademy Net Revenue: PHP " + rs.getDouble("total_rev"));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void executeUpdate(String sql) {
        try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) { e.printStackTrace(); }
    }
}