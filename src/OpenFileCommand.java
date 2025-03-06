
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;

public class OpenFileCommand implements ActionListener{
    private EmployeeController controller;

    public OpenFileCommand(EmployeeController controller) {
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            controller.openFile(fileChooser.getSelectedFile().getPath());
        }
    }

    
}
