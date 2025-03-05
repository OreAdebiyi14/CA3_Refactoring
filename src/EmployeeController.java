import java.util.ArrayList;
import java.util.List;

//all the business logic is in here
public class EmployeeController {
    private RandomFile fileHandler;

    public EmployeeController() {
        this.fileHandler = RandomFile.getInstance();
    }

    public void addEmployee(Employee employee) {
        fileHandler.addRecords(employee);
    }

    public Employee getEmployeeById(int id) {
        return fileHandler.findEmployeeById(id);
    }

    public List<Employee> getAllEmployees() {
        return fileHandler.getAllEmployees();
    }

    private List<EmployeeObserver> observers = new ArrayList<>();

    public void addObserver(EmployeeObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers() {
        for (EmployeeObserver observer : observers) {
            observer.update();
        }
    }

    // Call notifyObservers() whenever an employee is added/updated
    public void addEmployee(Employee employee) {
        fileHandler.addRecords(employee);
        notifyObservers();
    }

}
