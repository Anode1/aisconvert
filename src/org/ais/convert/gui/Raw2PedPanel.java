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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
public class Raw2PedPanel extends JPanel{

	private JButton processButton;
	
	private JTextField inputDirNameTextField = new JTextField();
	private JTextField outputFileTextField = new JTextField();
	private JButton resetButton;
	
	
	public Raw2PedPanel()throws Exception{

		this.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
		setLayout(new BorderLayout());

		//northPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		//
		//central panel - input controls
		//
		final JPanel panel = new JPanel();

		GridBagLayout gridbag = new GridBagLayout();
		panel.setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();            
		c.insets=new Insets(5,5,5,5);

		//panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,20,20,30));
		
		Dimension textFieldsDimension=new Dimension(350,inputDirNameTextField.getPreferredSize().height);		
		
		JLabel l2=new JLabel("<html>Data Directory:<br>(all files will be processed)</html>");
		c.gridx=1; c.gridy=4; //c.anchor=GridBagConstraints.EAST;
		gridbag.setConstraints(l2, c);
		panel.add(l2, c);           

		inputDirNameTextField.setMinimumSize(textFieldsDimension);
		inputDirNameTextField.setPreferredSize(textFieldsDimension);
		final String inputDir = UserParameters.getInputDir();
		inputDirNameTextField.setText(inputDir);
		c.gridx=2;
		gridbag.setConstraints(inputDirNameTextField, c);
		panel.add(inputDirNameTextField, c);

		JButton inputDirBrowseButton = new JButton("Browse");
		//browseButton.setEnabled(false);
		c.gridx=3;
		gridbag.setConstraints(inputDirBrowseButton, c);
		panel.add(inputDirBrowseButton,c);
		
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
		
		JLabel l3=new JLabel("Output File:");
		c.gridx=1; c.gridy=5; //c.anchor=GridBagConstraints.EAST;
		gridbag.setConstraints(l3, c);
		panel.add(l3, c);           

		outputFileTextField.setMinimumSize(textFieldsDimension);
		outputFileTextField.setPreferredSize(textFieldsDimension);
		
		final String outputFile = UserParameters.getOutputFile();
		outputFileTextField.setText(outputFile);		

		c.gridx=2; 
		gridbag.setConstraints(outputFileTextField, c);
		panel.add(outputFileTextField, c);

		JButton outputFileBrowseButton = new JButton("Browse");
		//browseButton.setEnabled(false);
		c.gridx=3;
		gridbag.setConstraints(outputFileBrowseButton, c);
		panel.add(outputFileBrowseButton,c);
		
		outputFileBrowseButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				GUIManager.statusBar.showStatus("");
				JFileChooser chooser = new JFileChooser();
				
				//FileFilter filter = new FileFilter();
			    //filter.addExtension("txt");
				
				File file = new File(outputFile);
				chooser.setSelectedFile(file);					

				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				//chooser.setFileFilter(filter);

				int status = chooser.showOpenDialog(null);
				if(status == JFileChooser.APPROVE_OPTION){

					File f = chooser.getSelectedFile();

					outputFileTextField.setText(f.getAbsolutePath());

					//Resource resource=new Resource(f.getAbsolutePath());
					//Controller.getInstance().put(key, resource);
				}
			}
		});				

		//////////////////////////////////////////////
		
		c.gridx=2; c.gridy=6; 
		
		processButton = new JButton("Process files");
		gridbag.setConstraints(processButton, c);
		panel.add(processButton,c);
		
		processButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				//valueTextField.setText(""); //clean 
				GUIManager.statusBar.showStatus("Processing...");

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
									JOptionPane.showMessageDialog(panel, "There are Errors - see logs in "+Config.getDefaultErrorReportFile());
								}
							});  							
						}
						return new Object();
				    }

				}.start();  //Start the background thread				

			}
		});	 		
		
    
		//////////////////////////////////////////////
		
		c.gridx=1; c.gridy=7; 

		resetButton = new JButton("Reset to Defaults");
		//browseButton.setEnabled(false);
		gridbag.setConstraints(resetButton, c);
		panel.add(resetButton,c);
		
		resetButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				inputDirNameTextField.setText(Config.getDefaultInputsDir());
				outputFileTextField.setText(Config.getDefaultOutputFile());
				
				
				//reset also User-defined values (if there will be a permanent
				//error during processing - these defaults should be saved 
				//anyway)
				UserParameters.saveInputDir(inputDirNameTextField.getText());
				UserParameters.saveOutputFile(outputFileTextField.getText());	
			}
		});	
		/*
		JLabel resetLabel=new JLabel("<html>You can reset directories and<br> output filename to the default values</html>");
		c.gridx=2; c.anchor=GridBagConstraints.WEST;
		gridbag.setConstraints(resetLabel, c);
		panel.add(resetLabel, c);           
		*/
		
		add(panel, BorderLayout.CENTER);
		
		//
		//south panel
		//
		/*
		JPanel southPanel = new JPanel();
		add(southPanel, BorderLayout.SOUTH);  
		southPanel.setLayout(new java.awt.FlowLayout());

		southPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		*/
	} 


	private boolean process(){
		
		try{
			//Thread.sleep(3000);
			
			String inputDir = inputDirNameTextField.getText();
			String outputFile = outputFileTextField.getText();
			
			Parameters.getInstance().put(Constants.SNIPS_MAPPING_KEY, Config.getDefaultSnipsMappingFile());
			
			ArrayList listOfFilesCollected = new ArrayList();
			Parameters.getInstance().put(Constants.INPUT_FILE_KEY, listOfFilesCollected);
			listOfFilesCollected.add(inputDir);
			
			Parameters.getInstance().put(Constants.OUTPUT_FILE_KEY, outputFile);
			
			
			MainProcessor processor = new MainProcessor();
			if(processor.init().thereAreErrors()){
				return false;
			}
			processor.process();
			processor.finish();
			
			
			//save directories - only if processing was successful
			UserParameters.saveInputDir(inputDir);
			UserParameters.saveOutputFile(outputFile);
		}
		catch(Throwable e){
			//log.error("", e);
			e.printStackTrace();
		}
	
		return true;
	}

}

