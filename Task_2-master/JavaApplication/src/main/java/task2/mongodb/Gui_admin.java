package task2.mongodb;

import java.awt.EventQueue;
import java.io.BufferedReader;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import java.awt.Image;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.FlowLayout;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.bson.Document;
import org.neo4j.driver.exceptions.ServiceUnavailableException;

import com.mongodb.MongoException;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.awt.Image;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.FlowLayout;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Component;

import java.text.DecimalFormat;

public class Gui_admin extends JFrame {

	private static final long serialVersionUID = 1L;
	private int index;
	JFrame frameAdmin;
	ArrayList<String> list_Movies_2000 = new ArrayList<String>();
	ArrayList<Request> Request_list = new ArrayList<Request>();

	private DefaultTableModel model;
	DefaultTableModel model_actors;
	DefaultTableModel model_platforms = new DefaultTableModel();
	DefaultTableModel model_awards = new DefaultTableModel();
	JPanel panelMovieCast;
	JFrame frameLogin;
	private int created = 0;
	private List<BasicFilm> films_list = new ArrayList<BasicFilm>();
	private int indexComment = 0;
	private int previndexComment = -1;
	private Film CurrentFilm;
	private String filmtitle = "";
	private BasicFilm filmToDelete;

	private JTextField textField;
	private JTable MoviesTable;
	private JTable PlatformsTable;
	private JTable ActorsTable;
	private JTable WatchedTable;
	private JTable FavoriteTable;
	private JTable MostViewed_LastMonthTable;
	private JTable RequestHistoryTable;

	private JTextField textField_Title;
	private JTextField Old_Password_text_Field;
	private JTextField New_Password_text_Field;
	private JTextField Confirm_Password_text_Field;

	private JTable RequestTable;

	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_OriginalTitle;
	private JTextField textField_ItalianTitle;
	private JTextField textField_Year;
	private JTextField textField_Runtime;
	private JTextField uname;
	private JTextField fname;
	private JTextField lname;
	private JTextField pass;
	private JTextField textField_9;

	private JTable tableNations;
	private boolean check;
	private List<RequestAdmin> requests_list;
	// private int created = 0;
	// private List<BasicFilm> films_list = new ArrayList<BasicFilm>();

	private final JComboBox YearcomboBox;
	private JTable tableAwards;
	// private Film CurrentFilm;

	// public static void main(String[] args) {

	/*
	 * EventQueue.invokeLater(new Runnable() { public void run() { try {
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } } }); }
	 */
	// }
	private static BufferedImage resize(BufferedImage img, int height, int width) {
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resized;
	}

	private class printOutput extends Thread {
		InputStream is = null;

		printOutput(InputStream is, String type) {
			this.is = is;
		}

