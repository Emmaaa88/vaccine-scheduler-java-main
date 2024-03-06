package scheduler.model;

import scheduler.db.ConnectionManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Availability {
    private final Date time;
    private final String username;

    public Availability(Date time, String username) {
        this.time = time;
        this.username = username;
    }

    public Date getTime() {
        return time;
    }

    public String getUsername() {
        return username;
    }

    public void saveToDB() throws SQLException {
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String addAvailability = "INSERT INTO Availabilities (Time, Username) VALUES (?, ?)";
        try (PreparedStatement statement = con.prepareStatement(addAvailability)) {
            statement.setDate(1, this.time);
            statement.setString(2, this.username);
            statement.executeUpdate();
        } finally {
            cm.closeConnection();
        }
    }
}
