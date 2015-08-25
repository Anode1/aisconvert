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
package org.ais.convert;

import java.util.StringTokenizer;

/**
 * Simple string parser. We assume that in our application the String
 * is not so big - otherwise change it to use StreamTokenizer instead.
 *
 * @since   JDK1.0
 */
public class Tokenizer{

	/**
	 * Tokenizes String using delimiters passed
	 */
	public static String[] tokenize(String str, String delim){

		StringTokenizer st = new StringTokenizer(str, delim, false);
		int n=st.countTokens();
		String[] toks = new String[n];
		for(int i=0; i<n; i++){
			toks[i]=st.nextToken().trim();
		}
		return toks;
	}

	/*public String decodeAP(String s){

      char buffer[]=new char[s.length()];
      StringBuffer sb=new StringBuffer(s.length());
      s.getChars(0,s.length(),buffer,0);
      int j=0;
      while(j<s.length()-1){
        if(buffer[j]==';'){
           if(buffer[j+1]==';'){  //";;"
              sb.append(';');
              j+=2; //";;" --> ';'
           }
           else{                  //";"
              sb.append('\n');
              j++;
           }
        }
        else{ //regular           //any
           sb.append(buffer[j++]);
        }
      }
      //last symbol:
      if(buffer[j]==';')sb.append('\n');
      else sb.append(buffer[j]);

      return sb.toString();
    }
	 */

	/**
	 * Encodes properties for using as an applet parameter
	 */
	public static String encode(String s, char toEsc, char escaper){

		StringBuffer sb = new StringBuffer();
		int len = (s != null) ? s.length() : 0;
		char c;
		for(int i=0; i<len; i++){
			c=s.charAt(i);
			if(c==toEsc){
				sb.append(escaper);
			}
			else if(c==escaper){
				sb.append(escaper);
				sb.append(escaper);
			}
			else{
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String decode(String s, char escaper, char escaped){

		char buffer[]=new char[s.length()];
		StringBuffer sb=new StringBuffer(s.length());
		s.getChars(0,s.length(),buffer,0);
		int j=0;
		while(j<s.length()-1){
			if(buffer[j]==escaper){
				if(buffer[j+1]==escaper){  //"\\"
					sb.append(escaper);      //add only one
					j+=2; //"\\" --> '\'
				}
				else{                  //"\"
					sb.append(escaped);
					j++;
				}
			}
			else{ //regular           //any
				sb.append(buffer[j++]);
			}
		}
		//last symbol:
		if(buffer[j]==escaper)sb.append(escaped);
		else sb.append(buffer[j]);

		return sb.toString();
	}

	/**
	 * Encode only doubled occurences of a char ignoring single occurences.
	 * Escapes escaper safely
	 *
	 * cc --> escaper;
	 * c --> c;
	 * escaper --> escaper, escaper
	 */
	public static String encodeDoubles(String s, char toEsc, char escaper){

		char buffer[]=new char[s.length()];
		StringBuffer sb=new StringBuffer(s.length());
		s.getChars(0,s.length(),buffer,0);
		int j=0;
		while(j<s.length()-1){
			if(buffer[j]==toEsc){
				if(buffer[j+1]==toEsc){  //"cc"
					sb.append(escaper);      //add only one
					j+=2; //"cc" --> 'c'
				}
				else{                    //"c"
					sb.append(toEsc);   //the same
					j++;
				}
			}
			else{          //any
				if(buffer[j]==escaper){  //escaper
					sb.append(escaper);
					sb.append(escaper);
					j++;
				}
				else sb.append(buffer[j++]);  //regular
			}
		}
		//last symbol:
		/*if(buffer[j]==escaper)sb.append(escaped);
      else */sb.append(buffer[j]);

      return sb.toString();
	}

	/**
	 * Encodes properties for using as an applet parameter
	 */
	public static String encodeAP(String s){

		return encode(s,'\n',';');    
	}

	public static String decodeAP(String s){

		return decode(s,';','\n');
	}      
}
