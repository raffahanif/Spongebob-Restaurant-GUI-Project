package Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.SimpleDateFormat;

public class CekPesananGUI {
    static Connection koneksi;
    private JFrame frame;
    private JPanel panel;

    // Komponen untuk menampilkan detail transaksi
    private JTextArea detailPesananArea;
    private JTextArea ringkasanArea;
    private JTextField pembayaranArea;

    public CekPesananGUI() {
        initializeDatabaseConnection();

        frame = new JFrame("CEK PESANAN");
        frame.setSize(500, 700);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon image = new ImageIcon("src/resources/images/Desain_Tampilan/Cek_Pesanan.jpg");
                    g.drawImage(image.getImage(), 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    // Fallback jika gambar tidak ditemukan
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        panel.setLayout(null);
        panel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        frame.setContentPane(panel);

        // Membuat tombol PEMBAYARAN
        createButton("PEMBAYARAN", 165, 600, 160, 40, () -> {
            double total = getTotalPembayaran();
            if (total > 0) {
                frame.dispose();
                new MetodePembayaran(getNamaPelanggan(), total, Menu1.idPesanan);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Tidak ada pesanan yang perlu dibayar",
                        "Informasi", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Area untuk detail pesanan
        detailPesananArea = new JTextArea();
        detailPesananArea.setBounds(86, 300, 315, 190);
        detailPesananArea.setEditable(false);
        detailPesananArea.setFont(new Font("Arial", Font.BOLD, 14));
        detailPesananArea.setForeground(Color.WHITE);
        detailPesananArea.setOpaque(false);
        loadDetailPesanan();
        panel.add(detailPesananArea);

        // Area untuk ringkasan transaksi
        ringkasanArea = new JTextArea();
        ringkasanArea.setBounds(86, 150, 315, 90);
        ringkasanArea.setEditable(false);
        ringkasanArea.setFont(new Font("Arial", Font.BOLD, 14));
        ringkasanArea.setForeground(Color.WHITE);
        ringkasanArea.setOpaque(false);
        loadRingkasanTransaksi();
        panel.add(ringkasanArea);

        // Area untuk pembayaran
        pembayaranArea = new JTextField();
        pembayaranArea.setBounds(160, 515, 240, 44);
        pembayaranArea.setEditable(false);
        pembayaranArea.setFont(new Font("Arial", Font.BOLD, 16));
        pembayaranArea.setOpaque(false);
        pembayaranArea.setForeground(Color.WHITE);
        pembayaranArea.setBorder(null);
        loadPembayaran();
        panel.add(pembayaranArea);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createButton(String text, int x, int y, int w, int h, Runnable action) {
        JPanel button = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Background tombol
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Border tombol
                g2d.setColor(new Color(255, 255, 255, 120));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);

                // Text tombol
                g2d.setColor(new Color(250, 244, 84));
                g2d.setFont(new Font("ARIAL", Font.BOLD, 16));

                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(text, textX, textY);
            }
        };
        button.setBounds(x, y, w, h);
        button.setOpaque(false);
        button.setBackground(new Color(44, 101, 167));

        button.addMouseListener(new MouseAdapter() {
            private final Color defaultColor = new Color(44, 101, 167);
            private final Color hoverColor = new Color(44, 101, 167, 170);

            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    action.run();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(defaultColor);
                button.repaint();
            }
        });

        panel.add(button);
        return button;
    }

    private void loadDetailPesanan() {
        try {
            StringBuilder sb = new StringBuilder();

            String query = "SELECT m.Nama_Menu, dp.Jumlah_Pesanan, dp.Harga_Satuan " +
                    "FROM DETAIL_PESANAN dp " +
                    "JOIN MENU m ON dp.ID_Menu = m.ID_Menu " +
                    "WHERE dp.ID_Pesanan = ?";

            try (PreparedStatement ps = koneksi.prepareStatement(query)) {
                ps.setInt(1, Menu1.idPesanan);
                ResultSet rs = ps.executeQuery();

                if (!rs.isBeforeFirst()) {
                    detailPesananArea.setText("Belum ada pesanan");
                    return;
                }

                double total = 0;
                while (rs.next()) {
                    String namaMenu = rs.getString("Nama_Menu");
                    int jumlah = rs.getInt("Jumlah_Pesanan");
                    double harga = rs.getDouble("Harga_Satuan");
                    double subtotal = jumlah * harga;

                    sb.append(String.format("%-20s %2d x %6.0f = %6.0f\n",
                            namaMenu, jumlah, harga, subtotal));
                    total += subtotal;
                }

                detailPesananArea.setText(sb.toString());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Gagal memuat detail pesanan: " + e.getMessage());
            detailPesananArea.setText("Gagal memuat detail pesanan");
        }
    }

    private void loadRingkasanTransaksi() {
        try {
            StringBuilder sb = new StringBuilder();

            String queryPesanan = "SELECT p.Jenis_Kunjungan, p.No_Meja, pl.Nama " +
                    "FROM PESANAN p " +
                    "JOIN PELANGGAN pl ON p.ID_Pelanggan = pl.ID_Pelanggan " +
                    "WHERE p.ID_Pesanan = ?";

            try (PreparedStatement ps = koneksi.prepareStatement(queryPesanan)) {
                ps.setInt(1, Menu1.idPesanan);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String jenisKunjungan = rs.getString("Jenis_Kunjungan");
                    String noMeja = rs.getString("No_Meja");
                    String nama = rs.getString("Nama");

                    sb.append("Nama: ").append(nama).append("\n");

                    if ("reservasi".equalsIgnoreCase(jenisKunjungan)) {
                        // Jika reservasi, tambahkan info reservasi
                        String queryReservasi = "SELECT Tanggal, JAM_RESERVASI FROM RESERVASI " +
                                "WHERE ID_Pelanggan = (SELECT ID_Pelanggan FROM PESANAN WHERE ID_Pesanan = ?) " +
                                "ORDER BY ID_Reservasi DESC LIMIT 1";

                        try (PreparedStatement psReservasi = koneksi.prepareStatement(queryReservasi)) {
                            psReservasi.setInt(1, Menu1.idPesanan);
                            ResultSet rsReservasi = psReservasi.executeQuery();

                            if (rsReservasi.next()) {
                                Date tanggal = rsReservasi.getDate("Tanggal");
                                Time waktu = rsReservasi.getTime("JAM_RESERVASI");

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

                                sb.append("Reservasi untuk: ").append(noMeja).append("\n");
                                sb.append("Tanggal: ").append(dateFormat.format(tanggal)).append("\n");
                                sb.append("Waktu: ").append(timeFormat.format(waktu)).append("\n");
                            }
                        }
                    } else if ("dine_in".equalsIgnoreCase(jenisKunjungan)) {
                        // Jika dine-in, cukup tampilkan meja
                        sb.append("Meja: ").append(noMeja).append("\n");
                    }
                    // Untuk take_away, tidak menampilkan info meja
                } else {
                    sb.append("Data pesanan tidak ditemukan");
                }

                ringkasanArea.setText(sb.toString());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Gagal memuat ringkasan transaksi: " + e.getMessage());
            ringkasanArea.setText("Gagal memuat ringkasan transaksi");
        }
    }

    private void loadPembayaran() {
        try {
            // Hitung total pembayaran
            String queryTotal = "SELECT SUM(Harga_Satuan * Jumlah_Pesanan) as Total " +
                    "FROM DETAIL_PESANAN " +
                    "WHERE ID_Pesanan = ?";

            try (PreparedStatement ps = koneksi.prepareStatement(queryTotal)) {
                ps.setInt(1, Menu1.idPesanan);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    double total = rs.getDouble("Total");
                    pembayaranArea.setText(String.format("Rp%,.0f", total));
                } else {
                    pembayaranArea.setText("Rp0");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Gagal memuat total pembayaran: " + e.getMessage());
            pembayaranArea.setText("Error");
        }
    }

    private double getTotalPembayaran() {
        try {
            String text = pembayaranArea.getText().replace("Rp", "").replace(",", "").trim();
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String getNamaPelanggan() {
        try {
            String query = "SELECT pl.Nama FROM PESANAN p " +
                    "JOIN PELANGGAN pl ON p.ID_Pelanggan = pl.ID_Pelanggan " +
                    "WHERE p.ID_Pesanan = ?";

            try (PreparedStatement ps = koneksi.prepareStatement(query)) {
                ps.setInt(1, Menu1.idPesanan);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return rs.getString("Nama");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Pelanggan";
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
            new CekPesananGUI();
        });
    }
}