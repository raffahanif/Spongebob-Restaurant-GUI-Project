package Program;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.awt.Image;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.Color;
import java.time.LocalDate;

public class Reservasi implements ActionListener {
    static Connection koneksi;
    static Statement stat;
    static ResultSet rs;

    private static String savedNama, savedNoHP, savedjumlahorang, savedTanggal, savedWaktu, savedMeja;
    private JTextField reservasi1, reservasi2, reservasi3;
    private JComboBox<String> waktu, meja, tanggal;
    private JFrame frame;
    private JButton lanjutkanmenu, kembaliawal;

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

    public Reservasi() {
        initializeDatabaseConnection();

        frame = new JFrame("Reservasi Restoran");
        BackgroundPanel bg = new BackgroundPanel("src/resources/images/Desain_Tampilan/Reservasi.jpg");
        bg.setLayout(null);
        frame.setContentPane(bg);

        initializeUIComponents();

        frame.setSize(500, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void initializeUIComponents() {
        reservasi1 = createTransparentTextField(120, 260, 200, 30);
        reservasi1.setFont(new Font("Arial", Font.BOLD, 16));
        reservasi2 = createTransparentTextField(120, 325, 200, 30);
        reservasi2.setFont(new Font("Arial", Font.BOLD, 16));
        reservasi3 = createTransparentTextField(120, 390, 200, 30);
        reservasi3.setFont(new Font("Arial", Font.BOLD, 16));

        loadComboBoxData();

        kembaliawal = createTransparentButton(35, 33, 40, 20);
        lanjutkanmenu = createTransparentButton(188, 610, 150, 30);

        frame.add(reservasi1); frame.add(reservasi2); frame.add(reservasi3);
        frame.add(tanggal); frame.add(waktu); frame.add(meja);
        frame.add(kembaliawal); frame.add(lanjutkanmenu);

        if (savedNama != null) reservasi1.setText(savedNama);
        if (savedNoHP != null) reservasi2.setText(savedNoHP);
        if (savedjumlahorang != null) reservasi3.setText(savedjumlahorang);
        if (savedTanggal != null) tanggal.setSelectedItem(savedTanggal);
        if (savedWaktu != null) waktu.setSelectedItem(savedWaktu);
        if (savedMeja != null) meja.setSelectedItem(savedMeja);
    }

    private void loadComboBoxData() {
        // Tanggal (1-10)
        String[] tanggalan = {"11","12","13","14","15","16","17","18","19","20"};
        tanggal = new JComboBox<>(tanggalan);
        tanggal.setBounds(120, 458, 110, 30);

        // Waktu
        String[] waktuan = {"10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00"};
        waktu = new JComboBox<>(waktuan);
        waktu.setBounds(270, 458, 110, 30);

        // Meja - dari database
        DefaultComboBoxModel<String> mejaModel = new DefaultComboBoxModel<>();
        try {
            String query = "SELECT No_Meja FROM meja WHERE Status = 'tersedia'";
            rs = stat.executeQuery(query);

            while (rs.next()) {
                mejaModel.addElement(rs.getString("No_Meja"));
            }

            if (mejaModel.getSize() == 0) {
                mejaModel.addElement("Tidak ada meja tersedia");
                JOptionPane.showMessageDialog(frame,
                        "Tidak ada meja tersedia saat ini",
                        "Informasi", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "Gagal memuat data meja: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        meja = new JComboBox<>(mejaModel);
        meja.setBounds(130, 555, 250, 30);
    }

    private JTextField createTransparentTextField(int x, int y, int width, int height) {
        JTextField textField = new JTextField(10);
        textField.setHorizontalAlignment(JTextField.LEFT);
        textField.setBounds(x, y, width, height);
        textField.setOpaque(false);
        textField.setBorder(null);
        textField.setBackground(new Color(0, 0, 0, 0));
        return textField;
    }

    private JButton createTransparentButton(int x, int y, int width, int height) {
        JButton button = new JButton("");
        button.addActionListener(this);
        button.setBounds(x, y, width, height);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        return button;
    }

    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource().equals(lanjutkanmenu)) {
            if (!validateInput()) {
                return;
            }

            savedNama = reservasi1.getText().trim();
            savedNoHP = reservasi2.getText().trim();
            savedjumlahorang = reservasi3.getText().trim();
            savedTanggal = (String) tanggal.getSelectedItem();
            savedWaktu = (String) waktu.getSelectedItem();
            savedMeja = (String) meja.getSelectedItem();

            if (savedMeja.equals("Tidak ada meja tersedia")) {
                JOptionPane.showMessageDialog(frame,
                        "Tidak ada meja tersedia untuk dipesan",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (saveReservationToDatabase()) {
                    frame.dispose();
                    new Menu1("Reservasi");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame,
                        "Gagal menyimpan reservasi: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (ev.getSource().equals(kembaliawal)) {
            frame.dispose();
            new Beranda();
        }
    }

    private boolean validateInput() {
        if (reservasi1.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Nama harus diisi", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!reservasi2.getText().trim().matches("\\d{10,15}")) {
            JOptionPane.showMessageDialog(frame,
                    "Nomor HP harus 10-15 digit angka",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (reservasi3.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Jumlah orang harus diisi", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean saveReservationToDatabase() throws SQLException {
        try {
            koneksi.setAutoCommit(false);

            // 1. Save customer data
            String insertPelanggan = "INSERT INTO pelanggan (Nama, No_HP) VALUES (?, ?)";
            try (PreparedStatement psPelanggan = koneksi.prepareStatement(insertPelanggan, Statement.RETURN_GENERATED_KEYS)) {
                psPelanggan.setString(1, savedNama);
                psPelanggan.setString(2, savedNoHP);
                psPelanggan.executeUpdate();

                try (ResultSet rsPelanggan = psPelanggan.getGeneratedKeys()) {
                    if (!rsPelanggan.next()) {
                        throw new SQLException("Gagal mendapatkan ID Pelanggan");
                    }
                    int idPelanggan = rsPelanggan.getInt(1);

                    // 2. Get table ID
                    int idMeja;
                    String getMejaID = "SELECT ID_Meja FROM meja WHERE No_Meja = ?";
                    try (PreparedStatement psGetMeja = koneksi.prepareStatement(getMejaID)) {
                        psGetMeja.setString(1, savedMeja);
                        try (ResultSet rsMeja = psGetMeja.executeQuery()) {
                            if (!rsMeja.next()) {
                                throw new SQLException("Meja tidak ditemukan");
                            }
                            idMeja = rsMeja.getInt("ID_Meja");
                        }
                    }

                    // 3. Update table status
                    String updateMeja = "UPDATE meja SET Status = 'dipesan' WHERE ID_Meja = ?";
                    try (PreparedStatement psUpdateMeja = koneksi.prepareStatement(updateMeja)) {
                        psUpdateMeja.setInt(1, idMeja);
                        psUpdateMeja.executeUpdate();
                    }

                    // 4. Save reservation with proper date format
                    String insertReservasi = "INSERT INTO reservasi (ID_Pelanggan, ID_Meja, Tanggal, Jam_Reservasi, Status) " +
                            "VALUES (?, ?, ?, ?, 'dipesan')";
                    try (PreparedStatement psReservasi = koneksi.prepareStatement(insertReservasi)) {
                        psReservasi.setInt(1, idPelanggan);
                        psReservasi.setInt(2, idMeja);

                        // Create proper date format using current year and month
                        LocalDate today = LocalDate.now();
                        String tanggalFull = String.format("%d-%02d-%02d",
                                today.getYear(),
                                today.getMonthValue(),
                                Integer.parseInt(savedTanggal));

                        String waktuFull = savedWaktu + ":00";

                        psReservasi.setDate(3, java.sql.Date.valueOf(tanggalFull));
                        psReservasi.setTime(4, java.sql.Time.valueOf(waktuFull));
                        psReservasi.executeUpdate();
                    }

                    // 5. Create order
                    String insertPesanan = "INSERT INTO pesanan (ID_Pelanggan, No_Meja, Jenis_Kunjungan, Status) " +
                            "VALUES (?, ?, 'reservasi', 'draft')";
                    try (PreparedStatement psPesanan = koneksi.prepareStatement(insertPesanan, Statement.RETURN_GENERATED_KEYS)) {
                        psPesanan.setInt(1, idPelanggan);
                        psPesanan.setString(2, savedMeja);
                        psPesanan.executeUpdate();

                        try (ResultSet rsPesanan = psPesanan.getGeneratedKeys()) {
                            if (rsPesanan.next()) {
                                Menu1.idPesanan = rsPesanan.getInt(1);
                            }
                        }
                    }

                    koneksi.commit();
                    return true;
                }
            }
        } catch (SQLException e) {
            try {
                koneksi.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw e;
        } finally {
            try {
                koneksi.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeDatabaseConnection() {
        try {
            koneksi = DatabaseConnection.getConnection();
            stat = koneksi.createStatement();
            System.out.println("Koneksi database berhasil");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Gagal terhubung ke database: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Reservasi();
        });
    }
}