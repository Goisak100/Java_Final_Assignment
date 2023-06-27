package finaljava.view;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import finaljava.EMFManager;
import finaljava.UsersTableManager;
import finaljava.model.User;
import finaljava.repository.UserRepository;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ManageUserPanel extends JPanel {

    private UserRepository userRepository = new UserRepository(EMFManager.getInstance());
    private UsersTable usersTable = UsersTableManager.getInstance();
    private String searchSelectedItem = "이메일";
    private String searchText = "";
    private String selectedEmail = "";

    public ManageUserPanel() {
        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        add(searchPanel, BorderLayout.NORTH);

        String[] items = {"이메일", "이름", "휴대전화", "주소"};
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.addActionListener(e -> {
            JComboBox<?> cb = (JComboBox<?>) e.getSource();
            searchSelectedItem = (String) cb.getSelectedItem();
        });
        searchPanel.add(comboBox);

        JTextField searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                searchText = searchField.getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchText = searchField.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
        searchPanel.add(searchField);

        JButton searchFieldClearButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setStroke(new BasicStroke(2));
                g2.setColor(Color.RED);

                int x1 = 5;
                int y1 = 5;
                int x2 = getWidth() - 5;
                int y2 = getHeight() - 5;

                g2.drawLine(x1, y1, x2, y2);
                g2.drawLine(x1, y2, x2, y1);

                g2.dispose();
            }
        };
        searchFieldClearButton.setPreferredSize(new Dimension(15, 15));
        searchFieldClearButton.addActionListener(e -> {
            searchField.setText("");
            searchText = "";
        });
        searchPanel.add(searchFieldClearButton);

        JButton searchButton = new JButton("검색");
        searchButton.addActionListener(e -> {
            List<User> users = new ArrayList<User>();
            switch (searchSelectedItem) {
                case "이메일":
                    users = userRepository.findByEmailLike(searchText);
                    break;
                case "이름":
                    users = userRepository.findByNameLike(searchText);
                    break;
                case "휴대전화":
                    users = userRepository.findByPhoneNumberLike(searchText);
                    break;
                case "주소":
                    users = userRepository.findByAddressLike(searchText);
                    break;
            }
            UsersTableManager.getInstance().updateUsersTable(users);
        });
        searchPanel.add(searchButton);

        JButton allSearchButton = new JButton("전체");
        allSearchButton.addActionListener(e -> {
            searchField.setText("");
            searchText = "";
            UsersTableManager.getInstance().updateUsersTable(userRepository.findByEmailLike(""));
        });
        searchPanel.add(allSearchButton);

        JTable table = usersTable.getUsersTable();
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

                if (e.getValueIsAdjusting()) {
                    return;
                }

                int selectedRow = table.getSelectedRow();

                if (selectedRow == -1) {
                    selectedEmail = "";
                    return;
                }

                selectedEmail = (String)table.getValueAt(selectedRow, 0);
            }
        });

        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);


        // 수정 및 삭제와 관련한 UI
        JPanel bottomPanel = new JPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton updateButton = new JButton("수정하기");
        updateButton.addActionListener(e -> {
            if (selectedEmail == "") {
                JOptionPane.showMessageDialog(ManageUserPanel.this, "수정할 회원을 선택해 주세요.");
                return;
            }

            UpdateForm form = new UpdateForm(selectedEmail, searchSelectedItem, searchText);
            form.setVisible(true);
        });
        bottomPanel.add(updateButton);

        JButton deleteButton = new JButton("삭제하기");
        deleteButton.addActionListener(e -> {
            if (selectedEmail == "") {
                JOptionPane.showMessageDialog(ManageUserPanel.this, "삭제할 회원을 선택해 주세요.");
                return;
            }
            int result = JOptionPane.showOptionDialog(ManageUserPanel.this,
                "정말로 해당 회원을 삭제하시겠습니까?",
                "경고",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                null,
                JOptionPane.YES_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                userRepository.deleteByEmail(selectedEmail);
                List<User> users = new ArrayList<>();
                switch (searchSelectedItem) {
                case "이메일":
                    users = userRepository.findByEmailLike(searchText);
                    break;
                case "이름":
                    users = userRepository.findByNameLike(searchText);
                    break;
                case "휴대전화":
                    users = userRepository.findByPhoneNumberLike(searchText);
                    break;
                case "주소":
                    users = userRepository.findByAddressLike(searchText);
                    break;
                }
                UsersTableManager.getInstance().updateUsersTable(users);
            }
        });
        bottomPanel.add(deleteButton);
    }
}

// 빈 값으로 검색을 했을 때 문제가 발생한다.
// 실제로 데이터베이스는 갱신이 되었으나 과거 첫 결과물이 나온다. 왜지?
// 검색할 때 사용되는 entityManager와 수정할 때 사용하는 entityManager가 달라서 그런 듯하다.