package task2.mongodb;

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
import java.awt.desktop.AboutHandler;

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
import javax.swing.Icon;
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

public class Gui_user extends JFrame {

	private static final long serialVersionUID = 1L;
	private int index;
	private int statistics1_done = 0;
	private int statistics2_done = 0;
	private DefaultTableModel model;
	DefaultTableModel model_actors;
	DefaultTableModel model_platforms = new DefaultTableModel();
	DefaultTableModel model_awards = new DefaultTableModel();
	DefaultTableModel model_moreLikeThis;
	DefaultTableModel model_OtherUsers;
	DefaultTableModel model_suggestByCast;
	JPanel panelMovieCast;
	JFrame frameLogin;
	private int created = 0;
	private List<BasicFilm> films_list = new ArrayList<BasicFilm>();
	private int indexComment = 0;
	private int previndexComment = -1;
	private Film CurrentFilm;

	private JTextField textField;
	private JTable MoviesTable;
	private JTable PlatformsTable;
	private JTable ActorsTable;
	private JTable WatchedTable;
	private JTable FavoriteTable;
	private JTable MostViewed_LastMonthTable;
	private JTable RequestHistoryTable;
	private JTable MoreLikeThisTable;
	private JTable OtherUsersTable;
	private JTable suggestByCastTable;

	private JTextField textField_Title;
	private JTextField Old_Password_text_Field;
	private JTextField New_Password_text_Field;
	private JTextField Confirm_Password_text_Field;
	
	private List<BasicFilm> advicesByOtherUsers;
	private List<BasicFilm> advicesByActors;
	private List<BasicFilm> advicesByDirectors;

	final JComboBox YearcomboBox;
	private JTable tableAwards;

	/*
	 * public static void main(String[] args) {
	 * 
	 * EventQueue.invokeLater(new Runnable() { public void run() { try { DaoMongo
	 * dao = DaoMongo.getInstance(); // creo il Dao //Gui_user window = new
	 * Gui_user(dao); // window.frameLogin.setVisible(true); } catch (Exception e) {
	 * e.printStackTrace(); } } }); }
	 */

	// Listener dei bottoni Like/Dislike dei commenti
	private ActionListener createActionListener(JButton like_clicked, JButton dislike_clicked, Comment comment_i,
			User user, int action, Dao dao, JLabel text_points, JLabel container_points, JPanel container) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int action_set = action;

				if ((like_clicked.isEnabled() == false && dislike_clicked.isEnabled() == true)
						|| (like_clicked.isEnabled() == true && dislike_clicked.isEnabled() == false)) {
					if (like_clicked.isEnabled() == true) {
						action_set = action_set + 1;
					} else if (dislike_clicked.isEnabled() == true) {
						action_set = action_set - 1;
					}
				}
				clickedCommentButton(comment_i, user, action_set, action, dao);

