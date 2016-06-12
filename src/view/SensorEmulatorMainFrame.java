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

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import controller.events.ImportTerrainEvent;
import controller.events.LidarRequestEvent;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.gui.EDTSafeFrame;
import hochberger.utilities.gui.lookandfeel.SetLookAndFeelTo;
import hochberger.utilities.text.Text;
import hochberger.utilities.text.i18n.DirectI18N;
import model.HeightMap;
import model.Position;
import model.Vector3D;
import net.miginfocom.swing.MigLayout;

public class SensorEmulatorMainFrame extends EDTSafeFrame {

    private final BasicSession session;
    private JTextArea lidarResultTextField;
    private TerrainVisualization visualization;
    private final FPSAnimator animator;

    public SensorEmulatorMainFrame(final BasicSession session) {
        super(session.getProperties().title() + Text.space() + session.getProperties().version());
        this.session = session;
        this.animator = new FPSAnimator(60);
    }

    @Override
    protected void buildUI() {
        setSize(1000, 1000);
        center();
        disposeOnClose();
        SetLookAndFeelTo.systemLookAndFeel();
        setContentPane(new JPanel(new MigLayout("", "[]", "[]")));
        disposeOnClose();
        add(settingsPanel(), "wrap");
        add(lidarPanel(), "wrap");
        add(opticalSensorPanel());
    }

    private JPanel settingsPanel() {
        final JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(new DirectI18N("Settings").toString()));
        panel.add(importButton());

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
        panel.add(issueRequestButton, "span 4, wrap");

        return panel;
    }

    private JPanel opticalSensorPanel() {
        final JPanel panel = new JPanel(new MigLayout());
        panel.setBorder(BorderFactory.createTitledBorder(new DirectI18N("Optical Sensor").toString()));
        final GLProfile glp = GLProfile.getDefault();
        final GLCapabilities caps = new GLCapabilities(glp);
        final GLCanvas canvas = new GLCanvas(caps);
        canvas.setPreferredSize(new Dimension(775, 500));
        this.visualization = new TerrainVisualization();
        canvas.addGLEventListener(this.visualization);
        this.animator.add(canvas);
        this.animator.start();
        panel.add(canvas);
        return panel;
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
        this.lidarResultTextField = new JTextArea();
        this.lidarResultTextField.setSize(150, 150);
        this.lidarResultTextField.setPreferredSize(new Dimension(775, 225));
        panel.add(this.lidarResultTextField, "span 4");
        return panel;
    }

    public void setLidarResultText(final String text) {
        this.lidarResultTextField.setText(text);
    }

    public void setHeightMap(final HeightMap map) {
        this.visualization.setPoints(map);
    }
}
