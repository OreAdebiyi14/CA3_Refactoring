import java.io.EOFException;
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
    private RandomAccessFile readFile;
    private RandomAccessFile writeFile;


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
            if (readFile != null) {
                readFile.close();
                readFile = null;
                System.out.println("DEBUG: Read file closed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeWriteFile() {
        try {
            if (writeFile != null) {
                writeFile.close();
                writeFile = null;
                System.out.println("DEBUG: Write file closed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openReadFile(String path) {
        try {
            if (readFile != null && filePath.equals(path)) { 
                System.out.println("DEBUG: Read file is already open: " + path);
                return;  //   Don't reopen if it's already open
            }
    
            if (readFile != null) {
                readFile.close(); //   Close only if it's a different file
            }
    
            readFile = new RandomAccessFile(path, "r"); //   Open in read-only mode
            filePath = path;
            System.out.println("DEBUG: Opened file for reading: " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void openWriteFile(String path) {
        try {
            if (writeFile != null && filePath.equals(path)) { 
                System.out.println("DEBUG: File is already open for writing: " + path);
                return;  //   Don't reopen if already correct
            }
    
            if (writeFile != null) {
                writeFile.close(); //   Close only if it's a different file
            }
    
            writeFile = new RandomAccessFile(path, "rw"); //   Open in read-write mode
            filePath = path;
            System.out.println("DEBUG: Opened file for writing: " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteRecords(long position) {
        try {
            file.seek(position);
            file.writeInt(0); //   Mark as deleted by setting Employee ID to 0
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
                    return true; //   At least one valid record exists
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
            if (readFile == null) {  // Ensure file is open before reading
                System.out.println("DEBUG: Read file is null! Reopening...");
                openReadFile(filePath);
            }
    
            readFile.seek(0); // Start at the beginning of the file
    
            while (readFile.getFilePointer() < readFile.length()) {
                Employee employee = readRecords(readFile.getFilePointer());
                if (employee != null && employee.getEmployeeId() != 0) { //   Skip empty records
                    return readFile.getFilePointer();
                }
                readFile.seek(readFile.getFilePointer() + RandomAccessEmployeeRecord.SIZE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        System.out.println("DEBUG: No employees found.");
        return -1; // Return -1 if no valid records exist
    }
    
    public long getLast() {
        try {
            long position = readFile.length() - RandomAccessEmployeeRecord.SIZE;
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
        return -1; //   No valid records found
    }
    
    public long getNext(long position) {
        try {
            position += RandomAccessEmployeeRecord.SIZE;
            while (position < readFile.length()) {  // Ensure we don’t go past EOF
                Employee employee = readRecords(position);
                if (employee != null && employee.getEmployeeId() != 0) {
                    return position;
                }
                position += RandomAccessEmployeeRecord.SIZE;
            }
        } catch (EOFException e) {
            System.out.println("DEBUG: End of file reached in getNext()");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;  // Return -1 if no valid record is found
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
        return -1; //   No previous valid record found
    }
     
    public Employee readRecords(long position) {
        try {
            if (readFile == null) {
                System.out.println("DEBUG: Read file is null! Reopening...");
                openReadFile(filePath);
            }
    
            if (position >= readFile.length()) {
                System.out.println("DEBUG: Reached EOF. No more records.");
                return null;
            }
    
            readFile.seek(position);
            System.out.println("DEBUG: Reading employee at position " + position);
    
            RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
            record.read(readFile);
    
            System.out.println("DEBUG: Employee read -> ID: " + record.getEmployeeId());
    
            // **Ensure we move to the next record properly**
            readFile.seek(position + RandomAccessEmployeeRecord.SIZE);
    
            return record;
        } catch (IOException e) {
            e.printStackTrace();
            return new Employee(); // Return an empty employee if an error occurs
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
        if (readFile == null) {
            System.out.println("DEBUG: Read file is null! Reopening...");
            openReadFile(filePath);
        }

        readFile.seek(0);
        System.out.println("DEBUG: Searching for employees with surname: " + surname);

        while (readFile.getFilePointer() < readFile.length()) {
            Employee employee = readRecords(readFile.getFilePointer());
            if (employee != null && employee.getEmployeeId() != 0 &&
                employee.getSurname().equalsIgnoreCase(surname)) {
                matchingEmployees.add(employee);
                System.out.println("DEBUG: Employee found -> " + employee);
            }
            readFile.seek(readFile.getFilePointer() + RandomAccessEmployeeRecord.SIZE);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return matchingEmployees;
}


    public boolean updateEmployeeInFile(Employee updatedEmployee) {
    try {
        if (writeFile == null) {  // 🔹 Ensure file is open before updating
            System.out.println("DEBUG: File is closed! Reopening...");
            openWriteFile(filePath);
        }

        writeFile.seek(0); // Start from the beginning

        while (writeFile.getFilePointer() < writeFile.length()) {
            long position = writeFile.getFilePointer();
            Employee employee = readRecords(position);

            if (employee.getEmployeeId() == updatedEmployee.getEmployeeId()) {
                writeFile.seek(position);

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

                record.write(writeFile);
                writeFile.getFD().sync();  //  Ensure immediate write

                System.out.println("DEBUG: Employee updated successfully -> " + updatedEmployee);
                return true;
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    
    System.out.println("DEBUG: Employee not found for update -> " + updatedEmployee.getEmployeeId());
    return false;
}

    public Employee findEmployeeById(int id) {
    try {
        if (readFile == null) {  
            System.out.println("DEBUG: Read file is null! Reopening...");
            openReadFile(filePath);
        }

        readFile.seek(0);  //   Reset pointer to start
        System.out.println("DEBUG: Searching for Employee ID " + id);

        while (readFile.getFilePointer() < readFile.length()) {
            long position = readFile.getFilePointer();
            Employee employee = readRecords(position);

            if (employee != null && employee.getEmployeeId() == id) {
                System.out.println("DEBUG: Employee found at position " + position + " -> " + employee);
                return employee;
            }
            readFile.seek(position + RandomAccessEmployeeRecord.SIZE);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    System.out.println("DEBUG: Employee ID " + id + " not found!");
    return null;
}
   
    public boolean isPpsExist(String pps, long currentByte) {
        try {
            readFile.seek(0);
            while (readFile.getFilePointer() < readFile.length()) {
                long position = readFile.getFilePointer(); // Save position before reading
                Employee e = readRecords(position); // Read the employee
                if (e.getPps().equals(pps)) return true;
                readFile.seek(position + RandomAccessEmployeeRecord.SIZE); // Move to next record manually
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
                return; //   Do not modify if position is invalid
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
            if (writeFile == null) {  //   Ensure file is open before deleting
                System.out.println("DEBUG: Write file is null! Reopening...");
                openWriteFile(filePath);
            }
    
            writeFile.seek(0);  //   Start from beginning
    
            while (writeFile.getFilePointer() < writeFile.length()) {
                Employee employee = readRecords(writeFile.getFilePointer());
                if (employee.getEmployeeId() != employeeId) { //   Keep only non-deleted employees
                    employees.add(employee);
                }
                writeFile.seek(writeFile.getFilePointer() + RandomAccessEmployeeRecord.SIZE);
            }
    
            writeFile.setLength(0); //   Clear the file
            for (Employee emp : employees) {
                changeRecords(emp, writeFile.getFilePointer()); //   Write back valid employees
            }
    
            System.out.println("DEBUG: Employee ID " + employeeId + " deleted successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void addEmployee(Employee newEmployee) {
        try {
            if (filePath == null || filePath.isEmpty()) {
                System.out.println("ERROR: File path is null or empty in addEmployee(). Cannot add employee.");
                return;
            }
            
            if (writeFile == null) {
                System.out.println("DEBUG: Write file is null! Reopening...");
                openWriteFile(filePath);
            }
    
            // **Ensure no duplicate employee IDs**
            Employee existing = findEmployeeById(newEmployee.getEmployeeId());
            if (existing != null) {
                System.out.println("ERROR: Employee ID already exists: " + newEmployee.getEmployeeId());
                return;
            }
    
            long writePosition = writeFile.length();
            writeFile.seek(writePosition);
    
            System.out.println("DEBUG: Adding Employee ID: " + newEmployee.getEmployeeId() + " at position: " + writePosition);
    
            RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord(
                newEmployee.getEmployeeId(), newEmployee.getPps(), newEmployee.getSurname(),
                newEmployee.getFirstName(), newEmployee.getGender(), newEmployee.getDepartment(),
                newEmployee.getSalary(), newEmployee.getFullTime()
            );
    
            record.write(writeFile);
            writeFile.getFD().sync();
            System.out.println("DEBUG: Employee added successfully with ID: " + newEmployee.getEmployeeId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    public long getFileLength() {
        try {
            if (readFile == null) {  // Ensure file is open before checking length
                System.out.println("DEBUG: Read file is null! Reopening...");
                openReadFile(filePath);
            }
            return readFile.length(); // Return file size
        } catch (IOException e) {
            e.printStackTrace();
            return 0; // Return 0 if an error occurs
        }
    }

    
}