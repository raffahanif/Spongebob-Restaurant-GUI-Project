package Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Take_Away {
    private JFrame frame;
    private JPanel panel;
    private JTextField nameField;
    private Connection koneksi;

    public Take_Away() {
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
        frame = new JFrame("TAKE AWAY");
        frame.setSize(500, 700);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon image = new ImageIcon("src/resources/images/Desain_Tampilan/TakeAway.jpg");
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

        // Field untuk input nama pelanggan
        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.BOLD, 16));
        nameField.setOpaque(false);
        nameField.setBorder(null);
        nameField.setBounds(113, 259, 272, 34);
        panel.add(nameField);

        // Membuat tombol Back
        createButton("", 23, 30, 58, 35, () -> {
            frame.dispose();
            new Beranda();
        });

        // Membuat tombol Next
        createButton("Lanjutkan", 188, 600, 100, 40, () -> {
            if (validateInput()) {
                try {
                    int idPesanan = saveTakeAwayData();
                    Menu1.idPesanan = idPesanan; // Set the static variable
                    frame.dispose();
                    new Menu1("Take_Away");
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(frame,
                            "Gagal menyimpan data: " + e.getMessage(),
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private boolean validateInput() {
        String nama = nameField.getText().trim();

        if (nama.isEmpty()) {
            showError("Nama pelanggan harus diisi");
            return false;
        }

        if (nama.length() > 50) {
            showError("Nama terlalu panjang (maks 50 karakter)");
            return false;
        }

        if (!nama.matches("^[a-zA-Z0-9 .'-]*$")) {
            showError("Nama mengandung karakter tidak valid");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
        nameField.setBackground(new Color(255, 200, 200));
        Timer timer = new Timer(1000, e -> {
            nameField.setBackground(Color.WHITE);
        });
        timer.setRepeats(false);
        timer.start();
    }

    // Perbaiki metode saveTakeAwayData dengan resource management yang lebih baik
    private int saveTakeAwayData() throws SQLException {
        Connection conn = null;
        PreparedStatement psPelanggan = null;
        PreparedStatement psPesanan = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Create customer data
            String queryPelanggan = "INSERT INTO pelanggan (Nama, No_HP, Tanggal_Daftar) VALUES (?, NULL, NOW())";
            psPelanggan = conn.prepareStatement(queryPelanggan, Statement.RETURN_GENERATED_KEYS);
            psPelanggan.setString(1, nameField.getText().trim());
            psPelanggan.executeUpdate();

            rs = psPelanggan.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("Gagal mendapatkan ID pelanggan");
            }
            int idPelanggan = rs.getInt(1);

            // 2. Create take away order
            String queryPesanan = "INSERT INTO pesanan (ID_Pelanggan, Jenis_Kunjungan, Status) VALUES (?, 'take_away', 'draft')";
            psPesanan = conn.prepareStatement(queryPesanan, Statement.RETURN_GENERATED_KEYS);
            psPesanan.setInt(1, idPelanggan);
            psPesanan.executeUpdate();

            // Get the generated order ID
            rs = psPesanan.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("Gagal mendapatkan ID pesanan");
            }
            int idPesanan = rs.getInt(1);

            conn.commit();
            return idPesanan;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (psPelanggan != null) psPelanggan.close();
            if (psPesanan != null) psPesanan.close();
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    private int createPelanggan(String nama) throws SQLException {
        String query = "INSERT INTO pelanggan (Nama, No_HP, Tanggal_Daftar) VALUES (?, '', NOW())";
        try (PreparedStatement ps = koneksi.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nama);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Gagal membuat pelanggan baru");
    }

    private void createPesananTakeAway(int idPelanggan) throws SQLException {
        String query = "INSERT INTO pesanan (ID_Pelanggan, Jenis_Kunjungan, Status) VALUES (?, 'take_away', 'diproses')";
        try (PreparedStatement ps = koneksi.prepareStatement(query)) {
            ps.setInt(1, idPelanggan);
            ps.executeUpdate();
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
        SwingUtilities.invokeLater(() -> new Take_Away());
    }
}