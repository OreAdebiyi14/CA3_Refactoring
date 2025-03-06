public class EmployeeController {
    private RandomFile fileHandler;

    public EmployeeController() {
        this.fileHandler = RandomFile.getInstance();
    }

    public Employee searchEmployeeById(int id) {
        return fileHandler.findEmployeeById(id);
    }

    public Employee searchEmployeeBySurname(String surname) {
        return fileHandler.findEmployeeBySurname(surname);
    }

    public boolean updateEmployee(Employee updatedEmployee) {
        return fileHandler.updateEmployeeInFile(updatedEmployee);
    }

    public void addEmployee(Employee newEmployee) {
        // insert newEmployee into storage (file, database, etc.)
        System.out.println("Employee added: " + newEmployee.getEmployeeId());
    }
    
    public void openFile(String path) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'openFile'");
    }
}