		public void run() {
			String s = null;
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				while ((s = br.readLine()) != null) {
					System.out.println(s);
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public Gui_admin(final Dao dao, final Admin user) {

		/*
		 * //populate list of movies try { Scanner s = new Scanner(new
		 * File("/Users/Amet/Desktop/database/task2/2000.txt")); while
		 * (s.hasNextLine()){ list_Movies_2000.add(s.nextLine()); } s.close(); }catch
		 * (Exception e) { e.printStackTrace(); }
		 * 
		 * 
		 * //populate list of requests try {
		 * 
		 * //Scanner s = new Scanner(new
		 * File("/Users/Amet/Desktop/database/task2/req.txt")); //while
		 * (s.hasNextLine()){ Request r=new Request("the matrix",1);
		 * Request_list.add(r); r=new Request("spiderman",1); Request_list.add(r); r=new
		 * Request("dumbo",2); Request_list.add(r); r=new Request("yogi",156);
		 * Request_list.add(r);
		 * 
		 * //} //s.close(); }catch (Exception e) { e.printStackTrace(); }
		 */

		index = 0;
		try {
			films_list = dao.getBasicFilmsIndex(index);
		} catch (MongoException e2) {
			e2.printStackTrace();
			System.err.println("The service is currently unavailable. Closing...");
			dao.exit();
			System.exit(-1);
		}catch(ServiceUnavailableException ex ) {
			System.err.println("The service is currently unavailable. Closing...");
			dao.exit();
			System.exit(-1);
		}

		// Content GUI
		Color myGray = new Color(238, 238, 238);
		Border border = BorderFactory.createEmptyBorder(0, 0, 0, 0);
		frameAdmin = new JFrame();
		frameAdmin.setResizable(false);
		frameAdmin.getContentPane().setBackground(new Color(65, 105, 225));
		frameAdmin.getContentPane().setForeground(new Color(30, 144, 255));
		frameAdmin.getContentPane().setLayout(null);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setBounds(0, 0, 1200, 678);
		splitPane.setResizeWeight(0.5);
		splitPane.setEnabled(false);
		frameAdmin.getContentPane().add(splitPane);

		JPanel paneLeft = new JPanel();
		splitPane.setLeftComponent(paneLeft);
		paneLeft.setLayout(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(6, 6, 581, 662);
		paneLeft.add(tabbedPane);

		try {
			user.fill_Request(dao);
		} catch (MongoException e2) {
			e2.printStackTrace();
			System.err.println("The service is currently unavailable. Closing...");
			dao.exit();
			System.exit(-1);
		}catch(ServiceUnavailableException ex ) {
			System.err.println("The service is currently unavailable. Closing...");
			dao.exit();
			System.exit(-1);
		}catch (Exception e) {
			// e.printStackTrace();
			System.out.println("L' Admin non ha ricevuto richieste");
		}

		// Variabile per controllare se sono necessari aggiornamenti delle richieste da
		// visualizzare
		check = false;

		/* PANEL PROFILE */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/
		// JPanel panel = new JPanel();
		// tabbedPane.addTab("Admin profile", null, panel, null);
		// panel.setLayout(null);

		String username = user.getUsername();
		String fname = user.getFirst_name();
		String lname = user.getLast_name();
		String country = user.getCountry();
		String year = user.getYear_of_birth().toString();

		JPanel panelProfile = new JPanel();
		tabbedPane.addTab("Profile", null, panelProfile, null);

		JLabel lblName = new JLabel("Hi, " + fname + "!");
		lblName.setFont(new Font("Lucida Grande", Font.BOLD, 20));

		JLabel lbl_Name_tag = new JLabel("Name");
		lbl_Name_tag.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		JLabel lbl_surname_tag = new JLabel("Surname");
		lbl_surname_tag.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		JLabel lbl_Name_Text = new JLabel(fname);

		JLabel lbl_surname_text = new JLabel(lname);

		JLabel lbl_Country_tag = new JLabel("Country");
		lbl_Country_tag.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		JLabel lbl_Country_text = new JLabel(country);

		JLabel lbl_Year_of_Birth_tag = new JLabel("Year");
		lbl_Year_of_Birth_tag.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		JLabel lbl_Year_of_Birth_text = new JLabel(year); /////////////// da sistemare

		JLabel lbl_General_Info_tag = new JLabel("Info");
		lbl_General_Info_tag.setFont(new Font("Lucida Grande", Font.BOLD, 17));

		JLabel lbl_Access_Info_tag = new JLabel("Credentials");
		lbl_Access_Info_tag.setFont(new Font("Lucida Grande", Font.BOLD, 17));

		JLabel lbl_Username_tag = new JLabel("Username");
		lbl_Username_tag.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		JLabel lbl_Username_text = new JLabel(username);

		JLabel lbl_Change_Password_tag = new JLabel("Change password");
		lbl_Change_Password_tag.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		Old_Password_text_Field = new JTextField();
		Old_Password_text_Field.setColumns(10);

		// Controllo vecchia password

		New_Password_text_Field = new JTextField();
		New_Password_text_Field.setColumns(10);

		Confirm_Password_text_Field = new JTextField();
		Confirm_Password_text_Field.setColumns(10);

		JButton btn_change_password = new JButton("Change"); // Bottone per il cambio della password

		btn_change_password.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean result = false;
				String oldPassword = Old_Password_text_Field.getText();
				String newPassword = New_Password_text_Field.getText();
				String confirmPassword = Confirm_Password_text_Field.getText();
				if (!newPassword.equals(confirmPassword)) {
					Object[] options = { "OK" };
					JOptionPane.showOptionDialog(null, "Passwords are not the same. Click OK to continue", "Warning",
							JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, null, options[0]);
					Old_Password_text_Field.setText("");
					New_Password_text_Field.setText("");
					Confirm_Password_text_Field.setText("");
				} else {
					try {
						result = dao.changePassword(user.getUserId(), oldPassword, confirmPassword);
					} catch (MongoException e2) {
						e2.printStackTrace();
						System.err.println("The service is currently unavailable. Closing...");
						dao.exit();
						System.exit(-1);
					}catch(ServiceUnavailableException ex ) {
						System.err.println("The service is currently unavailable. Closing...");
						dao.exit();
						System.exit(-1);
					}
					if (result == false) {
						Object[] options = { "OK" };
						JOptionPane.showOptionDialog(null, "The old password inserted is wrong. Click OK to continue",
								"Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, null,
								options[0]);
						Old_Password_text_Field.setText("");
						New_Password_text_Field.setText("");
						Confirm_Password_text_Field.setText("");
					} else {
						Object[] options = { "OK" };
						JOptionPane.showOptionDialog(null, "Password has been modified correctly. Click OK to continue",
								"Info", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null,
								options[0]);
						Old_Password_text_Field.setText("");
						New_Password_text_Field.setText("");
						Confirm_Password_text_Field.setText("");
					}
				}
			}
		});

		JLabel lbl_Old_Password = new JLabel("Old password");
		lbl_Old_Password.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		JLabel lblNewPassword = new JLabel("New password");
		lblNewPassword.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		JLabel lblConfirmPassword = new JLabel("Confirm password");
		lblConfirmPassword.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		JButton btnLogut = new JButton("Logout");
		btnLogut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// guiUser.setVisible(false);
				frameAdmin.dispose();
				Login l = new Login(dao);

			}
		});

		// admi profile gui
		GroupLayout gl_panelProfile = new GroupLayout(panelProfile);
		gl_panelProfile.setHorizontalGroup(gl_panelProfile.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelProfile.createSequentialGroup().addGroup(gl_panelProfile
						.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panelProfile.createSequentialGroup().addGap(44).addGroup(gl_panelProfile
								.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_panelProfile.createSequentialGroup()
										.addGroup(gl_panelProfile.createParallelGroup(Alignment.LEADING)
												.addComponent(lbl_General_Info_tag)
												.addGroup(gl_panelProfile.createParallelGroup(Alignment.TRAILING)
														.addComponent(lbl_Country_tag, GroupLayout.PREFERRED_SIZE, 71,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(lblName, GroupLayout.PREFERRED_SIZE, 142,
																GroupLayout.PREFERRED_SIZE))
												.addComponent(lbl_Access_Info_tag, GroupLayout.PREFERRED_SIZE, 112,
														GroupLayout.PREFERRED_SIZE))
										.addGap(13))
								.addGroup(
										gl_panelProfile.createSequentialGroup().addComponent(lbl_Name_tag).addGap(28)))
								.addPreferredGap(ComponentPlacement.RELATED, 27, Short.MAX_VALUE))
						.addGroup(gl_panelProfile.createSequentialGroup().addGap(119)
								.addGroup(gl_panelProfile
										.createParallelGroup(Alignment.TRAILING).addComponent(lbl_Username_tag)
										.addGroup(gl_panelProfile.createParallelGroup(Alignment.LEADING)
												.addComponent(lbl_Old_Password).addComponent(lblNewPassword)))
								.addGap(9)))
						.addGroup(gl_panelProfile.createParallelGroup(Alignment.LEADING)
								.addComponent(lbl_Name_Text, GroupLayout.PREFERRED_SIZE, 117,
										GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_panelProfile.createParallelGroup(Alignment.TRAILING)
										.addComponent(lbl_Country_text, GroupLayout.PREFERRED_SIZE, 84,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(lbl_Username_text)))
						.addGap(12)
						.addGroup(gl_panelProfile.createParallelGroup(Alignment.LEADING)
								.addComponent(lbl_surname_tag, GroupLayout.PREFERRED_SIZE, 73,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(lbl_Year_of_Birth_tag, GroupLayout.PREFERRED_SIZE, 40,
										GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(gl_panelProfile.createParallelGroup(Alignment.LEADING)
								.addComponent(lbl_Year_of_Birth_text)
								.addComponent(lbl_surname_text, GroupLayout.PREFERRED_SIZE, 100,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(btnLogut))
						.addGap(54))
				.addGroup(
						gl_panelProfile.createSequentialGroup().addGap(106).addComponent(lblConfirmPassword).addGap(18)
								.addGroup(gl_panelProfile.createParallelGroup(Alignment.LEADING, false)
										.addComponent(Old_Password_text_Field).addComponent(New_Password_text_Field)
										.addComponent(Confirm_Password_text_Field, GroupLayout.DEFAULT_SIZE, 248,
												Short.MAX_VALUE))
								.addGap(65))
				.addGroup(
						gl_panelProfile.createSequentialGroup().addGap(212)
								.addComponent(btn_change_password, GroupLayout.PREFERRED_SIZE, 73,
										GroupLayout.PREFERRED_SIZE)
								.addContainerGap(275, Short.MAX_VALUE))
				.addGroup(gl_panelProfile.createSequentialGroup().addGap(180).addComponent(lbl_Change_Password_tag)
						.addContainerGap(259, Short.MAX_VALUE)));
		gl_panelProfile
				.setVerticalGroup(
						gl_panelProfile.createParallelGroup(Alignment.LEADING)
								.addGroup(
										gl_panelProfile.createSequentialGroup().addGap(28)
												.addGroup(gl_panelProfile.createParallelGroup(Alignment.TRAILING)
														.addComponent(lblName, GroupLayout.PREFERRED_SIZE, 49,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(btnLogut))
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(lbl_General_Info_tag).addGap(8)
												.addGroup(gl_panelProfile.createParallelGroup(Alignment.BASELINE)
														.addComponent(lbl_Name_tag).addComponent(lbl_Name_Text)
														.addComponent(lbl_surname_tag).addComponent(lbl_surname_text))
												.addGap(18)
												.addGroup(gl_panelProfile.createParallelGroup(Alignment.BASELINE)
														.addComponent(lbl_Country_tag).addComponent(lbl_Country_text)
														.addComponent(lbl_Year_of_Birth_tag)
														.addComponent(lbl_Year_of_Birth_text))
												.addGap(12)
												.addComponent(lbl_Access_Info_tag, GroupLayout.PREFERRED_SIZE, 21,
														GroupLayout.PREFERRED_SIZE)
												.addGap(18)
												.addGroup(gl_panelProfile.createParallelGroup(Alignment.BASELINE)
														.addComponent(lbl_Username_text).addComponent(lbl_Username_tag))
												.addGap(18).addComponent(lbl_Change_Password_tag)
												.addPreferredGap(ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
												.addGroup(gl_panelProfile.createParallelGroup(Alignment.BASELINE)
														.addComponent(
																Old_Password_text_Field, GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addComponent(lbl_Old_Password))
												.addGap(25)
												.addGroup(gl_panelProfile.createParallelGroup(Alignment.BASELINE)
														.addComponent(New_Password_text_Field,
																GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(lblNewPassword))
												.addGap(26)
												.addGroup(gl_panelProfile.createParallelGroup(Alignment.BASELINE)
														.addComponent(Confirm_Password_text_Field,
																GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(lblConfirmPassword))
												.addGap(27).addComponent(btn_change_password,
														GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
												.addGap(145)));
		panelProfile.setLayout(gl_panelProfile);

		/*
		 * panel manage requests
		 * =============================================================================
		 * ===============
		 */

		JPanel PanelMangReq = new JPanel();
		tabbedPane.addTab("Manage Requests", null, PanelMangReq, null);
		PanelMangReq.setLayout(null);

		JScrollPane paneRequests = new JScrollPane();
		paneRequests.setBounds(6, 6, 548, 572);
		PanelMangReq.add(paneRequests);

		frameAdmin.setForeground(Color.BLUE);
		frameAdmin.setBounds(100, 100, 1200, 700);
		frameAdmin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// center the frame on the mid of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frameAdmin.setLocation(dim.width / 2 - frameAdmin.getSize().width / 2,
				dim.height / 2 - frameAdmin.getSize().height / 2);

		// fill the table of requests

		RequestTable = new JTable();
		RequestTable.setRowSelectionAllowed(false);
		RequestTable.setBackground(Color.WHITE);
		RequestTable.setBorder(null);
		RequestTable.setEnabled(false);
		paneRequests.setViewportView(RequestTable);

		DefaultTableModel model_Req = new DefaultTableModel();
		Object[] columns_Req = new Object[4];

		columns_Req[0] = "Movie Title";
		columns_Req[1] = "Username";
		columns_Req[2] = "Status";
		columns_Req[3] = "Date";

		model_Req.setColumnIdentifiers(columns_Req);
		RequestTable.setModel(model_Req);
		RequestTable.setGridColor(new Color(0, 0, 0));
		RequestTable.setEnabled(false);

		JButton btnRequestProcessed = new JButton("Request history");
		btnRequestProcessed.setBounds(201, 581, 166, 29);
		PanelMangReq.add(btnRequestProcessed);

		requests_list = user.getRequestAdmin();

		JButton btnBack = new JButton("Back");
		btnBack.setBounds(414, 581, 117, 29);
		PanelMangReq.add(btnBack);

		btnBack.setVisible(false);
		btnBack.setEnabled(false);

		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (check == true) {
					try {
						user.fill_Request(dao);
					} catch (MongoException e2) {
						e2.printStackTrace();
						System.err.println("The service is currently unavailable. Closing...");
						dao.exit();
						System.exit(-1);
					}catch(ServiceUnavailableException ex ) {
						System.err.println("The service is currently unavailable. Closing...");
						dao.exit();
						System.exit(-1);
					}
					requests_list = user.getRequestAdmin();
					check = false;
				}

				for (int i = model_Req.getRowCount() - 1; i >= 0; i--)
					model_Req.removeRow(i);
				for (int i = 0; i < requests_list.size(); i++) {
					int status = requests_list.get(i).getStatus();
					// Vengono inserite solo le richieste in attesa di approvazione/rifiuto
					if (status == 1) {
						DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
						String username_i = dao.getUsernameById(requests_list.get(i).get_UserId());
						model_Req.addRow(new Object[] { requests_list.get(i).get_FilmTitle(), username_i, "Waiting",
								df.format(requests_list.get(i).getDate()) });
					}
				}
				btnRequestProcessed.setEnabled(true);
				btnBack.setVisible(false);
				btnBack.setEnabled(false);
			}
		});

		btnRequestProcessed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (check == true) {
					try {
						user.fill_Request(dao);
					} catch (MongoException e2) {
						e2.printStackTrace();
						System.err.println("The service is currently unavailable. Closing...");
						dao.exit();
						System.exit(-1);
					}catch(ServiceUnavailableException ex ) {
						System.err.println("The service is currently unavailable. Closing...");
						dao.exit();
						System.exit(-1);
					}
					requests_list = user.getRequestAdmin();
					check = false;
				}
				for (int i = model_Req.getRowCount() - 1; i >= 0; i--)
					model_Req.removeRow(i);
				for (int i = 0; i < requests_list.size(); i++) {
					int status = requests_list.get(i).getStatus();
					// Vengono isnerite solo le richieste in attesa di approvazione/rifiuto
					if (status != 1) {
						String status_shown;
						if (status == 0) {
							status_shown = "Rejected";
						} else {
							status_shown = "Approved";
						}
						DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
						String username_i = "";
						try {
							username_i = dao.getUsernameById(requests_list.get(i).get_UserId());
						} catch (MongoException e2) {
							e2.printStackTrace();
							System.err.println("The service is currently unavailable. Closing...");
							dao.exit();
							System.exit(-1);
						}catch(ServiceUnavailableException ex ) {
							System.err.println("The service is currently unavailable. Closing...");
							dao.exit();
							System.exit(-1);
						}
						model_Req.addRow(new Object[] { requests_list.get(i).get_FilmTitle(), username_i, status_shown,
								df.format(requests_list.get(i).getDate()) });
					}
				}
				btnRequestProcessed.setEnabled(false);
				btnBack.setVisible(true);
				btnBack.setEnabled(true);
			}
		});

		/* INSERIMENTI DELLE RICHIESTE NELLA TABELLA DELLE RICHIESTE */
		// System.out.println(watched.get(0).getItalian_title());
		for (int i = 0; i < requests_list.size(); i++) {
			int status = requests_list.get(i).getStatus();
			// Vengono isnerite solo le richieste in attesa di approvazione/rifiuto
			if (status == 1) {
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				String username_i = "";
				try {
					username_i = dao.getUsernameById(requests_list.get(i).get_UserId());
				} catch (MongoException e2) {
					e2.printStackTrace();
					System.err.println("The service is currently unavailable. Closing...");
					dao.exit();
					System.exit(-1);
				}catch(ServiceUnavailableException ex ) {
					System.err.println("The service is currently unavailable. Closing...");
					dao.exit();
					System.exit(-1);
				}
				model_Req.addRow(new Object[] { requests_list.get(i).get_FilmTitle(), username_i, "Waiting",
						df.format(requests_list.get(i).getDate()) });
			}
		}

		/* end LAYOUT Requests PANEL */

		/* PANEL Update movies */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

		JPanel panelAddMovie = new JPanel();
		tabbedPane.addTab("Update Movies Database", null, panelAddMovie, null);
		panelAddMovie.setLayout(null);

		JLabel lblAddNewMovie = new JLabel("Update Movies Database");
		lblAddNewMovie.setBounds(176, 18, 248, 40);
		lblAddNewMovie.setFont(new Font("Lucida Grande", Font.PLAIN, 19));
		panelAddMovie.add(lblAddNewMovie);

		JLabel lblTitle = new JLabel("Original title");
		lblTitle.setBounds(116, 103, 93, 14);
		panelAddMovie.add(lblTitle);

		textField_OriginalTitle = new JTextField();
		textField_OriginalTitle.setColumns(10);
		textField_OriginalTitle.setBounds(209, 100, 249, 20);
		textField_OriginalTitle.setEnabled(false);
		panelAddMovie.add(textField_OriginalTitle);

		textField_ItalianTitle = new JTextField();
		textField_ItalianTitle.setColumns(10);
		textField_ItalianTitle.setBounds(209, 135, 249, 20);
		panelAddMovie.add(textField_ItalianTitle);

		JLabel lblItalisnTitle = new JLabel("Italian title");
		lblItalisnTitle.setBounds(126, 137, 71, 17);
		panelAddMovie.add(lblItalisnTitle);

		textField_Year = new JTextField();
		textField_Year.setColumns(10);
		textField_Year.setBounds(209, 167, 249, 20);
		panelAddMovie.add(textField_Year);

		JLabel lblY = new JLabel("Production year");
		lblY.setBounds(96, 170, 103, 14);
		panelAddMovie.add(lblY);

		textField_Runtime = new JTextField();
		textField_Runtime.setColumns(10);
		textField_Runtime.setBounds(209, 199, 249, 20);
		textField_Runtime.setEnabled(false);
		panelAddMovie.add(textField_Runtime);

		JLabel lblBoxOffice = new JLabel("Runtime");
		lblBoxOffice.setBounds(136, 202, 61, 14);
		panelAddMovie.add(lblBoxOffice);

		// update movies buttons
		JButton btnSearch_1 = new JButton("Search");
		btnSearch_1.setBounds(80, 257, 103, 23);
		panelAddMovie.add(btnSearch_1);

		JButton btnAdd = new JButton("Add");
		btnAdd.setEnabled(false);
		btnAdd.setBounds(188, 257, 103, 23);
		panelAddMovie.add(btnAdd);

		JButton btnDelete = new JButton("Delete");
		btnDelete.setEnabled(false);
		btnDelete.setBounds(303, 257, 103, 23);
		panelAddMovie.add(btnDelete);

		JButton btnReset = new JButton("Reset");
		btnReset.setBounds(418, 257, 103, 23);
		panelAddMovie.add(btnReset);

		btnSearch_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (textField_ItalianTitle.getText().strip() != "") {
					if (textField_Year.getText().strip() != "") {
						try {
							int year = Integer.parseInt(textField_Year.getText().strip());
							String title = textField_ItalianTitle.getText().strip();
							List<BasicFilm> film_list = new ArrayList<>();
							try {
								film_list = dao.findBasicFilms(title, year);
							} catch (MongoException e2) {
								e2.printStackTrace();
								System.err.println("The service is currently unavailable. Closing...");
								dao.exit();
								System.exit(-1);
							}catch(ServiceUnavailableException ex ) {
								System.err.println("The service is currently unavailable. Closing...");
								dao.exit();
								System.exit(-1);
							}
							if (film_list.size() == 0) {
								JOptionPane.showOptionDialog(null,
										"Could not find the requested film. Click OK to continue.", "Warning",
										JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, null, "OK");
								btnAdd.setEnabled(true);
								btnSearch_1.setEnabled(false);
								textField_ItalianTitle.setEnabled(false);
								textField_Year.setEnabled(false);

							} else if (film_list.size() > 1) {
								JOptionPane.showOptionDialog(null,
										"Found more than one film matching the title. Please refine your search.",
										"Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, null,
										"OK");
							} else {
								btnSearch_1.setEnabled(false);
								textField_ItalianTitle.setEnabled(false);
								textField_Year.setEnabled(false);
								btnAdd.setEnabled(false);
								btnDelete.setEnabled(true);
								filmToDelete = film_list.get(0);
								textField_Runtime.setText(filmToDelete.getRuntime().toString());
								textField_OriginalTitle.setText(filmToDelete.getOriginal_title());
								textField_ItalianTitle.setText(filmToDelete.getTitle());
								textField_Year.setText(filmToDelete.getYear().toString());
							}
						} catch (NumberFormatException e2) {
							JOptionPane.showOptionDialog(null,
									"\"Production year\" should be a numeric value. Press OK to continue.", "Warning",
									JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, null, "OK");
						}

					} else {
						String title = textField_ItalianTitle.getText().strip();
						List<BasicFilm> film_list = new ArrayList<>();
						try {
							film_list = dao.findBasicFilms(title);
						} catch (MongoException e2) {
							e2.printStackTrace();
							System.err.println("The service is currently unavailable. Closing...");
							dao.exit();
							System.exit(-1);
						}catch(ServiceUnavailableException ex ) {
							System.err.println("The service is currently unavailable. Closing...");
							dao.exit();
							System.exit(-1);
						}
						if (film_list.size() == 0) {
							JOptionPane.showOptionDialog(null,
									"Could not find the requested film. Click OK to continue.", "Warning",
									JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, null, "OK");
							btnAdd.setEnabled(true);
							btnSearch_1.setEnabled(false);
							textField_ItalianTitle.setEnabled(false);
							textField_Year.setEnabled(false);
						} else if (film_list.size() > 1) {
							JOptionPane.showOptionDialog(null,
									"Found more than one film matching the title. Please refine your search.",
									"Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, null,
									"OK");
						} else {
							btnSearch_1.setEnabled(false);
							textField_ItalianTitle.setEnabled(false);
							textField_Year.setEnabled(false);
							btnAdd.setEnabled(false);
							btnDelete.setEnabled(true);
							filmToDelete = film_list.get(0);
							textField_Runtime.setText(filmToDelete.getRuntime().toString());
							textField_OriginalTitle.setText(filmToDelete.getOriginal_title());
							textField_ItalianTitle.setText(filmToDelete.getTitle());
							textField_Year.setText(filmToDelete.getYear().toString());
						}
					}
				}
			}
		});

		btnReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnAdd.setEnabled(false);
				btnSearch_1.setEnabled(true);
				btnDelete.setEnabled(false);
				textField_ItalianTitle.setEnabled(true);
				textField_Year.setEnabled(true);
				textField_OriginalTitle.setText("");
				textField_ItalianTitle.setText("");
				textField_Year.setText("");
				textField_Runtime.setText("");
			}
		});

		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String titleToAdd = textField_ItalianTitle.getText().strip();
				if (!titleToAdd.equals("")) {
					try {
						FileWriter fw = new FileWriter(
								"/Users/andreadidonato/Desktop/GitHub/Task_2/mongodb_2/scraping/film_list.txt");
						fw.write(titleToAdd);
						fw.close();
						JOptionPane.showOptionDialog(null, "Movie correctly added to scraping queue.", "Info",
								JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, "OK");
						Test.main(null);
						System.out.println("Success...");
					} catch (Exception e2) {
						System.out.println(e2.getMessage());

					}
					btnAdd.setEnabled(false);
					btnSearch_1.setEnabled(true);
					btnDelete.setEnabled(false);
					textField_ItalianTitle.setEnabled(true);
					textField_Year.setEnabled(true);
					textField_OriginalTitle.setText("");
					textField_ItalianTitle.setText("");
					textField_Year.setText("");
					textField_Runtime.setText("");
				}

			}
		});

		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (dao.deleteFilm(filmToDelete.getFilm_id())) {
						JOptionPane.showOptionDialog(null, "Movie correctly removed from the database.", "Info",
								JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, "OK");
					} else {
						JOptionPane.showOptionDialog(null,
								"Something went wrong while removing the movie from the database", "Warning",
								JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, null, "OK");
					}
				} catch (MongoException e2) {
					e2.printStackTrace();
					System.err.println("The service is currently unavailable. Closing...");
					dao.exit();
					System.exit(-1);
				}catch(ServiceUnavailableException ex ) {
					System.err.println("The service is currently unavailable. Closing...");
					dao.exit();
					System.exit(-1);
				}
				btnAdd.setEnabled(false);
				btnSearch_1.setEnabled(true);
				btnDelete.setEnabled(false);
				textField_ItalianTitle.setEnabled(true);
				textField_Year.setEnabled(true);
				textField_OriginalTitle.setText("");
				textField_ItalianTitle.setText("");
				textField_Year.setText("");
				textField_Runtime.setText("");
			}
		});
		// end of update movies buttons

		/* end PANEL Update Movies */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

		/* PANEL manage users */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/
		JPanel panelManageUsers = new JPanel();
		tabbedPane.addTab("Manage Users", null, panelManageUsers, null);
		panelManageUsers.setLayout(null);

		JLabel lblManageUsers = new JLabel("Manage Users");
		lblManageUsers.setFont(new Font("Tahoma", Font.BOLD, 21));
		lblManageUsers.setBounds(198, 21, 185, 46);
		panelManageUsers.add(lblManageUsers);

		textField_1 = new JTextField();
		textField_1.setBounds(213, 121, 249, 20);
		panelManageUsers.add(textField_1);
		textField_1.setColumns(10);

		JLabel lblUserName = new JLabel("UserName");
		lblUserName.setBounds(118, 124, 74, 14);
		panelManageUsers.add(lblUserName);

		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(213, 153, 249, 20);
		panelManageUsers.add(textField_2);
		textField_2.setFocusable(false);

		JLabel lblCountry = new JLabel("Country");
		lblCountry.setBounds(138, 155, 74, 17);
		panelManageUsers.add(lblCountry);

		textField_3 = new JTextField();
		textField_3.setFocusable(false);
		textField_3.setColumns(10);
		textField_3.setBounds(213, 185, 249, 20);
		panelManageUsers.add(textField_3);

		textField_4 = new JTextField();
		textField_4.setColumns(10);
		textField_4.setFocusable(false);
		textField_4.setBounds(213, 217, 249, 20);
		panelManageUsers.add(textField_4);

		JLabel lblFirstName = new JLabel("First name");
		lblFirstName.setBounds(118, 188, 74, 14);
		panelManageUsers.add(lblFirstName);

		JLabel lblLastName = new JLabel("Last name");
		lblLastName.setBounds(118, 220, 74, 14);
		panelManageUsers.add(lblLastName);

		JButton btnDeleteUser = new JButton("Delete user");
		btnDeleteUser.setEnabled(false);
		btnDeleteUser.setBounds(302, 293, 125, 23);
		panelManageUsers.add(btnDeleteUser);

		JButton btnSearhByUser = new JButton("Search by Username");
		btnSearhByUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = textField_1.getText();
				Document doc = new Document();
				try {
					doc = dao.getUserInfo(username);
				} catch (MongoException e2) {
					e2.printStackTrace();
					System.err.println("The service is currently unavailable. Closing...");
					dao.exit();
					System.exit(-1);
				}catch(ServiceUnavailableException ex ) {
					System.err.println("The service is currently unavailable. Closing...");
					dao.exit();
					System.exit(-1);
				}
				if (doc != null) {
					Document doc1 = (Document) doc.get("general_info");
					// System.out.println(doc1.getString("country"));
					textField_2.setText(doc1.getString("country"));
					textField_3.setText(doc1.getString("first_name"));
					textField_4.setText(doc1.getString("last_name"));
					btnDeleteUser.setEnabled(true);
				} else {
					Object[] options = { "OK" };
					JOptionPane.showOptionDialog(null, "The user selected doesn't exist. Click OK to continue",
							"Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, null, options[0]);
					textField_1.setText("");
					textField_2.setText("");
					textField_3.setText("");
					textField_4.setText("");
					btnDeleteUser.setEnabled(false);
				}
			}
		});

		btnDeleteUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = textField_1.getText();
				try {
					if (dao.deleteUser(username)) {
						Object[] options = { "OK" };
						JOptionPane.showOptionDialog(null, "The user has been deleted correctly. Click OK to continue.",
								"Info", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null,
								options[0]);
						textField_1.setText("");
						textField_2.setText("");
						textField_3.setText("");
						textField_4.setText("");
						btnDeleteUser.setEnabled(false);
					} else {
						Object[] options = { "OK" };
						JOptionPane.showOptionDialog(null, "The user hasn't been deleted. Click OK to continue.",
								"Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, null,
								options[0]);
						textField_1.setText("");
						textField_2.setText("");
						textField_3.setText("");
						textField_4.setText("");
						btnDeleteUser.setEnabled(false);
					}
				} catch (MongoException e2) {
					e2.printStackTrace();
					System.err.println("The service is currently unavailable. Closing...");
					dao.exit();
					System.exit(-1);
				}catch(ServiceUnavailableException ex ) {
					System.err.println("The service is currently unavailable. Closing...");
					dao.exit();
					System.exit(-1);
				}
			}
		});

		btnSearhByUser.setBounds(138, 293, 158, 23);
		panelManageUsers.add(btnSearhByUser);

		// fill request table
		TableColumnModel columnModel_Req = RequestTable.getColumnModel();
		columnModel_Req.getColumn(0).setPreferredWidth(300);
		columnModel_Req.getColumn(0).setResizable(false);
		columnModel_Req.getColumn(1).setResizable(false);
		columnModel_Req.getColumn(2).setResizable(false);

		Object[] rowData2 = new Object[40];

		for (int i = 0; i < Request_list.size(); i++) {
			rowData2[0] = Request_list.get(i).get_FilmTitle();
			rowData2[1] = Request_list.get(i).getStatus();/// change to : getusrrname()
			rowData2[2] = Request_list.get(i).getDate();
			model_Req.addRow(rowData2);
		}

		// request table click handler (accept /reject request)
		RequestTable.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {

				// panelPoster.repaint();
				int row = RequestTable.rowAtPoint(me.getPoint());
				String title = RequestTable.getValueAt(row, 0).toString();
				String username = RequestTable.getValueAt(row, 1).toString();
				String status_row = RequestTable.getValueAt(row, 2).toString();

				if (status_row.equals("Waiting") == true) {
					int result = JOptionPane.showConfirmDialog(null,
							"Do you want to add this movie: \"" + title + "\"?", "Manage request",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (result == JOptionPane.YES_OPTION) {
						// System.out.println("You selected: Yes");
						model_Req.removeRow(row);

						// Si è deciso di aggiungere il film nel database -> parte lo scraping
						try {
							FileWriter fw = new FileWriter(
									"/Users/andreadidonato/Desktop/GitHub/Task_2/mongodb_2/scraping/film_list.txt");
							fw.write(title);
							fw.close();
							User user_to_process = dao.getUser(username);
							boolean changed = false;
							try {
								changed = dao.setRequestStatusById(user_to_process, title, 2);
							} catch (MongoException e2) {
								e2.printStackTrace();
								System.err.println("The service is currently unavailable. Closing...");
								dao.exit();
								System.exit(-1);
							}catch(ServiceUnavailableException ex ) {
								System.err.println("The service is currently unavailable. Closing...");
								dao.exit();
								System.exit(-1);
							}
							if (changed == true) {
								System.out.println("Richiesta approvata correttamente");
								// check necessario per comunicare di riaggiornare il vettore di richieste
								check = true;
							} else {
								System.out.println("Errore nella modifica dello stato di servizio della richiesta");
							}

						} catch (Exception e) {
							System.out.println(e.getMessage());

						}
						Test.main(null);
						System.out.println("Success...");
					} else if (result == JOptionPane.NO_OPTION) {
						// System.out.println("You selected: No");
						model_Req.removeRow(row);
						User user_to_process = new User();
						try {
							user_to_process = dao.getUser(username);
						} catch (MongoException e2) {
							e2.printStackTrace();
							System.err.println("The service is currently unavailable. Closing...");
							dao.exit();
							System.exit(-1);
						}catch(ServiceUnavailableException ex ) {
							System.err.println("The service is currently unavailable. Closing...");
							dao.exit();
							System.exit(-1);
						}
						boolean changed = false;
						try {
							changed = dao.setRequestStatusById(user_to_process, title, 0);
						} catch (MongoException e2) {
							e2.printStackTrace();
							System.err.println("The service is currently unavailable. Closing...");
							dao.exit();
							System.exit(-1);
						}catch(ServiceUnavailableException ex ) {
							System.err.println("The service is currently unavailable. Closing...");
							dao.exit();
							System.exit(-1);
						}
						if (changed == true) {
							System.out.println("Richiesta rifiutata correttamente");
							// check necessario per comunicare di riaggiornare il vettore di richieste
							check = true;
						} else {
							System.out.println("Errore nella modifica dello stato di servizio della richiesta");
						}

					} else {
						System.out.println("None selected");
					}
				}

			}
		});

		/* end PANEL manage requests */

		/* PANEL movies */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/
		JPanel panelMovies = new JPanel();
		tabbedPane.addTab("Movies", null, panelMovies, null);
		panelMovies.setLayout(null);

		textField = new JTextField();
		textField.setBounds(6, 6, 361, 26);
		panelMovies.add(textField);
		textField.setColumns(10);

		JButton btnSearch = new JButton("Search");
		btnSearch.setBounds(475, 6, 79, 29);

		// Aggiunta del bottone per terminare la ricerca -> di default non si vede
		JButton btnEndSearch = new JButton("Stop searching");
		btnEndSearch.setBounds(409, 581, 117, 29);
		panelMovies.add(btnEndSearch);
		btnEndSearch.setVisible(false);
		btnEndSearch.setEnabled(false);

		final JButton btnPrev = new JButton("Prev");
		JButton btnNext = new JButton("Next");

		/* LISTENER DEL BOTTONE SEARCH */
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filmSearched = textField.getText().strip();
				String yearcomb = YearcomboBox.getSelectedItem().toString();
				if (!(filmSearched.equals("") && yearcomb.equals("all years"))) {
					int year = -1;
					if (!yearcomb.equals("all years"))
						year = Integer.parseInt(yearcomb);

					for (int i = model.getRowCount() - 1; i >= 0; i--)
						model.removeRow(i);
					// textField.setText("");
					// System.out.println(title);
					films_list.clear();

					List<BasicFilm> films = new ArrayList<>();
					try {
						films = dao.searchFilms(filmSearched, year);
					} catch (MongoException e2) {
						e2.printStackTrace();
						System.err.println("The service is currently unavailable. Closing...");
						dao.exit();
						System.exit(-1);
					}catch(ServiceUnavailableException ex ) {
						System.err.println("The service is currently unavailable. Closing...");
						dao.exit();
						System.exit(-1);
					}

					if (!films.isEmpty()) {
						for (int i = 0; i < films.size(); i++) {
							BasicFilm film1 = films.get(i);
							films_list.add(film1);

							if (film1.getRuntime() == 0)
								model.addRow(new Object[] { film1.getTitle(), film1.getYear(), "N/A" });
							else
								model.addRow(
										new Object[] { film1.getTitle(), film1.getYear(), film1.getRuntime() + "'" });
						}
					} else
						model.addRow(new Object[] { "No title found. Please try again." });

					// Si rende visibile il bottone per terminare la ricerca
					btnEndSearch.setVisible(true);
					btnEndSearch.setEnabled(true);

					btnPrev.setEnabled(false);
					btnNext.setEnabled(false);
				}
			}
		});
		panelMovies.add(btnSearch);

		/* Handler botton endSearch */
		btnEndSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Si rende invisibile il bottone per terminare la ricerca
				btnEndSearch.setVisible(false);
				btnEndSearch.setEnabled(false);

				index = 0;
				films_list.clear();
				try {
					films_list = dao.getBasicFilmsIndex(index);
				} catch (MongoException e2) {
					e2.printStackTrace();
					System.err.println("The service is currently unavailable. Closing...");
					dao.exit();
					System.exit(-1);
				}catch(ServiceUnavailableException ex ) {
					System.err.println("The service is currently unavailable. Closing...");
					dao.exit();
					System.exit(-1);
				}

				for (int i = model.getRowCount() - 1; i >= 0; i--)
					model.removeRow(i);

				/* INSERIMENTO DI FILM NELLA TABELLA MOVIES */
				for (int i = 0; i < films_list.size(); i++) {
					BasicFilm film = films_list.get(i);
					model.addRow(new Object[] { film.getTitle(), film.getYear(), film.getRuntime() + "'" });

				}
				textField.setText("");
				btnPrev.setEnabled(false);
				btnNext.setEnabled(true);

			}
		});

		/* BOTTONE PREV -> OCCORRE DEFINIRE IL LISTENER */

		btnPrev.setEnabled(false);
		btnPrev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				index--;
				if (index == 0)
					btnPrev.setEnabled(false);
				films_list.clear();
				try {
					films_list = dao.getBasicFilmsIndex(index);
				} catch (MongoException e2) {
					e2.printStackTrace();
					System.err.println("The service is currently unavailable. Closing...");
					dao.exit();
					System.exit(-1);
				}catch(ServiceUnavailableException ex ) {
					System.err.println("The service is currently unavailable. Closing...");
					dao.exit();
					System.exit(-1);
				}

				for (int i = model.getRowCount() - 1; i >= 0; i--)
					model.removeRow(i);
				for (int i = 0; i < films_list.size(); i++) {
					BasicFilm film = films_list.get(i);
					if (film.getRuntime() == 0)
						model.addRow(new Object[] { film.getTitle(), film.getYear(), "N/A" });
					else
						model.addRow(new Object[] { film.getTitle(), film.getYear(), film.getRuntime() + "'" });
				}

			}
		});
		btnPrev.setBounds(131, 581, 117, 29);
		panelMovies.add(btnPrev);

		/* BOTTONE NEXT -> OCCORRE DEFINIRE IL LISTENER */
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				btnPrev.setEnabled(true);
				index++;
				films_list.clear();
				try {
					films_list = dao.getBasicFilmsIndex(index);
				} catch (MongoException e2) {
					e2.printStackTrace();
					System.err.println("The service is currently unavailable. Closing...");
					dao.exit();
					System.exit(-1);
				}catch(ServiceUnavailableException ex ) {
					System.err.println("The service is currently unavailable. Closing...");
					dao.exit();
					System.exit(-1);
				}

				for (int i = model.getRowCount() - 1; i >= 0; i--)
					model.removeRow(i);
				for (int i = 0; i < films_list.size(); i++) {
					BasicFilm film = films_list.get(i);
					if (film.getRuntime() == 0)
						model.addRow(new Object[] { film.getTitle(), film.getYear(), "N/A" });
					else
						model.addRow(new Object[] { film.getTitle(), film.getYear(), film.getRuntime() + "'" });
				}
			}
		});
		btnNext.setBounds(260, 581, 117, 29);
		panelMovies.add(btnNext);

		JScrollPane paneResults = new JScrollPane();
		paneResults.setBounds(6, 35, 548, 546);
		panelMovies.add(paneResults);

		frameAdmin.setForeground(Color.BLUE);
		frameAdmin.setBounds(100, 100, 1200, 700);
		frameAdmin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		Dimension dim1 = Toolkit.getDefaultToolkit().getScreenSize();
		frameAdmin.setLocation(dim1.width / 2 - frameAdmin.getSize().width / 2,
				dim1.height / 2 - frameAdmin.getSize().height / 2);

		MoviesTable = new JTable();
		MoviesTable.setRowSelectionAllowed(false);
		MoviesTable.setBackground(Color.WHITE);
		MoviesTable.setBorder(null);
		MoviesTable.setEnabled(false);
		paneResults.setViewportView(MoviesTable);

		model = new DefaultTableModel();
		Object[] columnsName = new Object[3];

		columnsName[0] = "Title";
		columnsName[1] = "Year";
		columnsName[2] = "Runtime";

		model.setColumnIdentifiers(columnsName);
		MoviesTable.setModel(model);
		MoviesTable.setGridColor(new Color(0, 0, 0));
		MoviesTable.setEnabled(false);

		YearcomboBox = new JComboBox();
		YearcomboBox.setBounds(369, 8, 108, 22);
		panelMovies.add(YearcomboBox);
		// final JComboBox rate_movie_combo_box = new JComboBox();
		String[] movieYears = new String[0];
		try {
			movieYears = dao.getMovieYears();
		} catch (MongoException e2) {
			e2.printStackTrace();
			System.err.println("The service is currently unavailable. Closing...");
			dao.exit();
			System.exit(-1);
		}catch(ServiceUnavailableException ex ) {
			System.err.println("The service is currently unavailable. Closing...");
			dao.exit();
			System.exit(-1);
		}
		YearcomboBox.setModel(new DefaultComboBoxModel(movieYears));
		YearcomboBox.setMaximumRowCount(22);

		TableColumnModel columnModel = MoviesTable.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(300);
		columnModel.getColumn(0).setResizable(false);
		columnModel.getColumn(1).setResizable(false);
		columnModel.getColumn(2).setResizable(false);

		Object[] rowData = new Object[40];

		/* INSERIMENTO DI FILM NELLA TABELLA MOVIES */
		for (int i = 0; i < films_list.size(); i++) {
			BasicFilm film = films_list.get(i);
			String runtimeText = "N/A";
			if (film.getRuntime() != 0)
				runtimeText = film.getRuntime() + "'";
			model.addRow(new Object[] { film.getTitle(), film.getYear(), runtimeText });

		}

		//////// end movies
		//////// panel==================================================================================

		/*
		 * right panel
		 * =========================================================================
		 */

		JPanel panelRight = new JPanel();
		splitPane.setRightComponent(panelRight);
		panelRight.setLayout(null);

		JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_1.setBounds(6, 6, 588, 662);
		panelRight.add(tabbedPane_1);
		tabbedPane_1.setEnabled(false);

		/* PANEL MOVIE DETAILS */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

		final JPanel panelMovieDetails = new JPanel();
		JPanel panelAwards = new JPanel();
		GroupLayout gl_panelMovieDetails = new GroupLayout(panelMovieDetails);
		gl_panelMovieDetails.setHorizontalGroup(gl_panelMovieDetails.createParallelGroup(Alignment.TRAILING).addGroup(
				Alignment.LEADING, gl_panelMovieDetails.createSequentialGroup().addContainerGap(402, Short.MAX_VALUE)
						.addComponent(panelAwards, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)));
		gl_panelMovieDetails.setVerticalGroup(gl_panelMovieDetails.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelMovieDetails.createSequentialGroup().addGap(187)
						.addComponent(panelAwards, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(309, Short.MAX_VALUE)));
		JScrollPane scrollPaneAwards = new JScrollPane();

		scrollPaneAwards.setBorder(border);
		scrollPaneAwards.getVerticalScrollBar().setPreferredSize(new Dimension(0, 100));
		scrollPaneAwards.getViewport().setBackground(myGray);
		GroupLayout gl_panelAwards = new GroupLayout(panelAwards);

		tableAwards = new JTable();
		tableAwards.setShowGrid(false);
		tableAwards.setRowSelectionAllowed(false);
		tableAwards.setBackground(myGray);
		tableAwards.setBorder(null);
		tableAwards.setEnabled(false);
		scrollPaneAwards.setViewportView(tableAwards);

		Object[] columnsName_awards = new Object[1];

		columnsName_awards[0] = "Awards";

		JTableHeader header = tableAwards.getTableHeader();
		header.setFont(header.getFont().deriveFont(Font.BOLD));

		model_awards.setColumnIdentifiers(columnsName_awards);
		tableAwards.setModel(model_awards);
		// tableAwards.setGridColor(new Color(0,0,0));
		tableAwards.setEnabled(false);

		TableColumnModel columnModel_awards = tableAwards.getColumnModel();
		columnModel_awards.getColumn(0).setResizable(false);

		scrollPaneAwards.setViewportView(tableAwards);
		panelAwards.setLayout(gl_panelAwards);
		panelMovieDetails.setLayout(gl_panelMovieDetails);
		// tabbedPane_1.setBackgroundAt(0, myGray);

		tabbedPane_1.addTab("Details", null, panelMovieDetails, null);

		final JPanel panelPoster = new JPanel();

		final JScrollPane Pane_It_Title = new JScrollPane();
		Pane_It_Title.setBorder(border);
		Pane_It_Title.getViewport().setBackground(myGray);
		Pane_It_Title.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		JLabel label_1 = new JLabel("Connecting to the database. Please wait...");
		label_1.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		Pane_It_Title.setViewportView(label_1);
		Pane_It_Title.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 4));

		final JScrollPane Pane_En_Title = new JScrollPane();
		Pane_En_Title.setBorder(border);
		Pane_En_Title.getViewport().setBackground(myGray);
		Pane_En_Title.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 2));

		final JLabel labelRuntime = new JLabel("");

		final JLabel lbl_Year_tag = new JLabel("Year");
		lbl_Year_tag.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		final JLabel lbl_RunTime_tag = new JLabel("Runtime");
		lbl_RunTime_tag.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		final JSeparator separator_1 = new JSeparator();
		final JScrollPane scrollPane_Synopsis = new JScrollPane();
		scrollPane_Synopsis.setBorder(border);
		scrollPane_Synopsis.getViewport().setBackground(myGray);

		final JLabel lbl_Synopsis = new JLabel("Plot");
		lbl_Synopsis.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		final JLabel lbl_Imdb_tag = new JLabel("IMDB");
		lbl_Imdb_tag.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		final JLabel lblCoomingSoon_tag = new JLabel("Cooming Soon");
		lblCoomingSoon_tag.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		final JLabel lblMymovies_Tag = new JLabel("MyMovies");
		lblMymovies_Tag.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		final JLabel lblUsers_tag = new JLabel("Users");
		lblUsers_tag.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		final JLabel lbl_Imdb_text = new JLabel("");

		final JLabel lbl_CoomingSoon_text = new JLabel("");

		final JLabel lbl_MyMovies_text = new JLabel("");

		final JLabel lbl_Application_rate = new JLabel("");

		final JSeparator separator_3 = new JSeparator();

		final JLabel lbl_Genres_tag = new JLabel("Genres");
		lbl_Genres_tag.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		final JScrollPane scrollPane_Genres = new JScrollPane();
		scrollPane_Genres.setBorder(border);
		scrollPane_Genres.getViewport().setBackground(myGray);

		final JTextField textField_Genres = new JTextField();
		textField_Genres.setEditable(false);
		scrollPane_Genres.setViewportView(textField_Genres);
		textField_Genres.setBackground(myGray);
		textField_Genres.setBorder(border);
		textField_Genres.setText("");
		scrollPane_Genres.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 2));

		final JLabel lbl_Countries_tag = new JLabel("Countries");
		lbl_Countries_tag.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		final JScrollPane scrollPane_Countries = new JScrollPane();
		scrollPane_Countries.setBorder(border);
		scrollPane_Countries.getViewport().setBackground(myGray);
		scrollPane_Countries.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 2));

		final JSeparator separator_2 = new JSeparator();

		final JScrollPane scrollPane_Platform = new JScrollPane();
		scrollPane_Platform.setBorder(border);
		scrollPane_Platform.getHorizontalScrollBar().setPreferredSize(new Dimension(2, 0));
		scrollPane_Platform.getViewport().setBackground(myGray);

		PlatformsTable = new JTable();
		PlatformsTable.setShowGrid(false);
		PlatformsTable.setRowSelectionAllowed(false);
		PlatformsTable.setBackground(myGray);
		PlatformsTable.setBorder(null);
		PlatformsTable.setEnabled(false);
		scrollPane_Platform.setViewportView(PlatformsTable);

		Object[] columnsName_platforms = new Object[2];

		columnsName_platforms[0] = "Platform";
		columnsName_platforms[1] = "Price";

		model_platforms.setColumnIdentifiers(columnsName_platforms);
		PlatformsTable.setModel(model_platforms);
		PlatformsTable.setGridColor(new Color(0, 0, 0));
		PlatformsTable.setEnabled(false);

		TableColumnModel columnModel_platform = PlatformsTable.getColumnModel();
		columnModel_platform.getColumn(0).setResizable(false);
		columnModel_platform.getColumn(1).setResizable(false);

		final JLabel lbl_Platform_tag = new JLabel("Available on");
		lbl_Platform_tag.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		final JTextField textField_Countries = new JTextField();
		textField_Countries.setEditable(false);
		textField_Countries.setBackground(myGray);
		textField_Countries.setBorder(border);
		textField_Countries.setText("");
		final JLabel lbl_Year_Text = new JLabel("");

		final JLabel lbl_BoxOffice_Tag = new JLabel("BoxOffice");
		lbl_BoxOffice_Tag.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		final JLabel lbl_BoxOffice_text = new JLabel("");

		final JLabel lbl_add_movie = new JLabel("Add to watched list");
		lbl_add_movie.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		final JComboBox rate_movie_combo_box = new JComboBox();
		rate_movie_combo_box
				.setModel(new DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
		rate_movie_combo_box.setMaximumRowCount(10);

		final JLabel lbl_rate = new JLabel("Rate");
		/* BOTTONE ADD MOVIE ALLA LISTA DI FILM VISTI */
		final JButton button = new JButton("Add");

		button.setVisible(false);
		lbl_add_movie.setVisible(false);
		lbl_rate.setVisible(false);
		rate_movie_combo_box.setVisible(false);

		final JTextArea textArea_Synopsis = new JTextArea();
		textArea_Synopsis.setEditable(false);
		scrollPane_Synopsis.setViewportView(textArea_Synopsis);
		textArea_Synopsis.setBackground(myGray);
		textArea_Synopsis.setText("");
		textArea_Synopsis.setWrapStyleWord(true);
		textArea_Synopsis.setLineWrap(true);
		scrollPane_Synopsis.getVerticalScrollBar().setPreferredSize(new Dimension(2, 0));

		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

		/* MOVIES'S CAST PANEL */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

		panelMovieCast = new JPanel();
		tabbedPane_1.addTab("Cast", null, panelMovieCast, null);
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

		/* MOVIE'S COMMENTS PANEL */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

		JPanel panelMovieComments = new JPanel();
		tabbedPane_1.addTab("Comments", null, panelMovieComments, null);
		panelMovieComments.setLayout(null);

		/* MOVIES'S ANALYTICS PANEL */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

		JPanel panel_Analytics = new JPanel();
		tabbedPane_1.addTab("Statistics and Analytics", null, panel_Analytics, null);
		panel_Analytics.setLayout(null);

		JLabel lblDistributionOfWa = new JLabel("Audience distribution per nation for : ");
		lblDistributionOfWa.setFont(new Font("Lucida Grande", Font.BOLD, 20));
		lblDistributionOfWa.setBounds(91, 20, 437, 21);
		panel_Analytics.add(lblDistributionOfWa);

		// fill table of users nations per movie

		JScrollPane scrollPaneNations = new JScrollPane();
		scrollPaneNations.setBounds(6, 101, 557, 509);
		panel_Analytics.add(scrollPaneNations);
		tableNations = new JTable();

		// tableNations.setBounds(569, 80, -543, 323);
		// panel_Analytics.add(tableNations);

		tableNations.setRowSelectionAllowed(false);
		tableNations.setBackground(Color.WHITE);
		tableNations.setBorder(null);
		tableNations.setEnabled(false);
		scrollPaneNations.setViewportView(tableNations);

		DefaultTableModel model_Nations = new DefaultTableModel();
		scrollPaneNations.setBorder(border);
		scrollPaneNations.getVerticalScrollBar().setPreferredSize(new Dimension(0, 2));
		scrollPaneNations.getViewport().setBackground(myGray);

		Object[] columns_Nations = new Object[3];

		columns_Nations[0] = "Nation";
		columns_Nations[1] = "No. users";
		columns_Nations[2] = "Percentage";

		model_Nations.setColumnIdentifiers(columns_Nations);
		tableNations.setModel(model_Nations);
		tableNations.setGridColor(new Color(0, 0, 0));
		// MoviesTable.setEnabled(false);
		TableColumnModel columnModel_Nations = tableNations.getColumnModel();
		columnModel_Nations.getColumn(0).setResizable(false);
		columnModel_Nations.getColumn(1).setResizable(false);

		tableNations.setBackground(myGray);

		JLabel lblNewLabel = new JLabel("New label");
		scrollPaneNations.setColumnHeaderView(lblNewLabel);

		JLabel lblnat_title = new JLabel("");
		lblnat_title.setFont(new Font("Dialog", Font.PLAIN, 14));
		lblnat_title.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblnat_title.setBounds(91, 51, 361, 21);
		panel_Analytics.add(lblnat_title);

		paneResults.setViewportView(MoviesTable);

		/*
		 * end analytics panel======================================================
		 * 
		 * /
		 * 
		 * //Quanto segue viene fatto per poter prendere la riga sulla quale l'utente ha
		 * cliccato e di conseguenza //mostrare nella parte destra della pagina le
		 * informazioni relative al film cliccato dall'utente
		 * 
		 * /*EVENT LISTENER TABELLA MOVIES
		 */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/
		MoviesTable.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				try {
					previndexComment = -1;
					indexComment = 0;
					tabbedPane_1.setEnabled(true);

					if (created == 1) {
						panelMovieCast.removeAll();
						CurrentFilm.setComments(new ArrayList<Comment>());
					}
					panelPoster.removeAll();
					panelPoster.validate();
					panelPoster.repaint();
					int row = MoviesTable.rowAtPoint(me.getPoint());
					String title = MoviesTable.getValueAt(row, 0).toString();
					String year = MoviesTable.getValueAt(row, 1).toString();

					// System.out.println("You clicked at row " + row);

					JLabel title_it = new JLabel(title);
					title_it.setFont(new Font("Lucida Grande", Font.BOLD, 13));
					Pane_It_Title.setViewportView(title_it);
					lbl_Year_Text.setText(year);

					Film film = new Film();

					try {
						film = dao.get_film(films_list.get(row).getFilm_id().toString()); // chiamo il metodo film per
																							// avere l'oggetto con tutte
																							// le informazioni
																							// necessarie
					} catch (MongoException e2) {
						e2.printStackTrace();
						System.err.println("The service is currently unavailable. Closing...");
						dao.exit();
						System.exit(-1);
					}catch(ServiceUnavailableException ex ) {
						System.err.println("The service is currently unavailable. Closing...");
						dao.exit();
						System.exit(-1);
					}
					CurrentFilm = film;

					button.setEnabled(true);
					/*
					 * for(int z = 0; z < watchedFilm_list.size(); z++) {
					 * if(film.getFilm_id().equals(watchedFilm_list.get(z).getFilm_id()) == true) {
					 * button.setEnabled(false); break; } }
					 */
					String title_or = film.getOriginal_title();
					Pane_En_Title.setViewportView(new JLabel(title_or));
					lbl_Year_Text.setText(year);
					int runtime = film.getRuntime();
					String runtimeText;
					if (runtime == 0)
						runtimeText = "N/A";
					else {
						runtimeText = Integer.toString(runtime);
					}
					labelRuntime.setText(runtimeText + "'");
					textArea_Synopsis.setText(film.getSynopsis());
					lbl_BoxOffice_text.setText(film.getBox_office() + "$");

					Map<String, Float> ratings = film.getRatings();
					// System.out.println(ratings.get("application_rating"));
					if (ratings.containsKey("imdb_rating"))
						lbl_Imdb_text.setText(Float.toString(ratings.get("imdb_rating")));
					else
						lbl_Imdb_text.setText("N/A");
					if (ratings.containsKey("comingsoon_rating"))
						lbl_CoomingSoon_text.setText(Float.toString(ratings.get("comingsoon_rating")));
					else
						lbl_CoomingSoon_text.setText("N/A");
					if (ratings.containsKey("mymovies_rating"))
						lbl_MyMovies_text.setText(Float.toString(ratings.get("mymovies_rating")));
					else
						lbl_MyMovies_text.setText("N/A");
					if (ratings.containsKey("application_rating"))
						lbl_Application_rate.setText(Float.toString(ratings.get("application_rating")));
					else
						lbl_Application_rate.setText("N/A");

					List<String> genres = new ArrayList<>();
					List<String> countries = new ArrayList<>();
					try {
						if (film.getGenres() != null)
							genres = film.getGenres();
					} catch (Exception e) {
					}
					try {
						if (film.getCountries() != null)
							countries = film.getCountries();
					} catch (Exception e) {
					}
					String gen_str = "";
					for (int i = 0; i < genres.size(); i++) {
						String gen = genres.get(i);
						if (i < genres.size() - 1)
							gen_str = gen_str + gen + ", ";
						else
							gen_str = gen_str + gen;
					}
					textField_Genres.setText(gen_str);

					String country_str = "";
					for (int i = 0; i < countries.size(); i++) {
						String cou = countries.get(i);
						if (i < countries.size() - 1)
							country_str = country_str + cou + ", ";
						else {
							country_str = country_str + cou;
						}
					}
					textField_Countries.setText(country_str);
					;

					////////////// AGGIORNO LA FINESTRA DEL CAST CON QUELLA DEL FILM SELEZIONATO
					created = 1;

					JLabel lbl_Directors_tag = new JLabel("Director");
					lbl_Directors_tag.setFont(new Font("Lucida Grande", Font.BOLD, 13));

					JScrollPane scrollPane_Director = new JScrollPane();

					JScrollPane scrollPane_Actors = new JScrollPane();

					scrollPane_Actors.setBorder(border);
					scrollPane_Actors.getVerticalScrollBar().setPreferredSize(new Dimension(0, 2));
					scrollPane_Actors.getViewport().setBackground(myGray);

					ActorsTable = new JTable();
					ActorsTable.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
					ActorsTable.setRowSelectionAllowed(false);
					ActorsTable.setBackground(myGray);
					ActorsTable.setEnabled(false);
					scrollPane_Actors.setViewportView(ActorsTable);

					model_actors = new DefaultTableModel();
					Object[] columnsName_actors = new Object[2];

					columnsName_actors[0] = "Actor";
					columnsName_actors[1] = "Role";

					model_actors.setColumnIdentifiers(columnsName_actors);
					ActorsTable.setModel(model_actors);
					ActorsTable.setGridColor(new Color(0, 0, 0));
					ActorsTable.setEnabled(false);

					TableColumnModel columnModel_actors = ActorsTable.getColumnModel();
					columnModel_actors.getColumn(0).setResizable(false);
					columnModel_actors.getColumn(1).setResizable(false);

					List<Actor> cast = film.getCast();
					Object[] rowData_actors = new Object[40];
					/* INSERIMENTO DEGLI ATTORI ALL'INTERNO DELLA TABELLA DI ATTORI */
					for (Actor actor : cast) {
						rowData_actors[0] = actor.getName();
						rowData_actors[1] = actor.getRole();
						model_actors.addRow(rowData_actors);
					}

					/* LAYOUT ACTOR PANEL */
					JLabel lbl_Actor_Tag = new JLabel("Actor");
					lbl_Actor_Tag.setFont(new Font("Lucida Grande", Font.BOLD, 13));
					GroupLayout gl_panelMovieCast = new GroupLayout(panelMovieCast);
					gl_panelMovieCast.setHorizontalGroup(gl_panelMovieCast.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_panelMovieCast.createSequentialGroup().addGap(24)
									.addGroup(gl_panelMovieCast.createParallelGroup(Alignment.LEADING)
											.addComponent(lbl_Actor_Tag, GroupLayout.PREFERRED_SIZE, 79,
													GroupLayout.PREFERRED_SIZE)
											.addGroup(gl_panelMovieCast.createSequentialGroup()
													.addComponent(lbl_Directors_tag, GroupLayout.PREFERRED_SIZE, 79,
															GroupLayout.PREFERRED_SIZE)
													.addPreferredGap(ComponentPlacement.RELATED)
													.addComponent(scrollPane_Director, GroupLayout.DEFAULT_SIZE, 452,
															Short.MAX_VALUE))
											.addGroup(gl_panelMovieCast.createSequentialGroup().addGap(20).addComponent(
													scrollPane_Actors, GroupLayout.PREFERRED_SIZE, 508,
													GroupLayout.PREFERRED_SIZE)))
									.addContainerGap()));
					gl_panelMovieCast.setVerticalGroup(gl_panelMovieCast.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_panelMovieCast.createSequentialGroup().addGap(17).addGroup(gl_panelMovieCast
									.createParallelGroup(Alignment.LEADING, false)
									.addComponent(scrollPane_Director, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE,
											GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(lbl_Directors_tag, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE,
											GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
									.addGap(14).addComponent(lbl_Actor_Tag).addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(scrollPane_Actors, GroupLayout.PREFERRED_SIZE, 529,
											GroupLayout.PREFERRED_SIZE)
									.addContainerGap(18, Short.MAX_VALUE)));

					scrollPane_Director.setBorder(border);
					scrollPane_Director.getViewport().setBackground(myGray);

					JTextField textField_Director = new JTextField();
					textField_Director.setEditable(false);
					textField_Director.setEditable(false);
					scrollPane_Director.setViewportView(textField_Director);
					textField_Director.setBackground(myGray);
					textField_Director.setBorder(border);
					// INSERIMENTO DEL REGISTA CHE HA FATTO IL FILM
					scrollPane_Director.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 2));
					panelMovieCast.setLayout(gl_panelMovieCast);

					List<String> directors = film.getDirectors();
					String dir_str = "";
					try {
						for (String dir : directors)
							dir_str = dir_str + dir + " ";
						textField_Director.setText(dir_str);
					} catch (Exception e) {
						System.err.println(e.getClass().getName() + ": " + e.getMessage());
					}

					List<Platform> plat = film.getPlatforms();
					Object[] rowData_plat = new Object[40];

					for (int i = model_platforms.getRowCount() - 1; i >= 0; i--)
						model_platforms.removeRow(i);

					/* INSERIMENTO DEGLI ATTORI ALL'INTERNO DELLA TABELLA DI ATTORI */
					for (Platform p : plat) {
						rowData_plat[0] = p.getPlatform();
						rowData_plat[1] = p.getPrice();
						model_platforms.addRow(rowData_plat);
					}

					// Inserimento awards

					List<String> awards = film.getAwards();
					Object[] rowData_awards = new Object[40];

					for (int i = model_awards.getRowCount() - 1; i >= 0; i--)
						model_awards.removeRow(i);

					/* INSERIMENTO DEGLI ATTORI ALL'INTERNO DELLA TABELLA DI ATTORI */
					for (String a : awards) {
						rowData_awards[0] = a;
						model_awards.addRow(rowData_awards);
					}

					/* INSERIMENTO DELLE PIATTAFORME IN CUI E' DISPONIBILE IL FILM */
					/* LAYOUT PANEL MOVIE DETAILS */
					gl_panelAwards.setHorizontalGroup(gl_panelAwards.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_panelAwards.createSequentialGroup().addContainerGap()
									.addComponent(scrollPaneAwards, GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
									.addContainerGap()));
					gl_panelAwards.setVerticalGroup(gl_panelAwards.createParallelGroup(Alignment.LEADING)
							.addGroup(Alignment.TRAILING, gl_panelAwards.createSequentialGroup().addContainerGap()
									.addComponent(scrollPaneAwards, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
									.addContainerGap()));

					GroupLayout gl_panelMovieDetails = new GroupLayout(panelMovieDetails);
					gl_panelMovieDetails.setHorizontalGroup(gl_panelMovieDetails.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_panelMovieDetails.createSequentialGroup().addGroup(gl_panelMovieDetails
									.createParallelGroup(Alignment.LEADING)
									.addGroup(gl_panelMovieDetails.createSequentialGroup().addGap(13)
											.addGroup(gl_panelMovieDetails.createParallelGroup(Alignment.LEADING)
													.addGroup(gl_panelMovieDetails.createSequentialGroup()
															.addGroup(gl_panelMovieDetails
																	.createParallelGroup(Alignment.LEADING)
																	.addComponent(lbl_Synopsis)
																	.addGroup(gl_panelMovieDetails
																			.createSequentialGroup()
																			.addComponent(panelPoster,
																					GroupLayout.PREFERRED_SIZE, 186,
																					GroupLayout.PREFERRED_SIZE)
																			.addGap(36)
																			.addGroup(gl_panelMovieDetails
																					.createParallelGroup(
																							Alignment.LEADING)
																					.addComponent(separator_1,
																							GroupLayout.DEFAULT_SIZE,
																							274, Short.MAX_VALUE)
																					.addComponent(Pane_It_Title,
																							GroupLayout.DEFAULT_SIZE,
																							274, Short.MAX_VALUE)
																					.addComponent(Pane_En_Title,
																							GroupLayout.PREFERRED_SIZE,
																							189,
																							GroupLayout.PREFERRED_SIZE)
																					.addGroup(gl_panelMovieDetails
																							.createSequentialGroup()
																							.addGroup(
																									gl_panelMovieDetails
																											.createParallelGroup(
																													Alignment.LEADING)
																											.addComponent(
																													lbl_Year_tag)
																											.addComponent(
																													lbl_RunTime_tag))
																							.addGap(29)
																							.addGroup(
																									gl_panelMovieDetails
																											.createParallelGroup(
																													Alignment.LEADING)
																											.addComponent(
																													lbl_Year_Text)
																											.addComponent(
																													labelRuntime))
																							.addGap(24)
																							.addGroup(
																									gl_panelMovieDetails
																											.createParallelGroup(
																													Alignment.LEADING)
																											.addComponent(
																													lblCoomingSoon_tag,
																													GroupLayout.PREFERRED_SIZE,
																													104,
																													GroupLayout.PREFERRED_SIZE)
																											.addComponent(
																													lbl_Imdb_tag,
																													GroupLayout.PREFERRED_SIZE,
																													53,
																													GroupLayout.PREFERRED_SIZE)
																											.addComponent(
																													lblMymovies_Tag)
																											.addComponent(
																													lblUsers_tag,
																													GroupLayout.PREFERRED_SIZE,
																													52,
																													GroupLayout.PREFERRED_SIZE))
																							.addPreferredGap(
																									ComponentPlacement.RELATED,
																									28,
																									Short.MAX_VALUE))
																					.addGroup(gl_panelMovieDetails
																							.createSequentialGroup()
																							.addPreferredGap(
																									ComponentPlacement.RELATED)
																							.addGroup(
																									gl_panelMovieDetails
																											.createParallelGroup(
																													Alignment.LEADING)
																											.addComponent(
																													separator_3,
																													GroupLayout.PREFERRED_SIZE,
																													270,
																													GroupLayout.PREFERRED_SIZE)
																											.addComponent(
																													lbl_add_movie)
																											.addGroup(
																													gl_panelMovieDetails
																															.createParallelGroup(
																																	Alignment.TRAILING)
																															.addGroup(
																																	gl_panelMovieDetails
																																			.createSequentialGroup()
																																			.addComponent(
																																					lbl_BoxOffice_Tag,
																																					GroupLayout.PREFERRED_SIZE,
																																					76,
																																					GroupLayout.PREFERRED_SIZE)
																																			.addPreferredGap(
																																					ComponentPlacement.RELATED)
																																			.addComponent(
																																					lbl_BoxOffice_text))
																															.addGroup(
																																	gl_panelMovieDetails
																																			.createSequentialGroup()
																																			.addComponent(
																																					lbl_rate)
																																			.addPreferredGap(
																																					ComponentPlacement.RELATED)
																																			.addGroup(
																																					gl_panelMovieDetails
																																							.createParallelGroup(
																																									Alignment.LEADING)
																																							.addGroup(
																																									gl_panelMovieDetails
																																											.createSequentialGroup()
																																											.addGap(6)
																																											.addComponent(
																																													button,
																																													GroupLayout.PREFERRED_SIZE,
																																													58,
																																													GroupLayout.PREFERRED_SIZE))
																																							.addComponent(
																																									rate_movie_combo_box,
																																									GroupLayout.PREFERRED_SIZE,
																																									72,
																																									GroupLayout.PREFERRED_SIZE)))))))))
															.addPreferredGap(ComponentPlacement.RELATED)
															.addGroup(gl_panelMovieDetails
																	.createParallelGroup(Alignment.LEADING)
																	.addGroup(gl_panelMovieDetails
																			.createParallelGroup(Alignment.TRAILING)
																			.addGroup(gl_panelMovieDetails
																					.createParallelGroup(
																							Alignment.LEADING)
																					.addComponent(lbl_CoomingSoon_text,
																							GroupLayout.PREFERRED_SIZE,
																							52,
																							GroupLayout.PREFERRED_SIZE)
																					.addComponent(lbl_Imdb_text,
																							GroupLayout.PREFERRED_SIZE,
																							52,
																							GroupLayout.PREFERRED_SIZE))
																			.addComponent(lbl_MyMovies_text,
																					GroupLayout.PREFERRED_SIZE, 52,
																					GroupLayout.PREFERRED_SIZE))
																	.addComponent(lbl_Application_rate,
																			GroupLayout.PREFERRED_SIZE, 52,
																			GroupLayout.PREFERRED_SIZE)))
													.addGroup(gl_panelMovieDetails.createSequentialGroup().addGap(6)
															.addGroup(gl_panelMovieDetails
																	.createParallelGroup(Alignment.LEADING)
																	.addComponent(separator_2,
																			GroupLayout.PREFERRED_SIZE, 496,
																			GroupLayout.PREFERRED_SIZE)
																	.addComponent(scrollPane_Synopsis,
																			GroupLayout.PREFERRED_SIZE, 526,
																			GroupLayout.PREFERRED_SIZE)))
													.addGroup(gl_panelMovieDetails.createSequentialGroup().addGap(6)
															.addGroup(gl_panelMovieDetails
																	.createParallelGroup(Alignment.LEADING)
																	.addGroup(gl_panelMovieDetails
																			.createSequentialGroup()
																			.addGroup(gl_panelMovieDetails
																					.createParallelGroup(
																							Alignment.LEADING)
																					.addComponent(lbl_Genres_tag)
																					.addComponent(lbl_Countries_tag))
																			.addPreferredGap(ComponentPlacement.RELATED,
																					55, Short.MAX_VALUE)
																			.addComponent(scrollPane_Countries,
																					GroupLayout.PREFERRED_SIZE,
																					GroupLayout.DEFAULT_SIZE,
																					GroupLayout.PREFERRED_SIZE)
																			.addPreferredGap(ComponentPlacement.RELATED)
																			.addGroup(gl_panelMovieDetails
																					.createParallelGroup(
																							Alignment.LEADING, false)
																					.addComponent(scrollPane_Genres,
																							GroupLayout.DEFAULT_SIZE,
																							416, Short.MAX_VALUE)
																					.addComponent(textField_Countries,
																							GroupLayout.PREFERRED_SIZE,
																							GroupLayout.DEFAULT_SIZE,
																							GroupLayout.PREFERRED_SIZE)))
																	.addComponent(lbl_Platform_tag)))))
									.addGroup(gl_panelMovieDetails.createSequentialGroup().addGap(37).addComponent(
											scrollPane_Platform, GroupLayout.PREFERRED_SIZE, 479,
											GroupLayout.PREFERRED_SIZE)))
									.addContainerGap()));
					gl_panelMovieDetails.setVerticalGroup(gl_panelMovieDetails.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_panelMovieDetails.createSequentialGroup().addGap(27)
									.addGroup(gl_panelMovieDetails.createParallelGroup(Alignment.LEADING, false)
											.addGroup(gl_panelMovieDetails.createSequentialGroup()
													.addComponent(Pane_It_Title, GroupLayout.PREFERRED_SIZE, 25,
															GroupLayout.PREFERRED_SIZE)
													.addPreferredGap(ComponentPlacement.RELATED)
													.addComponent(Pane_En_Title, GroupLayout.PREFERRED_SIZE, 16,
															GroupLayout.PREFERRED_SIZE)
													.addGap(10)
													.addComponent(separator_1, GroupLayout.PREFERRED_SIZE,
															GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addGap(6)
													.addGroup(gl_panelMovieDetails
															.createParallelGroup(Alignment.LEADING)
															.addGroup(gl_panelMovieDetails.createSequentialGroup()
																	.addGroup(gl_panelMovieDetails
																			.createParallelGroup(Alignment.BASELINE)
																			.addComponent(lbl_Year_tag)
																			.addComponent(lbl_Year_Text))
																	.addPreferredGap(ComponentPlacement.RELATED)
																	.addGroup(gl_panelMovieDetails
																			.createParallelGroup(Alignment.BASELINE)
																			.addComponent(lbl_RunTime_tag)
																			.addComponent(labelRuntime)))
															.addGroup(gl_panelMovieDetails.createSequentialGroup()
																	.addGroup(gl_panelMovieDetails
																			.createParallelGroup(Alignment.BASELINE)
																			.addComponent(lbl_Imdb_tag)
																			.addComponent(lbl_Imdb_text))
																	.addPreferredGap(ComponentPlacement.RELATED)
																	.addGroup(gl_panelMovieDetails
																			.createParallelGroup(Alignment.BASELINE)
																			.addComponent(lblCoomingSoon_tag)
																			.addComponent(lbl_CoomingSoon_text))
																	.addPreferredGap(ComponentPlacement.RELATED)
																	.addGroup(gl_panelMovieDetails
																			.createParallelGroup(Alignment.BASELINE)
																			.addComponent(lblMymovies_Tag)
																			.addComponent(lbl_MyMovies_text))
																	.addPreferredGap(ComponentPlacement.RELATED)
																	.addGroup(gl_panelMovieDetails
																			.createParallelGroup(Alignment.BASELINE)
																			.addComponent(lblUsers_tag)
																			.addComponent(lbl_Application_rate))))
													.addGap(9)
													.addGroup(
															gl_panelMovieDetails.createParallelGroup(Alignment.BASELINE)
																	.addComponent(lbl_BoxOffice_Tag)
																	.addComponent(lbl_BoxOffice_text))
													.addPreferredGap(ComponentPlacement.UNRELATED)
													.addComponent(lbl_add_movie)
													.addPreferredGap(ComponentPlacement.UNRELATED)
													.addGroup(gl_panelMovieDetails
															.createParallelGroup(Alignment.BASELINE)
															.addComponent(rate_movie_combo_box,
																	GroupLayout.PREFERRED_SIZE, 20,
																	GroupLayout.PREFERRED_SIZE)
															.addComponent(lbl_rate))
													.addPreferredGap(ComponentPlacement.UNRELATED).addComponent(button,
															GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
											.addComponent(panelPoster, GroupLayout.PREFERRED_SIZE, 273,
													GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE,
											Short.MAX_VALUE)
									.addGroup(gl_panelMovieDetails.createParallelGroup(Alignment.LEADING)
											.addComponent(lbl_Synopsis).addComponent(separator_3,
													GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
													GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(scrollPane_Synopsis, GroupLayout.PREFERRED_SIZE, 94,
											GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(separator_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
											GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_panelMovieDetails.createParallelGroup(Alignment.TRAILING)
											.addComponent(scrollPane_Genres, GroupLayout.PREFERRED_SIZE,
													GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(lbl_Genres_tag))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_panelMovieDetails.createParallelGroup(Alignment.LEADING)
											.addComponent(scrollPane_Countries, GroupLayout.PREFERRED_SIZE, 22,
													GroupLayout.PREFERRED_SIZE)
											.addGroup(gl_panelMovieDetails.createParallelGroup(Alignment.BASELINE)
													.addComponent(lbl_Countries_tag).addComponent(textField_Countries,
															GroupLayout.PREFERRED_SIZE, 22,
															GroupLayout.PREFERRED_SIZE)))
									.addPreferredGap(ComponentPlacement.RELATED).addComponent(lbl_Platform_tag)
									.addPreferredGap(ComponentPlacement.RELATED).addComponent(scrollPane_Platform,
											GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
									.addContainerGap()));
					panelMovieDetails.setLayout(gl_panelMovieDetails);

					panelMovieComments.removeAll();
					JScrollPane scrollPane_Container_Comments = new JScrollPane();
					scrollPane_Container_Comments.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
					scrollPane_Container_Comments.setBounds(6, 6, 533, 604);
					scrollPane_Container_Comments.setBackground(myGray);
					;
					scrollPane_Container_Comments.setBorder(border);
					panelMovieComments.add(scrollPane_Container_Comments);

					// CurrentFilm.getComments() = dao.getOtherComments(CurrentFilm.getComments(),
					// indexComment, CurrentFilm.getFilm_id());
					FlowLayout fl_panelComments = new FlowLayout(FlowLayout.LEFT);
					JPanel panelComments = new JPanel(fl_panelComments);
					panelComments.removeAll();
					panelComments.setBorder(null);
					scrollPane_Container_Comments.setViewportView(panelComments);
					panelComments.setBackground(myGray);
					panelComments.setLayout(new BoxLayout(panelComments, BoxLayout.Y_AXIS));
					scrollPane_Container_Comments.getVerticalScrollBar().setPreferredSize(new Dimension(3, 1));
					panelComments.add(Box.createVerticalStrut(20));

					JButton LoadMoreButton = new JButton("Load More Comments");
					// panelMovieComments.add(LoadMoreButton);

					LoadMoreButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// panelComments.removeAll();
							int i = indexComment;

							try {
								CurrentFilm = dao.getMoreComments(indexComment, CurrentFilm);
							} catch (MongoException e2) {
								e2.printStackTrace();
								System.err.println("The service is currently unavailable. Closing...");
								dao.exit();
								System.exit(-1);
							}catch(ServiceUnavailableException ex ) {
								System.err.println("The service is currently unavailable. Closing...");
								dao.exit();
								System.exit(-1);
							}
							indexComment += 10;

							if (CurrentFilm.getComments().size() % 10 != 0)
								LoadMoreButton.setEnabled(false);

							if (CurrentFilm.getComments().size() == previndexComment) {
								LoadMoreButton.setEnabled(false);
								// panelComments.remove(LoadMoreButton);
								System.out.println("Non ci sono altri commenti da caricare");
							}

							if (CurrentFilm.getComments().size() == 0) {

								scrollPane_Container_Comments
										.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
								JPanel comment_zero = new JPanel(new FlowLayout(FlowLayout.LEFT));
								// comment_zero.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
								comment_zero.setBackground(myGray);
								comment_zero.setLayout(new BoxLayout(comment_zero, BoxLayout.Y_AXIS));
								// panelMovieComments.add(comment_zero);

								JLabel no_comment = new JLabel("This movie does not have any comments.");

								// panelMovieComments.add(Box.createVerticalStrut(5));

								// comment_zero.add(no_comment);
								panelComments.add(no_comment);

								panelMovieComments.revalidate();
								panelMovieComments.repaint();

							}

							else {

								System.out.println("Carico altri 10 commenti...");
								System.out.println(CurrentFilm.getComments().size());

								for (; i < CurrentFilm.getComments().size(); i++) {

									String comment_id = CurrentFilm.getComments().get(i).getComment_id();

									JPanel comment_i = new JPanel(new FlowLayout(FlowLayout.LEFT));
									comment_i.setBackground(myGray);
									comment_i.setLayout(new BoxLayout(comment_i, BoxLayout.Y_AXIS));
									panelComments.add(comment_i);
									String type;

									if (CurrentFilm.getComments().get(i).getType() == 0)
										type = "application";
									else if (CurrentFilm.getComments().get(i).getType() == 1)
										type = "IMDb";
									else

										type = "MYMOVIES";

									JLabel author_tag = new JLabel("Author: \n\n\n\n\n\n");
									JLabel author_name = new JLabel(CurrentFilm.getComments().get(i).getAuthor());

									JLabel author_container = new JLabel(author_tag.getText() + author_name.getText());
									comment_i.add(author_container);

									comment_i.add(Box.createVerticalStrut(5));

									JLabel comment_type = new JLabel(type);
									JLabel source_container = new JLabel("Source : " + comment_type.getText());
									comment_i.add(source_container);

									comment_i.add(Box.createVerticalStrut(5));

									JLabel date_tag = new JLabel("Date: \n\n\n\n\n\n");
									DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
									JLabel date_text = new JLabel(
											dateFormat.format(CurrentFilm.getComments().get(i).getDate()));
									// JLabel test = new JLabel(String.valueOf(i));
									JLabel date_container = new JLabel(date_tag.getText() + date_text.getText());
									comment_i.add(date_container);

									comment_i.add(Box.createVerticalStrut(5));

									JLabel comment_tag = new JLabel("Comment:");
									comment_i.add(comment_tag);

									comment_i.add(Box.createVerticalStrut(3));

									JScrollPane scrollPane_comment_i = new JScrollPane();
									float align_x = comment_i.getAlignmentX();
									scrollPane_comment_i.setAlignmentX(align_x);
									scrollPane_comment_i.getViewport().setBackground(myGray);
									JTextArea textArea_comment_i = new JTextArea(6, 29);
									textArea_comment_i.setEditable(false);
									scrollPane_comment_i.setViewportView(textArea_comment_i);
									textArea_comment_i.setBackground(myGray);
									textArea_comment_i.setText(CurrentFilm.getComments().get(i).getText());
									textArea_comment_i.setWrapStyleWord(true);
									textArea_comment_i.setLineWrap(true);
									scrollPane_comment_i.getVerticalScrollBar().setPreferredSize(new Dimension(2, 0));
									textArea_comment_i.setCaretPosition(0);
									comment_i.add(scrollPane_comment_i);

									comment_i.add(Box.createVerticalStrut(5));

									JLabel comment_points_tag = new JLabel("Comment points: \n\n\n\n\n\n");
									JLabel comment_points_text = new JLabel(
											String.valueOf(CurrentFilm.getComments().get(i).getComment_points()));
									JLabel comment_points_container = new JLabel(
											comment_points_tag.getText() + comment_points_text.getText());
									comment_i.add(comment_points_container);

									comment_i.add(Box.createVerticalStrut(5));
									JButton btnDelCom = new JButton("Delete");
									comment_i.add(btnDelCom);

									btnDelCom.addActionListener(new ActionListener() {
										@Override
										public void actionPerformed(ActionEvent e) {
											try {
												user.deleteComment(comment_id, dao);
											} catch (MongoException e2) {
												e2.printStackTrace();
												System.err.println("The service is currently unavailable. Closing...");
												dao.exit();
												System.exit(-1);
											}catch(ServiceUnavailableException ex ) {
												System.err.println("The service is currently unavailable. Closing...");
												dao.exit();
												System.exit(-1);
											}
											panelComments.remove(comment_i);
											panelComments.revalidate();
											panelComments.repaint();
										}
									});

									comment_i.add(Box.createVerticalStrut(15));

									JSeparator comment_separator = new JSeparator(SwingConstants.HORIZONTAL);
									comment_separator.setPreferredSize(new Dimension(350, 15));
									comment_i.add(comment_separator);

									comment_i.add(Box.createVerticalStrut(15));

									if (i == CurrentFilm.getComments().size() - 1) {

										panelComments.add(LoadMoreButton);
										panelComments.add(Box.createVerticalStrut(15));
										previndexComment = CurrentFilm.getComments().size();

									}

									panelMovieComments.revalidate();
									panelMovieComments.repaint();

								}
							}
						}
					});

					// panelMovieComments.removeAll();

					LoadMoreButton.doClick();

					// System.out.println(CurrentFilm.getOriginal_title().replaceAll(" ", "_"));
					String toFind = CurrentFilm.getOriginal_title().replaceAll(" ", "_");

					File input = new File("/Users/andreadidonato/posters/"
							+ toFind.replace("'", "").replace("-", "").replace(":", "") + ".jpg");
					BufferedImage image = ImageIO.read(input);
					BufferedImage resized = resize(image, 273, 186);
					ImageIcon imageicon = new ImageIcon(resized);
					JLabel label_poster_img = new JLabel(imageicon);
					panelPoster.add(label_poster_img);
					panelPoster.validate();
					panelPoster.repaint();

				} catch (IOException e) {
					// System.out.println("Image not available");
					panelPoster.removeAll();
					panelPoster.revalidate();
					panelPoster.repaint();
					JLabel lbl_poster_text = new JLabel("Image not Available");
					panelPoster.add(lbl_poster_text);
				}

				// STATISTICA PER IL CALCOLO DELLA PERCENTUALE DI UTENTI CHE HANNO VISTO IL FILM
				// CORRENTE
				// filmtitle=CurrentFilm.getItalian_title();
				lblnat_title.setText(CurrentFilm.getItalian_title());

				for (int i = model_Nations.getRowCount() - 1; i >= 0; i--)
					model_Nations.removeRow(i);

				List<Document> PercentageUserNation = new ArrayList<>();

				try {
					PercentageUserNation = dao.ViewByNationality(CurrentFilm.getFilm_id());
				} catch (MongoException e2) {
					e2.printStackTrace();
					System.err.println("The service is currently unavailable. Closing...");
					dao.exit();
					System.exit(-1);
				}catch(ServiceUnavailableException ex ) {
					System.err.println("The service is currently unavailable. Closing...");
					dao.exit();
					System.exit(-1);
				}
				double totalViewers = PercentageUserNation.get(PercentageUserNation.size() - 1)
						.getInteger("total_visual");
				for (int i = 0; i < PercentageUserNation.size() - 1; i++) {
					String nation = PercentageUserNation.get(i).getString("_id");
					double numberOfViewers = PercentageUserNation.get(i).getInteger("viewers");
					double Percentage = (numberOfViewers * 100) / totalViewers;
					DecimalFormat format = new DecimalFormat("##.00");
					// System.out.println(" NAZIONE: " + nation + " Percentuale Visualizzazioni: " +
					// Percentage);
					// model_comedy.addRow(new Object[]{i+1, title});
					model_Nations.addRow(
							new Object[] { nation, Math.round(numberOfViewers), format.format(Percentage) + "%" });
				}

			}
		});

		frameAdmin.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dao.exit();
			}
		});
		this.frameAdmin.setVisible(true);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

/*
 * try { //gui_admin window = new gui_admin(); frameAdmin.setVisible(true); }
 * catch (Exception e) { e.printStackTrace(); }
 * 
 * } }
 */