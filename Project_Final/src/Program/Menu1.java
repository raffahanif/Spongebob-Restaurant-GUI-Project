package Program;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.awt.Image;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.Color;

public class Menu1 implements ActionListener {
    static Connection koneksi;

    private JLabel menu2, menu3, menu4, menu5;
    private JTextField jumlah, jumlah2, jumlah3, jumlah4;
    private JFrame frame;
    private JButton tambah1, tambah2, tambah3, tambah4;
    private JButton kurang1, kurang2, kurang3, kurang4;
    private JButton kembali1, lanjutkan, belanja;

    public static String jumlah1Text = "";
    public static String jumlah2Text = "";
    public static String jumlah3Text = "";
    public static String jumlah4Text = "";

    public static String previousScreen = "";
    public static int idPesanan;

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

    public Menu1(String source) {
        previousScreen = source;
        initializeDatabaseConnection();

        frame = new JFrame("");
        BackgroundPanel bg = new BackgroundPanel("src/resources/images/Desain_Tampilan/Menu1.jpg");
        bg.setLayout(null);
        frame.setContentPane(bg);

        frame.setSize(500, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Inisialisasi komponen UI
        menu2 = new JLabel(new ImageIcon("src/resources/images/Menu/KrabbyPatties.jpg"));
        menu2.setBounds(75, 165, 140, 84);
        jumlah = new JTextField(jumlah1Text);
        jumlah.setHorizontalAlignment(JTextField.CENTER);
        jumlah.setBounds(118, 280, 50, 30);
        jumlah.setOpaque(false);
        jumlah.setBorder(null);
        jumlah.setBackground(new Color(0, 0, 0, 0));

        menu3 = new JLabel(new ImageIcon("src/resources/images/Menu/KelpShake.jpg"));
        menu3.setBounds(275, 165, 140, 84);
        jumlah2 = new JTextField(jumlah2Text);
        jumlah2.setHorizontalAlignment(JTextField.CENTER);
        jumlah2.setBounds(320, 280, 50, 30);
        jumlah2.setOpaque(false);
        jumlah2.setBorder(null);
        jumlah2.setBackground(new Color(0, 0, 0, 0));

        menu4 = new JLabel(new ImageIcon("src/resources/images/Menu/Chum.jpg"));
        menu4.setBounds(75, 390, 140, 84);
        jumlah3 = new JTextField(jumlah3Text);
        jumlah3.setHorizontalAlignment(JTextField.CENTER);
        jumlah3.setBounds(118, 510, 50, 30);
        jumlah3.setOpaque(false);
        jumlah3.setBorder(null);
        jumlah3.setBackground(new Color(0, 0, 0, 0));

        menu5 = new JLabel(new ImageIcon("src/resources/images/Menu/Fries.jpg"));
        menu5.setBounds(275, 390, 140, 84);
        jumlah4 = new JTextField(jumlah4Text);
        jumlah4.setHorizontalAlignment(JTextField.CENTER);
        jumlah4.setBounds(320, 510, 50, 30);
        jumlah4.setOpaque(false);
        jumlah4.setBorder(null);
        jumlah4.setBackground(new Color(0, 0, 0, 0));

        lanjutkan = new JButton("");
        lanjutkan.addActionListener(this);
        lanjutkan.setBounds(275, 610, 150, 30);
        lanjutkan.setContentAreaFilled(false);
        lanjutkan.setBorderPainted(false);
        lanjutkan.setOpaque(false);

        belanja = new JButton("");
        belanja.addActionListener(this);
        belanja.setBounds(65, 610, 120, 30);
        belanja.setContentAreaFilled(false);
        belanja.setBorderPainted(false);
        belanja.setOpaque(false);

        tambah1 = new JButton("");
        tambah1.addActionListener(this);
        tambah1.setBounds(185, 280, 40, 20);
        tambah1.setContentAreaFilled(false);
        tambah1.setBorderPainted(false);
        tambah1.setOpaque(false);

        tambah2 = new JButton("");
        tambah2.addActionListener(this);
        tambah2.setBounds(385, 280, 40, 20);
        tambah2.setContentAreaFilled(false);
        tambah2.setBorderPainted(false);
        tambah2.setOpaque(false);

        tambah3 = new JButton("");
        tambah3.addActionListener(this);
        tambah3.setBounds(185, 515, 50, 30);
        tambah3.setContentAreaFilled(false);
        tambah3.setBorderPainted(false);
        tambah3.setOpaque(false);

        tambah4 = new JButton("");
        tambah4.addActionListener(this);
        tambah4.setBounds(385, 515, 50, 30);
        tambah4.setContentAreaFilled(false);
        tambah4.setBorderPainted(false);
        tambah4.setOpaque(false);

        kurang1 = new JButton("");
        kurang1.addActionListener(this);
        kurang1.setBounds(55, 280, 40, 20);
        kurang1.setContentAreaFilled(false);
        kurang1.setBorderPainted(false);
        kurang1.setOpaque(false);

        kurang2 = new JButton("");
        kurang2.addActionListener(this);
        kurang2.setBounds(260, 280, 40, 20);
        kurang2.setContentAreaFilled(false);
        kurang2.setBorderPainted(false);
        kurang2.setOpaque(false);

        kurang3 = new JButton("");
        kurang3.addActionListener(this);
        kurang3.setBounds(55, 515, 40, 20);
        kurang3.setContentAreaFilled(false);
        kurang3.setBorderPainted(false);
        kurang3.setOpaque(false);

        kurang4 = new JButton("");
        kurang4.addActionListener(this);
        kurang4.setBounds(260, 515, 40, 20);
        kurang4.setContentAreaFilled(false);
        kurang4.setBorderPainted(false);
        kurang4.setOpaque(false);

        kembali1 = new JButton("");
        kembali1.addActionListener(this);
        kembali1.setBounds(27, 30, 50, 30);
        kembali1.setContentAreaFilled(false);
        kembali1.setBorderPainted(false);
        kembali1.setOpaque(false);

        frame.add(kembali1);
        frame.add(lanjutkan);
        frame.add(belanja);
        frame.add(menu2);
        frame.add(jumlah);
        frame.add(menu3);
        frame.add(jumlah2);
        frame.add(menu4);
        frame.add(jumlah3);
        frame.add(menu5);
        frame.add(jumlah4);
        frame.add(tambah1);
        frame.add(tambah2);
        frame.add(tambah3);
        frame.add(tambah4);
        frame.add(kurang1);
        frame.add(kurang2);
        frame.add(kurang3);
        frame.add(kurang4);
    }

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
                    return;
                }
            }
            jumlah.setText(Integer.toString(hitung + 1));
        }
        else if (ev.getSource().equals(kurang1)) {
            String text = jumlah.getText();
            hitung = Integer.parseInt(text);
            if (hitung > 0) {
                jumlah.setText(Integer.toString(hitung - 1));
            }
        }
        else if (ev.getSource().equals(tambah2)) {
            String text2 = jumlah2.getText();
            if (!text2.isEmpty()) {
                try {
                    hitung2 = Integer.parseInt(text2);
                } catch (NumberFormatException e) {
                    return;
                }
            }
            jumlah2.setText(Integer.toString(hitung2 + 1));
        }
        else if (ev.getSource().equals(kurang2)) {
            String text2 = jumlah2.getText();
            hitung2 = Integer.parseInt(text2);
            if (hitung2 > 0) {
                jumlah2.setText(Integer.toString(hitung2 - 1));
            }
        }
        else if (ev.getSource().equals(tambah3)) {
            String text3 = jumlah3.getText();
            if (!text3.isEmpty()) {
                try {
                    hitung3 = Integer.parseInt(text3);
                } catch (NumberFormatException e) {
                    return;
                }
            }
            jumlah3.setText(Integer.toString(hitung3 + 1));
        }
        else if (ev.getSource().equals(kurang3)) {
            String text3 = jumlah3.getText();
            hitung3 = Integer.parseInt(text3);
            if (hitung3 > 0) {
                jumlah3.setText(Integer.toString(hitung3 - 1));
            }
        }
        else if (ev.getSource().equals(tambah4)) {
            String text4 = jumlah4.getText();
            if (!text4.isEmpty()) {
                try {
                    hitung4 = Integer.parseInt(text4);
                } catch (NumberFormatException e) {
                    return;
                }
            }
            jumlah4.setText(Integer.toString(hitung4 + 1));
        }
        else if (ev.getSource().equals(kurang4)) {
            String text4 = jumlah4.getText();
            hitung4 = Integer.parseInt(text4);
            if (hitung4 > 0) {
                jumlah4.setText(Integer.toString(hitung4 - 1));
            }
        }
        else if (ev.getSource().equals(belanja)) {
            jumlah1Text = jumlah.getText();
            jumlah2Text = jumlah2.getText();
            jumlah3Text = jumlah3.getText();
            jumlah4Text = jumlah4.getText();

            try {
                simpanPesananKeDatabase();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame,
                        "Gagal menyimpan pesanan: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (ev.getSource().equals(lanjutkan)) {
            jumlah1Text = jumlah.getText();
            jumlah2Text = jumlah2.getText();
            jumlah3Text = jumlah3.getText();
            jumlah4Text = jumlah4.getText();

            try {
                simpanPesananKeDatabase();
                frame.dispose();
                new Menu2();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame,
                        "Gagal menyimpan pesanan: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (ev.getSource().equals(kembali1)) {
            frame.dispose();
            if (previousScreen.equals("Dine_In")) {
                new Dine_In();
            } else if (previousScreen.equals("Take_Away")) {
                new Take_Away();
            } else {
                new Reservasi();
            }
        }
    }

    private void simpanPesananKeDatabase() throws SQLException {
        try {
            koneksi.setAutoCommit(false);

            String getHargaSql = "SELECT Harga FROM MENU WHERE ID_Menu = ?";
            String insertSql = "INSERT INTO detail_pesanan (ID_Pesanan, ID_Menu, Jumlah_Pesanan, Harga_Satuan) VALUES (?, ?, ?, ?)";

            simpanMenuPesanan(1, jumlah1Text, getHargaSql, insertSql);
            simpanMenuPesanan(2, jumlah2Text, getHargaSql, insertSql);
            simpanMenuPesanan(3, jumlah3Text, getHargaSql, insertSql);
            simpanMenuPesanan(4, jumlah4Text, getHargaSql, insertSql);

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

                    ps.setInt(1, idPesanan);
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

    public static void main(String[] args) {
        try {
            Connection koneksi = DatabaseConnection.getConnection();
            System.out.println("kamu telah terkoneksi");
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Koneksi gagal!");
        }
        new Menu1("");
    }
}