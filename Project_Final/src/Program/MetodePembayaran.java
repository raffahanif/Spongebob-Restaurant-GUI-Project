package Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;

public class MetodePembayaran extends JFrame {
    private JComboBox<String> comboMetode;
    private JLabel labelNoRek, labelTotal;
    private String namaPelanggan;
    private double totalPembayaran;
    private int idPesanan;
    private String jenisKunjungan;
    private Connection koneksi;

    public MetodePembayaran(String namaPelanggan, double totalPembayaran, int idPesanan) {
        initializeDatabaseConnection();
        this.namaPelanggan = namaPelanggan;
        this.totalPembayaran = totalPembayaran;
        this.idPesanan = idPesanan;

        // Ambil jenis kunjungan dari database
        this.jenisKunjungan = getJenisKunjunganFromDatabase();

        setTitle("Metode Pembayaran");
        setSize(500, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bg = new ImageIcon(getClass().getResource("/resources/images/Desain_Tampilan/MetodePembayaran.jpg")).getImage();
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        panel.setLayout(null);

        // ComboBox metode pembayaran sesuai database
        comboMetode = new JComboBox<>(new String[]{"Pilih Metode", "tunai", "debit", "kredit", "e-wallet"});
        comboMetode.setBounds(150, 230, 200, 30);
        panel.add(comboMetode);

        // Label No Rekening
        labelNoRek = new JLabel(" ");
        labelNoRek.setForeground(Color.WHITE);
        labelNoRek.setFont(new Font("Arial", Font.BOLD, 15));
        labelNoRek.setBounds(120, 310, 300, 30);
        panel.add(labelNoRek);

        comboMetode.addActionListener(e -> {
            String metode = (String) comboMetode.getSelectedItem();
            switch (metode) {
                case "debit":
                    labelNoRek.setText("No.Rek: 1234567890 (a.n Squidward)");
                    break;
                case "kredit":
                    labelNoRek.setText("No.Kartu: 1234-5678-9012-3456");
                    break;
                case "e-wallet":
                    labelNoRek.setText("OVO/DANA: 081234567890");
                    break;
                case "tunai":
                    labelNoRek.setText("Bayar langsung di kasir");
                    break;
                default:
                    labelNoRek.setText(" ");
            }
        });

        // Tombol Back
        JButton btnBack = new JButton();
        btnBack.setBounds(27, 30, 50, 30);
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.addActionListener(e -> {
            new CekPesananGUI();
            dispose();
        });
        panel.add(btnBack);

        // Button Selesai
        JButton btnSelesai = new JButton();
        btnSelesai.setBounds(165, 597, 160, 38);
        btnSelesai.setBorderPainted(false);
        btnSelesai.setContentAreaFilled(false);
        btnSelesai.addActionListener(e -> {
            String selectedMetode = (String) comboMetode.getSelectedItem();
            if ("Pilih Metode".equals(selectedMetode)) {
                JOptionPane.showMessageDialog(this, "Silakan pilih metode pembayaran terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                if (!validasiTotalPembayaran()) {
                    JOptionPane.showMessageDialog(this,
                            "Total pembayaran tidak sesuai dengan total pesanan",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (prosesPembayaran(selectedMetode)) {
                    new BuktiTransaksi(idPesanan);
                    dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Gagal memproses pembayaran: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(btnSelesai);

        add(panel);
        setVisible(true);
    }

    private String getJenisKunjunganFromDatabase() {
        String jenis = "dine_in"; // default
        try (Connection koneksi = DatabaseConnection.getConnection()) {
            String sql = "SELECT Jenis_Kunjungan FROM PESANAN WHERE ID_Pesanan = ?";
            try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
                ps.setInt(1, idPesanan);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    jenis = rs.getString("Jenis_Kunjungan");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mendapatkan jenis kunjungan: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return jenis;
    }

    private boolean validasiTotalPembayaran() throws SQLException {
        try (Connection koneksi = DatabaseConnection.getConnection()) {
            String sql = "SELECT SUM(Harga_Satuan * Jumlah_Pesanan) as Total " +
                    "FROM Detail_Pesanan WHERE ID_Pesanan = ?";

            try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
                ps.setInt(1, idPesanan);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    double totalPesanan = rs.getDouble("Total");
                    return Math.abs(totalPesanan - totalPembayaran) < 0.01;
                }
            }
        }
        return false;
    }

    private boolean prosesPembayaran(String metode) throws SQLException {
        Connection koneksi = null;
        try {
            koneksi = DatabaseConnection.getConnection();
            koneksi.setAutoCommit(false);

            if (!validasiPesananSebelumPembayaran(koneksi)) {
                koneksi.rollback();
                return false;
            }

            if (!kurangiStokMenu(koneksi)) {
                koneksi.rollback();
                return false;
            }

            String sqlPembayaran = "INSERT INTO PEMBAYARAN (ID_Pesanan, Metode_Pembayaran, Total_Pembayaran, Status) " +
                    "VALUES (?, ?, ?, 'lunas')";

            try (PreparedStatement ps = koneksi.prepareStatement(sqlPembayaran)) {
                ps.setInt(1, idPesanan);
                ps.setString(2, metode.toLowerCase());
                ps.setDouble(3, totalPembayaran);
                ps.executeUpdate();
            }

            String sqlUpdatePesanan = "UPDATE PESANAN SET Status = 'selesai' WHERE ID_Pesanan = ?";
            try (PreparedStatement ps = koneksi.prepareStatement(sqlUpdatePesanan)) {
                ps.setInt(1, idPesanan);
                ps.executeUpdate();
            }

            if (jenisKunjungan.equalsIgnoreCase("reservasi")) {
                String sqlUpdateReservasi = "UPDATE RESERVASI r JOIN PESANAN p ON r.ID_Pelanggan = p.ID_Pelanggan " +
                        "SET r.Status = 'checked_in' WHERE p.ID_Pesanan = ?";
                try (PreparedStatement ps = koneksi.prepareStatement(sqlUpdateReservasi)) {
                    ps.setInt(1, idPesanan);
                    ps.executeUpdate();
                }
            }

            koneksi.commit();
            return true;
        } catch (SQLException e) {
            if (koneksi != null) {
                koneksi.rollback();
            }
            throw e;
        } finally {
            if (koneksi != null) {
                try {
                    koneksi.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean validasiPesananSebelumPembayaran(Connection koneksi) throws SQLException {
        String sqlCekStatus = "SELECT Status FROM PESANAN WHERE ID_Pesanan = ?";
        try (PreparedStatement ps = koneksi.prepareStatement(sqlCekStatus)) {
            ps.setInt(1, idPesanan);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String status = rs.getString("Status");
                if (!"draft".equalsIgnoreCase(status)) {
                    JOptionPane.showMessageDialog(this,
                            "Pesanan sudah diproses atau tidak valid",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }

        if (jenisKunjungan.equalsIgnoreCase("reservasi")) {
            String sqlCekReservasi = "SELECT r.Status FROM RESERVASI r " +
                    "JOIN PESANAN p ON r.ID_Pelanggan = p.ID_Pelanggan " +
                    "WHERE p.ID_Pesanan = ?";
            try (PreparedStatement ps = koneksi.prepareStatement(sqlCekReservasi)) {
                ps.setInt(1, idPesanan);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String statusReservasi = rs.getString("Status");
                    if (!"dipesan".equalsIgnoreCase(statusReservasi)) {
                        JOptionPane.showMessageDialog(this,
                                "Reservasi tidak valid atau sudah diproses",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Data reservasi tidak ditemukan",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }

        return true;
    }

    private boolean kurangiStokMenu(Connection koneksi) throws SQLException {
        String sql = "SELECT dp.ID_Menu, dp.Jumlah_Pesanan, m.Stok, m.Nama_Menu " +
                "FROM Detail_Pesanan dp " +
                "JOIN MENU m ON dp.ID_Menu = m.ID_Menu " +
                "WHERE dp.ID_Pesanan = ?";

        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setInt(1, idPesanan);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idMenu = rs.getInt("ID_Menu");
                int jumlahPesanan = rs.getInt("Jumlah_Pesanan");
                int stok = rs.getInt("Stok");
                String namaMenu = rs.getString("Nama_Menu");

                if (stok < jumlahPesanan) {
                    JOptionPane.showMessageDialog(this,
                            "Stok " + namaMenu + " tidak mencukupi (tersedia: " + stok + ")",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                String updateStok = "UPDATE MENU SET Stok = Stok - ? WHERE ID_Menu = ?";
                try (PreparedStatement psUpdate = koneksi.prepareStatement(updateStok)) {
                    psUpdate.setInt(1, jumlahPesanan);
                    psUpdate.setInt(2, idMenu);
                    psUpdate.executeUpdate();
                }
            }
        }
        return true;
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
        SwingUtilities.invokeLater(() -> new MetodePembayaran("Nama Pelanggan", 150000.0, 1));
    }
}