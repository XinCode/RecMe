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
 * This class implements the algorithm which recommends 
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
import java.util.Collections;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import ch.epfl.lsir.xin.algorithm.IAlgorithm;
import ch.epfl.lsir.xin.datatype.RatingMatrix;
import ch.epfl.lsir.xin.evaluation.ResultUnit;

public class MostPopular implements IAlgorithm {
	
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
	 * Top N recommendation
	 * */
	private int topN = -1;
	
	/**
	 * a set recording each items' rating number
	 * */
	private ArrayList<Integer> ratingNumbers = null;
	
	/**
	 * constructor
	 * @param: input dataset
	 * */
	public MostPopular( RatingMatrix ratingMatrix )
	{
		//set configuration file for parameter setting.
		config.setFile(new File(".//conf//MostPopular.properties"));
		try {
			config.load();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.topN = this.config.getInt("TOP_N_RECOMMENDATION");		
		this.ratingMatrix = ratingMatrix;
		this.ratingNumbers = new ArrayList<Integer>();
	}
	
	/**
	 * constructor
	 * @param: training ratings
	 * @param: read a saved model or not
	 * @param: file of a saved model 
	 * */
	public MostPopular( RatingMatrix ratingMatrix , boolean readModel , String file)
	{
		//set configuration file for parameter setting.
		config.setFile(new File(".//conf//MostPopular.properties"));
		try {
			config.load();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.ratingMatrix = ratingMatrix;
		this.topN = this.config.getInt("TOP_N_RECOMMENDATION");					
		this.ratingNumbers = new ArrayList<Integer>();
		if( readModel )//read model from a local file
		{
			readModel( file );
		}
	}
	
	/**
	 * This function generates a recommendation list for a given user
	 * @param: user
	 * */
	public ArrayList<ResultUnit> getRecommendationList( int userIndex )
	{
		if( this.ratingMatrix.getUserRatingNumber(userIndex) < 10 )
			return null;
		ArrayList<ResultUnit> recommendationList = new ArrayList<ResultUnit>();
		//find all item candidate list (items that are not rated by the user)
		for( int i = 0 ; i < this.ratingMatrix.getColumn() ; i++ )
		{
			if( this.ratingMatrix.getRatingMatrix().get(userIndex).get(i) == null )
			{
				//this item has not been rated by the item
				ResultUnit unit = new ResultUnit( userIndex , i , this.ratingNumbers.get(i) );
				recommendationList.add(unit);
			}
		}
	
		//sort the recommendation list
		Collections.sort(recommendationList);
		ArrayList<ResultUnit> result = new ArrayList<ResultUnit>();
		for( int i = recommendationList.size() - 1 ; i >= recommendationList.size() - this.topN ; i-- )
		{
			result.add(recommendationList.get(i));
		}
		return result;
	}

	@Override
	public void saveModel(String file) {
		// TODO Auto-generated method stub
	
		//each line corresponds to each item
		try{
			PrintWriter printer = new PrintWriter( file );
			for( int i = 0 ; i < this.ratingNumbers.size() ; i++ )
			{
				printer.println(this.ratingNumbers.get(i));
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
			while( (line = reader.readLine()) != null )
			{
				this.ratingNumbers.add(Integer.parseInt(line.trim()));
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
		for( int i = 0 ; i < this.ratingMatrix.getColumn() ; i++ )
		{
			this.ratingNumbers.add(this.ratingMatrix.getItemRatingNumber(i));
		}
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
	 * @return the topN
	 */
	public int getTopN() {
		return topN;
	}

	/**
	 * @param topN the topN to set
	 */
	public void setTopN(int topN) {
		this.topN = topN;
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
