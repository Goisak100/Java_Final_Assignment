package finaljava.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import java.awt.Dialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import finaljava.EMFManager;
import finaljava.UsersTableManager;
import finaljava.repository.UserRepository;

public class SignUpPanel extends JPanel {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JTextField phoneNumberField;
    private JTextField addressField;
    private UserRepository userRepository = new UserRepository(EMFManager.getInstance());
    private boolean isDuplicateEmail = true;

    private final String EMAIL_PATTERN = "^[A-Za-z0-9_.]+@[A-Za-z]+\\.[A-Za-z]+$";
    private final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    private boolean isValidEmail(String email) {
        return pattern.matcher(email).matches();
    }

    public SignUpPanel(JTabbedPane tabbedPane, int manageUserTabIndex) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 이메일 입력 필드
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("이메일:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        emailField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { 
                isDuplicateEmail = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                isDuplicateEmail = true;
             }

            @Override
            public void changedUpdate(DocumentEvent e) {  }
        });
        add(emailField, gbc);

        gbc.gridx = 2;
        JButton checkDuplicationButton = new JButton("중복 확인");
        checkDuplicationButton.addActionListener(e -> {
        
            if (!isValidEmail(emailField.getText())) {
                JOptionPane.showMessageDialog(SignUpPanel.this, "이메일의 형식이 올바르지 않습니다.");
                return;
            }
        
            boolean result = userRepository.isDuplicateEmail(emailField.getText());
            if (result == false) {
                JOptionPane.showMessageDialog(SignUpPanel.this, "사용할 수 있는 이메일입니다.");
                isDuplicateEmail = false;
            } else {
                JOptionPane.showMessageDialog(SignUpPanel.this, "이미 사용 중인 이메일입니다.");
                isDuplicateEmail = true;
            }
        });
        add(checkDuplicationButton, gbc);

        // 비밀번호 입력 필드
        // 유효성 검사를 해야 함. 특수문자, 대소문자, 숫자가 섞인 12문자 이상
        // 위아래로 교차 검증하는 것이 필요함.
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("비밀번호:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("이름:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(20);
        add(nameField, gbc);

        // 휴대전화 번호가 입력될 때 숫자만 입력되게 해야 하고,
        // 자동으로 - 기호를 넣어주어야 한다. ex) 010-4477-0747
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("휴대전화"), gbc);

        gbc.gridx = 1;
        phoneNumberField = new JTextField(20);
        add(phoneNumberField, gbc);

        // 주소 API를 이용해서 주소를 검색해서 넣을 수 있게 수정해야 한다.
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("주소"), gbc);

        gbc.gridx = 1;
        addressField = new JTextField(20);
        add(addressField, gbc);
        

        // 회원가입 버튼
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton signUpButton = new JButton("회원가입");
        signUpButton.addActionListener(e -> {

            if (emailField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(SignUpPanel.this, "이메일을 입력해 주세요.");
                return;
            }

            if (new String(passwordField.getPassword()).isEmpty()) {
                JOptionPane.showMessageDialog(SignUpPanel.this, "비밀번호를 입력해 주세요.");
                return;
            }

            if (isDuplicateEmail) {
                JOptionPane.showMessageDialog(SignUpPanel.this, "이메일 중복을 확인해 주십시오.");
                return;
            }

            if (nameField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(SignUpPanel.this, "이름을 입력해 주세요.");
                return;
            }

            if (phoneNumberField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(SignUpPanel.this, "휴대전화를 입력해 주세요.");
                return;
            }

            if (addressField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(SignUpPanel.this, "주소를 입력해 주세요.");
                return;
            }

            JDialog dialog = new JDialog();
            dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setTitle("처리 중");
            dialog.setSize(200, 150);
            dialog.setLocationRelativeTo(this);
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    userRepository.register(
                        emailField.getText(), 
                        new String(passwordField.getPassword()),
                        nameField.getText(),
                        phoneNumberField.getText(),
                        addressField.getText()
                    );
                    return null;
                }

                @Override
                protected void done() {
                    dialog.dispose();
                    JOptionPane.showMessageDialog(SignUpPanel.this, "회원가입이 완료되었습니다.");
                    emailField.setText("");
                    passwordField.setText("");
                    nameField.setText("");
                    phoneNumberField.setText("");
                    addressField.setText("");
                    isDuplicateEmail = true;
                    tabbedPane.setSelectedIndex(manageUserTabIndex);
                    UsersTableManager.getInstance().updateUsersTable(userRepository.findByEmailLike(""));
                }
            };
            worker.execute();
            dialog.setVisible(true);
        });
        add(signUpButton, gbc);
    }
}