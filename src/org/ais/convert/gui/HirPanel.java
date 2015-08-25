/*
 	Copyright (C) 2009 Vasili Gavrilov

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.ais.convert.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.ais.convert.Config;
import org.ais.convert.Constants;
import org.ais.convert.MainProcessor;
import org.ais.convert.Parameters;

//import org.apache.log4j.Logger;

/**
 * Panel showing the name of the user, current logging time
 * and last login time in the MainMenuPanel.
 */
public class HirPanel extends JPanel{

	private JButton processButton;
	
	private JTextField inputDirNameTextField = new JTextField();
	private JTextField firstFileTextField = new JTextField();
	private JTextField secondFileTextField = new JTextField();
	private JRadioButton twoButton;
	private NumJTextField cMField;
	private NumJTextField snpField;
	
	public HirPanel() throws Exception{
		
		this.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		setLayout(new BorderLayout());

		JPanel northPanel = new JPanel();
		northPanel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
		add(northPanel, BorderLayout.NORTH);
		
		JPanel checkBoxPanel = new JPanel(new GridLayout(0, 1));
		checkBoxPanel.setBorder(BorderFactory.createEtchedBorder());
		northPanel.add(checkBoxPanel);
		
	    twoButton = new JRadioButton("Compare 2 files");
	    twoButton.setSelected(true);
	    checkBoxPanel.add(twoButton);
	    
		JRadioButton dirButton = new JRadioButton("Compare every file in a directory with each other");
	    checkBoxPanel.add(dirButton);
	    
	    northPanel.add(Box.createRigidArea(new Dimension(50,0)));
	    
	    JPanel thresholdsPanel = new JPanel(new GridLayout(0, 2));
	    GridBagLayout gridbagThreshold = new GridBagLayout();
	    thresholdsPanel.setLayout(gridbagThreshold);
		GridBagConstraints c = new GridBagConstraints();            
		c.insets=new Insets(5,5,5,5);	    
	    
	    JLabel cMLabel = new JLabel("Gen Distance Threshold");
	    //cMLabel.setAlignmentX(RIGHT_ALIGNMENT);
		c.gridx=1; c.gridy=1; c.anchor=GridBagConstraints.EAST;
		gridbagThreshold.setConstraints(cMLabel,c);
	    thresholdsPanel.add(cMLabel,c);
	    
	    cMField = new NumJTextField();
	    cMField.setText(Integer.toString(Constants.DEFAULT_CM));
	    c.gridx=2; c.anchor=GridBagConstraints.WEST;
	    Dimension d = new Dimension(20, inputDirNameTextField.getPreferredSize().height);
	    cMField.setMinimumSize(d);
	    cMField.setPreferredSize(d);
	    gridbagThreshold.setConstraints(cMField,c);
	    thresholdsPanel.add(cMField,c);
	    
	    JLabel SNPLabel = new JLabel("SNP Threshold");
	    //SNPLabel.setAlignmentX(RIGHT_ALIGNMENT);
	    c.gridx=1; c.gridy=2; c.anchor=GridBagConstraints.EAST;
	    gridbagThreshold.setConstraints(SNPLabel,c);
	    thresholdsPanel.add(SNPLabel,c);
	    
	    snpField = new NumJTextField();
	    snpField.setText(Integer.toString(Constants.DEFAULT_SNIPS));
	    c.gridx=2; c.anchor=GridBagConstraints.WEST;
	    
	    Dimension d2 = new Dimension(40, inputDirNameTextField.getPreferredSize().height);
	    snpField.setMinimumSize(d2);
	    snpField.setPreferredSize(d2);
	    gridbagThreshold.setConstraints(snpField,c);
	    thresholdsPanel.add(snpField,c);
	    
	    northPanel.add(thresholdsPanel);

	    ////////////////////////////////////////////////
		//
		//central panel - input controls
		//
		final JPanel centerPanel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		centerPanel.setLayout(gridbag);

		//panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,20,20,30));

		Dimension textFieldsDimension=new Dimension(350,inputDirNameTextField.getPreferredSize().height);		    
	    
		final JLabel l2=new JLabel("<html>Data Directory:<br>(all files will be processed)</html>");
		l2.setVisible(false);
		c.gridx=1; c.gridy=4; //c.anchor=GridBagConstraints.EAST;
		gridbag.setConstraints(l2, c);
		centerPanel.add(l2, c);           

		inputDirNameTextField.setMinimumSize(textFieldsDimension);
		inputDirNameTextField.setPreferredSize(textFieldsDimension);
		final String inputDir = UserParameters.getInputDir();
		inputDirNameTextField.setText(inputDir);
		inputDirNameTextField.setVisible(false);
		c.gridx=2;
		gridbag.setConstraints(inputDirNameTextField, c);
		centerPanel.add(inputDirNameTextField, c);
		
		
		final JButton inputDirBrowseButton = new JButton("Browse");
		inputDirBrowseButton.setVisible(false);
		c.gridx=3;
		gridbag.setConstraints(inputDirBrowseButton, c);
		centerPanel.add(inputDirBrowseButton,c);
		
		inputDirBrowseButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				/*
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {}
				});
				 */
		
				GUIManager.statusBar.showStatus("");
				/*
	            FileDialog f = new FileDialog(parent, "Open File", FileDialog.LOAD);
	            //GUIUtils.setCentalizedLocationRelativeMe(parent, f);
	            f.setDirectory(inputDir);       // Set the default directory

	       // Display the dialog and wait for the user's response
	            f.show();                        

	            String dir = f.getDirectory();    // Remember new default directory
	            String file = f.getFile();
	            //setFile(chosen, f.getFile()); // Load and display selection
	            f.dispose();                     // Get rid of the dialog box
	            
	            inputDirNameTextField.setText(dir + file);
	            */
				
				
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setDialogTitle("Files Directory");
				
				File dir = new File(inputDir);
				chooser.setCurrentDirectory(dir);
				//chooser.setSelectedFile(dir);

				//chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				//chooser.setMultiSelectionEnabled(true);

				File file = new File(inputDir);
				chooser.setSelectedFile(file);
				
				int status = chooser.showOpenDialog(null);
				if(status == JFileChooser.APPROVE_OPTION){

					File f = chooser.getSelectedFile();
					if(!f.isDirectory())
						f = chooser.getCurrentDirectory();

					inputDirNameTextField.setText(f.getAbsolutePath());
				}
			
			
			}
		});
				
		//////////////////////////////////////////////	
		
		final JLabel l3=new JLabel("1st File:");
		c.gridx=1; c.gridy=5; //c.anchor=GridBagConstraints.EAST;
		gridbag.setConstraints(l3, c);
		centerPanel.add(l3, c);           

		firstFileTextField.setMinimumSize(textFieldsDimension);
		firstFileTextField.setPreferredSize(textFieldsDimension);
		
		c.gridx=2; 
		gridbag.setConstraints(firstFileTextField, c);
		centerPanel.add(firstFileTextField, c);

		final JButton firstFileBrowseButton = new JButton("Browse");
		//browseButton.setEnabled(false);
		c.gridx=3;
		gridbag.setConstraints(firstFileBrowseButton, c);
		centerPanel.add(firstFileBrowseButton,c);
		
		firstFileBrowseButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				GUIManager.statusBar.showStatus("");
				JFileChooser chooser = new JFileChooser();
				
				//FileFilter filter = new FileFilter();
			    //filter.addExtension("txt");
				
				File file = new File(inputDir);
				chooser.setSelectedFile(file);					

				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				//chooser.setFileFilter(filter);

				int status = chooser.showOpenDialog(null);
				if(status == JFileChooser.APPROVE_OPTION){

					File f = chooser.getSelectedFile();

					firstFileTextField.setText(f.getAbsolutePath());

					//Resource resource=new Resource(f.getAbsolutePath());
					//Controller.getInstance().put(key, resource);
				}
			}
		});				

		//////////////////////////////////////////////
		
		final JLabel l4=new JLabel("2nd File:");
		c.gridx=1; c.gridy=6; //c.anchor=GridBagConstraints.EAST;
		gridbag.setConstraints(l4, c);
		centerPanel.add(l4, c);           

		secondFileTextField.setMinimumSize(textFieldsDimension);
		secondFileTextField.setPreferredSize(textFieldsDimension);
		
		c.gridx=2; 
		gridbag.setConstraints(secondFileTextField, c);
		centerPanel.add(secondFileTextField, c);

		final JButton secondFileBrowseButton = new JButton("Browse");
		c.gridx=3;
		gridbag.setConstraints(secondFileBrowseButton, c);
		centerPanel.add(secondFileBrowseButton,c);
		
		secondFileBrowseButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				GUIManager.statusBar.showStatus("");
				JFileChooser chooser = new JFileChooser();
				
				//FileFilter filter = new FileFilter();
			    //filter.addExtension("txt");
				
				File file = new File(inputDir);
				chooser.setSelectedFile(file);					

				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				//chooser.setFileFilter(filter);

				int status = chooser.showOpenDialog(null);
				if(status == JFileChooser.APPROVE_OPTION){

					File f = chooser.getSelectedFile();

					secondFileTextField.setText(f.getAbsolutePath());

					//Resource resource=new Resource(f.getAbsolutePath());
					//Controller.getInstance().put(key, resource);
				}
			}
		});
		
		
	    ButtonGroup group = new ButtonGroup();
	    group.add(twoButton);
	    group.add(dirButton);
	    twoButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){

				l3.setVisible(true);
				firstFileTextField.setVisible(true);
				firstFileBrowseButton.setVisible(true);
				l4.setVisible(true);
				secondFileTextField.setVisible(true);
				secondFileBrowseButton.setVisible(true);				
				
				l2.setVisible(false);
				inputDirNameTextField.setVisible(false);
				inputDirBrowseButton.setVisible(false);
			}
		});	
	    dirButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				
				l2.setVisible(true);
				inputDirNameTextField.setVisible(true);
				inputDirBrowseButton.setVisible(true);				
				
				l3.setVisible(false);
				firstFileTextField.setVisible(false);
				firstFileBrowseButton.setVisible(false);
				l4.setVisible(false);
				secondFileTextField.setVisible(false);
				secondFileBrowseButton.setVisible(false);
			}
		});		
		
		//////////////////////////////////////////////
		add(centerPanel, BorderLayout.CENTER);
		
		//
		//south panel
		//

		JPanel southPanel = new JPanel();
		add(southPanel, BorderLayout.SOUTH);  
		southPanel.setLayout(new java.awt.FlowLayout());

		southPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		processButton = new JButton("Process files");
		southPanel.add(processButton);
		
		processButton.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent evt){
				//valueTextField.setText(""); //clean
				
				GUIManager.statusBar.showStatus("Processing...");
				
				//validation of inputs:
				
				Properties props = Parameters.getInstance();
				if(twoButton.isSelected()){
					String firstFile = firstFileTextField.getText();
					String secondFile = secondFileTextField.getText();
					
					File firstFileFile = new File(firstFile);
					File secondFileFile = new File(secondFile);
					if(!firstFileFile.canRead() || !firstFileFile.isFile() || !secondFileFile.canRead() || !secondFileFile.isFile()){
						JOptionPane.showMessageDialog(centerPanel, "Please enter valid files");
						GUIManager.statusBar.showStatus("");
						return;
					}
					
					ArrayList newFilesList = new ArrayList();
					newFilesList.add(firstFile);
					newFilesList.add(secondFile);
					props.put(Constants.INPUT_FILE_KEY, newFilesList);
					
				}
				else{ //the whole directory is to be processed
					String inputDir = inputDirNameTextField.getText();
					File dir = new File(inputDir);
					if(!dir.isDirectory() || !dir.canRead()){
						JOptionPane.showMessageDialog(centerPanel, "Please enter a valid directory");
						GUIManager.statusBar.showStatus("");
						return;
					}
					
					ArrayList newFilesList = new ArrayList();
					newFilesList.add(inputDir);
					props.put(Constants.INPUT_FILE_KEY, newFilesList);
				}

				props.put(Constants.MIN_THRESHOLD_KEY, snpField.getText());
				props.put(Constants.SM_THRESHOLD_KEY, cMField.getText());
				
				new SwingWorker(){
				    public Object construct() {
				    	
						boolean success = process();
						
						if(success){
							SwingUtilities.invokeLater(new Runnable(){
								public void run(){
									GUIManager.statusBar.showStatus("Finished: " + "Success");
								}
							});  							
							
						}
						else{
							SwingUtilities.invokeLater(new Runnable(){
								public void run(){
									GUIManager.statusBar.showStatus("Finished: " + "There are Errors - see logs");
									JOptionPane.showMessageDialog(centerPanel, "There are Errors - see logs in "+Config.getDefaultErrorReportFile());
								}
							});  							
						}
						return new Object();
				    }

				}.start();  //Start the background thread				

			}
		});			
		
	}


	private boolean process(){
		
		try{
			
			MainProcessor processor = new MainProcessor();
			if(processor.init().thereAreErrors()){
				return false;
			}
			processor.process();
			processor.finish();
			
		}
		catch(Throwable e){
			//log.error("", e);
			e.printStackTrace();
		}
	
		return true;
	}

}

