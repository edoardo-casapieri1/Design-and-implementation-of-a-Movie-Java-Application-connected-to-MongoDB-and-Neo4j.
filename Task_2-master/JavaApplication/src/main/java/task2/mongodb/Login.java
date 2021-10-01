package task2.mongodb;

import java.awt.BorderLayout;
import org.neo4j.driver.Driver;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField uname;
	private JTextField passwordField;
	JFrame logframe;
	static Login frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Dao dao = DaoMongo.getInstance();
					frame = new Login(dao);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Login(final Dao dao) {

		frame = this; // risolvere il problema della finestra
		frame.setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblLogin = new JLabel("Login");
		lblLogin.setFont(new Font("Times New Roman", Font.BOLD, 20));
		lblLogin.setBounds(196, 26, 76, 24);
		contentPane.add(lblLogin);

		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(41, 83, 70, 24);
		contentPane.add(lblUsername);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(41, 129, 70, 24);
		contentPane.add(lblPassword);

		uname = new JTextField();
		uname.setBounds(107, 84, 270, 22);
		contentPane.add(uname);
		uname.setColumns(10);

		passwordField = new JPasswordField();
		passwordField.setBounds(106, 131, 270, 20);
		contentPane.add(passwordField);

		JButton btnNewButton = new JButton("Login");

		// login event
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = uname.getText();
				String password = passwordField.getText().toString();
				if (passwordField.getText().toString().trim().isEmpty() || uname.getText().trim().isEmpty())
					JOptionPane.showMessageDialog(null, "Please fill in all the fields.", "Error",
							JOptionPane.WARNING_MESSAGE);

				else {
					try {
						Driver driver_app = dao.getDriver();
						driver_app.verifyConnectivity();
						int role = dao.checkLogin(username, password);
						if (role != -1) {
							// creo l'oggetto utente
							if (role == 1) {
								User user = dao.getUser(username);
								System.out.println("Welcome, " + username + "!");
								JOptionPane.showMessageDialog(frame, "Welcome, " + username + "!", "WELCOME",
										JOptionPane.PLAIN_MESSAGE);
								Gui_user usergui = new Gui_user(dao, user);
								// usergui.setVisible(true);
								frame.dispose();

							} else {
								Admin admin = dao.getAdmin(username);
								System.out.println("Welcome, " + username + "!");
								JOptionPane.showMessageDialog(frame, "Welcome " + username, "WELCOME",
										JOptionPane.PLAIN_MESSAGE);
								Gui_admin adminGui = new Gui_admin(dao, admin);
								// admin.setVisible(true);
								frame.dispose();

							}
						}

						else {
							System.out.println("Enter a valid user ID and password.");
							JOptionPane.showMessageDialog(null, "Incorrect user ID or password.", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					} catch (Exception e2) {
						e2.printStackTrace();
						System.err.println("The service is currently unavailable. Closing...");
						dao.exit();
						System.exit(-1);
					}

				}
			}
		});
		btnNewButton.setBounds(107, 190, 89, 23);
		contentPane.add(btnNewButton);

		// redirect to registration window
		JButton btnSignup = new JButton("Signup");
		btnSignup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Registration regs = new Registration(dao);
				regs.setVisible(true);

				frame.dispose();
			}
		});
		btnSignup.setBounds(251, 190, 89, 23);
		contentPane.add(btnSignup);

		contentPane.getRootPane().setDefaultButton(btnNewButton);

		// put the frame at the center of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dao.exit();
			}
		});
	}
}
