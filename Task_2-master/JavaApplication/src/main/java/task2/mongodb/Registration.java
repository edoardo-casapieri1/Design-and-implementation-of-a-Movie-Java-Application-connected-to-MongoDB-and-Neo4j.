package task2.mongodb;

import java.awt.BorderLayout;
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
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Locale;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;

public class Registration extends JFrame {

  private JPanel contentPane;
  private JTextField uname;
  private JTextField fname;
  private JTextField pass;
  private JTextField lname;
  private JTextField year;
  private Registration reg=this;

 
  public Registration(final Dao dao) {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 610, 430);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(null);
    
    JLabel lblRegistration = new JLabel("Registration ");
    lblRegistration.setFont(new Font("Times New Roman", Font.BOLD, 22));
    lblRegistration.setBounds(236, 11, 248, 40);
    contentPane.add(lblRegistration);
    
    uname = new JTextField();
    uname.setColumns(10);
    uname.setBounds(165, 91, 298, 20);
    contentPane.add(uname);
    
    
    JLabel lblUsername = new JLabel("UserName");
    lblUsername.setBounds(81, 94, 74, 14);
    contentPane.add(lblUsername);
    
    JLabel lblFirstName = new JLabel("First Name");
    lblFirstName.setBounds(81, 125, 84, 17);
    contentPane.add(lblFirstName);
    
    fname = new JTextField();
    fname.setColumns(10);
    fname.setBounds(165, 123, 298, 20);
    contentPane.add(fname);
    
    JLabel lblLastName = new JLabel("Last Name");
    lblLastName.setBounds(81, 158, 84, 14);
    contentPane.add(lblLastName);
    
    JLabel lblPassword = new JLabel("Password");
    lblPassword.setBounds(81, 190, 74, 14);
    contentPane.add(lblPassword);
    
    pass = new JTextField();
    pass.setColumns(10);
    pass.setBounds(165, 187, 298, 20);
    contentPane.add(pass);
    
    
    
    JComboBox comboBox = new JComboBox();
    comboBox.setBounds(165, 258, 137, 22);
    contentPane.add(comboBox);
    Locale.setDefault(new Locale("ENG"));
    
    // generate list of country codes
    String[] countries = Locale.getISOCountries();
    String[] countriesnames = new String[countries.length] ;
    
    // Loop each country 
    for(int i = 0; i < countries.length; i++) { 
      
      String country = countries[i];
      Locale locale = new Locale("en", country);
         
      // Get the country name by calling getDisplayCountry()
      countriesnames[i] = locale.getDisplayCountry();
         
      
    }
    //sort list of countries
    Arrays.sort(countriesnames);
    
    comboBox.setModel(new DefaultComboBoxModel(countriesnames));
    //comboBox.setMaximumRowCount(22);
    
    
    
    //sign up event handler
    JButton btnSignUp = new JButton("Sign Up");
    btnSignUp.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
        if(pass.getText().trim().isEmpty()||fname.getText().trim().isEmpty()||lname.getText().trim().isEmpty()||uname.getText().trim().isEmpty()||year.getText().trim().isEmpty())
          JOptionPane.showMessageDialog(null,"Please fill in all the fields.",
              "Error",JOptionPane.WARNING_MESSAGE);
        else {
          
        try {
          
          User user = new User();
              user.setUsername(uname.getText().trim());

              user.setFirst_name(fname.getText().trim()); 
              user.setLast_name(lname.getText().trim()); 
              user.setPassword(pass.getText());
              user.setCountry(comboBox.getSelectedItem().toString());
              //try {
              Integer YOB=Integer.parseInt(year.getText());
              user.setYear_of_birth(YOB);
              
              if (YOB<1900||YOB>2015)
                throw new NumberFormatException();
          
          if(dao.addUser(user)) {
          user=dao.getUser(uname.getText().trim());
          Gui_user usergui = new Gui_user(dao, user);
          
          //usergui.setVisible(true);
          
        //usergui.setVisible(true);
        reg.dispose();
               }
          else {
            JOptionPane.showMessageDialog(null,"Duplicate username",
                "Error",JOptionPane.WARNING_MESSAGE);
          }
        }catch(NumberFormatException e1) {JOptionPane.showMessageDialog(null,"Invalid Date of birth",
            "Error",JOptionPane.WARNING_MESSAGE);};
          }
      
      }
    });
    btnSignUp.setBounds(199, 303, 103, 23);
    contentPane.add(btnSignUp);
    
    
    //back to login window , click handler  
    JButton btnCBack = new JButton("Back");
    btnCBack.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         new Login(dao);
      //  Login.setVisible(true);
        reg.dispose();
      }
    });
    btnCBack.setBounds(314, 303, 103, 23);
    contentPane.add(btnCBack);
    
    lname = new JTextField();
    lname.setColumns(10);
    lname.setBounds(165, 155, 298, 20);
    contentPane.add(lname);
    
    JLabel lblCountry = new JLabel("Country");
    lblCountry.setBounds(81, 261, 74, 14);
    contentPane.add(lblCountry);
    
    year = new JTextField();
    year.setColumns(10);
    year.setBounds(165, 219, 298, 20);
    contentPane.add(year);
    
    JLabel lblYearOfBirth = new JLabel("Year of Birth");
    lblYearOfBirth.setBounds(81, 222, 84, 14);
    contentPane.add(lblYearOfBirth);
    
    
    
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    reg.setLocation(dim.width/2-reg.getSize().width/2, dim.height/2-reg.getSize().height/2);
    
    this.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			System.out.println("Closing connection to database...");
			dao.exit();
		}
	});
  }
}