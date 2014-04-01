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
 * This class implements the item based collaborative filtering algorithm
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
import ch.epfl.lsir.xin.model.SItem;
import ch.epfl.lsir.xin.util.SimilarityCalculator;

public class ItemBasedCF implements IAlgorithm {

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
	
	/**
	 * similarity calculation method
	 * */
	private String similarityCalculation = null;
	
	
	
	/**
	 * constructor
	 * @param training ratings
	 * */
	public ItemBasedCF( RatingMatrix ratingMatrix )
	{
		//set configuration file for parameter setting.
		config.setFile(new File(".//conf//ItemBasedCF.properties"));
		try {
			config.load();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.topN = this.config.getInt("TOP_N_RECOMMENDATION");		
		this.similarityCalculation = this.config.getString("SIMILARITY");
		this.ratingMatrix = ratingMatrix;
		
		this.similarityMatrix = new double[this.ratingMatrix.getColumn()][this.ratingMatrix.getColumn()];
		similarityMatrixCalculation();
		
		//display similarity matrix
//		try {
//			PrintWriter printer = new PrintWriter("matrix");
//			for( int i = 0 ; i < this.similarityMatrix.length ; i++ )
//			{
//				for( int j = 0 ; j < this.similarityMatrix[i].length ; j++ )
//				{
//					printer.print(this.similarityMatrix[i][j] + "  ");
//				}
//				printer.println();
//			}
//			printer.flush();
//			printer.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	/**
	 * constructor
	 * @param: training ratings
	 * @param: read a saved model or not
	 * @param: file of a saved model 
	 * */
	public ItemBasedCF( RatingMatrix ratingMatrix , boolean readModel , String file )
	{
		config.setFile(new File(".//conf//ItemBasedCF.properties"));
		try {
			config.load();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.topN = this.config.getInt("TOP_N_RECOMMENDATION");		
		this.similarityCalculation = this.config.getString("SIMILARITY");
		this.ratingMatrix = ratingMatrix;
		
		this.similarityMatrix = new double[this.ratingMatrix.getColumn()][this.ratingMatrix.getColumn()];
		
		if( readModel )
		{
			readModel( file );
		}else{
			similarityMatrixCalculation();
		}
	}
	
	/**
	 * This function calculates the similarity matrix for users
	 * The similarity is in the range of [0,1]
	 * */
	public void similarityMatrixCalculation()
	{
		for( int i = 0 ; i < this.ratingMatrix.getColumn() ; i++ )
		{
			for( int j = i ; j < this.ratingMatrix.getColumn() ; j++ )
			{
				if( i == j ) //the similarity with herself is 1 
				{
					this.similarityMatrix[i][j] = 1;
				}else{
					ArrayList<Double> commonRatings1 = new ArrayList<Double>();
					ArrayList<Double> commonRatings2 = new ArrayList<Double>();
					//find common ratings for the two items
					for( int i1 = 0 ; i1 < this.ratingMatrix.getRow() ; i1++ )
					{
						if( this.ratingMatrix.getRatingMatrix().get(i1).get(i) != null && 
								this.ratingMatrix.getRatingMatrix().get(i1).get(j) != null )
						{
							commonRatings1.add(this.ratingMatrix.getRatingMatrix().get(i1).get(i));
							commonRatings2.add(this.ratingMatrix.getRatingMatrix().get(i1).get(j));
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
		
	}
	
	/**
	 * This function predicts a user's rating to an item
	 * @param: index of the user
	 * @param: index of the item
	 * @return: the predicted rating
	 * */
	public double predict( int userIndex , int itemIndex )
	{
		ArrayList<SItem> similarItems = new ArrayList<SItem>();
		int neighbors = this.config.getInt("NEIGHBOUR_SIZE");
		//find the similar items
		for( int i = 0 ; i < this.ratingMatrix.getColumn() ; i++ )
		{
			Double value = this.ratingMatrix.getRatingMatrix().get(userIndex).get(i);
			//this item is also rated by the target user
			if( value != null )
			{
				//get the similarity information
				SItem su = new SItem( i , value.doubleValue() , this.similarityMatrix[itemIndex][i] );
				similarItems.add(su);
			}
		}
//		System.out.println("Neighbour size: " + similarUsers.size());
		Collections.sort(similarItems);
		//collaborative filtering cannot work: first user-average and then global-average
		if( similarItems.size() < 1 || similarItems.get(similarItems.size()-1).getSimilarity() == 0 )
		{
//			System.out.println("Cannot be predicted by UserCF");
			double itemM = this.ratingMatrix.getItemMean(itemIndex);
			if( !Double.isNaN(itemM) )
			{
				return itemM;
			}else{
				return this.ratingMatrix.getAverageRating();
			}
		}
		
		double totalSimilarity = 0;
		double prediction = 0;
		int counter = 0;
		for( int i = similarItems.size() - 1 ; i >= 0 ; i-- )
		{
			totalSimilarity = totalSimilarity + Math.abs(similarItems.get(i).getSimilarity());
			prediction = prediction + similarItems.get(i).getSimilarity() * (similarItems.get(i).getRating() 
					- this.ratingMatrix.getItemsMean().get(similarItems.get(i).getItemIndex()));
			if(  Double.isNaN(totalSimilarity) || totalSimilarity == 0  )
			{
				System.out.println(similarItems.get(i).getSimilarity() + " ,  "
						+ similarItems.get(i).getRating() + " pred: " + prediction);
			}
			counter++;
			if( counter == neighbors )
				break;
		}
		if( Double.isNaN(totalSimilarity) || totalSimilarity == 0 )
		{
			System.out.println("Total similarity is NaN.");
			return Double.NaN;
		}
		prediction = prediction / totalSimilarity;	
		prediction = prediction + this.ratingMatrix.getItemsMean().get(itemIndex);
		int max =  this.config.getInt("MAX_RATING");
		if( prediction > max )
		{
			prediction = max;
		}
		int min = this.config.getInt("MIN_RATING");
		if( prediction < min )
		{
			prediction = min;
		}
		
		return prediction;
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
