package view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import controller.events.ImportTerrainEvent;
import controller.events.LidarRequestEvent;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.gui.EDTSafeFrame;
import hochberger.utilities.gui.lookandfeel.SetLookAndFeelTo;
import hochberger.utilities.text.Text;
import hochberger.utilities.text.i18n.DirectI18N;
import model.Position;
import model.Vector3D;
import net.miginfocom.swing.MigLayout;

public class SensorEmulatorMainFrame extends EDTSafeFrame {

    private final BasicSession session;
    private JTextArea lidarResultTextField;

    public SensorEmulatorMainFrame(final BasicSession session) {
        super(session.getProperties().title() + Text.space() + session.getProperties().version());
        this.session = session;
    }

    @Override
    protected void buildUI() {
        setSize(1000, 500);
        center();
        disposeOnClose();
        SetLookAndFeelTo.systemLookAndFeel();
        setContentPane(new JPanel(new MigLayout("", "[]", "[]")));
        disposeOnClose();
        add(importButton(), "wrap");
        add(lidarPanel(), "wrap");
    }

    private JButton importButton() {
        final JButton importButton = new JButton(new DirectI18N("Import...").toString());
        importButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                final JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
                fileChooser.showOpenDialog(frame());
                if (null == fileChooser.getSelectedFile()) {
                    return;
                }
                SensorEmulatorMainFrame.this.session.getEventBus().publishFromEDT(new ImportTerrainEvent(fileChooser.getSelectedFile().getAbsolutePath()));
            }
        });
        return importButton;
    }

    private JPanel lidarPanel() {
        final JPanel panel = new JPanel(new MigLayout());
        panel.setBorder(BorderFactory.createTitledBorder(new DirectI18N("Lidar").toString()));
        panel.add(new JLabel(new DirectI18N("Position: ").toString()));
        final JTextField positionX = new JTextField("0.0", 5);
        final JTextField positionY = new JTextField("0.0", 5);
        final JTextField positionZ = new JTextField("0.0", 5);
        panel.add(positionX);
        panel.add(positionY);
        panel.add(positionZ, "wrap");
        panel.add(new JLabel(new DirectI18N("Direction: ").toString()));
        final JTextField directionX = new JTextField("1.0", 5);
        final JTextField directionY = new JTextField("-1.0", 5);
        final JTextField directionZ = new JTextField("1.0", 5);
        panel.add(directionX);
        panel.add(directionY);
        panel.add(directionZ, "wrap");
        final JLabel result = new JLabel(new DirectI18N("Result:").toString());
        panel.add(result);
        this.lidarResultTextField = new JTextArea();
        this.lidarResultTextField.setSize(150, 150);
        this.lidarResultTextField.setPreferredSize(new Dimension(775, 225));
        panel.add(this.lidarResultTextField, "span 3, wrap");
        final JButton issueRequestButton = new JButton(new DirectI18N("Send").toString());
        issueRequestButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                try {
                    final double posX = Double.parseDouble(positionX.getText());
                    final double posY = Double.parseDouble(positionY.getText());
                    final double posZ = Double.parseDouble(positionZ.getText());
                    final double dirX = Double.parseDouble(directionX.getText());
                    final double dirY = Double.parseDouble(directionY.getText());
                    final double dirZ = Double.parseDouble(directionZ.getText());
                    final Vector3D direction = new Vector3D(dirX, dirY, dirZ);
                    final Position position = new Position(posX, posY, posZ);
                    SensorEmulatorMainFrame.this.session.getEventBus().publishFromEDT(new LidarRequestEvent(position, direction));
                } catch (final NumberFormatException exception) {
                    SensorEmulatorMainFrame.this.lidarResultTextField.setText(new DirectI18N("Please check input").toString());
                }
            }
        });
        panel.add(issueRequestButton, "span 2");
        return panel;
    }

    public void setLidarResultText(final String text) {
        this.lidarResultTextField.setText(text);
    }
}
