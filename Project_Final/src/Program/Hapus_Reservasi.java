package Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hapus_Reservasi {
    private JFrame frame;
    private JPanel panel;
    private JTextField textField1;
    private Connection koneksi;

    public Hapus_Reservasi() {
        initializeDatabaseConnection();
        initializeUI();
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

    private void initializeUI() {
        frame = new JFrame("HAPUS RESERVASI");
        frame.setSize(500, 700);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon image = new ImageIcon("src/resources/images/Desain_Tampilan/Hapus_Reservasi.jpg");
                    g.drawImage(image.getImage(), 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        panel.setLayout(null);
        panel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        frame.setContentPane(panel);

        // TextField untuk Nama Pelanggan yang akan menghapus reservasi
        textField1 = new JTextField();
        textField1.setFont(new Font("Arial", Font.BOLD, 16));
        textField1.setOpaque(false);
        textField1.setBounds(113, 259, 272, 34);
        textField1.setBorder(null);
        panel.add(textField1);

        // Tombol Back
        createButton("", 23, 30, 58, 35, () -> {
            frame.dispose();
            new Beranda();
        });

        // Tombol Hapus Reservasi
        createButton("Hapus", 180, 400, 140, 40, () -> {
            try {
                hapusReservasi();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Gagal menghapus reservasi: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private void hapusReservasi() throws SQLException {
        String namaPelanggan = textField1.getText().trim();

        if (namaPelanggan.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Nama pelanggan harus diisi",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Konfirmasi sebelum menghapus
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Apakah Anda yakin ingin menghapus semua reservasi untuk pelanggan: " + namaPelanggan + "?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            koneksi.setAutoCommit(false);

            // 1. Dapatkan ID_Pelanggan berdasarkan nama
            int idPelanggan = getPelangganId(namaPelanggan);
            if (idPelanggan == -1) {
                JOptionPane.showMessageDialog(frame,
                        "Pelanggan dengan nama '" + namaPelanggan + "' tidak ditemukan",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Dapatkan semua ID_Reservasi yang terkait
            List<Integer> reservasiIds = getReservasiIds(idPelanggan);

            if (reservasiIds.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Tidak ada reservasi ditemukan untuk pelanggan ini",
                        "Informasi", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            System.out.println("Memulai penghapusan untuk " + reservasiIds.size() + " reservasi...");

            // 3. Hapus data terkait secara berurutan
            int deletedPayments = hapusPembayaran(reservasiIds);
            System.out.println("Deleted " + deletedPayments + " pembayaran");

            int deletedDetails = hapusDetailPesanan(reservasiIds);
            System.out.println("Deleted " + deletedDetails + " detail pesanan");

            int deletedOrders = hapusPesanan(reservasiIds);
            System.out.println("Deleted " + deletedOrders + " pesanan");

            int updatedTables = updateStatusMeja(reservasiIds);
            System.out.println("Updated " + updatedTables + " meja");

            int deletedReservations = hapusReservasi(reservasiIds);
            System.out.println("Deleted " + deletedReservations + " reservasi");

            // 4. Hapus pelanggan jika tidak ada reservasi/pesanan lain
            hapusPelangganJikaTidakAdaDataLain(idPelanggan);

            koneksi.commit();

            JOptionPane.showMessageDialog(frame,
                    "Berhasil menghapus " + deletedReservations + " reservasi untuk pelanggan " + namaPelanggan,
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

            // Reset field
            textField1.setText("");

        } catch (SQLException e) {
            koneksi.rollback();
            throw e;
        } finally {
            try {
                koneksi.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private int getPelangganId(String namaPelanggan) throws SQLException {
        String query = "SELECT ID_Pelanggan FROM pelanggan WHERE Nama = ?";
        try (PreparedStatement ps = koneksi.prepareStatement(query)) {
            ps.setString(1, namaPelanggan);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("ID_Pelanggan") : -1;
        }
    }

    private List<Integer> getReservasiIds(int idPelanggan) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT ID_Reservasi FROM reservasi WHERE ID_Pelanggan = ?";
        try (PreparedStatement ps = koneksi.prepareStatement(query)) {
            ps.setInt(1, idPelanggan);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("ID_Reservasi"));
            }
        }
        return ids;
    }

    private int hapusDetailPesanan(List<Integer> reservasiIds) throws SQLException {
        if (reservasiIds.isEmpty()) return 0;

        String query = "DELETE FROM detail_pesanan WHERE ID_Pesanan IN " +
                "(SELECT ID_Pesanan FROM pesanan WHERE ID_Reservasi IN (" +
                String.join(",", Collections.nCopies(reservasiIds.size(), "?")) + "))";

        try (PreparedStatement ps = koneksi.prepareStatement(query)) {
            for (int i = 0; i < reservasiIds.size(); i++) {
                ps.setInt(i + 1, reservasiIds.get(i));
            }
            return ps.executeUpdate();
        }
    }

    private int hapusPembayaran(List<Integer> reservasiIds) throws SQLException {
        if (reservasiIds.isEmpty()) return 0;

        String query = "DELETE FROM pembayaran WHERE ID_Pesanan IN " +
                "(SELECT ID_Pesanan FROM pesanan WHERE ID_Reservasi IN (" +
                String.join(",", Collections.nCopies(reservasiIds.size(), "?")) + "))";

        try (PreparedStatement ps = koneksi.prepareStatement(query)) {
            for (int i = 0; i < reservasiIds.size(); i++) {
                ps.setInt(i + 1, reservasiIds.get(i));
            }
            return ps.executeUpdate();
        }
    }

    private int hapusPesanan(List<Integer> reservasiIds) throws SQLException {
        if (reservasiIds.isEmpty()) return 0;

        String query = "DELETE FROM pesanan WHERE ID_Reservasi IN (" +
                String.join(",", Collections.nCopies(reservasiIds.size(), "?")) + ")";

        try (PreparedStatement ps = koneksi.prepareStatement(query)) {
            for (int i = 0; i < reservasiIds.size(); i++) {
                ps.setInt(i + 1, reservasiIds.get(i));
            }
            return ps.executeUpdate();
        }
    }

    private int updateStatusMeja(List<Integer> reservasiIds) throws SQLException {
        if (reservasiIds.isEmpty()) return 0;

        String query = "UPDATE meja SET Status = 'tersedia' WHERE No_Meja IN " +
                "(SELECT No_Meja FROM reservasi WHERE ID_Reservasi IN (" +
                String.join(",", Collections.nCopies(reservasiIds.size(), "?")) + "))";

        try (PreparedStatement ps = koneksi.prepareStatement(query)) {
            for (int i = 0; i < reservasiIds.size(); i++) {
                ps.setInt(i + 1, reservasiIds.get(i));
            }
            return ps.executeUpdate();
        }
    }

    private int hapusReservasi(List<Integer> reservasiIds) throws SQLException {
        if (reservasiIds.isEmpty()) return 0;

        String query = "DELETE FROM reservasi WHERE ID_Reservasi IN (" +
                String.join(",", Collections.nCopies(reservasiIds.size(), "?")) + ")";

        try (PreparedStatement ps = koneksi.prepareStatement(query)) {
            for (int i = 0; i < reservasiIds.size(); i++) {
                ps.setInt(i + 1, reservasiIds.get(i));
            }
            return ps.executeUpdate();
        }
    }

    private void hapusPelangganJikaTidakAdaDataLain(int idPelanggan) throws SQLException {
        String queryCekReservasi = "SELECT COUNT(*) FROM reservasi WHERE ID_Pelanggan = ?";
        String queryCekPesanan = "SELECT COUNT(*) FROM pesanan WHERE ID_Pelanggan = ? AND ID_Reservasi IS NULL";

        try (PreparedStatement psReservasi = koneksi.prepareStatement(queryCekReservasi);
             PreparedStatement psPesanan = koneksi.prepareStatement(queryCekPesanan)) {

            psReservasi.setInt(1, idPelanggan);
            ResultSet rsReservasi = psReservasi.executeQuery();
            rsReservasi.next();
            int countReservasi = rsReservasi.getInt(1);

            psPesanan.setInt(1, idPelanggan);
            ResultSet rsPesanan = psPesanan.executeQuery();
            rsPesanan.next();
            int countPesanan = rsPesanan.getInt(1);

            if (countReservasi == 0 && countPesanan == 0) {
                String queryHapus = "DELETE FROM pelanggan WHERE ID_Pelanggan = ?";
                try (PreparedStatement psHapus = koneksi.prepareStatement(queryHapus)) {
                    psHapus.setInt(1, idPelanggan);
                    int deleted = psHapus.executeUpdate();
                    System.out.println("Deleted " + deleted + " pelanggan");
                }
            }
        }
    }

    private void createButton(String text, int x, int y, int w, int h, Runnable action) {
        JPanel button = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                if (!getBackground().equals(new Color(0, 0, 0, 0))) {
                    g2d.setColor(getBackground());
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                }

                if (!text.trim().isEmpty()) {
                    g2d.setColor(new Color(255, 255, 255, 120));
                    g2d.setStroke(new BasicStroke(1.5f));
                    g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);

                    g2d.setColor(new Color(250, 244, 84));
                    g2d.setFont(new Font("Arial", Font.BOLD, 16));
                    FontMetrics fm = g2d.getFontMetrics();
                    int textX = (getWidth() - fm.stringWidth(text)) / 2;
                    int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.drawString(text, textX, textY);
                }
            }
        };
        button.setBounds(x, y, w, h);
        button.setOpaque(false);
        button.setBackground(text.trim().isEmpty() ? new Color(0, 0, 0, 0) : new Color(44, 101, 167));

        button.addMouseListener(new MouseAdapter() {
            private final Color defaultColor = button.getBackground();
            private final Color hoverColor = text.trim().isEmpty()
                    ? new Color(0, 0, 255, 10)
                    : new Color(0, 0, 255, 170);

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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Hapus_Reservasi());
    }
}