import java.io.IOException;
import java.io.RandomAccessFile;
import javax.swing.JOptionPane;

public class EmployeeRecordManager {

    static long getLast() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    private RandomAccessFile file;

    public EmployeeRecordManager(String filePath) throws IOException {
        file = new RandomAccessFile(filePath, "rw"); // File is already open
    }

    public void addEmployee(Employee emp) {
        try {
            file.seek(file.length());
            file.writeInt(emp.getEmployeeId());
            file.writeUTF(emp.getPps());
            file.writeUTF(emp.getSurname());
            file.writeUTF(emp.getFirstName());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error writing to file!");
        }
    }

    public void close() throws IOException {
        if (file != null) file.close();
    }

    public void deleteRecord(long byteToStart) {
        try {
            file.seek(byteToStart);
            file.writeInt(0); // Mark record as deleted
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error deleting record!");
        }
    }

    public boolean isSomeoneToDisplay() {
        try {
            file.seek(0); // Start reading from the beginning
            while (file.getFilePointer() < file.length()) {
                int employeeId = file.readInt(); // Read Employee ID
                file.skipBytes(169); // Skip rest of the record (size 175 bytes - 4 bytes already read)
                if (employeeId > 0) { // If ID > 0, record exists
                    return true;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading file!");
        }
        return false;
    }
    

    public boolean isPpsExist(String pps, long currentByte) {
        try {
            file.seek(0); // Start from beginning
            while (file.getFilePointer() < file.length()) {
                long position = file.getFilePointer(); // Save position
                int employeeId = file.readInt();
                String filePps = file.readUTF(); // Read PPS number
    
                file.skipBytes(166); // Skip the rest of the record
    
                if (employeeId > 0 && filePps.equalsIgnoreCase(pps) && position != currentByte) {
                    JOptionPane.showMessageDialog(null, "PPS number already exists!");
                    return true;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading file!");
        }
        return false;
    }
    

    public void updateEmployee(Employee emp, long byteToStart) {
        try {
            file.seek(byteToStart);
            file.writeInt(emp.getEmployeeId());
            file.writeUTF(emp.getPps());
            file.writeUTF(emp.getSurname());
            file.writeUTF(emp.getFirstName());
            file.writeChar(emp.getGender());
            file.writeUTF(emp.getDepartment());
            file.writeDouble(emp.getSalary());
            file.writeBoolean(emp.getFullTime());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error updating employee record!");
        }
    }


    public long getFirst() throws IOException {
        file.seek(0); // Move to the beginning of the file
        return file.getFilePointer(); // Return first byte position
    }

    public Employee readRecord(long currentByteStart) throws IOException {
        file.seek(currentByteStart);
        int employeeId = file.readInt();
        String pps = file.readUTF();
        String surname = file.readUTF();
        String firstName = file.readUTF();
        char gender = file.readChar();
        String department = file.readUTF();
        double salary = file.readDouble();
        boolean fullTime = file.readBoolean();
    
        return new Employee(employeeId, pps, surname, firstName, gender, department, salary, fullTime);
    }
    
    public void closeReadFile() throws IOException {
        if (file != null) {
            file.close();
        }
    }
    
    public long getPrevious(long currentByteStart) throws IOException {
        long newByteStart = currentByteStart - RandomAccessEmployeeRecord.SIZE;
        if (newByteStart < 0) {
            newByteStart = 0; // Ensure it doesn't go before the file start
        }
        return newByteStart;
    }
    
    public long getNext(long currentByteStart) throws IOException {
        long newByteStart = currentByteStart + RandomAccessEmployeeRecord.SIZE;
        if (newByteStart >= file.length()) {
            newByteStart = file.length() - RandomAccessEmployeeRecord.SIZE; // Ensure it doesn't exceed file length
        }
        return newByteStart;
    }
    
    
}
