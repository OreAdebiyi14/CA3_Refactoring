import java.awt.Color;
import javax.swing.JTextField;

public class Validation {
    public static boolean validateTextField(JTextField field) {
        if (field.getText().trim().isEmpty()) {
            field.setBackground(new Color(255, 150, 150));
            return false;
        }
        field.setBackground(Color.WHITE);
        return true;
    }
}