				if (action == +1) {
					like_clicked.setEnabled(false);
					dislike_clicked.setEnabled(true);
					int new_points = Integer.parseInt(text_points.getText()) + action_set;
					// System.out.println("New Points: " + new_points);
					// System.out.println("Testo Label: " + text_points.getText());
					container_points.setText("Comment points: \n\n\n\n\n\n" + Integer.toString(new_points));
					text_points.setText(Integer.toString(new_points));
					container.validate();
					container.repaint();
				} else {
					like_clicked.setEnabled(true);
					dislike_clicked.setEnabled(false);
					int new_points = Integer.parseInt(text_points.getText()) + action_set;
					// System.out.println("New Points: " + new_points);
					// System.out.println("Testo Label: " + text_points.getText());
					container_points.setText("Comment points: \n\n\n\n\n\n" + Integer.toString(new_points));
					text_points.setText(Integer.toString(new_points));
					container_points.revalidate();
					container_points.repaint();
					container.validate();
					container.repaint();
				}
			}
		};
	}

	private void clickedCommentButton(Comment comment_i_clicked, User user_clicked, int total_action_clicked,
			int action_clicked, Dao dao_clicked) {
		try {
			dao_clicked.commentAction(user_clicked, comment_i_clicked, total_action_clicked, action_clicked);
		} catch (Exception e2) {
			e2.printStackTrace();
			System.err.println("The service is currently unavailable. Closing...");
			dao_clicked.exit();
			System.exit(-1);
		}
	}

	// Listener load commenti imdb

	/* FUNZIONE NECESSARIA PER RIDIMENSIONARE IL POSTER DEL FILM */
	private static BufferedImage resize(BufferedImage img, int height, int width) {
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resized;
	}

	public Gui_user(final Dao dao, final User user) {

		index = 0;

		try {
			films_list = dao.getBasicFilmsIndex(index);
		} catch (MongoException e2) {
			e2.printStackTrace();
			System.err.println("The service is currently unavailable. Closing...");
			dao.exit();
			System.exit(-1);
		} catch(ServiceUnavailableException ex ) {
			System.err.println("The service is currently unavailable. Closing...");
			dao.exit();
			System.exit(-1);
		}

		// Al login viene generata la lista di richieste effettuate dall'utente
		// user.fill_Request(dao);
		try {
			user.fill_Request(dao);
		} catch (MongoException e2) {
			e2.printStackTrace();
			System.err.println("The service is currently unavailable. Closing...");
			dao.exit();
			System.exit(-1);
		} catch(ServiceUnavailableException ex ) {
			System.err.println("The service is currently unavailable. Closing...");
			dao.exit();
			System.exit(-1);
		} catch (Exception e) {
			System.out.println("L'utente non ha effettuato richieste");
		}

		// Content GUI
		final Color myGray = new Color(238, 238, 238);
		final Border border = BorderFactory.createEmptyBorder(0, 0, 0, 0);
		frameLogin = new JFrame();
		frameLogin.setResizable(false);
		frameLogin.getContentPane().setBackground(new Color(65, 105, 225));
		frameLogin.getContentPane().setForeground(new Color(30, 144, 255));
		frameLogin.getContentPane().setLayout(null);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setBounds(0, 0, 1200, 678);
		splitPane.setResizeWeight(0.5);
		splitPane.setEnabled(false);
		frameLogin.getContentPane().add(splitPane);

		JPanel paneLeft = new JPanel();
		splitPane.setLeftComponent(paneLeft);
		paneLeft.setLayout(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(6, 6, 581, 662);
		paneLeft.add(tabbedPane);

		/* PANEL PROFILE */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

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

		JLabel lbl_Year_of_Birth_text = new JLabel(year);

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
				// System.out.println(oldPassword);
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
								"Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, null,
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

		/* LAYOUT PROFILE PANEL */

		JLabel lbl_Your_Comments = new JLabel("Your Comments");
		lbl_Your_Comments.setFont(new Font("Lucida Grande", Font.BOLD, 17));

		JPanel panel_Your_Comments_Container = new JPanel();
		panel_Your_Comments_Container.setBorder(null);

		JButton btnNewButton = new JButton("Logout");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frameLogin.dispose();
				Login l = new Login(dao);
			}
		});

		GroupLayout gl_panelProfile = new GroupLayout(panelProfile);
		gl_panelProfile
				.setHorizontalGroup(gl_panelProfile.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelProfile.createSequentialGroup().addGap(93)
								.addGroup(gl_panelProfile.createParallelGroup(Alignment.TRAILING)
										.addComponent(lblNewPassword).addComponent(lblConfirmPassword)
										.addGroup(gl_panelProfile.createParallelGroup(Alignment.LEADING)
												.addComponent(lbl_Username_tag).addComponent(lbl_Old_Password)))
								.addGap(27)
								.addGroup(gl_panelProfile.createParallelGroup(Alignment.LEADING)
										.addComponent(Confirm_Password_text_Field, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(New_Password_text_Field, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(Old_Password_text_Field, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGap(206))
						.addGroup(gl_panelProfile.createSequentialGroup().addContainerGap(286, Short.MAX_VALUE)
								.addComponent(btn_change_password, GroupLayout.PREFERRED_SIZE, 73,
										GroupLayout.PREFERRED_SIZE)
								.addGap(217))
						.addGroup(gl_panelProfile
								.createSequentialGroup().addGap(59)
								.addComponent(panel_Your_Comments_Container, GroupLayout.PREFERRED_SIZE, 473,
										GroupLayout.PREFERRED_SIZE)
								.addContainerGap(44, Short.MAX_VALUE))
						.addGroup(gl_panelProfile.createSequentialGroup().addGap(44)
								.addGroup(gl_panelProfile.createParallelGroup(Alignment.TRAILING)
										.addGroup(gl_panelProfile.createParallelGroup(Alignment.LEADING)
												.addComponent(lbl_General_Info_tag)
												.addGroup(gl_panelProfile.createParallelGroup(Alignment.TRAILING)
														.addComponent(lbl_Country_tag, GroupLayout.PREFERRED_SIZE, 71,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(lblName, GroupLayout.PREFERRED_SIZE, 142,
																GroupLayout.PREFERRED_SIZE))
												.addComponent(
														lbl_Access_Info_tag, GroupLayout.PREFERRED_SIZE, 112,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														lbl_Your_Comments, GroupLayout.PREFERRED_SIZE, 155,
														GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_panelProfile.createSequentialGroup().addComponent(lbl_Name_tag)
												.addGap(28)))
								.addGroup(gl_panelProfile.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_panelProfile.createSequentialGroup()
												.addPreferredGap(ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
												.addGroup(gl_panelProfile.createParallelGroup(Alignment.TRAILING, false)
														.addGroup(gl_panelProfile
																.createSequentialGroup()
																.addComponent(lbl_Username_text,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																.addGap(219))
														.addGroup(gl_panelProfile.createSequentialGroup()
																.addGroup(gl_panelProfile
																		.createParallelGroup(Alignment.LEADING)
																		.addComponent(lbl_Name_Text,
																				GroupLayout.PREFERRED_SIZE, 117,
																				GroupLayout.PREFERRED_SIZE)
																		.addComponent(lbl_Country_text,
																				GroupLayout.PREFERRED_SIZE, 123,
																				GroupLayout.PREFERRED_SIZE))
																.addPreferredGap(ComponentPlacement.UNRELATED)
																.addGroup(gl_panelProfile
																		.createParallelGroup(Alignment.LEADING)
																		.addComponent(lbl_surname_tag,
																				GroupLayout.PREFERRED_SIZE, 73,
																				GroupLayout.PREFERRED_SIZE)
																		.addComponent(lbl_Year_of_Birth_tag,
																				GroupLayout.PREFERRED_SIZE, 40,
																				GroupLayout.PREFERRED_SIZE))
																.addPreferredGap(ComponentPlacement.UNRELATED)
																.addGroup(gl_panelProfile
																		.createParallelGroup(Alignment.LEADING)
																		.addComponent(lbl_Year_of_Birth_text)
																		.addComponent(lbl_surname_text,
																				GroupLayout.PREFERRED_SIZE, 100,
																				GroupLayout.PREFERRED_SIZE))
																.addGap(54))))
										.addGroup(gl_panelProfile.createSequentialGroup().addGap(281)
												.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 73,
														GroupLayout.PREFERRED_SIZE)
												.addContainerGap())))
						.addGroup(gl_panelProfile.createSequentialGroup().addGap(176)
								.addComponent(lbl_Change_Password_tag).addContainerGap(287, Short.MAX_VALUE)));
		gl_panelProfile.setVerticalGroup(gl_panelProfile.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelProfile.createSequentialGroup().addGap(28)
						.addGroup(gl_panelProfile.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblName, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(lbl_General_Info_tag).addGap(8)
						.addGroup(gl_panelProfile.createParallelGroup(Alignment.BASELINE).addComponent(lbl_Name_tag)
								.addComponent(lbl_Name_Text).addComponent(lbl_surname_tag)
								.addComponent(lbl_surname_text))
						.addGap(18)
						.addGroup(gl_panelProfile.createParallelGroup(Alignment.BASELINE).addComponent(lbl_Country_tag)
								.addComponent(lbl_Country_text).addComponent(lbl_Year_of_Birth_tag)
								.addComponent(lbl_Year_of_Birth_text))
						.addGap(12)
						.addComponent(lbl_Access_Info_tag, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_panelProfile.createParallelGroup(Alignment.BASELINE)
								.addComponent(lbl_Username_text).addComponent(lbl_Username_tag))
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(lbl_Change_Password_tag)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_panelProfile.createParallelGroup(Alignment.BASELINE)
								.addComponent(Old_Password_text_Field, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lbl_Old_Password))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_panelProfile.createParallelGroup(Alignment.BASELINE)
								.addComponent(New_Password_text_Field, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNewPassword))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_panelProfile.createParallelGroup(Alignment.BASELINE)
								.addComponent(Confirm_Password_text_Field, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblConfirmPassword))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_panelProfile.createParallelGroup(Alignment.LEADING)
								.addComponent(btn_change_password, GroupLayout.PREFERRED_SIZE, 21,
										GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_panelProfile.createSequentialGroup().addGap(28).addComponent(
										lbl_Your_Comments, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel_Your_Comments_Container, GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
						.addContainerGap()));

		JScrollPane scrollPane_Your_Comments = new JScrollPane();
		scrollPane_Your_Comments.setBorder(border);
		scrollPane_Your_Comments.getVerticalScrollBar().setPreferredSize(new Dimension(0, 2));
		scrollPane_Your_Comments.getViewport().setBackground(myGray);

		GroupLayout gl_panel_Your_Comments_Container = new GroupLayout(panel_Your_Comments_Container);
		gl_panel_Your_Comments_Container
				.setHorizontalGroup(gl_panel_Your_Comments_Container.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_Your_Comments_Container.createSequentialGroup().addContainerGap()
								.addComponent(scrollPane_Your_Comments, GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
								.addContainerGap()));
		gl_panel_Your_Comments_Container
				.setVerticalGroup(gl_panel_Your_Comments_Container.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_Your_Comments_Container.createSequentialGroup().addContainerGap()
								.addComponent(scrollPane_Your_Comments, GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
								.addContainerGap()));

		JPanel panel_Your_Comments = new JPanel();
		scrollPane_Your_Comments.setViewportView(panel_Your_Comments);
		scrollPane_Your_Comments.getVerticalScrollBar().setPreferredSize(new Dimension(3, 1));
		panel_Your_Comments.setLayout(new BoxLayout(panel_Your_Comments, BoxLayout.Y_AXIS));
		scrollPane_Your_Comments.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		List<Comment> user_comments = user.getUser_comments();

		for (int i = 0; i < user_comments.size(); i++) {

			String comment_id = user_comments.get(i).getComment_id();

			JPanel your_comment_i = new JPanel(new FlowLayout(FlowLayout.LEFT));
			your_comment_i.setBackground(myGray);
			your_comment_i.setLayout(new BoxLayout(your_comment_i, BoxLayout.Y_AXIS));
			panel_Your_Comments.add(your_comment_i);

			JLabel film_your_comment_tag = new JLabel("Movie: \n\n\n\n\n\n");
			String movieString = "";
			try {
				movieString = dao.getBasicFilm(user_comments.get(i).getFilm_id()).getTitle();
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
			JLabel film_your_comment_text = new JLabel(movieString);
			JLabel film_your_comment_container = new JLabel(
					film_your_comment_tag.getText() + film_your_comment_text.getText());
			your_comment_i.add(film_your_comment_container);

			JLabel date_your_comment_tag = new JLabel("Date: \n\n\n\n\n");
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			JLabel date_your_comment_text = new JLabel(df.format(user_comments.get(i).getDate()));
			JLabel date_your_comment_container = new JLabel(
					date_your_comment_tag.getText() + date_your_comment_text.getText());
			your_comment_i.add(date_your_comment_container);

			panel_Your_Comments.add(Box.createVerticalStrut(5));

			JLabel your_comment_tag = new JLabel("Comment:");
			your_comment_i.add(your_comment_tag);

			panel_Your_Comments.add(Box.createVerticalStrut(3));

			JScrollPane scrollPane_your_comment_i = new JScrollPane();
			float align_your_comment_x = your_comment_i.getAlignmentX();
			scrollPane_your_comment_i.setAlignmentX(align_your_comment_x);
			scrollPane_your_comment_i.getViewport().setBackground(myGray);
			JTextArea textArea_your_comment_i = new JTextArea(6, 22);
			textArea_your_comment_i.setEditable(false);
			scrollPane_your_comment_i.setViewportView(textArea_your_comment_i);
			textArea_your_comment_i.setBackground(myGray);
			textArea_your_comment_i.setText(user_comments.get(i).getText());
			textArea_your_comment_i.setWrapStyleWord(true);
			textArea_your_comment_i.setLineWrap(true);
			scrollPane_your_comment_i.getVerticalScrollBar().setPreferredSize(new Dimension(2, 0));
			textArea_your_comment_i.setCaretPosition(0);
			your_comment_i.add(scrollPane_your_comment_i);

			your_comment_i.add(Box.createVerticalStrut(5));

			JButton btnDelete = new JButton("Delete");
			your_comment_i.add(btnDelete);

			btnDelete.addActionListener(new ActionListener() {
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
					panel_Your_Comments.remove(your_comment_i);
					panel_Your_Comments.revalidate();
					panel_Your_Comments.repaint();
				}
			});

			your_comment_i.add(Box.createVerticalStrut(15));

			if (i != 4) {
				JSeparator your_comment_separator = new JSeparator(SwingConstants.HORIZONTAL);
				your_comment_separator.setPreferredSize(new Dimension(350, 15));
				your_comment_i.add(your_comment_separator);
			}

			your_comment_i.add(Box.createVerticalStrut(15));

		}

		panel_Your_Comments_Container.setLayout(gl_panel_Your_Comments_Container);
		panelProfile.setLayout(gl_panelProfile);
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

		/* PANEL MOVIES */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/
		JPanel panelMovies = new JPanel();
		tabbedPane.addTab("Movies", null, panelMovies, null);
		panelMovies.setLayout(null);

		textField = new JTextField();
		textField.setBounds(6, 6, 355, 26);
		panelMovies.add(textField);
		textField.setColumns(10);

		JButton btnSearch = new JButton("Search");
		btnSearch.setBounds(460, 6, 94, 29);

		// Aggiunta del bottone per terminare la ricerca -> di default non si vede
		JButton btnEndSearch = new JButton("Stop searching");
		btnEndSearch.setBounds(437, 581, 117, 29);
		panelMovies.add(btnEndSearch);
		btnEndSearch.setVisible(false);
		btnEndSearch.setEnabled(false);

		JButton btnNext = new JButton("Next");
		final JButton btnPrev = new JButton("Prev");

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
					films_list.clear();
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

					btnNext.setEnabled(false);
					btnPrev.setEnabled(false);
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
					String runtimeText = "N/A";
					if (film.getRuntime() != 0)
						// System.out.println(film.getRuntime());
						runtimeText = film.getRuntime() + "'";
					model.addRow(new Object[] { film.getTitle(), film.getYear(), runtimeText });

				}

				btnPrev.setEnabled(false);
				btnNext.setEnabled(true);
				textField.setText("");

			}
		});

		/* BOTTONE PREV -> OCCORRE DEFINIRE IL LISTENER */

		btnPrev.setEnabled(false);
		btnPrev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnNext.setEnabled(true);
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
		btnPrev.setBounds(166, 581, 117, 29);
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

				if (films_list.size() < 40)
					btnNext.setEnabled(false);

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
		btnNext.setBounds(306, 581, 117, 29);
		panelMovies.add(btnNext);

		JScrollPane paneResults = new JScrollPane();
		paneResults.setBounds(6, 34, 548, 544);
		panelMovies.add(paneResults);

		frameLogin.setForeground(Color.BLUE);
		frameLogin.setBounds(100, 100, 1200, 700);
		frameLogin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frameLogin.setLocation(dim.width / 2 - frameLogin.getSize().width / 2,
				dim.height / 2 - frameLogin.getSize().height / 2);

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
		YearcomboBox.setBounds(355, 8, 108, 22);
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

		/* INSERIMENTO DI FILM NELLA TABELLA MOVIES */
		for (int i = 0; i < films_list.size(); i++) {
			BasicFilm film = films_list.get(i);
			String runtimeText = "N/A";
			if (film.getRuntime() != 0)
				runtimeText = film.getRuntime() + "'";
			model.addRow(new Object[] { film.getTitle(), film.getYear(), runtimeText });

		}
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

		/* PANEL WATCHED MOVIES */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/
		JPanel panel_Watched = new JPanel();
		tabbedPane.addTab("Watched", null, panel_Watched, null);

		JScrollPane scrollPane_Watched = new JScrollPane();
		scrollPane_Watched.setBorder(border);
		scrollPane_Watched.getVerticalScrollBar().setPreferredSize(new Dimension(0, 2));
		scrollPane_Watched.getViewport().setBackground(myGray);
		scrollPane_Watched.setBounds(554, 6, -551, 604);

		WatchedTable = new JTable();
		WatchedTable.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		WatchedTable.setRowSelectionAllowed(false);
		WatchedTable.setBackground(myGray);
		WatchedTable.setEnabled(false);
		scrollPane_Watched.setViewportView(WatchedTable);

		/*
		 * Necessario per poter abilitare unicamente la modifica della checkbox della
		 * colonna 2
		 */
		final DefaultTableModel model_watched = new DefaultTableModel() {

			@Override
			public boolean isCellEditable(int row, int column) {
				if (column == 3)
					return true;
				else
					return false;
			}
		};
		Object[] columnsName_watched = new Object[4];

		columnsName_watched[0] = "Title";
		columnsName_watched[1] = "MyRate";
		columnsName_watched[2] = "Date";
		columnsName_watched[3] = "Favorite";

		model_watched.setColumnIdentifiers(columnsName_watched);
		WatchedTable.setModel(model_watched);
		WatchedTable.setGridColor(new Color(0, 0, 0));
		WatchedTable.setEnabled(true);

		TableColumnModel columnModel_watched = WatchedTable.getColumnModel();
		columnModel_watched.getColumn(0).setResizable(false);
		columnModel_watched.getColumn(1).setResizable(false);
		columnModel_watched.getColumn(2).setResizable(false);
		columnModel_watched.getColumn(3).setResizable(false);
		columnModel_watched.getColumn(0).setPreferredWidth(1000);
		columnModel_watched.getColumn(2).setPreferredWidth(500);
		columnModel_watched.getColumn(1).setPreferredWidth(200);
		columnModel_watched.getColumn(3).setPreferredWidth(200);

		/*
		 * Questo è necessario per poter mettere la checkbox all'interno della riga
		 * della Tabella)
		 */
		TableColumn tc = WatchedTable.getColumnModel().getColumn(3);
		tc.setCellEditor(WatchedTable.getDefaultEditor(Boolean.class));
		tc.setCellRenderer(WatchedTable.getDefaultRenderer(Boolean.class));

		/* INSERIMENTO DI FILM NELLA TABELLA FILM VISTI */
		/*
		 * Questo viene fatto per aggiungere righe alla tabella dei film Visti (è un
		 * altro metodo per aggiungere righe rispetto a quello utilizzato
		 * precendetemente per inserire righe nelle altre tabelle dell'interfaccia
		 */

		// User user1 = dao.getUser("kyron511", dao);
		final List<WatchedFilm> watchedFilm_list = user.getWatched();
		// System.out.println(watched.get(0).getItalian_title());
		for (int i = 0; i < watchedFilm_list.size(); i++) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			model_watched.addRow(
					new Object[] { watchedFilm_list.get(i).getItalian_title(), watchedFilm_list.get(i).get_rate(),
							df.format(watchedFilm_list.get(i).getAdded_on()), watchedFilm_list.get(i).getFavorite() });
		}
		// model_watched.addRow(new Object[]{"Quei Bravi Ragazzi", "8",
		// "2000-09-07",Boolean.FALSE});
		// model_watched.addRow(new Object[]{"Pulp Fiction", "9",
		// "2000-09-07",Boolean.TRUE});

		GroupLayout gl_panel_Watched = new GroupLayout(panel_Watched);
		gl_panel_Watched.setHorizontalGroup(gl_panel_Watched.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_Watched.createSequentialGroup().addContainerGap()
						.addComponent(scrollPane_Watched, GroupLayout.PREFERRED_SIZE, 548, GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		gl_panel_Watched.setVerticalGroup(gl_panel_Watched.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_Watched.createSequentialGroup().addGap(5)
						.addComponent(scrollPane_Watched, GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
						.addContainerGap()));
		panel_Watched.setLayout(gl_panel_Watched);

		DefaultTableModel model_favorite = new DefaultTableModel();

		WatchedTable.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				if (WatchedTable.columnAtPoint(me.getPoint()) == 3) {
					int row = WatchedTable.rowAtPoint(me.getPoint());
					String title = WatchedTable.getValueAt(row, 0).toString();
					boolean state = (boolean) WatchedTable.getValueAt(row, 3);
					// System.out.println(title);
					// System.out.println(state);
					List<WatchedFilm> list = new ArrayList<WatchedFilm>();
					list = user.getWatched_films();
					for (WatchedFilm film : list) {
						if (film.getItalian_title() == title)
							try {
								dao.modifyFavouriteList(film, state, user);
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
					if (state)
						model_favorite.addRow(new Object[] { title });
					else {
						// System.out.println("Cancello riga nella tabella");
						for (int j = 0; j < model_favorite.getRowCount(); j++) {
							String str = model_favorite.getValueAt(j, 0).toString();
							if (str.equals(title))
								model_favorite.removeRow(j);
						}
					}
				}
			}
		});
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

		/* PANNELLO FAVORITE FILM */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/
		JPanel panelFavorite = new JPanel();
		tabbedPane.addTab("Favorite", null, panelFavorite, null);

		JScrollPane scrollPane_Favorite = new JScrollPane();
		scrollPane_Favorite.setBorder(border);
		scrollPane_Favorite.getVerticalScrollBar().setPreferredSize(new Dimension(0, 2));
		scrollPane_Favorite.getViewport().setBackground(myGray);

		/* TABELLA FAVORITE FILM */
		FavoriteTable = new JTable();
		FavoriteTable.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		FavoriteTable.setRowSelectionAllowed(false);
		FavoriteTable.setBackground(myGray);
		FavoriteTable.setEnabled(false);
		scrollPane_Favorite.setViewportView(FavoriteTable);
		Object[] columnsName_favorite = new Object[1];

		columnsName_favorite[0] = "Title";

		model_favorite.setColumnIdentifiers(columnsName_favorite);
		FavoriteTable.setModel(model_favorite);
		FavoriteTable.setGridColor(new Color(0, 0, 0));
		FavoriteTable.setEnabled(true);

		TableColumnModel columnModel_favorite = FavoriteTable.getColumnModel();
		columnModel_favorite.getColumn(0).setResizable(false);

		/*
		 * INSERIMENTO DI FILM ALL'INTERNO DELLA LISTA DI FILM PREFERITI -> INSERIMENTO
		 * DA FARE QUANDO VIENE MESSA A TRUE LA CHECKBOX RELATIVA NELLA TABELLA DI FILM
		 * VISTI
		 */
		for (int i = 0; i < watchedFilm_list.size(); i++) {
			if (watchedFilm_list.get(i).getFavorite()) {
				model_favorite.addRow(new Object[] { watchedFilm_list.get(i).getItalian_title() });
			}
		}

		/* LAYOUT PANNELLO FAVORITE FILM */
		GroupLayout gl_panelFavorite = new GroupLayout(panelFavorite);
		gl_panelFavorite.setHorizontalGroup(gl_panelFavorite.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelFavorite.createSequentialGroup().addContainerGap()
						.addComponent(scrollPane_Favorite, GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
						.addContainerGap()));
		gl_panelFavorite.setVerticalGroup(gl_panelFavorite.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelFavorite.createSequentialGroup().addContainerGap()
						.addComponent(scrollPane_Favorite, GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
						.addContainerGap()));
		panelFavorite.setLayout(gl_panelFavorite);
		FavoriteTable.setEnabled(false);
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

		/* PANEL REQUEST A MOVIE TO THE ADMIN */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/
		JPanel panelRequest = new JPanel();
		tabbedPane.addTab("Request", null, panelRequest, null);

		JLabel label_ask = new JLabel("Didn't find your movie?");
		label_ask.setFont(new Font("Lucida Grande", Font.PLAIN, 19));

		JLabel label_answer = new JLabel("Send us a request!");
		label_answer.setFont(new Font("Lucida Grande", Font.PLAIN, 16));

		textField_Title = new JTextField();
		textField_Title.setColumns(10);

		JLabel lbl_Title = new JLabel("Title");

		JPanel panel_Request_History = new JPanel();
		panel_Request_History.setBorder(null);

		JButton btn_Request = new JButton("Send");

		JLabel lbl_History_Request_tag = new JLabel("Request history");
		lbl_History_Request_tag.setFont(new Font("Lucida Grande", Font.BOLD, 19));
		GroupLayout gl_panelRequest = new GroupLayout(panelRequest);
		gl_panelRequest.setHorizontalGroup(gl_panelRequest.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelRequest.createSequentialGroup().addGroup(gl_panelRequest
						.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelRequest.createSequentialGroup().addGap(169).addComponent(label_ask,
								GroupLayout.PREFERRED_SIZE, 248, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panelRequest.createSequentialGroup().addGap(245).addComponent(label_answer))
						.addGroup(gl_panelRequest.createSequentialGroup().addGap(135).addComponent(lbl_Title).addGap(6)
								.addComponent(textField_Title, GroupLayout.PREFERRED_SIZE, 222,
										GroupLayout.PREFERRED_SIZE)
								.addGap(6).addComponent(btn_Request))
						.addGroup(gl_panelRequest.createSequentialGroup().addContainerGap()
								.addComponent(panel_Request_History, GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE))
						.addGroup(gl_panelRequest.createSequentialGroup().addContainerGap().addComponent(
								lbl_History_Request_tag, GroupLayout.PREFERRED_SIZE, 206, GroupLayout.PREFERRED_SIZE)))
						.addContainerGap()));
		gl_panelRequest.setVerticalGroup(gl_panelRequest.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelRequest.createSequentialGroup().addGap(19)
						.addComponent(label_ask, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE).addGap(6)
						.addComponent(label_answer).addGap(72)
						.addGroup(gl_panelRequest.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelRequest.createSequentialGroup().addGap(5).addComponent(lbl_Title))
								.addComponent(textField_Title, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(btn_Request))
						.addPreferredGap(ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
						.addComponent(lbl_History_Request_tag).addGap(18).addComponent(panel_Request_History,
								GroupLayout.PREFERRED_SIZE, 306, GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));

		JScrollPane scrollPane_Request_History = new JScrollPane();
		scrollPane_Request_History.setBorder(border);
		scrollPane_Request_History.getVerticalScrollBar().setPreferredSize(new Dimension(0, 2));
		scrollPane_Request_History.getViewport().setBackground(myGray);

		RequestHistoryTable = new JTable();
		RequestHistoryTable.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		RequestHistoryTable.setRowSelectionAllowed(false);
		RequestHistoryTable.setBackground(myGray);
		scrollPane_Request_History.setViewportView(RequestHistoryTable);
		final DefaultTableModel model_RequestHistory = new DefaultTableModel();
		Object[] columnsName_RequestHistory = new Object[3];

		columnsName_RequestHistory[0] = "Title";
		columnsName_RequestHistory[1] = "Status";
		columnsName_RequestHistory[2] = "Date";

		model_RequestHistory.setColumnIdentifiers(columnsName_RequestHistory);
		RequestHistoryTable.setModel(model_RequestHistory);
		RequestHistoryTable.setGridColor(new Color(0, 0, 0));
		RequestHistoryTable.setEnabled(false);

		TableColumnModel columnModel_RequestHistory = RequestHistoryTable.getColumnModel();
		columnModel_RequestHistory.getColumn(0).setResizable(false);
		columnModel_RequestHistory.getColumn(1).setResizable(false);
		columnModel_RequestHistory.getColumn(2).setResizable(false);

		/* INSERIMENTI DELLE RICHIESTE NELLA TABELLA DELLE RICHIESTE */
		List<Request> requests_list = user.getRequest();
		// System.out.println(watched.get(0).getItalian_title());
		for (int i = 0; i < requests_list.size(); i++) {
			String status;
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			switch (requests_list.get(i).getStatus()) {
			case 1:
				status = "waiting";
				break;
			case 2:
				status = "approved";
				break;
			default:
				status = "rejected";
			}
			model_RequestHistory.addRow(new Object[] { requests_list.get(i).get_FilmTitle(), status,
					df.format(requests_list.get(i).getDate()) });
		}

		/* Event Listener Bottone Send */
		btn_Request.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String italianTitle = textField_Title.getText();
				boolean foundRequest = dao.checkRequest(user, italianTitle);
				if (foundRequest == false) {
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					Date today = Calendar.getInstance().getTime();
					boolean result = false;
					try {
						result = user.add_request(italianTitle, dao);
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
					if (result) {
						model_RequestHistory.addRow(new Object[] { italianTitle, "waiting", df.format(today) });
						System.out.println("Request succesfully added");
					} else {
						System.out.println("Request Failed !");
					}
				} else {
					JOptionPane.showMessageDialog(scrollPane_Request_History, "Request already made");
					textField_Title.setText("");
				}
			}
		});

		GroupLayout gl_panel_Request_History = new GroupLayout(panel_Request_History);
		gl_panel_Request_History.setHorizontalGroup(gl_panel_Request_History.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_Request_History.createSequentialGroup().addContainerGap()
						.addComponent(scrollPane_Request_History, GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
						.addContainerGap()));
		gl_panel_Request_History.setVerticalGroup(gl_panel_Request_History.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_Request_History.createSequentialGroup().addContainerGap()
						.addComponent(scrollPane_Request_History, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
						.addContainerGap()));
		panel_Request_History.setLayout(gl_panel_Request_History);
		panelRequest.setLayout(gl_panelRequest);
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

		/* PANEL MOST VIEWED BY GENRE */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/
		final JPanel panelMostByGenre = new JPanel();
		tabbedPane.addTab("10 by Genre", null, panelMostByGenre, null);
		tabbedPane.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				if (e.getSource() instanceof JTabbedPane) {
					JTabbedPane pane = (JTabbedPane) e.getSource();
					// System.out.println("Selected paneNo : " + pane.getSelectedIndex());
					if (pane.getSelectedIndex() == 5 && statistics1_done == 0) {
						System.out.println("Calcolo le statistiche..."); // La statistica viene calcolata solo la prima
																			// volta che si clicca
						statistics1_done = 1;

						JScrollPane scrollPane_Container_Movies_Genre = new JScrollPane();
						scrollPane_Container_Movies_Genre.setBorder(border);
						scrollPane_Container_Movies_Genre.getVerticalScrollBar().setPreferredSize(new Dimension(0, 2));
						scrollPane_Container_Movies_Genre.getViewport().setBackground(myGray);

						/* LAYOUT MOST VIEWED BY GENRE */
						GroupLayout gl_panelMostByGenre = new GroupLayout(panelMostByGenre);
						gl_panelMostByGenre
								.setHorizontalGroup(gl_panelMostByGenre.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_panelMostByGenre.createSequentialGroup().addContainerGap()
												.addComponent(scrollPane_Container_Movies_Genre,
														GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
												.addContainerGap()));
						gl_panelMostByGenre
								.setVerticalGroup(gl_panelMostByGenre.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_panelMostByGenre.createSequentialGroup().addContainerGap()
												.addComponent(scrollPane_Container_Movies_Genre,
														GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
												.addContainerGap()));

						JPanel panelMovies_By_Genre = new JPanel();
						scrollPane_Container_Movies_Genre.setViewportView(panelMovies_By_Genre);
						panelMostByGenre.setLayout(gl_panelMostByGenre);

						panelMovies_By_Genre.setBorder(null);
						scrollPane_Container_Movies_Genre.setViewportView(panelMovies_By_Genre);
						panelMovies_By_Genre.setBackground(myGray);
						panelMovies_By_Genre.setLayout(new BoxLayout(panelMovies_By_Genre, BoxLayout.Y_AXIS));
						scrollPane_Container_Movies_Genre.getVerticalScrollBar().setPreferredSize(new Dimension(3, 1));

						String[] genres = new String[0];
						try {
							genres = dao.getGenres();
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

						for (String genre : genres) {
							JLabel i_tag = new JLabel(genre);
							i_tag.setAlignmentX(Component.CENTER_ALIGNMENT);
							panelMovies_By_Genre.add(i_tag);
							i_tag.setFont(new Font("Lucida Grande", Font.BOLD, 18));
							panelMovies_By_Genre.add(Box.createVerticalStrut(20));

							JPanel i_panel = new JPanel();
							i_panel.setBackground(myGray);
							i_panel.setLayout(new BoxLayout(i_panel, BoxLayout.Y_AXIS));
							panelMovies_By_Genre.add(i_panel);

							JScrollPane ScrollPane_i = new JScrollPane();
							ScrollPane_i.setBorder(border);
							ScrollPane_i.getViewport().setBackground(myGray);
							ScrollPane_i.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

							JTable i_Table = new JTable();
							i_Table.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
							i_Table.setRowSelectionAllowed(false);
							i_Table.setBackground(myGray);
							ScrollPane_i.setViewportView(i_Table);
							DefaultTableModel model_i = new DefaultTableModel();
							Object[] columnsName_i = new Object[2];

							columnsName_i[0] = "Pos";
							columnsName_i[1] = "Title";

							model_i.setColumnIdentifiers(columnsName_i);
							i_Table.setModel(model_i);
							i_Table.setGridColor(new Color(0, 0, 0));
							i_Table.setEnabled(false);

							TableColumnModel columnModel_i = i_Table.getColumnModel();
							columnModel_i.getColumn(0).setResizable(false);
							columnModel_i.getColumn(1).setResizable(false);
							columnModel_i.getColumn(0).setPreferredWidth(40);
							columnModel_i.getColumn(1).setPreferredWidth(1000);
							String title = "";
							List<Document> standings = new ArrayList<>();
							try {
								standings = dao.mostWatchedByGenre(genre);
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

							/*
							 * INSERIMENTO DI FILM ALL'INTERNO DELLA LISTA DEI 10 FILM PIU' VISTI PER GENERE
							 */
							for (int i = 0; i < (10 > standings.size() ? standings.size() : 10); i++) {
								org.bson.Document docGen = standings.get(i);
								title = (((org.bson.Document) docGen.get("_id")).getString("italian_title"));
								model_i.addRow(new Object[] { i + 1, title });
							}

							GroupLayout gl_i_panel = new GroupLayout(i_panel);
							gl_i_panel.setHorizontalGroup(
									gl_i_panel.createParallelGroup(Alignment.LEADING).addComponent(ScrollPane_i,
											GroupLayout.PREFERRED_SIZE, 533, GroupLayout.PREFERRED_SIZE));
							gl_i_panel.setVerticalGroup(gl_i_panel.createParallelGroup(Alignment.LEADING).addComponent(
									ScrollPane_i, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE));
							i_panel.setLayout(gl_i_panel);
						}

					}
				}
			}
		});

		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

		/* PANEL other users  */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/
		JPanel panelOtherUsers = new JPanel();
		tabbedPane.addTab("Seen by others", null, panelOtherUsers, null);
		
		JScrollPane scrollPaneOtherUsers = new JScrollPane();
		GroupLayout gl_panelOtherUsers = new GroupLayout(panelOtherUsers);
		gl_panelOtherUsers.setHorizontalGroup(
			gl_panelOtherUsers.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelOtherUsers.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPaneOtherUsers, GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panelOtherUsers.setVerticalGroup(
			gl_panelOtherUsers.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panelOtherUsers.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPaneOtherUsers, GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
					.addContainerGap())
		);
		panelOtherUsers.setLayout(gl_panelOtherUsers);
		
		scrollPaneOtherUsers.setBorder(border);
		scrollPaneOtherUsers.getVerticalScrollBar().setPreferredSize(new Dimension(0, 2));
		scrollPaneOtherUsers.getViewport().setBackground(myGray);

		OtherUsersTable = new JTable();
		OtherUsersTable.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		OtherUsersTable.setRowSelectionAllowed(false);
		OtherUsersTable.setBackground(myGray);
		OtherUsersTable.setEnabled(false);
		scrollPaneOtherUsers.setViewportView(OtherUsersTable);

		model_OtherUsers = new DefaultTableModel();
		Object[] columnsName_OtherUsers = new Object[2];

		columnsName_OtherUsers[0] = "Title";

		model_OtherUsers.setColumnIdentifiers(columnsName_OtherUsers);
		OtherUsersTable.setModel(model_OtherUsers);
		OtherUsersTable.setGridColor(new Color(0, 0, 0));
		OtherUsersTable.setEnabled(false);

		TableColumnModel columnModel_OtherUsers = OtherUsersTable.getColumnModel();
		columnModel_OtherUsers.getColumn(0).setResizable(false);
				
		advicesByOtherUsers = dao.similarRating(user.getUsername());
		
		Object[] rowData_adviceByOtherUsers = new Object[2];
		
		for(BasicFilm adviceByUser : advicesByOtherUsers) {
			rowData_adviceByOtherUsers[0] = adviceByUser.getItalian_title();
			rowData_adviceByOtherUsers[1] = adviceByUser.getFilm_id();
			model_OtherUsers.addRow(rowData_adviceByOtherUsers);
		}
		
		OtherUsersTable.removeColumn(columnModel_OtherUsers.getColumn(1)); // REMOVE
		
		
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/
		
		/* PANEL Suggest by cast  */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/
		JPanel panelSuggestByCast = new JPanel();
		tabbedPane.addTab("Because you watched ", null, panelSuggestByCast, null);
		
		JScrollPane scrollPaneByCast = new JScrollPane();
		GroupLayout gl_panelSuggestByCast = new GroupLayout(panelSuggestByCast);
		gl_panelSuggestByCast.setHorizontalGroup(
			gl_panelSuggestByCast.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelSuggestByCast.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPaneByCast, GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panelSuggestByCast.setVerticalGroup(
			gl_panelSuggestByCast.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panelSuggestByCast.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPaneByCast, GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
					.addContainerGap())
		);
		panelSuggestByCast.setLayout(gl_panelSuggestByCast);

		scrollPaneByCast.setBorder(border);
		scrollPaneByCast.getVerticalScrollBar().setPreferredSize(new Dimension(0, 2));
		scrollPaneByCast.getViewport().setBackground(myGray);
		
		suggestByCastTable = new JTable();
		suggestByCastTable.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		suggestByCastTable.setRowSelectionAllowed(false);
		suggestByCastTable.setBackground(myGray);
		suggestByCastTable.setEnabled(false);
		scrollPaneByCast.setViewportView(suggestByCastTable);

		model_suggestByCast = new DefaultTableModel();
		Object[] columnsName_suggestByCast = new Object[2];

		columnsName_suggestByCast[0] = "Title";

		model_suggestByCast.setColumnIdentifiers(columnsName_suggestByCast);
		suggestByCastTable.setModel(model_suggestByCast);
		suggestByCastTable.setGridColor(new Color(0, 0, 0));
		suggestByCastTable.setEnabled(false);

		TableColumnModel columnModel_suggestByCast = suggestByCastTable.getColumnModel();
		columnModel_suggestByCast.getColumn(0).setResizable(false);
		
		advicesByActors = dao.sameActors(user.getUsername());
		advicesByDirectors = dao.sameDirectors(user.getUsername());
		
		Object[] rowData_adviceByCast = new Object[2];
		
		for(BasicFilm adviceByActor : advicesByActors) {
			rowData_adviceByCast[0] = adviceByActor.getItalian_title();
			rowData_adviceByCast[1] = adviceByActor.getFilm_id();
			model_suggestByCast.addRow(rowData_adviceByCast);
		}
		
		for(BasicFilm adviceByDirector : advicesByDirectors) {
			rowData_adviceByCast[0] = adviceByDirector.getItalian_title();
			rowData_adviceByCast[1] = adviceByDirector.getFilm_id();
			model_suggestByCast.addRow(rowData_adviceByCast);
		}
		
		suggestByCastTable.removeColumn(columnModel_suggestByCast.getColumn(1)); // REMOVE
		
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/
		
		/* PANEL MOST VIEWED IN THE LAST MONTH */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/
		final JPanel Most_In_Last_month = new JPanel();
		tabbedPane.addTab("Popular", null, Most_In_Last_month, null);
		
		tabbedPane.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				if (e.getSource() instanceof JTabbedPane) {
					JTabbedPane pane = (JTabbedPane) e.getSource();
					// System.out.println("Selected paneNo : " + pane.getSelectedIndex());
					if (pane.getSelectedIndex() == 6 && statistics2_done == 0) {
						System.out.println("calcolo le statistiche sui film recenti...");
						// Map<String, org.bson.Document> standings = new HashMap <>();
						List<String> standings = new ArrayList<>();
						try {
							standings = dao.mostPopular();
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
						statistics2_done = 1;

						JScrollPane scrollPane_Month_Movies = new JScrollPane();
						scrollPane_Month_Movies.setBorder(border);
						scrollPane_Month_Movies.getVerticalScrollBar().setPreferredSize(new Dimension(0, 2));
						scrollPane_Month_Movies.getViewport().setBackground(myGray);

						MostViewed_LastMonthTable = new JTable();
						MostViewed_LastMonthTable.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
						MostViewed_LastMonthTable.setRowSelectionAllowed(false);
						MostViewed_LastMonthTable.setBackground(myGray);
						MostViewed_LastMonthTable.setEnabled(false);
						scrollPane_Month_Movies.setViewportView(MostViewed_LastMonthTable);
						DefaultTableModel model_month_movies = new DefaultTableModel();
						Object[] columnsName_month_movies = new Object[1];

						columnsName_month_movies[0] = "Title";

						model_month_movies.setColumnIdentifiers(columnsName_month_movies);
						MostViewed_LastMonthTable.setModel(model_month_movies);
						MostViewed_LastMonthTable.setGridColor(new Color(0, 0, 0));
						MostViewed_LastMonthTable.setEnabled(true);

						TableColumnModel columnModel_month_movies = FavoriteTable.getColumnModel();
						columnModel_month_movies.getColumn(0).setResizable(false);

						/*
						 * INSERIMENTO DI FILM ALL'INTERNO DELLA LISTA DI FILM PIU' VISTI NELL'ULTIMO
						 * MESE
						 */
						for (int i = 0; i < standings.size(); i++) {
							model_month_movies.addRow(new Object[] { standings.get(i) });
						}

						/* LAYOUT PANELL MOST VIEWED IN THE LAST MONTH */
						GroupLayout gl_Most_In_Last_month = new GroupLayout(Most_In_Last_month);
						gl_Most_In_Last_month
								.setHorizontalGroup(
										gl_Most_In_Last_month.createParallelGroup(Alignment.LEADING)
												.addGroup(gl_Most_In_Last_month.createSequentialGroup()
														.addContainerGap().addComponent(scrollPane_Month_Movies,
																GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
														.addContainerGap()));
						gl_Most_In_Last_month
								.setVerticalGroup(
										gl_Most_In_Last_month.createParallelGroup(Alignment.LEADING)
												.addGroup(gl_Most_In_Last_month.createSequentialGroup()
														.addContainerGap().addComponent(scrollPane_Month_Movies,
																GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
														.addContainerGap()));
						Most_In_Last_month.setLayout(gl_Most_In_Last_month);
						MostViewed_LastMonthTable.setEnabled(false);
					}
				}
			}
		});

		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

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
		tabbedPane_1.addTab("Details", null, panelMovieDetails, null);

		JPanel panelAwards = new JPanel();
		GroupLayout gl_panelMovieDetails = new GroupLayout(panelMovieDetails);
		gl_panelMovieDetails.setHorizontalGroup(
			gl_panelMovieDetails.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panelMovieDetails.createSequentialGroup()
					.addContainerGap(413, Short.MAX_VALUE)
					.addComponent(panelAwards, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE))
		);
		gl_panelMovieDetails.setVerticalGroup(
			gl_panelMovieDetails.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelMovieDetails.createSequentialGroup()
					.addGap(187)
					.addComponent(panelAwards, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(309, Short.MAX_VALUE))
		);

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
		tabbedPane_1.setBackgroundAt(0, myGray);

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

		final JLabel lbl_Imdb_tag = new JLabel("IMDb");
		lbl_Imdb_tag.setFont(new Font("Lucida Grande", Font.BOLD, 13));

		final JLabel lblCoomingSoon_tag = new JLabel("Coming Soon");
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

		// Listener per poter aggiungere film alla lista di film visti
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String italianTitle = CurrentFilm.getItalian_title();
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				int given_rate = Integer.parseInt(rate_movie_combo_box.getSelectedItem().toString());
				Date today = Calendar.getInstance().getTime();
				boolean result = false;
				try {
					result = user.add_watchedFilm(CurrentFilm.getFilm_id(), CurrentFilm.getItalian_title(), given_rate,
							dao);
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
				if (result) {
					model_watched.addRow(new Object[] { italianTitle, given_rate, df.format(today), Boolean.FALSE });
					button.setEnabled(false);
					
					//Aggiorno statistica SEEN BY OTHERS
					for (int i = model_OtherUsers.getRowCount() - 1; i >= 0; i--)
						model_OtherUsers.removeRow(i);
					
					advicesByOtherUsers.clear();
					advicesByOtherUsers = dao.similarRating(user.getUsername());
					
					Object[] rowData_adviceByOtherUsers = new Object[2];
					
					for(BasicFilm adviceByUser : advicesByOtherUsers) {
						rowData_adviceByOtherUsers[0] = adviceByUser.getItalian_title();
						rowData_adviceByOtherUsers[1] = adviceByUser.getFilm_id();
						model_OtherUsers.addRow(rowData_adviceByOtherUsers);
					}
					
					
					// Aggiorno statistica BECAUSE YOU WATCHED
					for (int i = model_suggestByCast.getRowCount() - 1; i >= 0; i--)
						model_suggestByCast.removeRow(i);
					
					advicesByActors.clear();
					advicesByDirectors.clear();
					advicesByActors = dao.sameActors(user.getUsername());
					advicesByDirectors = dao.sameDirectors(user.getUsername());
					
					Object[] rowData_adviceByCast = new Object[2];
					
					for(BasicFilm adviceByActor : advicesByActors) {
						rowData_adviceByCast[0] = adviceByActor.getItalian_title();
						rowData_adviceByCast[1] = adviceByActor.getFilm_id();
						model_suggestByCast.addRow(rowData_adviceByCast);
					}
					
					for(BasicFilm adviceByDirector : advicesByDirectors) {
						rowData_adviceByCast[0] = adviceByDirector.getItalian_title();
						rowData_adviceByCast[1] = adviceByDirector.getFilm_id();
						model_suggestByCast.addRow(rowData_adviceByCast);
					}

					System.out.println("Film succesfully added to watched list");
				} else {
					System.out.println("Film not added !");
				}
			}
		});

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

		/* Add COMMENTS PANEL */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

		JPanel panelAddComment = new JPanel();
		tabbedPane_1.addTab("Add a comment", null, panelAddComment, null);

		JScrollPane scrollPane_AddComment = new JScrollPane();

		JLabel lblAddComment = new JLabel("Review this movie!");
		lblAddComment.setFont(new Font("Lucida Grande", Font.PLAIN, 19));

		JButton btnAddComment = new JButton("Add");
		GroupLayout gl_panelAddComment = new GroupLayout(panelAddComment);
		gl_panelAddComment.setHorizontalGroup(gl_panelAddComment.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelAddComment.createSequentialGroup().addContainerGap()
						.addGroup(gl_panelAddComment.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelAddComment.createParallelGroup(Alignment.TRAILING)
										.addGroup(gl_panelAddComment.createSequentialGroup()
												.addComponent(scrollPane_AddComment, GroupLayout.DEFAULT_SIZE, 555,
														Short.MAX_VALUE)
												.addContainerGap())
										.addGroup(gl_panelAddComment.createSequentialGroup()
												.addComponent(lblAddComment, GroupLayout.PREFERRED_SIZE, 191,
														GroupLayout.PREFERRED_SIZE)
												.addGap(169)))
								.addGroup(Alignment.TRAILING, gl_panelAddComment.createSequentialGroup()
										.addComponent(btnAddComment).addGap(229)))));
		gl_panelAddComment.setVerticalGroup(gl_panelAddComment.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelAddComment.createSequentialGroup().addGap(27)
						.addComponent(lblAddComment, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
						.addGap(34)
						.addComponent(scrollPane_AddComment, GroupLayout.PREFERRED_SIZE, 265,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED).addComponent(btnAddComment)
						.addContainerGap(219, Short.MAX_VALUE)));

		JTextArea textAreaAddComment = new JTextArea();
		textAreaAddComment.setLineWrap(true);
		textAreaAddComment.setWrapStyleWord(true);
		scrollPane_AddComment.setViewportView(textAreaAddComment);
		panelAddComment.setLayout(gl_panelAddComment);

		btnAddComment.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = textAreaAddComment.getText().strip();
				if (text != "") {
					try {
						user.add_comment(CurrentFilm.getFilm_id(), text, dao);
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

					// Aggiunta commento al pannello utente

					JPanel your_comment_i = new JPanel(new FlowLayout(FlowLayout.LEFT));
					your_comment_i.setBackground(myGray);
					your_comment_i.setLayout(new BoxLayout(your_comment_i, BoxLayout.Y_AXIS));
					panel_Your_Comments.add(your_comment_i);

					JLabel film_your_comment_tag = new JLabel("Movie: \n\n\n\n\n\n");
					String movieString = "";
					try {
						movieString = dao.getBasicFilm(CurrentFilm.getFilm_id()).getTitle();
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
					JLabel film_your_comment_text = new JLabel(movieString);
					JLabel film_your_comment_container = new JLabel(
							film_your_comment_tag.getText() + film_your_comment_text.getText());
					your_comment_i.add(film_your_comment_container);

					JLabel date_your_comment_tag = new JLabel("Date: \n\n\n\n\n");
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					JLabel date_your_comment_text = new JLabel(df.format(Calendar.getInstance().getTime()));
					JLabel date_your_comment_container = new JLabel(
							date_your_comment_tag.getText() + date_your_comment_text.getText());
					your_comment_i.add(date_your_comment_container);

					panel_Your_Comments.add(Box.createVerticalStrut(5));

					JLabel your_comment_tag = new JLabel("Comment:");
					your_comment_i.add(your_comment_tag);

					panel_Your_Comments.add(Box.createVerticalStrut(3));

					JScrollPane scrollPane_your_comment_i = new JScrollPane();
					float align_your_comment_x = your_comment_i.getAlignmentX();
					scrollPane_your_comment_i.setAlignmentX(align_your_comment_x);
					scrollPane_your_comment_i.getViewport().setBackground(myGray);
					JTextArea textArea_your_comment_i = new JTextArea(6, 22);
					textArea_your_comment_i.setEditable(false);
					scrollPane_your_comment_i.setViewportView(textArea_your_comment_i);
					textArea_your_comment_i.setBackground(myGray);
					textArea_your_comment_i.setText(text);
					textArea_your_comment_i.setWrapStyleWord(true);
					textArea_your_comment_i.setLineWrap(true);
					scrollPane_your_comment_i.getVerticalScrollBar().setPreferredSize(new Dimension(2, 0));
					textArea_your_comment_i.setCaretPosition(0);
					your_comment_i.add(scrollPane_your_comment_i);

					your_comment_i.add(Box.createVerticalStrut(5));

					JButton btnDelete = new JButton("Delete");
					your_comment_i.add(btnDelete);

					btnDelete.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								user.deleteComment(
										user.getUser_comments().get(user.getUser_comments().size() - 1).getComment_id(),
										dao);
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
							panel_Your_Comments.remove(your_comment_i);
							panel_Your_Comments.revalidate();
							panel_Your_Comments.repaint();
						}
					});

					your_comment_i.add(Box.createVerticalStrut(15));

					JSeparator your_comment_separator = new JSeparator(SwingConstants.HORIZONTAL);
					your_comment_separator.setPreferredSize(new Dimension(350, 15));
					your_comment_i.add(your_comment_separator);

					your_comment_i.add(Box.createVerticalStrut(15));

					panel_Your_Comments.revalidate();
					panel_Your_Comments.repaint();
				}
				textAreaAddComment.setText(null);
			}
		});

		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

		/* MOVIE'S COMMENTS PANEL */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

		JPanel panelMovieComments = new JPanel();
		tabbedPane_1.addTab("Comments", null, panelMovieComments, null);
		panelMovieComments.setLayout(null);
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/
		
		/* MOVIE'S LIKE THIS PANEL */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/
		
		JPanel panelLikeThis = new JPanel();
		tabbedPane_1.addTab("More like this ", null, panelLikeThis, null);
		
		JScrollPane scrollPaneLikeThis = new JScrollPane();
		GroupLayout gl_panelLikeThis = new GroupLayout(panelLikeThis);
		gl_panelLikeThis.setHorizontalGroup(
			gl_panelLikeThis.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelLikeThis.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPaneLikeThis, GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panelLikeThis.setVerticalGroup(
			gl_panelLikeThis.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panelLikeThis.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPaneLikeThis, GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
					.addContainerGap())
		);
		panelLikeThis.setLayout(gl_panelLikeThis);
		
		scrollPaneLikeThis.setBorder(border);
		scrollPaneLikeThis.getVerticalScrollBar().setPreferredSize(new Dimension(0, 2));
		scrollPaneLikeThis.getViewport().setBackground(myGray);
		
		MoreLikeThisTable = new JTable();
		MoreLikeThisTable.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		MoreLikeThisTable.setRowSelectionAllowed(false);
		MoreLikeThisTable.setBackground(myGray);
		MoreLikeThisTable.setEnabled(false);
		scrollPaneLikeThis.setViewportView(MoreLikeThisTable);

		model_moreLikeThis = new DefaultTableModel();
		Object[] columnsName_moreLikeThis = new Object[1];

		columnsName_moreLikeThis[0] = "Title";

		model_moreLikeThis.setColumnIdentifiers(columnsName_moreLikeThis);
		MoreLikeThisTable.setModel(model_moreLikeThis);
		MoreLikeThisTable.setGridColor(new Color(0, 0, 0));
		MoreLikeThisTable.setEnabled(false);

		TableColumnModel columnModel_moreLikeThis = MoreLikeThisTable.getColumnModel();
		columnModel_moreLikeThis.getColumn(0).setResizable(false);

		
		//List<Actor> cast = film.getCast();
		//Object[] rowData_actors = new Object[40];
		/* INSERIMENTO DEGLI ATTORI ALL'INTERNO DELLA TABELLA DI ATTORI */
		/*for (Actor actor : cast) {
			rowData_actors[0] = actor.getName();
			rowData_actors[1] = actor.getRole();
			model_actors.addRow(rowData_actors);
		}
		*/


		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/

		// Quanto segue viene fatto per poter prendere la riga sulla quale l'utente ha
		// cliccato e di conseguenza
		// mostrare nella parte destra della pagina le informazioni relative al film
		// cliccato dall'utente

		/* EVENT LISTENER TABELLA MOVIES */
		/*------------------------------------------------------------------------------------------------------------------------------------------------------*/
		MoviesTable.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				indexComment = 0;
				previndexComment = -1;
				// CurrentFilm.getComments().clear();
				try {

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
						CurrentFilm = film;
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

					button.setEnabled(true);
					for (int z = 0; z < watchedFilm_list.size(); z++) {
						if (film.getFilm_id().equals(watchedFilm_list.get(z).getFilm_id()) == true) {
							button.setEnabled(false);
							break;
						}
					}
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
					
					//System.out.println(ActorsTable.getModel().getValueAt(0, 1));  // notice I'm using "getModel()"
					
					/* Inserimento film consigliati in base al film corrente*/
					for (int i = model_moreLikeThis.getRowCount() - 1; i >= 0; i--)
						model_moreLikeThis.removeRow(i);
					
					List<String> advices = dao.moreLikeThis(CurrentFilm.getFilm_id());
					
					Object[] rowData_advice = new Object[1];
					
					for(String advice : advices) {
						rowData_advice[0] = advice;
						model_moreLikeThis.addRow(rowData_advice);
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

					// Necessario per poter aggiornare il pannello dei commenti relativi al film
					// selezionato
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
								indexComment += 10;
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

							if (CurrentFilm.getComments().size() % 10 != 0)
								LoadMoreButton.setEnabled(false);

							if (CurrentFilm.getComments().size() == previndexComment) {
								LoadMoreButton.setEnabled(false);
								// panelComments.remove(LoadMoreButton);
								// System.out.println("remove comm");
							}
							// for films with no comments
							if (CurrentFilm.getComments().size() == 0) {

								scrollPane_Container_Comments
										.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
								JPanel comment_zero = new JPanel(new FlowLayout(FlowLayout.LEFT));
								// comment_zero.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
								comment_zero.setBackground(myGray);
								comment_zero.setLayout(new BoxLayout(comment_zero, BoxLayout.Y_AXIS));
								// panelMovieComments.add(comment_zero);

								JLabel no_comment = new JLabel("This movie does not have any comments.");

								panelComments.add(no_comment);

								panelMovieComments.revalidate();
								panelMovieComments.repaint();

							}

							else {

								System.out.println("Carico altri 10 commenti...");
								System.out.println(CurrentFilm.getComments().size());

								for (; i < CurrentFilm.getComments().size(); i++) {
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
									JLabel test = new JLabel(String.valueOf(i));
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

									// Viene controllato se l'utente ha messo like o dislike precedentemente al
									// seguente commento
									int check_myMovies = 0;
									try {
										check_myMovies = user.checkComment(CurrentFilm.getComments().get(i), dao);
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

									JButton btnLike = new JButton("Like");
									comment_i.add(btnLike);
									JButton btnDisLike = new JButton("Dislike");
									comment_i.add(btnDisLike);

									if (check_myMovies == 1) {
										btnLike.setEnabled(false);
									} else if (check_myMovies == -1) {
										btnDisLike.setEnabled(false);
									}

									// Event listener button like e dislike dei commenti myMovies
									btnLike.addActionListener(createActionListener(btnLike, btnDisLike,
											CurrentFilm.getComments().get(i), user, +1, dao, comment_points_text,
											comment_points_container, comment_i));
									btnDisLike.addActionListener(createActionListener(btnLike, btnDisLike,
											CurrentFilm.getComments().get(i), user, -1, dao, comment_points_text,
											comment_points_container, comment_i));

									comment_i.add(Box.createVerticalStrut(15));

									JSeparator comment_separator = new JSeparator(SwingConstants.HORIZONTAL);
									comment_separator.setPreferredSize(new Dimension(350, 15));
									comment_i.add(comment_separator);

									/*
									 * if( i != 4) { JSeparator comment_separator = new
									 * JSeparator(SwingConstants.HORIZONTAL); comment_separator.setPreferredSize(new
									 * Dimension(350,15)); comment_i.add(comment_separator); }
									 */

									comment_i.add(Box.createVerticalStrut(15));

									if (i == CurrentFilm.getComments().size() - 1) {

										panelComments.add(LoadMoreButton);
										panelComments.add(Box.createVerticalStrut(15));
										// LoadMoreButton_MYMOVIES.addActionListener(createActionListenerLoad(CurrentFilm,
										// dao));

										previndexComment = CurrentFilm.getComments().size();

									}

									panelMovieComments.revalidate();
									panelMovieComments.repaint();

								}
							}
						}
					});

					LoadMoreButton.doClick(); // Serve per caricare i primi 10 commenti una volta che l'utente clicca su
												// un nuovo film facendo attivare il listener della loadMore

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

			}
		});
		
		OtherUsersTable.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				indexComment = 0;
				previndexComment = -1;
				// CurrentFilm.getComments().clear();
				try {

					tabbedPane_1.setEnabled(true);

					if (created == 1) {
						panelMovieCast.removeAll();
						CurrentFilm.setComments(new ArrayList<Comment>());
					}
					panelPoster.removeAll();
					panelPoster.validate();
					panelPoster.repaint();
					int row = OtherUsersTable.rowAtPoint(me.getPoint());
					//String title = MoviesTable.getValueAt(row, 0).toString();
					//String year = MoviesTable.getValueAt(row, 1).toString();

					// System.out.println("You clicked at row " + row);

					//System.out.println(ActorsTable.getModel().getValueAt(0, 1));  // notice I'm using "getModel()"
					
					Film film = new Film();
					try {
						film = dao.get_film(OtherUsersTable.getModel().getValueAt(row, 1).toString()); // chiamo il metodo film per
																							// avere l'oggetto con tutte
																							// le informazioni
																							// necessarie
						CurrentFilm = film;
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

					JLabel title_it = new JLabel(film.getItalian_title());
					title_it.setFont(new Font("Lucida Grande", Font.BOLD, 13));
					Pane_It_Title.setViewportView(title_it);
					lbl_Year_Text.setText(film.getYear().toString());

					
					button.setEnabled(true);
					for (int z = 0; z < watchedFilm_list.size(); z++) {
						if (film.getFilm_id().equals(watchedFilm_list.get(z).getFilm_id()) == true) {
							button.setEnabled(false);
							break;
						}
					}
					String title_or = film.getOriginal_title();
					Pane_En_Title.setViewportView(new JLabel(title_or));
					lbl_Year_Text.setText(film.getYear().toString());
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
					
					//System.out.println(ActorsTable.getModel().getValueAt(0, 1));  // notice I'm using "getModel()"
					
					/* Inserimento film consigliati in base al film corrente*/
					for (int i = model_moreLikeThis.getRowCount() - 1; i >= 0; i--)
						model_moreLikeThis.removeRow(i);
					
					List<String> advices = dao.moreLikeThis(CurrentFilm.getFilm_id());
					
					Object[] rowData_advice = new Object[1];
					
					for(String advice : advices) {
						rowData_advice[0] = advice;
						model_moreLikeThis.addRow(rowData_advice);
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

					// Necessario per poter aggiornare il pannello dei commenti relativi al film
					// selezionato
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
								indexComment += 10;
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

							if (CurrentFilm.getComments().size() % 10 != 0)
								LoadMoreButton.setEnabled(false);

							if (CurrentFilm.getComments().size() == previndexComment) {
								LoadMoreButton.setEnabled(false);
								// panelComments.remove(LoadMoreButton);
								// System.out.println("remove comm");
							}
							// for films with no comments
							if (CurrentFilm.getComments().size() == 0) {

								scrollPane_Container_Comments
										.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
								JPanel comment_zero = new JPanel(new FlowLayout(FlowLayout.LEFT));
								// comment_zero.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
								comment_zero.setBackground(myGray);
								comment_zero.setLayout(new BoxLayout(comment_zero, BoxLayout.Y_AXIS));
								// panelMovieComments.add(comment_zero);

								JLabel no_comment = new JLabel("This movie does not have any comments.");

								panelComments.add(no_comment);

								panelMovieComments.revalidate();
								panelMovieComments.repaint();

							}

							else {

								System.out.println("Carico altri 10 commenti...");
								System.out.println(CurrentFilm.getComments().size());

								for (; i < CurrentFilm.getComments().size(); i++) {
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
									JLabel test = new JLabel(String.valueOf(i));
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

									// Viene controllato se l'utente ha messo like o dislike precedentemente al
									// seguente commento
									int check_myMovies = 0;
									try {
										check_myMovies = user.checkComment(CurrentFilm.getComments().get(i), dao);
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

									JButton btnLike = new JButton("Like");
									comment_i.add(btnLike);
									JButton btnDisLike = new JButton("Dislike");
									comment_i.add(btnDisLike);

									if (check_myMovies == 1) {
										btnLike.setEnabled(false);
									} else if (check_myMovies == -1) {
										btnDisLike.setEnabled(false);
									}

									// Event listener button like e dislike dei commenti myMovies
									btnLike.addActionListener(createActionListener(btnLike, btnDisLike,
											CurrentFilm.getComments().get(i), user, +1, dao, comment_points_text,
											comment_points_container, comment_i));
									btnDisLike.addActionListener(createActionListener(btnLike, btnDisLike,
											CurrentFilm.getComments().get(i), user, -1, dao, comment_points_text,
											comment_points_container, comment_i));

									comment_i.add(Box.createVerticalStrut(15));

									JSeparator comment_separator = new JSeparator(SwingConstants.HORIZONTAL);
									comment_separator.setPreferredSize(new Dimension(350, 15));
									comment_i.add(comment_separator);

									/*
									 * if( i != 4) { JSeparator comment_separator = new
									 * JSeparator(SwingConstants.HORIZONTAL); comment_separator.setPreferredSize(new
									 * Dimension(350,15)); comment_i.add(comment_separator); }
									 */

									comment_i.add(Box.createVerticalStrut(15));

									if (i == CurrentFilm.getComments().size() - 1) {

										panelComments.add(LoadMoreButton);
										panelComments.add(Box.createVerticalStrut(15));
										// LoadMoreButton_MYMOVIES.addActionListener(createActionListenerLoad(CurrentFilm,
										// dao));

										previndexComment = CurrentFilm.getComments().size();

									}

									panelMovieComments.revalidate();
									panelMovieComments.repaint();

								}
							}
						}
					});

					LoadMoreButton.doClick(); // Serve per caricare i primi 10 commenti una volta che l'utente clicca su
												// un nuovo film facendo attivare il listener della loadMore

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

			}
		});
		
		suggestByCastTable.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				indexComment = 0;
				previndexComment = -1;
				// CurrentFilm.getComments().clear();
				try {

					tabbedPane_1.setEnabled(true);

					if (created == 1) {
						panelMovieCast.removeAll();
						CurrentFilm.setComments(new ArrayList<Comment>());
					}
					panelPoster.removeAll();
					panelPoster.validate();
					panelPoster.repaint();
					int row = suggestByCastTable.rowAtPoint(me.getPoint());
					//String title = MoviesTable.getValueAt(row, 0).toString();
					//String year = MoviesTable.getValueAt(row, 1).toString();

					// System.out.println("You clicked at row " + row);

					//System.out.println(ActorsTable.getModel().getValueAt(0, 1));  // notice I'm using "getModel()"
					
					Film film = new Film();
					try {
						film = dao.get_film(suggestByCastTable.getModel().getValueAt(row, 1).toString()); // chiamo il metodo film per
																							// avere l'oggetto con tutte
																							// le informazioni
																							// necessarie
						CurrentFilm = film;
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

					JLabel title_it = new JLabel(film.getItalian_title());
					title_it.setFont(new Font("Lucida Grande", Font.BOLD, 13));
					Pane_It_Title.setViewportView(title_it);
					lbl_Year_Text.setText(film.getYear().toString());

					
					button.setEnabled(true);
					for (int z = 0; z < watchedFilm_list.size(); z++) {
						if (film.getFilm_id().equals(watchedFilm_list.get(z).getFilm_id()) == true) {
							button.setEnabled(false);
							break;
						}
					}
					String title_or = film.getOriginal_title();
					Pane_En_Title.setViewportView(new JLabel(title_or));
					lbl_Year_Text.setText(film.getYear().toString());
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
					
					//System.out.println(ActorsTable.getModel().getValueAt(0, 1));  // notice I'm using "getModel()"
					
					/* Inserimento film consigliati in base al film corrente*/
					for (int i = model_moreLikeThis.getRowCount() - 1; i >= 0; i--)
						model_moreLikeThis.removeRow(i);
					
					List<String> advices = dao.moreLikeThis(CurrentFilm.getFilm_id());
					
					Object[] rowData_advice = new Object[1];
					
					for(String advice : advices) {
						rowData_advice[0] = advice;
						model_moreLikeThis.addRow(rowData_advice);
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

					// Necessario per poter aggiornare il pannello dei commenti relativi al film
					// selezionato
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
								indexComment += 10;
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

							if (CurrentFilm.getComments().size() % 10 != 0)
								LoadMoreButton.setEnabled(false);

							if (CurrentFilm.getComments().size() == previndexComment) {
								LoadMoreButton.setEnabled(false);
								// panelComments.remove(LoadMoreButton);
								// System.out.println("remove comm");
							}
							// for films with no comments
							if (CurrentFilm.getComments().size() == 0) {

								scrollPane_Container_Comments
										.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
								JPanel comment_zero = new JPanel(new FlowLayout(FlowLayout.LEFT));
								// comment_zero.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
								comment_zero.setBackground(myGray);
								comment_zero.setLayout(new BoxLayout(comment_zero, BoxLayout.Y_AXIS));
								// panelMovieComments.add(comment_zero);

								JLabel no_comment = new JLabel("This movie does not have any comments.");

								panelComments.add(no_comment);

								panelMovieComments.revalidate();
								panelMovieComments.repaint();

							}

							else {

								System.out.println("Carico altri 10 commenti...");
								System.out.println(CurrentFilm.getComments().size());

								for (; i < CurrentFilm.getComments().size(); i++) {
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
									JLabel test = new JLabel(String.valueOf(i));
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

									// Viene controllato se l'utente ha messo like o dislike precedentemente al
									// seguente commento
									int check_myMovies = 0;
									try {
										check_myMovies = user.checkComment(CurrentFilm.getComments().get(i), dao);
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

									JButton btnLike = new JButton("Like");
									comment_i.add(btnLike);
									JButton btnDisLike = new JButton("Dislike");
									comment_i.add(btnDisLike);

									if (check_myMovies == 1) {
										btnLike.setEnabled(false);
									} else if (check_myMovies == -1) {
										btnDisLike.setEnabled(false);
									}

									// Event listener button like e dislike dei commenti myMovies
									btnLike.addActionListener(createActionListener(btnLike, btnDisLike,
											CurrentFilm.getComments().get(i), user, +1, dao, comment_points_text,
											comment_points_container, comment_i));
									btnDisLike.addActionListener(createActionListener(btnLike, btnDisLike,
											CurrentFilm.getComments().get(i), user, -1, dao, comment_points_text,
											comment_points_container, comment_i));

									comment_i.add(Box.createVerticalStrut(15));

									JSeparator comment_separator = new JSeparator(SwingConstants.HORIZONTAL);
									comment_separator.setPreferredSize(new Dimension(350, 15));
									comment_i.add(comment_separator);

									/*
									 * if( i != 4) { JSeparator comment_separator = new
									 * JSeparator(SwingConstants.HORIZONTAL); comment_separator.setPreferredSize(new
									 * Dimension(350,15)); comment_i.add(comment_separator); }
									 */

									comment_i.add(Box.createVerticalStrut(15));

									if (i == CurrentFilm.getComments().size() - 1) {

										panelComments.add(LoadMoreButton);
										panelComments.add(Box.createVerticalStrut(15));
										// LoadMoreButton_MYMOVIES.addActionListener(createActionListenerLoad(CurrentFilm,
										// dao));

										previndexComment = CurrentFilm.getComments().size();

									}

									panelMovieComments.revalidate();
									panelMovieComments.repaint();

								}
							}
						}
					});

					LoadMoreButton.doClick(); // Serve per caricare i primi 10 commenti una volta che l'utente clicca su
												// un nuovo film facendo attivare il listener della loadMore

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

			}
		});


		frameLogin.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dao.exit();
			}
		});
		this.frameLogin.setVisible(true);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------------------------*/