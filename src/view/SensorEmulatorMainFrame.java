package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import controller.ImportTerrainEvent;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.gui.EDTSafeFrame;
import hochberger.utilities.gui.lookandfeel.SetLookAndFeelTo;
import hochberger.utilities.text.Text;
import hochberger.utilities.text.i18n.DirectI18N;
import net.miginfocom.swing.MigLayout;

public class SensorEmulatorMainFrame extends EDTSafeFrame {

    private final BasicSession session;

    public SensorEmulatorMainFrame(final BasicSession session) {
        super(session.getProperties().title() + Text.space() + session.getProperties().version());
        this.session = session;
    }

    @Override
    protected void buildUI() {
        setSize(500, 500);
        center();
        disposeOnClose();
        SetLookAndFeelTo.systemLookAndFeel();
        setContentPane(new JPanel(new MigLayout()));
        disposeOnClose();
        getContentPane().add(importButton());
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

}
