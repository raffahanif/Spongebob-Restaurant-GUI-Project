package Program;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.awt.Image;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import java.awt.Font;

public class coba implements ActionListener{
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/lab";
    static final String USER = "root";
    static final String PASS = "rabyan";

    static Connection koneksi;
    static Statement stat;
    static ResultSet rs;

    private JLabel menu, menu2, menu3, menu4, menu5, menu6, menu7;
    private JTextField jumlah, jumlah2, jumlah3, jumlah4, jumlah5, jumlah6;
    private JFrame frame;
    private JButton tambah1, tambah2, tambah3, tambah4, tambah5, tambah6, next;
    private JButton kurang1, kurang2, kurang3, kurang4, kurang5, kurang6;

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String path) {
            backgroundImage = new ImageIcon(path).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public coba() {
        frame = new JFrame("");
        BackgroundPanel bg = new BackgroundPanel("C:/Users/ASUS/Downloads/white.jpg");
        bg.setLayout(null); // supaya bisa pakai .setBounds()
        frame.setContentPane(bg);

        menu = new JLabel("Menu Makanan");
        menu.setFont(new Font("Arial",Font.BOLD, 16));
        menu.setBounds(300, 50, 150, 25);

        menu2 = new JLabel(new ImageIcon("C:/Users/ASUS/Downloads/krabbypatty.jpg"));
        menu2.setBounds(100, 100, 200, 128);
        jumlah = new JTextField(10);
        jumlah.setHorizontalAlignment(JTextField.CENTER);
        jumlah.setBounds(150, 250, 100, 25);

        menu3 = new JLabel(new ImageIcon("C:/Users/ASUS/Downloads/fries.jpg"));
        menu3.setBounds(430, 100, 120, 138);
        jumlah2 = new JTextField(10);
        jumlah2.setHorizontalAlignment(JTextField.CENTER);
        jumlah2.setBounds(450, 250, 100, 25);

        menu4 = new JLabel(new ImageIcon("C:/Users/ASUS/Downloads/kelp.png"));
        menu4.setBounds(100, 380, 200, 152);
        jumlah3 = new JTextField(10);
        jumlah3.setHorizontalAlignment(JTextField.CENTER);
        jumlah3.setBounds(150, 550, 100, 25);

        menu5 = new JLabel(new ImageIcon("src/resources/images/AlphabetShoup_1_120x120.jpg"));
        menu5.setBounds(425, 380, 150, 155);
        jumlah4 = new JTextField(10);
        jumlah4.setHorizontalAlignment(JTextField.CENTER);
        jumlah4.setBounds(450, 550, 100, 25);

        //menu6 = new JLabel(new ImageIcon("C:/Users/ASUS/Downloads/pelangi.jpg"));
        //menu6.setBounds(40, 60, 100, 25);
        //jumlah5 = new JTextField(10);
        //jumlah5.setBounds(120, 60, 100, 25);

        //menu7 = new JLabel(new ImageIcon("C:/Users/ASUS/Downloads/paket.jpg"));
        //menu7.setBounds(40, 60, 100, 25);
        //jumlah6 = new JTextField(10);
        //jumlah6.setBounds(120, 60, 100, 25);

        tambah1 = new JButton("+");
        tambah1.addActionListener(this);
        tambah1.setBounds(255, 250, 50, 25);    //jumlah.setBounds(150, 250, 100, 25);

        tambah2 = new JButton("+");
        tambah2.addActionListener(this);
        tambah2.setBounds(555, 250, 50, 25);    //jumlah2.setBounds(450, 250, 100, 25);

        tambah3 = new JButton("+");
        tambah3.addActionListener(this);
        tambah3.setBounds(255, 550, 50, 25);    //jumlah3.setBounds(150, 550, 100, 25);

        tambah4 = new JButton("+");
        tambah4.addActionListener(this);
        tambah4.setBounds(555, 550, 50, 25);    //jumlah4.setBounds(450, 550, 100, 25);

        //tambah5 = new JButton("+");
        //tambah5.addActionListener(this);
        //tambah5.setBounds(140, 100, 70, 25);

        //tambah6 = new JButton("+");
        //tambah6.addActionListener(this);
        //tambah6.setBounds(140, 100, 70, 25);

        kurang1 = new JButton("-");
        kurang1.addActionListener(this);
        kurang1.setBounds(95, 250, 50, 25);        //jumlah.setBounds(150, 250, 100, 25);

        kurang2 = new JButton("-");
        kurang2.addActionListener(this);
        kurang2.setBounds(395, 250, 50, 25);        //jumlah2.setBounds(450, 250, 100, 25);

        kurang3 = new JButton("-");
        kurang3.addActionListener(this);
        kurang3.setBounds(95, 550, 50, 25);        //jumlah3.setBounds(150, 550, 100, 25);

        kurang4 = new JButton("-");
        kurang4.addActionListener(this);
        kurang4.setBounds(395, 550, 50, 25);        //jumlah4.setBounds(450, 550, 100, 25);

        //kurang5 = new JButton("-");
        //kurang5.addActionListener(this);
        //kurang5.setBounds(140, 100, 70, 25);

        //kurang6 = new JButton("-");
        //kurang6.addActionListener(this);
        //kurang6.setBounds(140, 100, 70, 25);

        next = new JButton("next");
        next.addActionListener(this);
        next.setBounds(320, 600, 70, 25);

        frame.add(menu);
        frame.add(menu2);
        frame.add(jumlah);
        frame.add(menu3);
        frame.add(jumlah2);
        frame.add(menu4);
        frame.add(jumlah3);
        frame.add(menu5);
        frame.add(jumlah4);
        //frame.add(menu6);
        //frame.add(jumlah5);
        //frame.add(menu7);
        //frame.add(jumlah6);
        frame.add(tambah1);
        frame.add(tambah2);
        frame.add(tambah3);
        frame.add(tambah4);
        //frame.add(tambah5);
        //frame.add(tambah6);
        frame.add(kurang1);
        frame.add(kurang2);
        frame.add(kurang3);
        frame.add(kurang4);
        //frame.add(kurang5);
        //frame.add(kurang6);
        frame.add(next);
        frame.setSize(720, 717);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);}

    public void actionPerformed(ActionEvent ev) {
        int hitung = 0;
        int hitung2 = 0;
        int hitung3 = 0;
        int hitung4 = 0;
        if (ev.getSource().equals(tambah1)) {
            String text = jumlah.getText();
            if (!text.isEmpty()) {
                try {
                    hitung = Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Input tidak valid di field 1");
                    return;
                }
            }
            jumlah.setText(Integer.toString(hitung + 1));
        }  else if (ev.getSource().equals(kurang1)) {
            String text = jumlah.getText();
            jumlah.setText(Integer.toString(hitung - 1));
        } else if (ev.getSource().equals(tambah2)) {
            String text2 = jumlah2.getText();
            if (!text2.isEmpty()) {
                try {
                    hitung2 = Integer.parseInt(text2);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Input tidak valid di field 2");
                    return;
                }
            }
            jumlah2.setText(Integer.toString(hitung2 + 1));
        } else if (ev.getSource().equals(kurang2)) {
            String text2 = jumlah.getText();
            jumlah2.setText(Integer.toString(hitung2 - 1));
        } else if (ev.getSource().equals(tambah3)) {
            String text3 = jumlah3.getText();
            if (!text3.isEmpty()) {
                try {
                    hitung3 = Integer.parseInt(text3);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Input tidak valid di field 2");
                    return;
                }
            }
            jumlah3.setText(Integer.toString(hitung3 + 1));
        } else if (ev.getSource().equals(kurang3)) {
            String text = jumlah.getText();
            jumlah3.setText(Integer.toString(hitung3 - 1));
        } else if (ev.getSource().equals(tambah4)) {
            String text4 = jumlah4.getText();
            if (!text4.isEmpty()) {
                try {
                    hitung4 = Integer.parseInt(text4);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Input tidak valid di field 2");
                    return;
                }
            }
            jumlah4.setText(Integer.toString(hitung4 + 1));
        } else if (ev.getSource().equals(kurang4)) {
            jumlah4.setText(Integer.toString(hitung4 - 1));
        }
    }

    public static void main(String[] args) {
        new coba();}}