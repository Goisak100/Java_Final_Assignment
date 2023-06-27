package finaljava;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.*;

import finaljava.view.LoginPanel;
import finaljava.view.ManageUserPanel;
import finaljava.view.SignUpPanel;

public class App {
    public static void main(String[] args) {

        JFrame frame = new JFrame("회원 관리 프로그램");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // 회원가입 탭 추가
        SignUpPanel signUpPanel = new SignUpPanel(tabbedPane, 2);
        tabbedPane.addTab("회원가입", null, signUpPanel, "회원가입 탭");

        // 로그인 탭 추가
        LoginPanel loginPanel = new LoginPanel(tabbedPane, 2);
        tabbedPane.addTab("로그인", null, loginPanel, "로그인 탭");

        // 회원 관리 탭 추가
        ManageUserPanel manageUserPanel = new ManageUserPanel();
        tabbedPane.addTab("회원 관리", null, manageUserPanel, "회원 관리 탭");

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}