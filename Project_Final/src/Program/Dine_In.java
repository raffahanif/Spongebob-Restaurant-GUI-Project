package Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Dine_In {
    private JFrame frame;
    private JPanel panel;
    private JTextField textField1;
    private JComboBox<String> comboBox;
    private Connection koneksi;

    public Dine_In() {
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
        frame = new JFrame("DINE IN");
        frame.setSize(500, 700);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon image = new ImageIcon("src/resources/images/Desain_Tampilan/Dine_in.jpg");
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

        // TextField untuk Nama
        textField1 = new JTextField();
        textField1.setFont(new Font("Arial", Font.BOLD, 16));
        textField1.setOpaque(false);
        textField1.setBounds(113, 259, 272, 34);
        textField1.setBorder(null);
        panel.add(textField1);

        // ComboBox untuk meja
        comboBox = new JComboBox<>(getMejaTersedia());
        comboBox.setBounds(140, 340, 170, 34);
        comboBox.setBackground(new Color(255, 255, 255, 200));
        panel.add(comboBox);

        // Tombol Back
        createButton("", 23, 30, 58, 35, () -> {
            frame.dispose();
            new Beranda();
        });

        // Tombol Next
        createButton("Lanjutkan", 188, 600, 100, 40, () -> {
            if (validateInput()) {
                try {
                    if (saveDineInData()) {
                        frame.dispose();
                        new Menu1("Dine_In");
                    }
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

    private String[] getMejaTersedia() {
        List<String> mejaList = new ArrayList<>();
        try (Statement stmt = koneksi.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT No_Meja, Kapasitas FROM MEJA WHERE Status = 'tersedia'")) {

            while (rs.next()) {
                mejaList.add(rs.getString("No_Meja") + " (Kapasitas: " + rs.getInt("Kapasitas") + ")");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error mendapatkan data meja: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return mejaList.toArray(new String[0]);
    }

    private boolean validateInput() {
        if (textField1.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Nama harus diisi", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (comboBox.getSelectedItem() == null || comboBox.getSelectedItem().toString().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Silakan pilih meja", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean saveDineInData() throws SQLException {
        try {
            koneksi.setAutoCommit(false);

            // 1. Cek atau buat data pelanggan
            int idPelanggan = getOrCreatePelanggan(textField1.getText().trim());

            // 2. Ambil No_Meja yang dipilih (tanpa informasi kapasitas)
            String selectedMeja = (String) comboBox.getSelectedItem();
            String noMeja = selectedMeja.split(" ")[0];

            // 3. Update status meja
            updateStatusMeja(noMeja, "terpakai");

            // 4. Buat pesanan dine-in dan dapatkan ID_Pesanan
            String insertPesanan = "INSERT INTO PESANAN (ID_Pelanggan, No_Meja, Jenis_Kunjungan, Status) VALUES (?, ?, 'dine_in', 'draft')";
            try (PreparedStatement ps = koneksi.prepareStatement(insertPesanan, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, idPelanggan);
                ps.setString(2, noMeja);
                ps.executeUpdate();

                // Simpan ID_Pesanan untuk digunakan di Menu1
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        Menu1.idPesanan = rs.getInt(1);
                    }
                }
            }

            koneksi.commit();
            return true;
        } catch (SQLException e) {
            koneksi.rollback();
            throw e;
        } finally {
            koneksi.setAutoCommit(true);
        }
    }

    private int getOrCreatePelanggan(String nama) throws SQLException {
        // Cek apakah pelanggan sudah ada
        String queryCek = "SELECT ID_Pelanggan FROM PELANGGAN WHERE Nama = ?";
        try (PreparedStatement ps = koneksi.prepareStatement(queryCek)) {
            ps.setString(1, nama);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID_Pelanggan");
            }
        }

        // Jika tidak ada, buat baru
        String queryInsert = "INSERT INTO PELANGGAN (Nama, No_HP) VALUES (?, NULL)";
        try (PreparedStatement ps = koneksi.prepareStatement(queryInsert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nama);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        throw new SQLException("Gagal membuat/mendapatkan ID Pelanggan");
    }

    private void updateStatusMeja(String noMeja, String status) throws SQLException {
        String query = "UPDATE MEJA SET Status = ? WHERE No_Meja = ?";
        try (PreparedStatement ps = koneksi.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setString(2, noMeja);
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
        SwingUtilities.invokeLater(() -> new Dine_In());
    }
}