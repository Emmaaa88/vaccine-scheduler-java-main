package scheduler.model;

import scheduler.db.ConnectionManager;
import scheduler.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class Patient {
    private final String patientname;
    private final byte[] salt;
    private final byte[] hash;

    private Patient(PatientBuilder builder) {
        this.patientname = builder.patientname;
        this.salt = builder.salt;
        this.hash = builder.hash;
    }

    public String getPatientname() {
        return patientname;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getHash() {
        return hash;
    }

    public void saveToDB() throws SQLException {
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String addPatient = "INSERT INTO Patients VALUES (?, ?, ?)";
        try (PreparedStatement statement = con.prepareStatement(addPatient)) {
            statement.setString(1, this.patientname);
            statement.setBytes(2, this.salt);
            statement.setBytes(3, this.hash);
            statement.executeUpdate();
        } finally {
            cm.closeConnection();
        }
    }

    public static class PatientBuilder {
        private final String patientname;
        private final byte[] salt;
        private final byte[] hash;

        public PatientBuilder(String patientname, byte[] salt, byte[] hash) {
            this.patientname = patientname;
            this.salt = salt;
            this.hash = hash;
        }

        public Patient build() {
            return new Patient(this);
        }
    }

    public static class PatientGetter {
        private final String patientname;
        private final String password;
        private byte[] salt;
        private byte[] hash;

        public PatientGetter(String patientname, String password) {
            this.patientname = patientname;
            this.password = password;
        }

        public Patient get() throws SQLException {
            ConnectionManager cm = new ConnectionManager();
            Connection con = cm.createConnection();

            String getPatient = "SELECT Salt, Hash FROM Patients WHERE Patientname = ?";
            try (PreparedStatement statement = con.prepareStatement(getPatient)) {
                statement.setString(1, this.patientname);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    this.salt = resultSet.getBytes("Salt");
                    this.hash = Util.trim(resultSet.getBytes("Hash"));
                    byte[] calculatedHash = Util.generateHash(password, salt);
                    if (Arrays.equals(hash, calculatedHash)) {
                        PatientBuilder builder = new PatientBuilder(this.patientname, this.salt, this.hash);
                        return new Patient(builder);
                    }
                }
                return null;
            } finally {
                cm.closeConnection();
            }
        }
    }
}
