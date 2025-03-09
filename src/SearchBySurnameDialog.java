/*
 * 
 * This is a dialog for searching Employees by their surname.
 * 
 * */

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

public class SearchBySurnameDialog extends JDialog implements ActionListener{
	EmployeeDetails parent;
	JButton search, cancel;
	JTextField searchField;
	// constructor for search by surname dialog
	public SearchBySurnameDialog(EmployeeDetails parent) {
		setTitle("Search by Surname");
		setModal(true);
		this.parent = parent;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JScrollPane scrollPane = new JScrollPane(searchPane());
		setContentPane(scrollPane);

		getRootPane().setDefaultButton(search);
		
		setSize(500, 190);
		setLocation(350, 250);
		setVisible(true);
	}// end SearchBySurnameDialog
	
	// initialize search container
	public Container searchPane() {
		JPanel searchPanel = new JPanel(new GridLayout(3,1));
		JPanel textPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		JLabel searchLabel;

		searchPanel.add(new JLabel("Search by Surname"));
	
		textPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		textPanel.add(searchLabel = new JLabel("Enter Surname:"));
		searchLabel.setFont(this.parent.font1);
		textPanel.add(searchField = new JTextField(20));
		searchField.setFont(this.parent.font1);
		searchField.setDocument(new JTextFieldLimit(20));

		buttonPanel.add(search = new JButton("Search"));
		search.addActionListener(this);
		search.requestFocus();
		
		buttonPanel.add(cancel = new JButton("Cancel"));
		cancel.addActionListener(this);
		
		searchPanel.add(textPanel);
		searchPanel.add(buttonPanel);

		return searchPanel;
	}// end searchPane

	// action listener for save and cancel button
    @Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == search) {
			String surname = searchField.getText().trim().toUpperCase();
        	System.out.println("DEBUG: Searching for employees with surname: " + surname);
        
			List<Employee> employees = parent.getApplication().findEmployeeBySurname(surname);
			Vector<Vector<Object>> employeeData = new Vector<>();

			if (employees.isEmpty()) {
				JOptionPane.showMessageDialog(this, "No employees found with that surname.");
			} else {
				for (Employee emp : employees) {
					Vector<Object> row = new Vector<>();
					row.add(emp.getEmployeeId());
					row.add(emp.getPps());
					row.add(emp.getSurname());
					row.add(emp.getFirstName());
					row.add(emp.getGender());
					row.add(emp.getDepartment());
					row.add(emp.getSalary());
					row.add(emp.getFullTime());
					employeeData.add(row);
			}
				new EmployeeSummaryDialog(employeeData);
			}
			dispose();
		} else if (e.getSource() == cancel) {
			dispose();
		}
	}
// end actionPerformed
}// end class SearchBySurnameDialog
