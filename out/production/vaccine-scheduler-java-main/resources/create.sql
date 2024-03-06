CREATE TABLE Caregivers (
    Username varchar(255),
    Salt BINARY(16),
    Hash BINARY(16),
    PRIMARY KEY (Username)
);

CREATE TABLE Availabilities (
    Time date,
    Username varchar(255) REFERENCES Caregivers,
    PRIMARY KEY (Time, Username)
);

CREATE TABLE Vaccines (
    Name varchar(255),
    Doses int,
    PRIMARY KEY (Name)
);

CREATE TABLE Patients (
    Patientname varchar(255) PRIMARY KEY,
    Salt varbinary(16),
    Hash varbinary(16)
);

CREATE TABLE Appointments (
    AppointmentID INT PRIMARY KEY IDENTITY(1,1),
    Time DATE NOT NULL,
    VaccineName VARCHAR(255) NOT NULL,
    Patientname VARCHAR(255) NOT NULL,
    Username VARCHAR(255) NOT NULL,
    FOREIGN KEY (VaccineName) REFERENCES Vaccines(Name),
    FOREIGN KEY (Patientname) REFERENCES Patients(Patientname),
    FOREIGN KEY (Username) REFERENCES Caregivers(Username)
);