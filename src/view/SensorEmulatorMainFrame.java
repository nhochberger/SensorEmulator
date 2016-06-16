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
import controller.events.OpticalSensorRequestEvent;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.gui.EDTSafeFrame;
import hochberger.utilities.gui.lookandfeel.SetLookAndFeelTo;
import hochberger.utilities.mathematics.Vector3D;
import hochberger.utilities.text.Text;
import hochberger.utilities.text.i18n.DirectI18N;
import model.HeightMap;
import model.Position;
import net.miginfocom.swing.MigLayout;

public class SensorEmulatorMainFrame extends EDTSafeFrame {

    private final BasicSession session;
    private JTextArea lidarResultTextArea;
    private TerrainVisualization visualization;
    private final FPSAnimator animator;

    public SensorEmulatorMainFrame(final BasicSession session) {
        super(session.getProperties().title() + Text.space() + session.getProperties().version());
        this.session = session;
        this.animator = new FPSAnimator(60);
    }

    @Override
    protected void buildUI() {
        setSize(825, 975);
        center();
        disposeOnClose();
        SetLookAndFeelTo.systemLookAndFeel();
        setContentPane(new JPanel(new MigLayout("", "5[]5", "[]")));
        disposeOnClose();
        add(settingsPanel(), "grow, wrap");
        add(lidarPanel(), "grow, wrap");
        add(opticalSensorPanel(), "grow");
    }

    private JPanel settingsPanel() {
        final JPanel panel = new JPanel(new MigLayout("", "[]100[]5[]15[]5[]100[]", "[][][]"));
        panel.setBorder(BorderFactory.createTitledBorder(new DirectI18N("Settings").toString()));
        panel.add(importButton(), "cell 0 0");

        panel.add(new JLabel(new DirectI18N("Position: ").toString()), "cell 1 0");
        final JTextField positionX = new JTextField("0.0", 5);
        final JTextField positionY = new JTextField("500.0", 5);
        final JTextField positionZ = new JTextField("0.0", 5);
        panel.add(positionX, "cell 2 0");
        panel.add(positionY, "cell 2 1");
        panel.add(positionZ, "cell 2 2");
        panel.add(new JLabel(new DirectI18N("Direction: ").toString()), "cell 3 0");
        final JTextField directionX = new JTextField("0.0", 5);
        final JTextField directionY = new JTextField("-1.0", 5);
        final JTextField directionZ = new JTextField("0.0", 5);
        panel.add(directionX, "cell 4 0");
        panel.add(directionY, "cell 4 1");
        panel.add(directionZ, "cell 4 2");

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
                    SensorEmulatorMainFrame.this.session.getEventBus().publishFromEDT(new OpticalSensorRequestEvent(position, direction));
                } catch (final NumberFormatException exception) {
                    SensorEmulatorMainFrame.this.lidarResultTextArea.setText(new DirectI18N("Please check input").toString());
                }
            }
        });
        panel.add(issueRequestButton, "cell 0 2");
        panel.add(screenshotStoragePanel(), "cell 5 0, span 1 2");
        return panel;
    }

    private JPanel screenshotStoragePanel() {
        final JPanel panel = new JPanel(new MigLayout());
        panel.add(new JLabel(new DirectI18N("Screenshot storage:").toString()), "wrap");
        final JLabel folderLabel = new JLabel(System.getProperty("user.home"));
        panel.add(folderLabel, "wrap");
        final JButton button = new JButton(new DirectI18N("Choose Folder...").toString());
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                final JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);
                final int dialogResult = fileChooser.showOpenDialog(frame());
                if (!(JFileChooser.APPROVE_OPTION == dialogResult)) {
                    return;
                }
                final String screenshotStoragePath = fileChooser.getSelectedFile().getAbsolutePath();
                SensorEmulatorMainFrame.this.session.getLogger().info(screenshotStoragePath + " was selected as screenshot storage");
                SensorEmulatorMainFrame.this.visualization.setScreenshotStorageFolder(screenshotStoragePath);
                folderLabel.setText(screenshotStoragePath);
            }
        });
        panel.add(button);
        return panel;
    }

    private JPanel opticalSensorPanel() {
        final JPanel panel = new JPanel(new MigLayout());
        panel.setBorder(BorderFactory.createTitledBorder(new DirectI18N("Optical Sensor").toString()));
        final GLProfile glp = GLProfile.getDefault();
        final GLCapabilities caps = new GLCapabilities(glp);
        final GLCanvas canvas = new GLCanvas(caps);
        canvas.setPreferredSize(new Dimension(775, 500));
        this.visualization = new TerrainVisualization(775, 500);
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
        this.lidarResultTextArea = new JTextArea();
        this.lidarResultTextArea.setPreferredSize(new Dimension(775, 225));
        panel.add(this.lidarResultTextArea);
        return panel;
    }

    public void setLidarResultText(final String text) {
        this.lidarResultTextArea.setText(text);
    }

    public void setHeightMap(final HeightMap map) {
        this.visualization.setPoints(map);
    }

    public void setOpticalSensor(final Position position, final Position viewTargetPosition) {
        this.visualization.setOpticalSensor(position, viewTargetPosition);
    }

    public String prepareScreenshot() {
        return this.visualization.prepareScreenshot();
    }
}
