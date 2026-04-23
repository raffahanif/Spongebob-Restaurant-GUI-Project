package Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Beranda {
    private JFrame frame;
    private JPanel panel, reservasi, narahubung;
    private JPanel n_reservasiButton, nt_reservasiButton;

    public Beranda() {
        DatabaseConnection.getConnection();

        frame = new JFrame("BERANDA");
        frame.setSize(500, 700);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon image = new ImageIcon("src/resources/images/Desain_Tampilan/Beranda.jpg");
                    g.drawImage(image.getImage(), 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    // Fallback jika gambar tidak ditemukan
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());}}};
        panel.setLayout(null);
        panel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        frame.setContentPane(panel);

        // Membuat tombol RESERVASI
        nt_reservasiButton = createButton("RESERVASI", 90, 378, 310, 58, () -> {
            panel.remove(nt_reservasiButton); // Hapus tombol TANPA RESERVASI

            // Buat tombol Hapus Reservasi dan Ke menu Reservasi
            createButton("Hapus Reservasi", 90, 378, 155, 60, () -> {
                frame.dispose();
                new Hapus_Reservasi();});

            createButton("Reservasi", 245, 378, 155, 60, () -> {
                frame.dispose();
                new Reservasi();});

            // Perbarui tampilan
            panel.revalidate(); panel.repaint();});

        // Membuat tombol TANPA RESERVASI
        n_reservasiButton = createButton("TANPA RESERVASI", 90, 470, 310, 60, () -> {
            panel.remove(n_reservasiButton); // Hapus tombol TANPA RESERVASI

            // Buat tombol Dine In dan Take Away
            createButton("Dine In", 90, 470, 155, 60, () -> {
                frame.dispose();
                new Dine_In();});

            createButton("Take Away", 245, 470, 155, 60, () -> {
                frame.dispose();
                new Take_Away();});

            // Perbarui tampilan
            panel.revalidate(); panel.repaint(); });

        // Membuat tombol NARAHUBUNG
        createButton("NARAHUBUNG", 90, 565, 310, 40, () -> {
            frame.dispose();
            new Narahubung();
        });
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);}

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
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Poppins", Font.BOLD, 16));

                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(text, textX, textY);
            }
        };
        button.setBounds(x, y, w, h);
        button.setOpaque(false);
        button.setBackground(new Color(255, 255, 255, 60));

        button.addMouseListener(new MouseAdapter() {
            private final Color defaultColor = new Color(255, 255, 255, 60);
            private final Color hoverColor = new Color(200, 200, 255, 70);

            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    action.run();}}

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
                button.repaint();}

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(defaultColor);
                button.repaint();}
        });

        panel.add(button);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Beranda();
        });
    }
}