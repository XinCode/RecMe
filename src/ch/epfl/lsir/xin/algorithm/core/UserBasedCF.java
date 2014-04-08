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
 * This class implements the user based collaborative filtering algorithm
 * 
 * @author Xin Liu
 * */

package ch.epfl.lsir.xin.algorithm.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import ch.epfl.lsir.xin.algorithm.IAlgorithm;
import ch.epfl.lsir.xin.datatype.RatingMatrix;
import ch.epfl.lsir.xin.evaluation.ResultUnit;
import ch.epfl.lsir.xin.model.SUser;
import ch.epfl.lsir.xin.util.SimilarityCalculator;

public class UserBasedCF implements IAlgorithm {
	
	/**
	 * the rating matrix
	 * */
	private RatingMatrix ratingMatrix = null;
	
	/**
	 * user similarity matrix
	 * */
	private double[][] similarityMatrix = null;
	
	/**
	 * logger of the system
	 * */
	private PrintWriter logger = null;
	
	/**
	 * Configuration file for parameter setting.
	 * */
	public PropertiesConfiguration config = new PropertiesConfiguration();
	
	/**
	 * Top N recommendation
	 * */
	private int topN = -1;
	
	private int maxRating = -1;
	private int minRating = -1;
	
	/**
	 * similarity calculation method
	 * */
	private String similarityCalculation = null;
	
