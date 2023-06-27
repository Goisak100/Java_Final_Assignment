package finaljava.view;

import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import finaljava.model.User;

public class UsersTable {

    DefaultTableModel model;
    JTable table;

    public UsersTable(List<User> users) {
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn("이메일");
        model.addColumn("이름");
        model.addColumn("휴대전화");
        model.addColumn("주소");

        for (User user : users) {
            model.addRow(new Object[] {
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getAddress()
            });
        }

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public JTable getUsersTable() {
        return table;
    }

    public void updateUsersTable(List<User> users) {
        model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (User user : users) {
            model.addRow(new Object[] {
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getAddress()
            });
        }
        model.fireTableDataChanged();
    }
}