package Program;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.awt.Image;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.Color;

public class Menu2 implements ActionListener {
    static Connection koneksi;

    private JLabel menu6, menu7, menu8, menu9;
    private JTextField jumlah5, jumlah6, jumlah7, jumlah8;
    private JFrame frame;
    private JButton tambah5, tambah6, tambah7, tambah8;
    private JButton kurang5, kurang6, kurang7, kurang8;
    private JButton belanja2;

    public static String jumlah5Text = "";
    public static String jumlah6Text = "";
    public static String jumlah7Text = "";
    public static String jumlah8Text = "";

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

    public Menu2() {
        initializeDatabaseConnection();
        frame = new JFrame("");
        BackgroundPanel bg = new BackgroundPanel("src/resources/images/Desain_Tampilan/Menu2.jpg");
        bg.setLayout(null);
        frame.setContentPane(bg);

        menu6 = new JLabel(new ImageIcon("src/resources/images/Menu/kelpDeviledEggs.jpg"));
        menu6.setBounds(75, 165, 140, 84);
        jumlah5 = new JTextField(jumlah5Text);
        jumlah5.setHorizontalAlignment(JTextField.CENTER);
        jumlah5.setBounds(118, 280, 50, 30);
        jumlah5.setOpaque(false);
        jumlah5.setBorder(null);
        jumlah5.setBackground(new Color(0, 0, 0, 0));

        menu7 = new JLabel(new ImageIcon("src/resources/images/Menu/CherryPie.jpg"));
        menu7.setBounds(275, 165, 140, 84);
        jumlah6 = new JTextField(jumlah6Text);
        jumlah6.setHorizontalAlignment(JTextField.CENTER);
        jumlah6.setBounds(320, 280, 50, 30);
        jumlah6.setOpaque(false);
        jumlah6.setBorder(null);
        jumlah6.setBackground(new Color(0, 0, 0, 0));

        menu8 = new JLabel(new ImageIcon("src/resources/images/Menu/PrettyPatties.jpg"));
        menu8.setBounds(75, 390, 140, 84);
        jumlah7 = new JTextField(jumlah7Text);
        jumlah7.setHorizontalAlignment(JTextField.CENTER);
        jumlah7.setBounds(118, 510, 50, 30);
        jumlah7.setOpaque(false);
        jumlah7.setBorder(null);
        jumlah7.setBackground(new Color(0, 0, 0, 0));

        menu9 = new JLabel(new ImageIcon("src/resources/images/Menu/GoofyGooberSundae.jpg"));
        menu9.setBounds(275, 390, 140, 84);
        jumlah8 = new JTextField(jumlah8Text);
        jumlah8.setHorizontalAlignment(JTextField.CENTER);
        jumlah8.setBounds(320, 510, 50, 30);
        jumlah8.setOpaque(false);
        jumlah8.setBorder(null);
        jumlah8.setBackground(new Color(0, 0, 0, 0));

        belanja2 = new JButton("");
        belanja2.addActionListener(this);
        belanja2.setBounds(290, 610, 130, 30);
        belanja2.setContentAreaFilled(false);
        belanja2.setBorderPainted(false);
        belanja2.setOpaque(false);

        tambah5 = new JButton("");
        tambah5.addActionListener(this);
        tambah5.setBounds(185, 280, 40, 20);
        tambah5.setContentAreaFilled(false);
        tambah5.setBorderPainted(false);
        tambah5.setOpaque(false);

        tambah6 = new JButton("");
        tambah6.addActionListener(this);
        tambah6.setBounds(385, 280, 40, 20);
        tambah6.setContentAreaFilled(false);
        tambah6.setBorderPainted(false);
        tambah6.setOpaque(false);

        tambah7 = new JButton("");
        tambah7.addActionListener(this);
        tambah7.setBounds(185, 515, 50, 30);
        tambah7.setContentAreaFilled(false);
        tambah7.setBorderPainted(false);
        tambah7.setOpaque(false);

        tambah8 = new JButton("");
        tambah8.addActionListener(this);
        tambah8.setBounds(385, 515, 50, 30);
        tambah8.setContentAreaFilled(false);
        tambah8.setBorderPainted(false);
        tambah8.setOpaque(false);

        kurang5 = new JButton("");
        kurang5.addActionListener(this);
        kurang5.setBounds(55, 280, 40, 20);
        kurang5.setContentAreaFilled(false);
        kurang5.setBorderPainted(false);
        kurang5.setOpaque(false);

        kurang6 = new JButton("");
        kurang6.addActionListener(this);
        kurang6.setBounds(260, 280, 40, 20);
        kurang6.setContentAreaFilled(false);
        kurang6.setBorderPainted(false);
        kurang6.setOpaque(false);

        kurang7 = new JButton("");
        kurang7.addActionListener(this);
        kurang7.setBounds(55, 515, 40, 20);
        kurang7.setContentAreaFilled(false);
        kurang7.setBorderPainted(false);
        kurang7.setOpaque(false);

        kurang8 = new JButton("");
        kurang8.addActionListener(this);
        kurang8.setBounds(260, 515, 40, 20);
        kurang8.setContentAreaFilled(false);
        kurang8.setBorderPainted(false);
        kurang8.setOpaque(false);

        frame.add(belanja2); frame.add(menu6);
        frame.add(jumlah5); frame.add(menu7);
        frame.add(jumlah6); frame.add(menu8);
        frame.add(jumlah7); frame.add(menu9);
        frame.add(jumlah8); frame.add(tambah5);
        frame.add(tambah6); frame.add(tambah7);
        frame.add(tambah8); frame.add(kurang5);
        frame.add(kurang6); frame.add(kurang7);
        frame.add(kurang8);
        frame.setSize(500, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void initializeDatabaseConnection() {
        try {
            koneksi = DatabaseConnection.getConnection();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Gagal terhubung ke database: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public void actionPerformed(ActionEvent ev) {
        int hitung5 = 0;
        int hitung6 = 0;
        int hitung7 = 0;
        int hitung8 = 0;

        if (ev.getSource().equals(tambah5)) {
            String text5 = jumlah5.getText();
            if (!text5.isEmpty()) {
                try {
                    hitung5 = Integer.parseInt(text5);
                } catch (NumberFormatException e) {
                    return;
                }
            }
            jumlah5.setText(Integer.toString(hitung5 + 1));
        }
        else if (ev.getSource().equals(kurang5)) {
            String text5 = jumlah5.getText();
            hitung5 = Integer.parseInt(text5);
            if (hitung5 > 0) {
                jumlah5.setText(Integer.toString(hitung5 - 1));
            }
        }
        else if (ev.getSource().equals(tambah6)) {
            String text6 = jumlah6.getText();
            if (!text6.isEmpty()) {
                try {
                    hitung6 = Integer.parseInt(text6);
                } catch (NumberFormatException e) {
                    return;
                }
            }
            jumlah6.setText(Integer.toString(hitung6 + 1));
        }
        else if (ev.getSource().equals(kurang6)) {
            String text6 = jumlah6.getText();
            hitung6 = Integer.parseInt(text6);
            if (hitung6 > 0) {
                jumlah6.setText(Integer.toString(hitung6 - 1));
            }
        }
        else if (ev.getSource().equals(tambah7)) {
            String text7 = jumlah7.getText();
            if (!text7.isEmpty()) {
                try {
                    hitung7 = Integer.parseInt(text7);
                } catch (NumberFormatException e) {
                    return;
                }
            }
            jumlah7.setText(Integer.toString(hitung7 + 1));
        }
        else if (ev.getSource().equals(kurang7)) {
            String text7 = jumlah7.getText();
            hitung7 = Integer.parseInt(text7);
            if (hitung7 > 0) {
                jumlah7.setText(Integer.toString(hitung7 - 1));
            }
        }
        else if (ev.getSource().equals(tambah8)) {
            String text8 = jumlah8.getText();
            if (!text8.isEmpty()) {
                try {
                    hitung8 = Integer.parseInt(text8);
                } catch (NumberFormatException e) {
                    return;
                }
            }
            jumlah8.setText(Integer.toString(hitung8 + 1));
        }
        else if (ev.getSource().equals(kurang8)) {
            String text8 = jumlah8.getText();
            hitung8 = Integer.parseInt(text8);
            if (hitung8 > 0) {
                jumlah8.setText(Integer.toString(hitung8 - 1));
            }
        }

        else if (ev.getSource().equals(belanja2)) {
            jumlah5Text = jumlah5.getText();
            jumlah6Text = jumlah6.getText();
            jumlah7Text = jumlah7.getText();
            jumlah8Text = jumlah8.getText();

            try {
                simpanPesananKeDatabase();
                frame.dispose();
                new CekPesananGUI();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame,
                        "Gagal menyimpan pesanan: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void simpanPesananKeDatabase() throws SQLException {
        try {
            koneksi.setAutoCommit(false);

            String getHargaSql = "SELECT Harga FROM MENU WHERE ID_Menu = ?";
            String insertSql = "INSERT INTO detail_pesanan (ID_Pesanan, ID_Menu, Jumlah_Pesanan, Harga_Satuan) VALUES (?, ?, ?, ?)";

            simpanMenuPesanan(5, jumlah5Text, getHargaSql, insertSql);
            simpanMenuPesanan(6, jumlah6Text, getHargaSql, insertSql);
            simpanMenuPesanan(7, jumlah7Text, getHargaSql, insertSql);
            simpanMenuPesanan(8, jumlah8Text, getHargaSql, insertSql);

            koneksi.commit();
        } catch (SQLException e) {
            koneksi.rollback();
            throw e;
        } finally {
            koneksi.setAutoCommit(true);
        }
    }

    private void simpanMenuPesanan(int menuId, String jumlahText, String getHargaSql, String insertSql) throws SQLException {
        if (!jumlahText.isEmpty() && Integer.parseInt(jumlahText) > 0) {
            try (PreparedStatement getHarga = koneksi.prepareStatement(getHargaSql);
                 PreparedStatement ps = koneksi.prepareStatement(insertSql)) {

                getHarga.setInt(1, menuId);
                ResultSet rs = getHarga.executeQuery();

                if (rs.next()) {
                    double harga = rs.getDouble("Harga");

                    ps.setInt(1, Menu1.idPesanan);
                    ps.setInt(2, menuId);
                    ps.setInt(3, Integer.parseInt(jumlahText));
                    ps.setDouble(4, harga);
                    ps.executeUpdate();
                } else {
                    throw new SQLException("Harga untuk menu ID " + menuId + " tidak ditemukan");
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            Connection koneksi = DatabaseConnection.getConnection();
            System.out.println("kamu telah terkoneksi");
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Koneksi gagal!");
        }
        new Menu2();
    }
}