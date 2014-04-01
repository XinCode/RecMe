//Copyright (C) 2014  Xin Liu
//
//RecMe: a lightweight recommendation algorithm library
//
//RecMe is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

/**
 * This class implements the function of reading data from a file
 * */

package ch.epfl.lsir.xin.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import ch.epfl.lsir.xin.datatype.RatingMatrix;
import ch.epfl.lsir.xin.model.DataSetNumeric;
import ch.epfl.lsir.xin.model.NumericRating;

public class DataLoaderFile {

	/**
	 * name of the file to be read
	 * */
	private String fileName = null;
	
	/**
	 * dataset
	 * */
	private DataSetNumeric dataset = null;
	
	/**
	 * logger of the system
	 * */
	public Logger logger = Logger.getLogger(DataLoaderFile.class);
	
	
	/**
	 * constructor:
	 * @param: file name
	 * */
	public DataLoaderFile( String fileName )
	{
		this.fileName = fileName;
	}
	
	/**
	 * This function reads data from the file to form the dataset. 
	 * The format of the file should be "UserID , itemID , Rating".
	 * # indicates comments
	 * */
	public void readSimple()
	{
		ArrayList<NumericRating> ratings = new ArrayList<NumericRating>();
		double maxRating = Double.MIN_VALUE;
		double minRating = Double.MAX_VALUE;
		try{
			BufferedReader reader = new BufferedReader( new FileReader(this.fileName) );
			String line = null;
			int lineCounter = 0;
			while( (line = reader.readLine()) != null )
			{
				lineCounter++;
				if( line.startsWith("#") )//this line is commented
				{
					continue;
				}
				StringTokenizer tokens = new StringTokenizer( line , " ,;|");
				String userId = null;
				if( tokens.hasMoreElements() )
				{
					userId = tokens.nextToken();
				}else{
					logger.info("Error in reading data from file: " + this.fileName + 
							" line: " + lineCounter);
					continue;
				}
				String itemId = null;
				if( tokens.hasMoreElements() )
				{
					itemId = tokens.nextToken();
				}else{
					logger.info("Error in reading data from file: " + this.fileName + 
							" line: " + lineCounter);
					continue;
				}
				double value = Double.NaN;
				if( tokens.hasMoreElements() )
				{
					value = Double.parseDouble(tokens.nextToken());
				}else{
					logger.info("Error in reading data from file: " + this.fileName + 
							" line: " + lineCounter);
					continue;
				}
				
				if( value < minRating )
				{
					minRating = value;
				}
				if( value > maxRating )
				{
					maxRating = value;
				}
				
				NumericRating rating = new NumericRating( userId , itemId , value );
				ratings.add(rating);
			}
		}catch( IOException e )
		{
			e.printStackTrace();
			logger.info("Error in reading data from file: " + this.fileName);
		}
		//create the dataset
		this.dataset = new DataSetNumeric( ratings );
		this.dataset.createUserItemIDMapping();
		this.dataset.setMaxRating(maxRating);
		this.dataset.setMinRating(minRating);
	}
	
	/**
	 * This function reads social/trust information from another file. 
	 * Format: userID , friendID , social/trust indicator
	 * Note: this function should be called after rating information has been read
	 * @param: name of file storing social / trust information
	 * */
	public void readRelation( String fileName )
	{
		RatingMatrix relationships = new RatingMatrix(this.dataset.getUserIDs().size() ,
				this.dataset.getUserIDs().size());
		try{
			BufferedReader reader = new BufferedReader( new FileReader(fileName) );
			String line = null;
			while( (line = reader.readLine()) != null )
			{
				StringTokenizer tokens = new StringTokenizer( line );
				String userID = tokens.nextToken();
				Integer userIndex = this.dataset.getUserIDMapping().get(userID);
				if( userIndex == null )
					continue;
				String fID = tokens.nextToken();
				Integer index = this.dataset.getUserIDMapping().get(fID);
				if( index == null )//no rating information, do not consider
					continue;
				int fIndex = index.intValue();
				double value = Double.parseDouble(tokens.nextToken());
				relationships.set(userIndex, fIndex, value);
			}
			reader.close();
		}catch( IOException e )
		{
			e.printStackTrace();
		}
		this.dataset.setRelationships(relationships);
	}
	
	
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the dataset
	 */
	public DataSetNumeric getDataset() {
		return dataset;
	}

	/**
	 * @param dataset the dataset to set
	 */
	public void setDataset(DataSetNumeric dataset) {
		this.dataset = dataset;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		BasicConfigurator.configure();  
		PropertyConfigurator.configure(".//conf//log4j.properties");
		DataLoaderFile loader = new DataLoaderFile(".//data//MoveLens100k.txt");
		loader.readSimple();
		System.out.println("File name: " + loader.fileName);
		DataSetNumeric dataset = loader.getDataset();
		System.out.println("Number of ratings: " + dataset.getRatings().size() + 
				" Number of users: " + dataset.getUserIDs().size() + " Number of items: "
				+ dataset.getItemIDs().size());
//		for( int i = 0 ; i < dataset.getRatings().size() ; i++ )
//		{
//			NumericRating rating = dataset.getRatings().get(i);
//			System.out.println(rating.getUserID() + " , " + rating.getItemID() + " , " + rating.getValue());
//		}
		
//		HashMap<String , Integer> userMapping = dataset.getUserIDMapping(); 
//		for( Map.Entry<String, Integer> entry : userMapping.entrySet() )
//		{
//			System.out.println( entry.getKey() + " : " + entry.getValue());
//			if( dataset.getUserIDs().indexOf(entry.getKey()) != entry.getValue() )
//			{
//				System.out.println("error");
//				break;
//			}
//		}
		
		HashMap<String , Integer> itemMapping = dataset.getItemIDMapping(); 
		for( Map.Entry<String, Integer> entry : itemMapping.entrySet() )
		{
			System.out.println( entry.getKey() + " : " + entry.getValue() + " " 
					+ dataset.getItemIDs().indexOf(entry.getKey()));
			if( dataset.getItemIDs().indexOf(entry.getKey()) != entry.getValue() )
			{
				System.out.println("error");
				break;
			}
		}
	}

}
