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
 * This class implements social regularization MF.
 * Recommender Systems with Social Regularization, WSDM 2011
 * 
 * @author Xin Liu
 * */

package ch.epfl.lsir.xin.algorithm.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import ch.epfl.lsir.xin.algorithm.IAlgorithm;
import ch.epfl.lsir.xin.datatype.LatentMatrix;
import ch.epfl.lsir.xin.datatype.MatrixEntry2D;
import ch.epfl.lsir.xin.datatype.RatingMatrix;
import ch.epfl.lsir.xin.util.SimilarityCalculator;

public class SocialReg implements IAlgorithm {

	/**
	 * the rating matrix
	 * */
	private RatingMatrix ratingMatrix = null;
	
	/**
	 * users' social information matrix
	 * */
	private RatingMatrix socialMatrix = null;
	
	/**
	 * user similarity matrix
	 * */
//	private ArrayList<HashMap<Integer , Double>> similarityMatrix = null;
	
	/**
	 * user's latent matrix
	 * */
	private LatentMatrix userMatrix = null;
	
	private LatentMatrix userMatrixPrevious = null;
	
	/**
	 * item's latent matrix
	 * */
	private LatentMatrix itemMatrix = null;
	
	private LatentMatrix itemMatrixPrevious = null;
	
	/**
	 * logger of the system
	 * */
	private PrintWriter logger = null;
	
	/**
	 * Configuration file for parameter setting.
	 * */
	public PropertiesConfiguration config = new PropertiesConfiguration();
	
	/**
	 * latent factor initialization method
	 * */
	private int initialization = -1;
	
	/**
	 * SGD parameters
	 * */
	private int latentFactors = -1;
	private int Iterations = -1;
	private double learningRate = -1;
	private double regUser = -1;
	private double regItem = -1;
	private double convergence = -1;
	
	/**
	 * optimization method indicator
	 * */
	private String optimization = null;
	
	private int topN = -1;
	
	private int maxRating = -1;
	private int minRating = -1;
	
	/**
	 * social regularization
	 * */
	private double socialReg = -1;
	
