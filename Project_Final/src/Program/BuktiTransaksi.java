package Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;
import java.text.DecimalFormat;
import javax.imageio.ImageIO;

public class BuktiTransaksi extends JFrame {
    private String namaPelanggan;
    private double totalPembayaran;
    private String jenisKunjungan;
    private String statusPembayaran;
    private Connection koneksi;

    public BuktiTransaksi(int idPesanan) {
        initializeDatabaseConnection();
        setTitle("Bukti Transaksi");
        setSize(500, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Ambil data dari database berdasarkan ID_Pesanan
        TransaksiData data = getDataTransaksi(idPesanan);

        // Jika data tidak ditemukan
        if (data == null) {
            JOptionPane.showMessageDialog(this, "Data transaksi tidak ditemukan", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        this.namaPelanggan = data.getNamaPelanggan();
        //this.totalPembayaran = data.getTotalPembayaran();
        this.jenisKunjungan = data.getJenisKunjungan();
        this.statusPembayaran = data.getStatusPembayaran();

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bg = new ImageIcon(getClass().getResource("/resources/images/Desain_Tampilan/BuktiTransaksi.jpg")).getImage();
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        panel.setLayout(null);

        // Format total pembayaran
        /*DecimalFormat df = new DecimalFormat("Rp #,##0.00");
        String totalFormatted = df.format(data.getTotalPembayaran());*/

        // Nama Pelanggan
        JTextField fieldNama = createTransparentTextField(data.getNamaPelanggan(), 150, 310);
        fieldNama.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(fieldNama);

        // Metode Pembayaran
        JTextField fieldMetode = createTransparentTextField(data.getMetodePembayaran(), 150, 385);
        fieldMetode.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(fieldMetode);

        // Status Pembayaran
        String statusDisplay;
        switch (data.getStatusPembayaran().toLowerCase()) {
            case "lunas":
                statusDisplay = "Lunas";
                break;
            case "pending":
                statusDisplay = "Menunggu Pembayaran";
                break;
            case "gagal":
                statusDisplay = "Gagal";
                break;
            default:
                statusDisplay = data.getStatusPembayaran();
        }

        JTextField fieldStatus = createTransparentTextField(statusDisplay, 150, 460);
        fieldStatus.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(fieldStatus);

        // Total Pembayaran
        /*JTextField fieldTotal = createTransparentTextField(totalFormatted, 150, 535);
        panel.add(fieldTotal);*/

        // Tombol Back
        JButton btnBack = createInvisibleButton(27, 30, 50, 30);
        btnBack.addActionListener(e -> {
            new MetodePembayaran(namaPelanggan, totalPembayaran, idPesanan);
            dispose();
        });
        panel.add(btnBack);

        // Tombol Home
        JButton btnHome = createInvisibleButton(45, 580, 80, 45);
        btnHome.addActionListener(e -> {
            new Beranda();
            dispose();
        });
        panel.add(btnHome);

        // Tombol Save (untuk menyimpan gambar bukti transaksi)
        JButton btnSave = createInvisibleButton(200, 580, 90, 45);
        btnSave.addActionListener(e -> saveBuktiTransaksi(panel));
        panel.add(btnSave);

        // Tombol Exit
        JButton btnExit = createInvisibleButton(355, 580, 90, 45);
        btnExit.addActionListener(e -> System.exit(0));
        panel.add(btnExit);

        add(panel);
        setVisible(true);

        // Jika status lunas, update status meja sesuai jenis kunjungan
        if (statusPembayaran.equalsIgnoreCase("lunas")) {
            updateStatusMeja(idPesanan, jenisKunjungan);
        }
    }

    private class TransaksiData {
        private String namaPelanggan;
        private String metodePembayaran;
        private String statusPembayaran;
        private double totalPembayaran;
        private String jenisKunjungan;

        public TransaksiData(String namaPelanggan, String metodePembayaran,
                             String statusPembayaran, double totalPembayaran,
                             String jenisKunjungan) {
            this.namaPelanggan = namaPelanggan;
            this.metodePembayaran = metodePembayaran;
            this.statusPembayaran = statusPembayaran;
            this.totalPembayaran = totalPembayaran;
            this.jenisKunjungan = jenisKunjungan;
        }

        public String getNamaPelanggan() { return namaPelanggan; }
        public String getMetodePembayaran() { return metodePembayaran; }
        public String getStatusPembayaran() { return statusPembayaran; }
        public double getTotalPembayaran() { return totalPembayaran; }
        public String getJenisKunjungan() { return jenisKunjungan; }
    }

    private TransaksiData getDataTransaksi(int idPesanan) {
        try {
            String query = "SELECT pl.Nama AS namaPelanggan, " +
                    "pm.Metode_Pembayaran AS metodePembayaran, " +
                    "pm.Status AS statusPembayaran, " +
                    "pm.Total_Pembayaran AS totalPembayaran, " +
                    "p.Jenis_Kunjungan AS jenisKunjungan " +
                    "FROM PEMBAYARAN pm " +
                    "JOIN PESANAN p ON pm.ID_Pesanan = p.ID_Pesanan " +
                    "JOIN PELANGGAN pl ON p.ID_Pelanggan = pl.ID_Pelanggan " +
                    "WHERE p.ID_Pesanan = ?";

            try (PreparedStatement ps = koneksi.prepareStatement(query)) {
                ps.setInt(1, idPesanan);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return new TransaksiData(
                            rs.getString("namaPelanggan"),
                            rs.getString("metodePembayaran"),
                            rs.getString("statusPembayaran"),
                            rs.getDouble("totalPembayaran"),
                            rs.getString("jenisKunjungan")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gagal mengambil data transaksi: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private void saveBuktiTransaksi(JPanel panel) {
        try {
            BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            panel.paint(g2d);
            g2d.dispose();

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Simpan Bukti Transaksi");
            fileChooser.setSelectedFile(new File("BuktiTransaksi.png"));
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".png")) {
                    fileToSave = new File(filePath + ".png");
                }
                ImageIO.write(image, "png", fileToSave);
                JOptionPane.showMessageDialog(this, "Bukti transaksi berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan bukti transaksi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatusMeja(int idPesanan, String jenisKunjungan) {
        try {
            String sql = "SELECT p.No_Meja FROM PESANAN p WHERE p.ID_Pesanan = ?";
            try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
                ps.setInt(1, idPesanan);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String noMeja = rs.getString("No_Meja");
                    if (noMeja != null) {
                        String newStatus = "tersedia";
                        if (jenisKunjungan.equalsIgnoreCase("reservasi")) {
                            newStatus = "dipesan";
                        } else if (jenisKunjungan.equalsIgnoreCase("dine_in")) {
                            newStatus = "terpakai";
                        }

                        String updateSql = "UPDATE MEJA SET Status = ? WHERE No_Meja = ?";
                        try (PreparedStatement psUpdate = koneksi.prepareStatement(updateSql)) {
                            psUpdate.setString(1, newStatus);
                            psUpdate.setString(2, noMeja);
                            psUpdate.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gagal update status meja: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField createTransparentTextField(String text, int x, int y) {
        JTextField field = new JTextField(text);
        field.setBounds(x, y, 200, 30);
        field.setBorder(null);
        field.setOpaque(false);
        field.setForeground(Color.WHITE);
        field.setFont(new Font("Arial", Font.BOLD, 12));
        field.setEditable(false);
        return field;
    }

    private JButton createInvisibleButton(int x, int y, int width, int height) {
        JButton button = new JButton();
        button.setBounds(x, y, width, height);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        return button;
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
        SwingUtilities.invokeLater(() -> {
            new BuktiTransaksi(1); // Contoh dengan ID pesanan 1
        });
    }
}