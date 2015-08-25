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

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ais.convert.Constants;
import org.ais.convert.Parameters;


/**
 * Panel where tabs are placed (we may put panels dynamically here - not to create too many widgets - if more GUI will be necessary
 */
public class MainPanel extends JPanel{

	
	public MainPanel()throws Exception{
		
		super(new GridLayout(1, 1));
		JTabbedPane tabbedPane = new JTabbedPane();
		this.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        tabbedPane.addTab("Phasing", new PhasePanel());		
		
        tabbedPane.addTab("HIR compare", new HirPanel());
        //tabbedPane.setMnemonicAt(0, KeyEvent.VK_h);
        Parameters.getInstance().put(Constants.PHASE_KEY, "true"); //default
        
        //argsAsProps.put(Constants.UPS_MODE_KEY, "true");
         
        tabbedPane.addTab("raw2ped", new Raw2PedPanel());
        //tabbedPane.setMnemonicAt(1, KeyEvent.VK_r);

        
		add(tabbedPane);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		
		tabbedPane.addChangeListener(new ChangeListener(){
	        public void stateChanged(ChangeEvent changeEvent) {
	          JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
	          int index = sourceTabbedPane.getSelectedIndex();

	          //reset previous
   			  Parameters.getInstance().put(Constants.RAW2PED_MODE_KEY, "false");
			  Parameters.getInstance().put(Constants.UPS_MODE_KEY, "false");
			  Parameters.getInstance().put(Constants.PHASE_KEY, "false");

	          switch(index){
            
				case 0:
					//FIXME! we should make a single key with multipe values instead of set of flags (this should be done in Args)
					Parameters.getInstance().put(Constants.PHASE_KEY, "true");
					break;
				case 1:
					Parameters.getInstance().put(Constants.RAW2PED_MODE_KEY, "true");
					break;
				case 2:
					Parameters.getInstance().put(Constants.UPS_MODE_KEY, "true");
					break;					
				default:
					throw new NullPointerException("Not expected to be here");
			}
          }
	    });	
		
	}




}