	/**
	 * constructor
	 * */
	public SocialReg( RatingMatrix ratings , RatingMatrix social , boolean readModel , String modelFile)
	{
		//set configuration file for parameter setting.
		config.setFile(new File(".//conf//SocialReg.properties"));
		try {
			config.load();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
								
		this.ratingMatrix = ratings;
		this.socialMatrix = social;
//		this.similarityMatrix = new ArrayList<HashMap<Integer , Double>>();
//		for( int i = 0 ; i < this.socialMatrix.getRow() ; i++ )
//		{
//			this.similarityMatrix.add(new HashMap<Integer , Double>());
//		}
		this.initialization = this.config.getInt("INITIALIZATION");
		this.latentFactors = this.config.getInt("LATENT_FACTORS");
		this.Iterations = this.config.getInt("ITERATIONS");
		this.learningRate = this.config.getDouble("LEARNING_RATE");
		this.regUser = this.config.getDouble("REGULARIZATION_USER");
		this.regItem = this.config.getDouble("REGULARIZATION_ITEM");
		this.convergence = this.config.getDouble("CONVERGENCE");
		this.optimization = this.config.getString("OPTIMIZATION_METHOD");
		this.userMatrix = new LatentMatrix( this.ratingMatrix.getRow() , this.latentFactors);
		this.userMatrix.setInitialization(this.initialization); 
		this.userMatrix.valueInitialization();
		this.userMatrixPrevious = this.userMatrix.clone();
		this.itemMatrix = new LatentMatrix( this.ratingMatrix.getColumn() , this.latentFactors);
		this.itemMatrix.setInitialization(this.initialization);
		this.itemMatrix.valueInitialization();
		this.itemMatrixPrevious = this.itemMatrix.clone();
		this.topN = this.config.getInt("TOP_N_RECOMMENDATION");
		this.socialReg = this.config.getDouble("SOCIAL_REGULARIZATION");
		this.maxRating = this.config.getInt("MAX_RATING");
		this.minRating = this.config.getInt("MIN_RATING");
		
		if( readModel )
		{
			this.readModel(modelFile);
		}else{
			//calculate the similarity matrix
			this.calculateSimilarityMatrix(this.config.getString("USER_SIMILARITY"));
//			try{
//				PrintWriter printer = new PrintWriter("similarity");
//				for( int i = 0 ; i < this.socialMatrix.getRatingMatrix().size() ; i++ )
//				{
//					for( Map.Entry<Integer, Double> entry : this.socialMatrix.getRatingMatrix().get(i).entrySet() )
//					{
//						printer.print(entry.getValue() + "\t");
//					}
//					printer.println();
//				}
//				printer.flush();
//				printer.close();
//			}catch( IOException e )
//			{
//				e.printStackTrace();
//			}
		}
	}
	
	/**
	 * This function calculates the similarity among users
	 * @param: similarity calculation method
	 * */
	public void calculateSimilarityMatrix( String method )
	{
		for( int i = 0 ; i < this.socialMatrix.getRatingMatrix().size() ; i++ )
		{
			for( Map.Entry<Integer, Double> entry : this.socialMatrix.getRatingMatrix().get(i).entrySet() )
			{
				if( i == entry.getKey() )
				{
					this.socialMatrix.getRatingMatrix().get(i).put(entry.getKey(), 1.0);
				}else{
					ArrayList<Double> commonRatings1 = new ArrayList<Double>();
					ArrayList<Double> commonRatings2 = new ArrayList<Double>();
					//find common items for the two users
					for( Map.Entry<Integer, Double> element : this.ratingMatrix.getRatingMatrix().get(i).entrySet() )
					{
						Double rating = this.ratingMatrix.getRatingMatrix().get(entry.getKey()).get(element.getKey());
						if( rating != null )
						{
							commonRatings1.add(element.getValue());
							commonRatings2.add(rating);
						}
					}
					double similarity = Double.NaN;
					if( method.equals("PCC") )
					{
						similarity = SimilarityCalculator.getSimilarityPCC(commonRatings1, commonRatings2 , 
								this.config.getInt("SHRINKAGE"));
						//normalize for PCC
						similarity = (1 + similarity)/2;
					}else if( method.equals("COSINE") )
					{
						similarity = SimilarityCalculator.getSimilarityCosine(commonRatings1, commonRatings2
								, this.config.getInt("SHRINKAGE"));
					}else{
						logger.println("Similarity calculation method is not set correctly!");
					}
					if( Double.isNaN(similarity) )
					{
						similarity = 0;
					}
					this.socialMatrix.getRatingMatrix().get(i).put(entry.getKey(), similarity);
				}
			}
//			System.out.println("user index:" + i );
		}		
	}
	
	@Override
	public void saveModel(String file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readModel(String file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void build() {
		// TODO Auto-generated method stub

		if( this.optimization.equals("SGD") )
		{
			buildSGD();
			logger.println("SGD is used to train the model.");
		}else if( this.optimization.equals("ALS") )
		{
//			buildALS();
//			logger.println("ALS is used to train the model.");
		}else{
			logger.println("Optimization method is not set properly.");
		}
	}
	
	/**
	 * This function learns a matrix factorization model using Stochastic Gradient Descent 
	 * */
	public void buildSGD()
	{
		double preError = Double.MAX_VALUE;
		for( int i = 0 ; i < this.Iterations ; i++ )
		{
			System.out.println("Iteration: " + i);
			ArrayList<MatrixEntry2D> entries = this.ratingMatrix.getValidEntries();
			double error = 0; //overall error of this iteration
			System.out.println("Ratings: " + entries.size());
			while( entries.size() > 0 )
			{
				//find a random entry
				int r = new Random().nextInt(entries.size());
				MatrixEntry2D entry = entries.get(r);
				double prediction = predict( entry.getRowIndex() , entry.getColumnIndex() );
//				System.out.println(prediction);
				if( prediction > this.maxRating )
					prediction = this.maxRating;
				if( prediction < this.minRating )
					prediction = this.minRating;
				double difference = entry.getValue() - prediction;
				for( int l = 0 ; l < this.latentFactors ; l++ )
				{
					//user latent update is a modified version of the original paper
					double tempU = -1;			
					if( this.config.getString("MODE").equals("AVERAGE") )
					{
						ArrayList<Integer> friendIDs = this.socialMatrix.getRatedItemIndex(entry.getRowIndex());
						double simSum = 0;
						double friendSum = 0;
						for( int j = 0 ; j < friendIDs.size() ; j++ )
						{
							double sim = this.socialMatrix.getRatingMatrix().get(entry.getRowIndex()).get(friendIDs.get(j));
							if( sim == 0 )
								continue;
							simSum = simSum + sim;
							friendSum = friendSum + sim	* this.userMatrix.get(friendIDs.get(j), l);
						}
						if( simSum != 0 )
							friendSum = friendSum/simSum;
						else
							friendSum = 0;
//						System.out.println(friendSum);
						tempU = this.userMatrix.get(entry.getRowIndex(), l)  + this.learningRate * ( difference
								* this.itemMatrix.get(entry.getColumnIndex(), l) - this.regUser * this.userMatrix.get(entry.getRowIndex(), l)
								- this.socialReg * (this.userMatrix.get(entry.getRowIndex(), l) - friendSum));
					}else if( this.config.getString("MODE").equals("INDIVIDUAL") )
					{
						ArrayList<Integer> friendIDs = this.socialMatrix.getRatedItemIndex(entry.getRowIndex());
						double friendSum = 0;
						for( int j = 0 ; j < friendIDs.size() ; j++ )
						{
							friendSum += this.socialMatrix.getRatingMatrix().get(entry.getRowIndex()).get(friendIDs.get(j)) *
									(this.userMatrix.get(entry.getRowIndex(), l) - this.userMatrix.get(friendIDs.get(j), l));
						}
						tempU = this.userMatrix.get(entry.getRowIndex(), l)  + this.learningRate * ( difference
								* this.itemMatrix.get(entry.getColumnIndex(), l) - this.regUser * this.userMatrix.get(entry.getRowIndex(), l)
								- this.socialReg * friendSum); 
					}else{
						logger.println("MODE is not set correctly.");
					}
					double tempI = this.itemMatrix.get(entry.getColumnIndex(), l) + this.learningRate * ( 2 *
							difference * this.userMatrix.get(entry.getRowIndex(), l) - this.regItem * 
							this.itemMatrix.get(entry.getColumnIndex(), l) );
					this.userMatrix.set(entry.getRowIndex(), l, tempU);
					this.itemMatrix.set(entry.getColumnIndex(), l, tempI);
				}
				
				entries.remove(r);
			}
			
			//error
			entries = this.ratingMatrix.getValidEntries();
			for( int k = 0 ; k < entries.size() ; k++ )
			{
				MatrixEntry2D entry = entries.get(k);
				double prediction = predict( entry.getRowIndex() , entry.getColumnIndex() );
				if( prediction > this.maxRating )
					prediction = this.maxRating;
				if( prediction < this.minRating )
					prediction = this.minRating;
				error = error + Math.abs(entry.getValue() - prediction);
//				for( int j = 0 ; j < this.latentFactors ; j++ )
//				{
//					error = error + this.regUser/2 * Math.pow(this.userMatrix.get(entry.getRowIndex(), j), 2) + 
//							this.regItem/2 * Math.pow(this.itemMatrix.get(entry.getColumnIndex(), j), 2);
//				}		
			}
			this.logger.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + 
					" Iteration " + i + " : Error ~ " + error );
			this.logger.flush();
			//check for convergence
			if( Math.abs(error - preError) <= this.convergence && error <= preError )
			{
				logger.println("The algorithm convergences.");
				this.logger.flush();
				break;
			}
			// learning rate update strategy 
			updateLearningRate( error , preError );
			
			preError = error;	
			logger.flush();
		}
	}
	
	/**
	 * This function updates the learning rate at each iteration
	 * @param: error in current iteration
	 * @param: error in previous iteration
	 * */
	public void updateLearningRate( double error , double preError )
	{
		int update = this.config.getInt("LEARNING_RATE_UPDATE");
		if( update == 1 )//no need to update the learning rate
		{				

		}else if( update == 2 )//bold driver learning rate update algorithm
		{
			if( Math.abs(error) < Math.abs(preError) )
			{
				this.learningRate = (1 + 0.05) * this.learningRate;
				logger.println("Increase learning rate by 5%.");
				this.userMatrixPrevious = this.userMatrix.clone();
				this.itemMatrixPrevious = this.itemMatrix.clone();
			}else if( Math.abs(error) > Math.abs(preError) )
			{
				this.learningRate = 0.5 * this.learningRate;
				this.userMatrix = this.userMatrixPrevious.clone();//roll back to previous iteration
				this.itemMatrix = this.itemMatrixPrevious.clone();
				logger.println("Decrease learning rate by 50%.");
				}
		}else if( update == 3 ) {//decaying learning rate by a constant rate
			this.learningRate = this.learningRate * this.config.getDouble("LEARNING_RATING_DECAY");
			this.userMatrixPrevious = this.userMatrix.clone();
			this.itemMatrixPrevious = this.itemMatrix.clone();
		}
	}
	
	/**
	 * this function predicts a user to an item
	 * @param: index of the user
	 * @param: index of the item
	 * */
	public double predict( int user , int item )
	{
		double prediction = 0;
		for( int i = 0 ; i < this.latentFactors ; i++ )
		{
			prediction = prediction + this.userMatrix.getValues()[user][i] *
					this.itemMatrix.getValues()[item][i];
		}
		return prediction;
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
