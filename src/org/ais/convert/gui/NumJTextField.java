package org.ais.convert.gui;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * JTextField which filters out not numbers, not allowing to type 
 */
public class NumJTextField extends JTextField{
	

	protected Document createDefaultModel() {
		return new MyTextDocument();
	}

	public int getValue() {
		try{
			return Integer.parseInt(getText());
		}catch (NumberFormatException e) {
			return 0;
		}
	}

	class MyTextDocument extends PlainDocument {
		
		public void insertString(int ind, String s, AttributeSet a) throws BadLocationException {
			if (s == null)
				return;
			String old = getText(0, getLength());
			if(isNumber(old.substring(0, ind) + s + old.substring(ind)))
				super.insertString(ind, s, a);
		}
	}
	
	private boolean isNumber(String s){
		try {
			Integer.parseInt(s + "0");
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
