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


package ch.epfl.lsir.xin.algorithm.baseline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import ch.epfl.lsir.xin.algorithm.IAlgorithm;
import ch.epfl.lsir.xin.datatype.RatingMatrix;
import ch.epfl.lsir.xin.model.NumericRating;

public class GlobalAverage implements IAlgorithm {
	
	/**
	 * the rating matrix
	 * */
	private RatingMatrix ratingMatrix = null;
	
	/**
	 * logger of the system
	 * */
	private PrintWriter logger = null;
	
	/**
	 * Configuration file for parameter setting.
	 * */
	private PropertiesConfiguration config = new PropertiesConfiguration(); 
	
	/**
	 * mean
	 * */
	private double mean = Double.NaN;
	
	/**
	 * constructor
	 * @param: training ratings
	 * */
	public GlobalAverage( RatingMatrix ratingMatrix )
	{
		//set configuration file for parameter setting.
		config.setFile(new File(".//conf//GlobalMean.properties"));
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
	public GlobalAverage( RatingMatrix ratingMatrix , boolean readModel , String file )
	{
		//set configuration file for parameter setting.
		config.setFile(new File(".//conf//GlobalMean.properties"));
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
	 * This function predicts the rating from a user to an item
	 * @param: id of the user
	 * @param: id of the item
	 * */
	public double predict( String userID , String itemID )
	{
		return this.mean;
	}
	
	/**
	 * This function predicts the rating from a user to an item
	 * @param: index of the user
	 * @param: index of the item
	 * */
	public double predict( int userIndex , int itemIndex )
	{
		return this.mean;
	}
	
	@Override
	public void saveModel(String file) {
		// TODO Auto-generated method stub
		try{
			PrintWriter printer = new PrintWriter(file);
			printer.println(this.mean);
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
		logger.append("Read an existing model from a local file: " + file.toString() + "\n");
		try{
			BufferedReader reader = new BufferedReader( new FileReader(file) );
			String line = null;
			while( (line = reader.readLine()) != null )
			{
				this.mean = Double.parseDouble(line.trim());
			}
			reader.close();
		}catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public void build() {
		// TODO Auto-generated method stub
		this.ratingMatrix.calculateGlobalAverage();
		this.mean = this.ratingMatrix.getAverageRating();
	}
	

	/**
	 * @return the mean
	 */
	public double getMean() {
		return mean;
	}

	/**
	 * @param mean the mean to set
	 */
	public void setMean(double mean) {
		this.mean = mean;
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
