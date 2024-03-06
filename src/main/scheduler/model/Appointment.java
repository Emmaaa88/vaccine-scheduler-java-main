package scheduler.model;

import scheduler.db.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Appointment {
    private int appointmentID;
    private final java.sql.Date time;
    private final String vaccineName;
    private final String patientname;
    private final String username;

    public Appointment(int appointmentID, java.sql.Date time, String vaccineName, String patientname, String username) {
        this.appointmentID = appointmentID;
        this.time = time;
        this.vaccineName = vaccineName;
        this.patientname = patientname;
        this.username = username;
    }

    public int getAppointmentID() {
        return appointmentID;
    }

    public java.sql.Date getTime() {
        return time;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public String getPatientname() {
        return patientname;
    }

    public String getUsername() {
        return username;
    }

    public void saveToDB() throws SQLException {
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String addAppointment = "INSERT INTO Appointments (Time, VaccineName, Patientname, Username) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = con.prepareStatement(addAppointment, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setDate(1, this.time);
            statement.setString(2, this.vaccineName);
            statement.setString(3, this.patientname);
            statement.setString(4, this.username);
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating appointment failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.appointmentID = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating appointment failed, no ID obtained.");
                }
            }
        } finally {
            cm.closeConnection();
        }
    }
}
