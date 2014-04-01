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
 * This class implements the basic matrix factorization model for recommendation
 * 
 * Ruslan Salakhutdinov and Andriy Mnih, Probabilistic Matrix Factorization, NIPS 2008.
 * 
 * @author Xin Liu
 * */

package ch.epfl.lsir.xin.algorithm.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import Jama.Matrix;
import ch.epfl.lsir.xin.algorithm.IAlgorithm;
import ch.epfl.lsir.xin.datatype.LatentMatrix;
import ch.epfl.lsir.xin.datatype.MatrixEntry2D;
import ch.epfl.lsir.xin.datatype.RatingMatrix;
import ch.epfl.lsir.xin.evaluation.ResultUnit;

public class MatrixFactorization implements IAlgorithm {

	/**
	 * the rating matrix
	 * */
	private RatingMatrix ratingMatrix = null;
	
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
	 * constructor
	 * @param: rating matrix
	 * @param: indicator for loading an existing model
	 * @param: location of the file that stores the model
	 * */
	public MatrixFactorization( RatingMatrix ratingMatrix , boolean readModel , String modelFile )
	{
		//set configuration file for parameter setting.
		config.setFile(new File(".//conf//MF.properties"));
		try {
			config.load();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.ratingMatrix = ratingMatrix;
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
		this.maxRating = this.config.getInt("MAX_RATING");
		this.minRating = this.config.getInt("MIN_RATING");
		
		if( readModel )
		{
			this.readModel(modelFile);
		}
	}
	
	/**
	 * this function trains the model
	 * */
	@Override
	public void build() {
		// TODO Auto-generated method stub

		if( this.optimization.equals("SGD") )
		{
			buildSGD();
			logger.println("SGD is used to train the model.");
		}else if( this.optimization.equals("ALS") )
		{
			buildALS();
			logger.println("ALS is used to train the model.");
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
			while( entries.size() > 0 )
			{
				//find a random entry
				int r = new Random().nextInt(entries.size());
				MatrixEntry2D entry = entries.get(r);
				double prediction = predict( entry.getRowIndex() , entry.getColumnIndex() );
				if( prediction > this.maxRating )
					prediction = this.maxRating;
				if( prediction < this.minRating )
					prediction = this.minRating;
				double difference = entry.getValue() - prediction;
				for( int l = 0 ; l < this.latentFactors ; l++ )
				{
					double tempU = this.userMatrix.get(entry.getRowIndex(), l)  + this.learningRate * ( 2 * 
							difference * this.itemMatrix.get(entry.getColumnIndex(), l) - this.regUser * 
							this.userMatrix.get(entry.getRowIndex(), l) );
					double tempI = this.itemMatrix.get(entry.getColumnIndex(), l) + this.learningRate * ( 2 *
							difference * this.userMatrix.get(entry.getRowIndex(), l) - this.regItem * 
							this.itemMatrix.get(entry.getColumnIndex(), l) );
					this.userMatrix.set(entry.getRowIndex(), l, tempU);
					this.itemMatrix.set(entry.getColumnIndex(), l, tempI);
				}
				//one rating is only processed once in an iteration
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
	 * This function learns a matrix factorization model using Alternative Least Square  
	 * refer to "Large-scale Parallel Collaborative Filtering for the Netﬂix Prize"
	 * */
	public void buildALS()
	{
		double preError = Double.MAX_VALUE;
		Matrix identify = Matrix.identity(this.latentFactors, this.latentFactors);
		for( int i = 0 ; i < this.Iterations ; i++ )
		{
			System.out.println("Iteration: " + i);
			double error = 0; //overall error of this iteration
			//fix item matrix M, solve user matrix U
			for( int j = 0 ; j < this.userMatrix.getRows() ; j++ )
			{
				//latent factor of items that are rated by user j
				int n = this.ratingMatrix.getUserRatingNumber(j);//number of items rated by user j
				double[][] m = new double[n][this.latentFactors];
				int index = 0;
				for( int k = 0 ; k < this.itemMatrix.getRows() ; k++ )
				{
					if( !Double.isNaN(this.ratingMatrix.get(j, k)) )
					{
						m[index++] = this.itemMatrix.getRowValues(k);
					}
				}
				Matrix M = new Matrix(m);
				//step 1:
				Matrix A = M.transpose().times(M).plus(identify.times(this.regUser).times(n));
				//step 2:
				double[][] r = new double[1][n];//ratings of this user
				int index1 = 0;
				for( int k = 0 ; k < this.ratingMatrix.getColumn() ; k++ )
				{
					Double rating = this.ratingMatrix.getRatingMatrix().get(j).get(k);
					if( rating != null )
					{
						r[0][index1++] = rating.doubleValue();
					}
				}
				Matrix R = new Matrix(r);
				Matrix V = M.transpose().times(R.transpose());
				//step 3: the updated user matrix wrt user j
				Matrix uj = A.inverse().times(V);
				this.userMatrix.getValues()[j] = uj.transpose().getArray()[0];
			}
			//fix user matrix U, solve item matrix M
			for( int j = 0 ; j < this.itemMatrix.getRows() ; j++ )
			{
				//latent factor of users that have rated item j
				int n = this.ratingMatrix.getItemRatingNumber(j);//number of users rate item j
				double[][] u = new double[n][this.latentFactors];
				int index = 0;
				for( int k = 0 ; k < this.userMatrix.getRows() ; k++ )
				{
					if( !Double.isNaN(this.ratingMatrix.get(k, j)) )
					{
						u[index++] = this.userMatrix.getRowValues(k);
					}
				}
				if( u.length == 0 )
					continue;
				Matrix U = new Matrix(u);
				
				//step 1:
				Matrix A = U.transpose().times(U).plus(identify.times(this.regItem).times(n));
				
				//step 2:
				double[][] r = new double[1][n];//ratings of this item
				int index1 = 0;
				for( int k = 0 ; k < this.ratingMatrix.getRow() ; k++ )
				{
					Double rating = this.ratingMatrix.getRatingMatrix().get(k).get(j);
					if( rating != null )
					{
						r[0][index1++] = rating.doubleValue();
					}
				}
				Matrix R = new Matrix(r);
				Matrix V = U.transpose().times(R.transpose());
				//step 3: the updated item matrix wrt item j
				Matrix mj = A.inverse().times(V);
				this.itemMatrix.getValues()[j] = mj.transpose().getArray()[0];
			}
			//check for convergence
			//error
			ArrayList<MatrixEntry2D> entries = this.ratingMatrix.getValidEntries();
			for( int k = 0 ; k < entries.size() ; k++ )
			{
				MatrixEntry2D entry = entries.get(k);
				double prediction = predict( entry.getRowIndex() , entry.getColumnIndex() );
				if( prediction > this.maxRating )
					prediction = this.maxRating;
				if( prediction < this.minRating )
					prediction = this.minRating;
				error = error + Math.abs(entry.getValue() - prediction);
				for( int j = 0 ; j < this.latentFactors ; j++ )
				{
					error = error + this.regUser/2 * Math.pow(this.userMatrix.get(entry.getRowIndex(), j), 2) + 
							this.regItem/2 * Math.pow(this.itemMatrix.get(entry.getColumnIndex(), j), 2);
				}		
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
//			this.userMatrixPrevious = this.userMatrix.clone();
//			this.itemMatrixPrevious = this.itemMatrix.clone();
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
//		int max = this.config.getInt("MAX_RATING");
//		int min = this.config.getInt("MIN_RATING");
//		if( prediction > max )
//		{
//			prediction = max;
//		}
//		if( prediction < min )
//		{
//			prediction = min;
//		}
		return prediction;
	}
	
	/**
	 * This function generates a recommendation list for a given user
	 * @param: index of the user
	 * */
	public ArrayList<ResultUnit> getRecommendationList( int userIndex )
	{
		ArrayList<ResultUnit> recommendationList = new ArrayList<ResultUnit>();
		//find all item candidate list (items that are not rated by the user)
		for( int i = 0 ; i < this.ratingMatrix.getColumn() ; i++ )
		{
			if( this.ratingMatrix.getRatingMatrix().get(userIndex).get(i) != null )
			{
				//this item has not been rated by the item
				ResultUnit unit = new ResultUnit( userIndex , i , predict( userIndex , i) );
				recommendationList.add(unit);
			}
		}
	
		//sort the recommendation list
		Collections.sort(recommendationList);
		ArrayList<ResultUnit> result = new ArrayList<ResultUnit>();
		for( int i = recommendationList.size() - 1 ; i >= recommendationList.size() - this.topN ; i-- )
		{
			System.out.print(recommendationList.get(i).getPrediciton() + " , ");
			result.add(recommendationList.get(i));
		}
		System.out.println();
		return result;
	}
	
	@Override
	public void saveModel(String file) {
		// TODO Auto-generated method stub
		try{
			//save users latent factors
			PrintWriter printer1 = new PrintWriter(file+"_user");
			for( int i = 0 ; i < this.userMatrix.getRows() ; i++ )
			{
				for( int j = 0 ; j < this.userMatrix.getColumns() ; j++ )
				{
					printer1.print(this.userMatrix.get(i, j) + "\t");
				}
				printer1.println();
			}
			printer1.flush();
			printer1.close();
			
			//save items latent factors
			PrintWriter printer2 = new PrintWriter(file+"_item");
			for( int i = 0 ; i < this.itemMatrix.getRows() ; i++ )
			{
				for( int j = 0 ; j < this.itemMatrix.getColumns() ; j++ )
				{
					printer2.print(this.itemMatrix.get(i, j) + "\t");
				}
				printer2.println();
			}
			printer2.flush();
			printer2.close();
		}catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public void readModel(String file) {
		// TODO Auto-generated method stub
		try{
			//read user latent factors
			BufferedReader reader1 = new BufferedReader( new FileReader(file+"_user") );
			String line = null;
			int u1 = 0;
			while( (line = reader1.readLine()) != null )
			{
				StringTokenizer tokens = new StringTokenizer( line.trim() );
				int u2 = 0;
				while( tokens.hasMoreElements() )
				{
					this.userMatrix.set(u1, u2, Double.parseDouble(tokens.nextToken()));
					u2++;
				}
				u1++;
			}
			reader1.close();
			
			//read item latent factors
			BufferedReader reader2 = new BufferedReader( new FileReader(file+"_item") );
			String line2 = null;
			int i1 = 0;
			while( (line2 = reader2.readLine()) != null )
			{
				StringTokenizer tokens = new StringTokenizer( line2.trim() );
				int i2 = 0;
				while( tokens.hasMoreElements() )
				{
					this.itemMatrix.set(i1, i2, Double.parseDouble(tokens.nextToken()));
					i2++;
				}
				i1++;
			}
			reader2.close();
		}catch( IOException e )
		{
			e.printStackTrace();
		}
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
	 * @return the userMatrix
	 */
	public LatentMatrix getUserMatrix() {
		return userMatrix;
	}


	/**
	 * @param userMatrix the userMatrix to set
	 */
	public void setUserMatrix(LatentMatrix userMatrix) {
		this.userMatrix = userMatrix;
	}


	/**
	 * @return the itemMatrix
	 */
	public LatentMatrix getItemMatrix() {
		return itemMatrix;
	}


	/**
	 * @param itemMatrix the itemMatrix to set
	 */
	public void setItemMatrix(LatentMatrix itemMatrix) {
		this.itemMatrix = itemMatrix;
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
	 * @return the initialization
	 */
	public int getInitialization() {
		return initialization;
	}


	/**
	 * @param initialization the initialization to set
	 */
	public void setInitialization(int initialization) {
		this.initialization = initialization;
	}


	/**
	 * @return the latentFactors
	 */
	public int getLatentFactors() {
		return latentFactors;
	}


	/**
	 * @param latentFactors the latentFactors to set
	 */
	public void setLatentFactors(int latentFactors) {
		this.latentFactors = latentFactors;
	}


	/**
	 * @return the iterations
	 */
	public int getIterations() {
		return Iterations;
	}


	/**
	 * @param iterations the iterations to set
	 */
	public void setIterations(int iterations) {
		Iterations = iterations;
	}


	/**
	 * @return the learningRate
	 */
	public double getLearningRate() {
		return learningRate;
	}


	/**
	 * @param learningRate the learningRate to set
	 */
	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	/**
	 * @return the regUser
	 */
	public double getRegUser() {
		return regUser;
	}

	/**
	 * @param regUser the regUser to set
	 */
	public void setRegUser(double regUser) {
		this.regUser = regUser;
	}

	/**
	 * @return the regItem
	 */
	public double getRegItem() {
		return regItem;
	}

	/**
	 * @param regItem the regItem to set
	 */
	public void setRegItem(double regItem) {
		this.regItem = regItem;
	}

	/**
	 * @return the convergence
	 */
	public double getConvergence() {
		return convergence;
	}

	/**
	 * @param convergence the convergence to set
	 */
	public void setConvergence(double convergence) {
		this.convergence = convergence;
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
