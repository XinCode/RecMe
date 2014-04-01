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
 * This class implements the algorithm which predicts a rating by averaging the 
 * corresponding item's past ratings.
 * 
 * @author Xin Liu
 * */

package ch.epfl.lsir.xin.algorithm.baseline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import ch.epfl.lsir.xin.algorithm.IAlgorithm;
import ch.epfl.lsir.xin.datatype.RatingMatrix;

public class ItemAverage implements IAlgorithm {

	/**
	 * the rating matrix
	 * */
	private RatingMatrix ratingMatrix = null;
	
	/**
	 * Configuration file for parameter setting.
	 * */
	public PropertiesConfiguration config = new PropertiesConfiguration();
	
	/**
	 * logger of the system
	 * */
	private PrintWriter logger = null;
	
	/**
	 * constructor
	 * @param: input dataset
	 * */
	public ItemAverage( RatingMatrix ratingMatrix )
	{
		//set configuration file for parameter setting.
		config.setFile(new File(".//conf//ItemAverage.properties"));
		try {
			config.load();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.ratingMatrix = ratingMatrix;
	}
	
	/**
	 * constructor
	 * @param: training ratings
	 * @param: read a saved model or not
	 * @param: file of a saved model 
	 * */
	public ItemAverage( RatingMatrix ratingMatrix , boolean readModel , String file )
	{
		//set configuration file for parameter setting.
		config.setFile(new File(".//conf//ItemAverage.properties"));
		try {
			config.load();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.ratingMatrix = ratingMatrix;
		if( readModel )//read model from a local file
		{
			readModel( file );
		}
	}
	
	/**
	 * this function predicts a rating from a user to an item
	 * */
	public double predict( int userIndex , int itemIndex )
	{	
		double value = this.ratingMatrix.getItemMean(itemIndex);		
		if( Double.isNaN(value) )
		{
			return this.ratingMatrix.getAverageRating();
		}else{
			return value;
		}
	}
	
	@Override
	public void saveModel(String file) {
		// TODO Auto-generated method stub
		//each line corresponds to each item
		try{
			PrintWriter printer = new PrintWriter( file );
			for( int i = 0 ; i < this.ratingMatrix.getItemsMean().size() ; i++ )
			{
				printer.println(this.ratingMatrix.getItemsMean().get(i));
			}
			printer.flush();
			printer.close();
		}catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public void readModel(String file) {
		// TODO Auto-generated method stub
		try{
			BufferedReader reader = new BufferedReader( new FileReader(file) );
			String line = null;
			ArrayList<Double> itemsMean = new ArrayList<Double>();
			while( (line = reader.readLine()) != null )
			{
				itemsMean.add(Double.parseDouble(line.trim()));
			}
			this.ratingMatrix.setItemsMean(itemsMean);
			reader.close();
		}catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public void build() {
		// TODO Auto-generated method stub
		this.ratingMatrix.calculateItemsMean();
	}

	/**
	 * @return the config
	 */
	public PropertiesConfiguration getConfig() {
		return config;
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(PropertiesConfiguration config) {
		this.config = config;
	}

	/**
	 * @return the logger
	 */
	public PrintWriter getLogger() {
		return logger;
	}

	/**
	 * @param logger the logger to set
	 */
	public void setLogger(PrintWriter logger) {
		this.logger = logger;
	}
	
	
}
