import java.util.List;

public class EmployeeController {
    private RandomFile fileHandler;

    public EmployeeController() {
        this.fileHandler = RandomFile.getInstance();
    }

    public Employee searchEmployeeById(int id) {
        return fileHandler.findEmployeeById(id);
    }

    public List<Employee> searchEmployeeBySurname(String surname) {
        return fileHandler.findEmployeeBySurname(surname);
    }

    public boolean updateEmployee(Employee updatedEmployee) {
        return fileHandler.updateEmployeeInFile(updatedEmployee);
    }

    public void addEmployee(Employee newEmployee) {
        // insert newEmployee into storage (file, database, etc.)
        fileHandler.addEmployee(newEmployee);
    }

    public void deleteEmployee(int id) {
        fileHandler.deleteEmployee(id);
    }

    public void openFile(String path) {
        if (path == null || path.isEmpty()) {
            System.out.println("Error: File path is empty!");
            return;
        }
    
        try {
            RandomFile.getInstance().openWriteFile(path); // Open the file for reading and writing
            System.out.println("File opened successfully: " + path); 
        } catch (Exception e) {
            System.out.println("Error opening file: " + e.getMessage());
        }
    }
    
    
    
}