	/**
	 * constructor
	 * @param training ratings
	 * */
	public UserBasedCF( RatingMatrix ratingMatrix )
	{
		//set configuration file for parameter setting.
		config.setFile(new File(".//conf//UserBasedCF.properties"));
		try {
			config.load();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.topN = this.config.getInt("TOP_N_RECOMMENDATION");		
		this.similarityCalculation = this.config.getString("SIMILARITY");
		this.ratingMatrix = ratingMatrix;
		this.maxRating = this.config.getInt("MAX_RATING");
		this.minRating = this.config.getInt("MIN_RATING");
		this.similarityMatrix = new double[this.ratingMatrix.getRow()][this.ratingMatrix.getRow()];
	}
	
	/**
	 * constructor
	 * @param: training ratings
	 * @param: read a saved model or not
	 * @param: file of a saved model 
	 * */
	public UserBasedCF( RatingMatrix ratingMatrix , boolean readModel , String file )
	{
		//set configuration file for parameter setting.
		config.setFile(new File(".//conf//UserBasedCF.properties"));
		try {
			config.load();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.topN = this.config.getInt("TOP_N_RECOMMENDATION");		
		this.similarityCalculation = this.config.getString("SIMILARITY");
		this.ratingMatrix = ratingMatrix;
		this.maxRating = this.config.getInt("MAX_RATING");
		this.minRating = this.config.getInt("MIN_RATING");
		this.similarityMatrix = new double[this.ratingMatrix.getRow()][this.ratingMatrix.getRow()];
		if( readModel )
		{
			readModel( file );
		}
	}
	
	
	/**
	 * This function calculates the similarity matrix for users
	 * */
	public void similarityMatrixCalculation()
	{
		for( int i = 0 ; i < this.ratingMatrix.getRow() ; i++ )
		{
			for( int j = i ; j < this.ratingMatrix.getRow() ; j++ )
			{
				if( i == j ) //the similarity with herself is 1 
				{
					this.similarityMatrix[i][j] = 1;
				}else{
					ArrayList<Double> commonRatings1 = new ArrayList<Double>();
					ArrayList<Double> commonRatings2 = new ArrayList<Double>();
					//find common items for the two users
					for( int u1 = 0 ; u1 < this.ratingMatrix.getColumn() ; u1++ )
					{
						if( this.ratingMatrix.getRatingMatrix().get(i).get(u1) != null && 
								this.ratingMatrix.getRatingMatrix().get(j).get(u1) != null )
						{
							commonRatings1.add(this.ratingMatrix.getRatingMatrix().get(i).get(u1));
							commonRatings2.add(this.ratingMatrix.getRatingMatrix().get(j).get(u1));
						}
					}
					
					double similarity = Double.NaN;
					if( this.similarityCalculation.equals("pcc") )
					{
						similarity = SimilarityCalculator.getSimilarityPCC(commonRatings1, commonRatings2 , 
								this.config.getInt("SHRINKAGE"));
					}else if( this.similarityCalculation.equals("cosine") )
					{
						similarity = SimilarityCalculator.getSimilarityCosine(commonRatings1, commonRatings2
								, this.config.getInt("SHRINKAGE"));
					}else{
						logger.append("Cannot determine which similarity calculation method is used for. \n");
						return;
					}
							
					if( Double.isNaN(similarity) )
					{
						similarity = 0;
					}
					this.similarityMatrix[i][j] = similarity;
					this.similarityMatrix[j][i] = similarity;
				}
			}
		}
	}
	
	/**
	 * This function predicts a user's rating to an item
	 * @param: index of the user
	 * @param: index of the item
	 * @return: the predicted rating
	 * */
	public double predict( int userIndex , int itemIndex )
	{
		ArrayList<SUser> similarUsers = new ArrayList<SUser>();
		int neighbors = this.config.getInt("NEIGHBOUR_SIZE");
		//find the similar users
		for( int i = 0 ; i < this.ratingMatrix.getRow() ; i++ )
		{
			Double value = this.ratingMatrix.getRatingMatrix().get(i).get(itemIndex);
			//this user also rated the target item
			if( value != null && this.similarityMatrix[userIndex][i] > 0 )
			{
				//get the similarity information
				SUser su = new SUser( i , value.doubleValue() ,	this.similarityMatrix[userIndex][i] );
				similarUsers.add(su);
			}
		}

		//collaborative filtering cannot work: first user-average and then global-average
		if( similarUsers.size() < 1 )
		{
			double userM = this.ratingMatrix.getUsersMean().get(userIndex); 
			if( !Double.isNaN(userM) )
			{
				return userM;
			}else{
				return this.ratingMatrix.getAverageRating();
			}
		}
		Collections.sort(similarUsers);
		
		double totalSimilarity = 0;
		double prediction = 0;
		int counter = 0;
		for( int i = similarUsers.size() - 1 ; i >= 0 ; i-- )
		{
			totalSimilarity = totalSimilarity + Math.abs(similarUsers.get(i).getSimilarity());
			prediction = prediction + similarUsers.get(i).getSimilarity() * (similarUsers.get(i).getRating() 
					- this.ratingMatrix.getUsersMean().get(similarUsers.get(i).getUserIndex())); 
			counter++;
			if( counter == neighbors )
				break;
		}
		if( Double.isNaN(totalSimilarity) || totalSimilarity == 0 )
		{
			double userM = this.ratingMatrix.getUsersMean().get(userIndex); 
			if( !Double.isNaN(userM) )
			{
				return userM;
			}else{
				return this.ratingMatrix.getAverageRating();
			}
		}
		prediction = prediction / totalSimilarity;	
		prediction = prediction + this.ratingMatrix.getUsersMean().get(userIndex);

		return prediction;
	}
	
	@Override
	public void saveModel(String file) {
		// TODO Auto-generated method stub
		//save the similarity matrix
		try{
			PrintWriter printer = new PrintWriter( file );
			for( int i = 0 ; i < this.similarityMatrix.length ; i++ )
			{
				for( int j = 0 ; j < this.similarityMatrix[0].length ; j++ )
				{
					printer.print(this.similarityMatrix[i][j] + "\t");
				}
				printer.println();
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
			int u1 = 0;
			while( (line = reader.readLine()) != null )
			{
				StringTokenizer tokens = new StringTokenizer( line.trim() );
				int u2 = 0;
				while( tokens.hasMoreElements() )
				{
					this.similarityMatrix[u1][u2] = Double.parseDouble(tokens.nextToken());
					u2++;
				}
				u1++;
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
		
		similarityMatrixCalculation();
	}
	
	/**
	 * This function generates a recommendation list for a given user
	 * @param: user
	 * */
	public ArrayList<ResultUnit> getRecommendationList( int userIndex )
	{
		ArrayList<ResultUnit> recommendationList = new ArrayList<ResultUnit>();
		//find all item candidate list (items that are not rated by the user)
		for( int i = 0 ; i < this.ratingMatrix.getColumn() ; i++ )
		{
			if( this.ratingMatrix.getRatingMatrix().get(userIndex).get(i) == null )
			{
				//this item has not been rated by the user
				ResultUnit unit = new ResultUnit( userIndex , i , predict(userIndex , i)
						/*getPredictionRanking(userIndex , i) */);
				recommendationList.add(unit);
			}
		}
		//sort the recommendation list
		Collections.sort(recommendationList);
		ArrayList<ResultUnit> result = new ArrayList<ResultUnit>();
		int top = 0;
		for( int i = recommendationList.size() - 1 ; i >= 0 ; i-- )
		{
			result.add(recommendationList.get(i));
			top++;			
//			System.out.print(recommendationList.get(i).getPrediciton() + " , ");
			if( top == this.topN )
				break;
		}
//		System.out.println();
		return result;
	}
	
	public double getPredictionRanking( int userIndex , int itemIndex )
	{
		ArrayList<SUser> similarUsers = new ArrayList<SUser>();
		
		//find the similar users
		for( int i = 0 ; i < this.ratingMatrix.getRow() ; i++ )
		{
			//get the similarity information
			if( i == userIndex )
				continue;
			Double rating = this.ratingMatrix.getRatingMatrix().get(i).get(itemIndex);
			if( rating == null )
				continue;
			SUser su = new SUser( i , rating.doubleValue() , this.similarityMatrix[userIndex][i] );
			similarUsers.add(su);

		}
		Collections.sort(similarUsers);
		if( similarUsers.size() < 1 || similarUsers.get(similarUsers.size()-1).getSimilarity() == 0 )
		{
			return 0;
		}
		int count = 0;
		int neighbors = this.config.getInt("NEIGHBOUR_SIZE");
		int c = 0;
		for( int i = similarUsers.size() - 1 ; i >= 0 ; i-- )
		{
			if( this.ratingMatrix.getRatingMatrix().get(similarUsers.get(i).getUserIndex()).get(itemIndex) != null )
			{
				count++;
			}
			c++;
			if( c == neighbors )
				break;
		}
		return (double) count/neighbors;
	}
	
	/**
	 * @return the ratingMatrix
	 */
	public RatingMatrix getRatingMatrix() {
		return ratingMatrix;
	}

	/**
	 * @param ratingMatrix the ratingMatrix to set
	 */
	public void setRatingMatrix(RatingMatrix ratingMatrix) {
		this.ratingMatrix = ratingMatrix;
	}

	/**
	 * @return the similarityMatrix
	 */
	public double[][] getSimilarityMatrix() {
		return similarityMatrix;
	}

	/**
	 * @param similarityMatrix the similarityMatrix to set
	 */
	public void setSimilarityMatrix(double[][] similarityMatrix) {
		this.similarityMatrix = similarityMatrix;
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

	/**
	 * @return the similarityCalculation
	 */
	public String getSimilarityCalculation() {
		return similarityCalculation;
	}

	/**
	 * @param similarityCalculation the similarityCalculation to set
	 */
	public void setSimilarityCalculation(String similarityCalculation) {
		this.similarityCalculation = similarityCalculation;
	}

	/**
	 * @return the maxRating
	 */
	public int getMaxRating() {
		return maxRating;
	}

	/**
	 * @param maxRating the maxRating to set
	 */
	public void setMaxRating(int maxRating) {
		this.maxRating = maxRating;
	}

	/**
	 * @return the minRating
	 */
	public int getMinRating() {
		return minRating;
	}

	/**
	 * @param minRating the minRating to set
	 */
	public void setMinRating(int minRating) {
		this.minRating = minRating;
	}
	
	
}
