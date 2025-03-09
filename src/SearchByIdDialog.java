/*
 * 
 * This is the dialog for Employee search by ID
 * 
 * */

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class SearchByIdDialog extends JDialog implements ActionListener {
	EmployeeDetails parent;
	JButton search, cancel;
	JTextField searchField;
	// constructor for SearchByIdDialog 
	public SearchByIdDialog(EmployeeDetails parent) {
		setTitle("Search by Id");
		setModal(true);
		this.parent = parent;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JScrollPane scrollPane = new JScrollPane(searchPane());
		setContentPane(scrollPane);

		getRootPane().setDefaultButton(search);
		
		setSize(500, 190);
		setLocation(350, 250);
		setVisible(true);
	}// end SearchByIdDialog
	
	// initialize search container
	public Container searchPane() {
		JPanel searchPanel = new JPanel(new GridLayout(3, 1));
		JPanel textPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		JLabel searchLabel;

		searchPanel.add(new JLabel("Search by ID"));

		textPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		textPanel.add(searchLabel = new JLabel("Enter ID:"));
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

	public void update() {
        // Refresh any search results when notified
    }

	// action listener for save and cancel button
    @Override
	public void actionPerformed(ActionEvent e) {
		// if option search, search for Employee
		if (e.getSource() == search) {
			// try get correct valus from text field
			try {
				int id = Integer.parseInt(searchField.getText().trim());
				System.out.println("DEBUG: Searching for Employee ID " + id);
            
				Employee employee = parent.getApplication().findEmployeeById(id);

				if (employee != null) {
					System.out.println("DEBUG: Employee found -> " + employee);
					parent.displayRecords(employee);
				} else {
					JOptionPane.showMessageDialog(this, "Employee not found!", "Search Result", JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (NumberFormatException num) {
				searchField.setBackground(new Color(255, 150, 150));
				JOptionPane.showMessageDialog(this, "Invalid ID format!");
			}
		} else if (e.getSource() == cancel) {
			dispose();
		}// end actionPerformed
	}// end class searchByIdDialog
}
