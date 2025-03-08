import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

//implements searching employees by id
//reads employee records from the file

public class RandomFile {
    private static RandomFile instance;
    private RandomAccessFile file;
    private String filePath;

    //singleton pattern
    private RandomFile() {}

    public static RandomFile getInstance() {
        if (instance == null) {
            instance = new RandomFile();
        }
        return instance;
    }
    
    public void closeReadFile() {
        try {
            if (file != null) file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void closeWriteFile() {
        try {
            if (file != null) file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void openReadFile(String path) {
        try {
            if (file != null) {
                file.close();
            }
            file = new RandomAccessFile(path, "rw");
            filePath = path; // ✅ Store the path
        } catch (IOException e) {
            e.printStackTrace();
        }
    }    

    public void openWriteFile(String path) {
        try {
            if (file != null) {
                file.close(); // Close the previous file if it's open
            }
            file = new RandomAccessFile(path, "rw"); // Open file in read-write mode
            filePath = path;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteRecords(long position) {
        try {
            file.seek(position);
            file.writeInt(0); // ✅ Mark as deleted by setting Employee ID to 0
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isSomeoneToDisplay() {
        try {
            file.seek(0);
            while (file.getFilePointer() < file.length()) {
                Employee employee = readRecords(file.getFilePointer());
                if (employee != null && employee.getEmployeeId() != 0) {
                    return true; // ✅ At least one valid record exists
                }
                file.seek(file.getFilePointer() + RandomAccessEmployeeRecord.SIZE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }    

    public long getFirst() {
        try {
            file.seek(0);
            while (file.getFilePointer() < file.length()) {
                Employee employee = readRecords(file.getFilePointer());
                if (employee != null && employee.getEmployeeId() != 0) { // ✅ Skip empty records
                    return file.getFilePointer();
                }
                file.seek(file.getFilePointer() + RandomAccessEmployeeRecord.SIZE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }    
    
    public long getLast() {
        try {
            long position = file.length() - RandomAccessEmployeeRecord.SIZE;
            while (position >= 0) {
                Employee employee = readRecords(position);
                if (employee.getEmployeeId() != 0) {
                    return position;
                }
                position -= RandomAccessEmployeeRecord.SIZE;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1; // ✅ No valid records found
    }
    
    public long getNext(long position) {
        try {
            position += RandomAccessEmployeeRecord.SIZE;
            while (position < file.length()) {
                Employee employee = readRecords(position);
                if (employee.getEmployeeId() != 0) {
                    return position;
                }
                position += RandomAccessEmployeeRecord.SIZE;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1; // ✅ No next valid record found
    }
    
    public long getPrevious(long position) {
        position -= RandomAccessEmployeeRecord.SIZE;
        while (position >= 0) {
            Employee employee = readRecords(position);
            if (employee.getEmployeeId() != 0) {
                return position;
            }
            position -= RandomAccessEmployeeRecord.SIZE;
        }
        return -1; // ✅ No previous valid record found
    }
     
    public Employee readRecords(long position) {
        try {
            file.seek(position);
            RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
            record.read(file);
            return record;
        } catch (IOException e) {
            e.printStackTrace();
            return new Employee();
        }
    }
    
    private Employee readEmployee() throws IOException {
        int id = file.readInt();
        String pps = readString(9);
        String surname = readString(20);
        String firstName = readString(20);
        char gender = file.readChar();
        String department = readString(20);
        double salary = file.readDouble();
        boolean fullTime = file.readBoolean();
    
        return new Employee(id, pps, surname, firstName, gender, department, salary, fullTime);
    }
    
    private String readString(int length) throws IOException {
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = file.readChar();
        }
        return new String(chars).trim();
    }

   public List<Employee> findEmployeeBySurname(String surname) {
    List<Employee> matchingEmployees = new ArrayList<>();
    try {
        file.seek(0);
        while (file.getFilePointer() < file.length()) {
            Employee employee = readRecords(file.getFilePointer());
            if (employee != null && employee.getEmployeeId() != 0 &&
                employee.getSurname().equalsIgnoreCase(surname)) {
                matchingEmployees.add(employee);
            }
            file.seek(file.getFilePointer() + RandomAccessEmployeeRecord.SIZE);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return matchingEmployees;
}

public boolean updateEmployeeInFile(Employee updatedEmployee) {
    try {
        file.seek(0); // Start from the beginning of the file

        while (file.getFilePointer() < file.length()) {
            long position = file.getFilePointer(); // Store current position
            Employee employee = readRecords(position); // Read the current record

            if (employee.getEmployeeId() == updatedEmployee.getEmployeeId()) { // Match found
                file.seek(position); // Move back to the correct position

                RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord(
                    updatedEmployee.getEmployeeId(),
                    updatedEmployee.getPps(),
                    updatedEmployee.getSurname(),
                    updatedEmployee.getFirstName(),
                    updatedEmployee.getGender(),
                    updatedEmployee.getDepartment(),
                    updatedEmployee.getSalary(),
                    updatedEmployee.getFullTime()
                );

                record.write(file); // Overwrite the existing record
                file.getFD().sync(); // Ensure the data is written immediately
                System.out.println("Employee updated successfully: " + updatedEmployee); 
                return true;
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    System.out.println("Employee not found for update: " + updatedEmployee.getEmployeeId()); 
    return false; // Employee ID not found
}


    public Employee findEmployeeById(int id) {
        try {
            file.seek(0);
            while (file.getFilePointer() < file.length()) {
                Employee employee = readRecords(file.getFilePointer());
                if (employee != null && employee.getEmployeeId() == id) {
                    return employee; // Found
                }
                file.seek(file.getFilePointer() + RandomAccessEmployeeRecord.SIZE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Employee not found
    }
    
    public boolean isPpsExist(String pps, long currentByte) {
        try {
            file.seek(0);
            while (file.getFilePointer() < file.length()) {
                long position = file.getFilePointer(); // Save position before reading
                Employee e = readRecords(position); // Read the employee
                if (e.getPps().equals(pps)) return true;
                file.seek(position + RandomAccessEmployeeRecord.SIZE); // Move to next record manually
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void createFile(String path) {
        try {
            if (file != null) {
                file.close(); // Close the previous file if it's open
            }
            file = new RandomAccessFile(path, "rw"); // Open file in read-write mode
            filePath = path; // Store the path
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
      
    public void changeRecords(Employee employee, long position) {
        try {
            if (position < 0 || position >= file.length()) {
                System.out.println("Invalid position: Cannot change record.");
                return; // ✅ Do not modify if position is invalid
            }
            file.seek(position);
            RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord(
                employee.getEmployeeId(),
                employee.getPps(),
                employee.getSurname(),
                employee.getFirstName(),
                employee.getGender(),
                employee.getDepartment(),
                employee.getSalary(),
                employee.getFullTime()
            );
            record.write(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteEmployee(int employeeId) {
        List<Employee> employees = new ArrayList<>();
    
        try {
            file.seek(0);
            while (file.getFilePointer() < file.length()) {
                Employee employee = readRecords(file.getFilePointer());
                if (employee.getEmployeeId() != employeeId) { // Keep only non-deleted employees
                    employees.add(employee);
                }
                file.seek(file.getFilePointer() + RandomAccessEmployeeRecord.SIZE);
            }
    
            file.setLength(0); // Clear the file
            for (Employee emp : employees) {
                changeRecords(emp, file.getFilePointer()); // Write back valid employees
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void addEmployee(Employee newEmployee) {
        try {
            if (file == null) {
                System.out.println("Error: File is not open!");
                openWriteFile(filePath);
            }
    
            file.seek(file.length()); // Move to the end of the file
            RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord(
                newEmployee.getEmployeeId(), newEmployee.getPps(), newEmployee.getSurname(),
                newEmployee.getFirstName(), newEmployee.getGender(), newEmployee.getDepartment(),
                newEmployee.getSalary(), newEmployee.getFullTime()
            );
            record.write(file);
            file.getFD().sync(); // Ensure data is written
            System.out.println("Employee added to file: " + newEmployee); // ✅ Debugging
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}