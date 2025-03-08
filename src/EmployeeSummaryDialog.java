/*
 * 
 * This is the summary dialog for displaying all Employee details
 * 
 * */

 import java.awt.Component;
 import java.awt.Container;
 import java.awt.Dimension;
 import java.awt.FlowLayout;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.text.DecimalFormat;
 import java.util.Vector;
 import javax.swing.BorderFactory;
 import javax.swing.JButton;
 import javax.swing.JDialog;
 import javax.swing.JFrame;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.table.DefaultTableCellRenderer;
 import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;
 
 
public class EmployeeSummaryDialog extends JDialog implements ActionListener, EmployeeObserver {
	// Vector containing all employee details
	 private Vector<Vector<Object>> allEmployees;
	 private JButton back;
	 private JTable employeeTable;
	 private DefaultTableModel tableModel;
	 
	 public EmployeeSummaryDialog(Vector<Vector<Object>> allEmployees) {
		setTitle("Employee Summary");
		setModal(true);
		this.allEmployees = allEmployees; 
		EmployeeDetails.getInstance().addObserver(this); // Subscribe to updates
	
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	
		JScrollPane scrollPane = new JScrollPane(summaryPane());
		setContentPane(scrollPane);
	
		setSize(850, 500);
		setLocation(350, 250);
		setVisible(true);
	}
	
 
	 // Initialize container
	 public Container summaryPane() {
		 JPanel summaryDialog = new JPanel(new MigLayout());
		 JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
 
		 // Column headers
		 Vector<String> header = new Vector<>();
		 header.add("ID");
		 header.add("PPS Number");
		 header.add("Surname");
		 header.add("First Name");
		 header.add("Gender");
		 header.add("Department");
		 header.add("Salary");
		 header.add("Full Time");
 
		 // Column widths
		 int[] colWidth = { 15, 100, 120, 120, 50, 120, 80, 80 };
 
		 // Column alignments
		 DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		 centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		 DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		 leftRenderer.setHorizontalAlignment(JLabel.LEFT);
 
		 // ✅ Correct table model with proper vector types
		 tableModel = new DefaultTableModel(allEmployees, header) {
			 @Override
			 public Class<?> getColumnClass(int columnIndex) {
				 switch (columnIndex) {
					 case 0: return Integer.class;
					 case 4: return Character.class;
					 case 6: return Double.class;
					 case 7: return Boolean.class;
					 default: return String.class;
				 }
			 }
		 };
 
		 // Create table
		 employeeTable = new JTable(tableModel);
 
		 // Set column widths
		 for (int i = 0; i < employeeTable.getColumnCount(); i++) {
			 employeeTable.getColumnModel().getColumn(i).setMinWidth(colWidth[i]);
		 }
 
		 // Set alignments
		 employeeTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
		 employeeTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
		 employeeTable.getColumnModel().getColumn(6).setCellRenderer(new DecimalFormatRenderer());
 
		 employeeTable.setEnabled(false);
		 employeeTable.setPreferredScrollableViewportSize(new Dimension(800, (15 * employeeTable.getRowCount() + 15)));
		 employeeTable.setAutoCreateRowSorter(true);
		 JScrollPane scrollPane = new JScrollPane(employeeTable);
 
		 // Add back button
		 buttonPanel.add(back = new JButton("Back"));
		 back.addActionListener(this);
		 back.setToolTipText("Return to main screen");
 
		 // Add components to summary dialog
		 summaryDialog.add(buttonPanel, "growx, pushx, wrap");
		 summaryDialog.add(scrollPane, "growx, pushx, wrap");
		 scrollPane.setBorder(BorderFactory.createTitledBorder("Employee Details"));
 
		 return summaryDialog;
	 }
 
	 @Override
	 public void actionPerformed(ActionEvent e) {
		 if (e.getSource() == back) {
			 dispose();
		 }
	 }
 
	 @Override
	 public void update() {
		 tableModel.setRowCount(0); // Clear existing rows
		 Vector<Vector<Object>> updatedEmployees = EmployeeDetails.getInstance().getAllEmployees();
		 for (Vector<Object> emp : updatedEmployees) {
			 tableModel.addRow(emp);
		 }
		 tableModel.fireTableDataChanged(); // Notify the table to refresh
	 }
	 

	 // Format for salary column
	 static class DecimalFormatRenderer extends DefaultTableCellRenderer {
		 private static final DecimalFormat format = new DecimalFormat("\u20ac ###,###,##0.00");
 
		 @Override
		 public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				 int row, int column) {
 
			 Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			 JLabel label = (JLabel) c;
			 label.setHorizontalAlignment(JLabel.RIGHT);
			 // Format salary column
			 value = format.format((Number) value);
 
			 return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		 }
	 }
 } // ✅ End of class EmployeeSummaryDialog
 