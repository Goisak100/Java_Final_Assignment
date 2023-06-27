package finaljava.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dialog;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import finaljava.EMFManager;
import finaljava.repository.UserRepository;

public class LoginPanel extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private UserRepository userRepository = new UserRepository(EMFManager.getInstance());

    public LoginPanel(JTabbedPane tabbedPane, int manageUserTabIndex) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 이메일 입력 필드
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("이메일:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        add(emailField, gbc);

        // 비밀번호 입력 필드
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("비밀번호:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);

        // 회원가입 버튼
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton loginButton = new JButton("로그인");
        loginButton.addActionListener(e -> {
            JDialog dialog = new JDialog();
            dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setTitle("처리 중");
            dialog.setSize(200, 150);
            dialog.setLocationRelativeTo(this);
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    return userRepository.login(emailField.getText(), new String(passwordField.getPassword()));
                }

                @Override
                protected void done() {
                    dialog.dispose();
                    try {
                        if (get()) {
                            tabbedPane.setSelectedIndex(manageUserTabIndex);
                            emailField.setText("");
                            passwordField.setText("");
                        } else {
                            JOptionPane.showMessageDialog(LoginPanel.this, "아이디나 비밀번호가 올바르지 않습니다.");
                            passwordField.setText("");
                            return;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(LoginPanel.this, "로그인 중 오류가 발생했습니다.");
                        return;
                    }
                }
            };
            worker.execute();
            dialog.setVisible(true);
        });
        add(loginButton, gbc);
    }
}