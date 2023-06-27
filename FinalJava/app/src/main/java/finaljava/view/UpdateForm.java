package finaljava.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import finaljava.EMFManager;
import finaljava.UsersTableManager;
import finaljava.model.User;
import finaljava.repository.UserRepository;

public class UpdateForm extends JFrame {

    private UserRepository userRepository = new UserRepository(EMFManager.getInstance());
    private String updateName = "";
    private String updatePhoneNumber = "";
    private String updateAddress = "";

    public UpdateForm(String email, String searchSelectedItem, String searchText) {

        setTitle("회원정보 수정 화면");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLayout(new GridBagLayout());
        setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();

        Optional<User> finduser = userRepository.findByEmail(email).stream().findFirst();
        User user = finduser.get();

        updateName = user.getName();
        updatePhoneNumber = user.getPhoneNumber();
        updateAddress = user.getAddress();

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("이메일: "), gbc);
        
        gbc.gridx = 1;
        add(new JLabel(user.getEmail()));

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("이름: "), gbc);
        
        gbc.gridx = 1;
        JTextField nameField = new JTextField(user.getName(), 20);
        nameField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateName = nameField.getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateName = nameField.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
        add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("휴대전화: "), gbc);
        
        gbc.gridx = 1;
        JTextField phoneNumberField = new JTextField(user.getPhoneNumber(), 20);
        phoneNumberField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePhoneNumber = phoneNumberField.getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePhoneNumber = phoneNumberField.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
        add(phoneNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("주소: "), gbc);
        
        gbc.gridx = 1;
        JTextField addressField = new JTextField(user.getAddress(), 20);
        addressField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateAddress = addressField.getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateAddress = addressField.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
        add(addressField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JButton updateButton = new JButton("수정하기");
        updateButton.addActionListener(e -> {
            user.setName(updateName);
            user.setPhoneNumber(updatePhoneNumber);
            user.setAddress(updateAddress);
            userRepository.updateUser(user);
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
                dispose();
        });

        // 수정하기 로직 작동
        // 수정한 다음에 새로운 테이블 조회하는 거 갱신까지
        // 폼 닫는 것도 해야 함
        
        add(updateButton, gbc);
    }
}
