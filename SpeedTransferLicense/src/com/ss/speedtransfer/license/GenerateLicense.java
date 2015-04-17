package com.ss.speedtransfer.license;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.jdatepicker.DateModel;
import net.sourceforge.jdatepicker.JDateComponentFactory;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

public class GenerateLicense implements ActionListener {

	public static String PROPERTIES_FILE = "SpeedTransferProperties.xml";
	public static String OUTPUT_DIR = "directory";
	public static String EXP_MSG = "expiry message";

	protected JFrame mainFrame = null;

	protected JTextField licNumberText = null;
	protected JTextField dirText = null;
	protected JButton dirButton = null;
	protected JRadioButton studioButton = null;
	protected JRadioButton browserButton = null;
	protected JCheckBox selectOnlyCb = null;
	protected DateModel<Date> expDateModel = new UtilDateModel();
	protected JDatePickerImpl expDate = null;
	protected JTextField expMessage = null;

	protected JButton generateButton = null;
	protected JButton cancelButton = null;
	protected JButton okButton = null;

	Properties props = null;

	final static GenerateLicense ui = new GenerateLicense();

	protected JDialog dialog = null;

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
	 */
	protected void createAndShowGUI() {

		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		mainFrame = new JFrame("Generate SpeedTransfer License File");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setPreferredSize(new Dimension(600, 325));

		JPanel frame = new JPanel();
		mainFrame.getContentPane().setLayout(new BorderLayout());
		mainFrame.getContentPane().add(frame, BorderLayout.NORTH);

		frame.setLayout(new GridBagLayout());

		// Add dummy row
		JLabel dummy = new JLabel("");
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = 3;
		c.ipady = 8;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.00;
		frame.add(dummy, c);

		// Add license number
		JLabel licNumberLabel = new JLabel("License number: ");
		c = new GridBagConstraints();
		c.insets = new Insets(0, 5, 0, 5);
		c.gridy = 1;
		c.ipady = 8;
		frame.add(licNumberLabel, c);

		licNumberText = new JTextField();
		c = new GridBagConstraints();
		c.gridy = 1;
		c.ipady = 8;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.00;
		frame.add(licNumberText, c);
		licNumberText.setText("");

		// Add Directory
		JLabel dirLabel = new JLabel("Put license file in: ");
		c = new GridBagConstraints();
		c.insets = new Insets(0, 5, 0, 5);
		c.gridy = 2;
		c.ipady = 8;
		frame.add(dirLabel, c);

		dirText = new JTextField();
		c = new GridBagConstraints();
		c.gridy = 2;
		c.ipady = 8;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.00;
		frame.add(dirText, c);
		dirText.setText(getProperties().getProperty(OUTPUT_DIR));

		dirButton = new JButton("...");
		dirButton.addActionListener(this);
		c = new GridBagConstraints();
		c.gridy = 2;
		c.insets = new Insets(0, 10, 0, 10);
		frame.add(dirButton, c);

		// Add dummy row
		dummy = new JLabel("");
		c = new GridBagConstraints();
		c.gridy = 3;
		c.gridwidth = 3;
		c.ipady = 8;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.00;
		frame.add(dummy, c);

		// Add type (Studio/Browser)
		JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		c = new GridBagConstraints();
		c.gridy = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		frame.add(typePanel, c);

		ButtonGroup typeGroup = new ButtonGroup();

		studioButton = new JRadioButton("Studio");
		studioButton.setSelected(true);
		typePanel.add(studioButton);

		browserButton = new JRadioButton("Browser");
		typePanel.add(browserButton);

		selectOnlyCb = new JCheckBox("Allow only SELECT in SQL");
		typePanel.add(selectOnlyCb, c);

		typeGroup.add(studioButton);
		typeGroup.add(browserButton);

		// Add dummy row
		dummy = new JLabel("");
		c = new GridBagConstraints();
		c.insets = new Insets(0, 0, 10, 0);
		c.gridy = 5;
		c.gridwidth = 3;
		c.ipady = 8;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.00;
		frame.add(dummy, c);

		JLabel expDateLabel = new JLabel("Expiry date (Leave empty for permanent license)");
		c = new GridBagConstraints();
		c.insets = new Insets(0, 5, 0, 5);
		c.anchor = GridBagConstraints.WEST;
		c.gridy = 6;
		c.ipady = 8;
		c.gridwidth = 3;
		frame.add(expDateLabel, c);

		expDate = (JDatePickerImpl) JDateComponentFactory.createJDatePicker(expDateModel);
		c = new GridBagConstraints();
		c.insets = new Insets(0, 5, 0, 5);
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 3;
		c.gridy = 7;
		c.ipady = 8;
		frame.add(expDate, c);

		final JLabel expMsgLabel = new JLabel("Message to show when license expires");

		expDateModel.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				toggleMessage();
			}
		});

		c = new GridBagConstraints();
		c.insets = new Insets(0, 5, 0, 5);
		c.anchor = GridBagConstraints.WEST;
		c.gridy = 8;
		c.ipady = 8;
		c.gridwidth = 3;
		frame.add(expMsgLabel, c);

		expMessage = new JTextField("Your trial period has ended");
		c = new GridBagConstraints();
		c.insets = new Insets(0, 5, 0, 5);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 3;
		c.gridy = 9;
		c.ipady = 8;
		frame.add(expMessage, c);
		if (getProperties().getProperty(EXP_MSG) != null && getProperties().getProperty(EXP_MSG).trim().length() > 0)
			expMessage.setText(getProperties().getProperty(EXP_MSG));

		// Add dummy row
		dummy = new JLabel("");
		c = new GridBagConstraints();
		c.insets = new Insets(0, 0, 10, 0);
		c.gridy = 10;
		c.gridwidth = 3;
		c.ipady = 8;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.00;
		frame.add(dummy, c);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		c = new GridBagConstraints();
		c.gridy = 11;
		c.gridx = 0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.EAST;
		c.weightx = 1.00;
		frame.add(buttonPanel, c);

		generateButton = new JButton("Generate");
		generateButton.setPreferredSize(new Dimension(100, generateButton.getPreferredSize().height));
		generateButton.addActionListener(this);
		buttonPanel.add(generateButton);

		cancelButton = new JButton("Cancel");
		cancelButton.setPreferredSize(new Dimension(100, cancelButton.getPreferredSize().height));
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);

		mainFrame.pack();

		// Determine the new location of the window
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int w = mainFrame.getSize().width;
		int h = mainFrame.getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;

		// Move the window
		mainFrame.setLocation(x, y);

		// Display the window.
		mainFrame.setVisible(true);

		toggleMessage();

	}

	protected void toggleMessage() {
		if (expDateModel.getValue() == null)
			expMessage.setEnabled(false);
		else
			expMessage.setEnabled(true);

	}

	public static void main(String[] args) {

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				ui.createAndShowGUI();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == cancelButton) {
			mainFrame.dispose();
		}

		if (e.getSource() == okButton) {
			dialog.dispose();
		}

		if (e.getSource() == dirButton) {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Select directory");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setSelectedFile(new File(dirText.getText().trim()));

			// disable the "All files" option.
			chooser.setAcceptAllFileFilterUsed(false);

			if (chooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
				if (e.getSource() == dirButton)
					dirText.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		}

		if (e.getSource() == generateButton) {
			generate();
		}

	}

	protected void generate() {

		saveProperties();

		LicenseFileGenerator generator = new LicenseFileGenerator();
		generator.setLicNumber(licNumberText.getText());
		if (studioButton.isSelected())
			generator.setType(LicenseFileGenerator.TYPE_STUDIO);
		if (browserButton.isSelected())
			generator.setType(LicenseFileGenerator.TYPE_BROWSER);
		generator.setDirectory(dirText.getText());
		generator.setSelectOnly(selectOnlyCb.isSelected());
		generator.setExpiryDate(expDateModel.getValue());
		generator.setExpiryMessage(expMessage.getText());

		String message = "License file generated";
		try {
			generator.generate();
		} catch (Exception e) {
			message = "License file generation failed. Error: " + e.getMessage();
		}

		// Create and set up the dialog.
		dialog = new JDialog(mainFrame, "Generate SpeedTransfer License File");
		dialog.setPreferredSize(new Dimension(500, 150));
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		JPanel frame = new JPanel();
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(frame, BorderLayout.NORTH);

		frame.setLayout(new GridBagLayout());

		// Add dummy row
		JLabel dummy = new JLabel("");
		GridBagConstraints c = new GridBagConstraints();
		c.ipady = 8;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.00;
		frame.add(dummy, c);

		// Add message
		JLabel infoLabel = new JLabel("<html>" + message + "</html>");
		c = new GridBagConstraints();
		c.gridy = 2;
		c.insets = new Insets(0, 10, 0, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.00;
		frame.add(infoLabel, c);

		okButton = new JButton("OK");
		okButton.addActionListener(this);
		c = new GridBagConstraints();
		c.gridy = 3;
		c.insets = new Insets(10, 0, 10, 10);
		frame.add(okButton, c);

		dialog.pack();

		// Determine the new location of the window
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int w = dialog.getSize().width;
		int h = dialog.getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;

		// Move the window
		dialog.setLocation(x, y);

		// task = new Task();
		// task.execute();

		// Display the window.
		dialog.setVisible(true);

	}

	protected Properties getProperties() {
		if (props == null) {
			props = new Properties();
			try {
				props.loadFromXML(new FileInputStream(PROPERTIES_FILE));
			} catch (Exception e) {
			}
		}

		return props;

	}

	protected void saveProperties() {
		if (props != null) {
			props.setProperty(OUTPUT_DIR, dirText.getText());
			props.setProperty(EXP_MSG, expMessage.getText());
			try {
				props.storeToXML(new FileOutputStream(PROPERTIES_FILE), null);
			} catch (Exception e) {
			}
		}

	}

}
