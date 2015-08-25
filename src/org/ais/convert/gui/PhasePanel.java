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

public class PhasePanel extends JPanel{

	private JButton processButton;
	
	private JTextField firstFileTextField = new JTextField();
	private JTextField secondFileTextField = new JTextField();

	
	public PhasePanel() throws Exception{
		
		this.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		setLayout(new BorderLayout());

		JPanel northPanel = new JPanel();
		northPanel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
		add(northPanel, BorderLayout.NORTH);

	    ////////////////////////////////////////////////
		//
		//central panel - input controls
		//
		final JPanel centerPanel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();   
		c.insets=new Insets(5,5,5,5);
		
		centerPanel.setLayout(gridbag);
		
		//////////////////////////////////////////////	
		
		final JLabel l3=new JLabel("Parent's File:");
		c.gridx=1; c.gridy=5; //c.anchor=GridBagConstraints.EAST;
		gridbag.setConstraints(l3, c);
		centerPanel.add(l3, c);           

		
		Dimension textFieldsDimension=new Dimension(350,firstFileTextField.getPreferredSize().height);			
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
		
		final String inputDir = UserParameters.getInputDir();
		
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
		
		final JLabel l4=new JLabel("Child's File:");
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

