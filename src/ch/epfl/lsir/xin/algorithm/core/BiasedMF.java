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
 * This class implements the biased matrix factorization see:
 * Section 'adding Biases' of paper "MATRIX FACTORIZATION TECHNIQUES FOR RECOMMENDER SYSTEMS"
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

import ch.epfl.lsir.xin.algorithm.IAlgorithm;
import ch.epfl.lsir.xin.datatype.LatentMatrix;
import ch.epfl.lsir.xin.datatype.MatrixEntry2D;
import ch.epfl.lsir.xin.datatype.RatingMatrix;
import ch.epfl.lsir.xin.evaluation.ResultUnit;

public class BiasedMF implements IAlgorithm {

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
	 * user bias
	 * */
	private double[] userBias = null;
	
	/**
	 * item bias
	 * */
	private double[] itemBias = null;
	
	/**
	 * SGD parameters
	 * */
	private int latentFactors = -1;
	private int iterations = -1;
	private double learningRate = -1;
	private double biasLearningRate = -1;
	private double userReg = -1;
	private double itemReg = -1;
	private double biasUserReg = -1;
	private double biasItemReg = -1;
	private double convergence = -1;
	
	private double globalAverage = -1;
	
	private int topN = -1;
	
	private String optimization = null;
	
	private int maxRating = -1;
	private int minRating = -1;
	
	
	
	/**
	 * constructor
	 * */
	public BiasedMF( RatingMatrix ratingMatrix , boolean readModel , String modelFile )
	{
		//set configuration file for parameter setting.
		config.setFile(new File(".//conf//biasedMF.properties"));
		try {
			config.load();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.ratingMatrix = ratingMatrix;
		this.initialization = this.config.getInt("INITIALIZATION");
		this.iterations = this.config.getInt("ITERATIONS");
		this.convergence = this.config.getDouble("CONVERGENCE");
		this.optimization = this.config.getString("OPTIMIZATION_METHOD");
		this.topN = this.config.getInt("TOP_N_RECOMMENDATION");
		this.ratingMatrix.calculateGlobalAverage();
		this.globalAverage = this.ratingMatrix.getAverageRating();
		this.maxRating = this.config.getInt("MAX_RATING");
		this.minRating = this.config.getInt("MIN_RATING");
		
		this.latentFactors = this.config.getInt("LATENT_FACTORS");
		this.userReg = this.config.getDouble("REGULARIZATION_USER");
		this.itemReg = this.config.getDouble("REGULARIZATION_ITEM");
		this.biasUserReg = this.config.getDouble("BIAS_REGULARIZATION_USER");
		this.biasItemReg = this.config.getDouble("BIAS_REGULARIZATION_ITEM");
		this.learningRate = this.config.getDouble("LEARNING_RATE");
		this.biasLearningRate = this.config.getDouble("BIAS_LEARNING_RATE");
		
		this.userMatrix = new LatentMatrix( this.ratingMatrix.getRow() , this.latentFactors);
		this.userMatrix.setInitialization(this.initialization); 
		this.userMatrix.valueInitialization();
		this.userMatrixPrevious = this.userMatrix.clone();
		this.itemMatrix = new LatentMatrix( this.ratingMatrix.getColumn() , this.latentFactors);
		this.itemMatrix.setInitialization(this.initialization);
		this.itemMatrix.valueInitialization();
		this.itemMatrixPrevious = this.itemMatrix.clone();
		
		this.userBias = new double[this.ratingMatrix.getRow()];
		this.itemBias = new double[this.ratingMatrix.getColumn()];
		
		if( readModel )
		{
			this.readModel(modelFile);
		}
	}
	
	@Override
	public void saveModel(String file) {
		// TODO Auto-generated method stub

		try {
			//write user bias
			PrintWriter ubPrinter = new PrintWriter(file + "_userBias");
			for( int i = 0 ; i < this.userBias.length ; i++ )
			{
				ubPrinter.println(this.userBias[i]);
			}
			ubPrinter.flush();
			ubPrinter.close();
			
			//write item bias
			PrintWriter ibPrinter = new PrintWriter(file + "_itemBias");
			for( int i = 0 ; i < this.itemBias.length ; i++ )
			{
				ibPrinter.println(this.itemBias[i]);
			}
			ibPrinter.flush();
			ibPrinter.close();
			
			//user factors
			PrintWriter uPrinter = new PrintWriter(file + "_userFactors");
			for( int i = 0 ; i < this.userMatrix.getValues().length ; i++ )
			{
				for( int j = 0 ; j < this.userMatrix.getValues()[i].length ; j++ )
				{
					uPrinter.print(this.userMatrix.getValues()[i][j] + "\t");
				}
				uPrinter.println();
			}
			uPrinter.flush();
			uPrinter.close();
			
			//item factors
			PrintWriter iPrinter = new PrintWriter(file + "_itemFactors");
			for( int i = 0 ; i < this.itemMatrix.getValues().length ; i++ )
			{
				for( int j = 0 ; j < this.itemMatrix.getValues()[i].length ; j++ )
				{
					iPrinter.print(this.itemMatrix.getValues()[i][j] + "\t");
				}
				iPrinter.println();
			}
			iPrinter.flush();
			iPrinter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void readModel(String file) {
		// TODO Auto-generated method stub

		try{
			//read user bias
			BufferedReader ubReader = new BufferedReader( new FileReader(file + "_userBias") );
			String line = null;
			int index1 = 0;
			while( (line = ubReader.readLine()) != null )
			{
				this.userBias[index1++] = Double.parseDouble(line);
			}
			ubReader.close();
			
			//read item bias
			BufferedReader ibReader = new BufferedReader( new FileReader(file + "_itemBias") );
			int index2 = 0;
			while( (line = ibReader.readLine()) != null )
			{
				this.itemBias[index2++] = Double.parseDouble(line);
			}
			ibReader.close();
			
			//read user factors
			BufferedReader uReader = new BufferedReader( new FileReader(file + "_userFactors") );
			int index3 = 0;
			while( (line = uReader.readLine()) != null )
			{
				StringTokenizer tokens = new StringTokenizer( line );
				int index = 0;
				while( tokens.hasMoreElements() )
				{
					this.userMatrix.set(index3 , index++ , Double.parseDouble(tokens.nextToken()));
				}
				index3++;
			}
			uReader.close();
			
			//read item factors
			BufferedReader iReader = new BufferedReader( new FileReader(file + "_itemFactors") );
			int index4 = 0;
			while( (line = iReader.readLine()) != null )
			{
				StringTokenizer tokens = new StringTokenizer( line );
				int index = 0;
				while( tokens.hasMoreElements() )
				{
					this.itemMatrix.set(index4, index++ , Double.parseDouble(tokens.nextToken()));
				}
				index4++;
			}
			iReader.close();
		}catch( IOException e )
		{
			e.printStackTrace();
		}
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
			buildALS();
			logger.println("ALS is used to train the model.");
		}else{
			logger.println("Optimization method is not set properly.");
		}
	}
	
	/**
	 * SGD learning 
	 * */
	public void buildSGD()
	{
		double preError = Double.MAX_VALUE;
		for( int i = 0 ; i < this.iterations ; i++ )
		{
			System.out.println("Iteration: " + i);
			ArrayList<MatrixEntry2D> entries = this.ratingMatrix.getValidEntries();
			double error = 0; //overall error of this iteration
			while( entries.size() > 0 )
			{
				//find a random entry
				int r = new Random().nextInt(entries.size());
				MatrixEntry2D entry = entries.get(r);
				double prediction = predict( entry.getRowIndex() , entry.getColumnIndex() , false );
				if( prediction > this.maxRating )
					prediction = this.maxRating;
				if( prediction < this.minRating )
					prediction = this.minRating;
				double difference = entry.getValue() - prediction;
				
				//update user bias
				double newUserBias = this.userBias[entry.getRowIndex()] + this.biasLearningRate * ( difference
						- this.biasUserReg * this.userBias[entry.getRowIndex()] );
				this.userBias[entry.getRowIndex()] = newUserBias;
				//update item bias
				double newItemBias = this.itemBias[entry.getColumnIndex()] + this.biasLearningRate * ( difference
						- this.biasItemReg * this.itemBias[entry.getColumnIndex()] );				
				this.itemBias[entry.getColumnIndex()] = newItemBias;
				
				//update user and item latent factors
				for( int j = 0 ; j < this.latentFactors ; j++ )
				{
					//update user factors
					double newUserFactors = this.userMatrix.get(entry.getRowIndex(), j) + this.learningRate
							* ( difference * this.itemMatrix.get(entry.getColumnIndex(), j) - this.userReg
							* this.userMatrix.get(entry.getRowIndex(), j) );
					//update item factors
					double newItemFactors = this.itemMatrix.get(entry.getColumnIndex(), j) + this.learningRate *
							( difference * this.userMatrix.get(entry.getRowIndex(), j) - this.itemReg * 
							this.itemMatrix.get(entry.getColumnIndex(), j) );
					this.userMatrix.set(entry.getRowIndex(), j, newUserFactors);
					this.itemMatrix.set(entry.getColumnIndex(), j, newItemFactors);
				}
				
				//one rating is only processed once in an iteration
				entries.remove(r);
			}
			//error
			entries = this.ratingMatrix.getValidEntries();
			for( int k = 0 ; k < entries.size() ; k++ )
			{
				MatrixEntry2D entry = entries.get(k);
				double prediction = predict( entry.getRowIndex() , entry.getColumnIndex() , false);
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
//			this.userMatrixPrevious = this.userMatrix.clone();
//			this.itemMatrixPrevious = this.itemMatrix.clone();
		}else if( update == 2 )//bold driver learning rate update algorithm
		{
			if( Math.abs(error) < Math.abs(preError) )
			{
				this.learningRate = (1 + 0.05) * this.learningRate;
				this.biasLearningRate = (1 + 0.05) * this.biasLearningRate;
				logger.println("Increase learning rate by 5%.");
				this.userMatrixPrevious = this.userMatrix.clone();
				this.itemMatrixPrevious = this.itemMatrix.clone();
			}else if( Math.abs(error) > Math.abs(preError) )
			{
				this.learningRate = 0.5 * this.learningRate;
				this.biasLearningRate = 0.5 * this.biasLearningRate;
				this.userMatrix = this.userMatrixPrevious.clone();//roll back to previous iteration
				this.itemMatrix = this.itemMatrixPrevious.clone();
				logger.println("Decrease learning rate by 50%.");
				}
		}else if( update == 3 ) {//decaying learning rate by a constant rate
			double decay = this.config.getDouble("LEARNING_RATING_DECAY");
			this.learningRate = this.learningRate * decay;
			this.biasLearningRate = this.biasLearningRate * decay;
			this.userMatrixPrevious = this.userMatrix.clone();
			this.itemMatrixPrevious = this.itemMatrix.clone();
		}
	}
	
	/**
	 * ALS learning
	 * */
	public void buildALS(){}
	
	/**
	 * this function predicts a user to an item
	 * @param: index of the user
	 * @param: index of the item
	 * */
	public double predict( int user , int item , boolean rank )
	{
		double prediction = 0;
		for( int i = 0 ; i < this.latentFactors ; i++ )
		{
			prediction = prediction + this.userMatrix.getValues()[user][i] * 
					this.itemMatrix.getValues()[item][i];
		}
//		if( rank )
//			return prediction;
		prediction += this.globalAverage + this.userBias[user] + this.itemBias[item];
		
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
			if( this.ratingMatrix.getRatingMatrix().get(userIndex).get(i) == null )
			{
				//this item has not been rated by the item
				ResultUnit unit = new ResultUnit( userIndex , i , predict( userIndex , i , true) );
				recommendationList.add(unit);
			}
		}
	
		//sort the recommendation list
		Collections.sort(recommendationList);
		ArrayList<ResultUnit> result = new ArrayList<ResultUnit>();
		for( int i = recommendationList.size() - 1 ; i >= recommendationList.size() - this.topN ; i-- )
		{
//			System.out.print(recommendationList.get(i).getPrediciton() + " , ");
			result.add(recommendationList.get(i));
		}
//		System.out.println();
		return result;
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
	 * @return the userBias
	 */
	public double[] getUserBias() {
		return userBias;
	}

	/**
	 * @param userBias the userBias to set
	 */
	public void setUserBias(double[] userBias) {
		this.userBias = userBias;
	}

	/**
	 * @return the itemBias
	 */
	public double[] getItemBias() {
		return itemBias;
	}

	/**
	 * @param itemBias the itemBias to set
	 */
	public void setItemBias(double[] itemBias) {
		this.itemBias = itemBias;
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
		return iterations;
	}

	/**
	 * @param iterations the iterations to set
	 */
	public void setIterations(int iterations) {
		this.iterations = iterations;
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
	 * @return the biasLearningRate
	 */
	public double getBiasLearningRate() {
		return biasLearningRate;
	}

	/**
	 * @param biasLearningRate the biasLearningRate to set
	 */
	public void setBiasLearningRate(double biasLearningRate) {
		this.biasLearningRate = biasLearningRate;
	}

	/**
	 * @return the userReg
	 */
	public double getUserReg() {
		return userReg;
	}

	/**
	 * @param userReg the userReg to set
	 */
	public void setUserReg(double userReg) {
		this.userReg = userReg;
	}

	/**
	 * @return the itemReg
	 */
	public double getItemReg() {
		return itemReg;
	}

	/**
	 * @param itemReg the itemReg to set
	 */
	public void setItemReg(double itemReg) {
		this.itemReg = itemReg;
	}

	/**
	 * @return the biasUserReg
	 */
	public double getBiasUserReg() {
		return biasUserReg;
	}

	/**
	 * @param biasUserReg the biasUserReg to set
	 */
	public void setBiasUserReg(double biasUserReg) {
		this.biasUserReg = biasUserReg;
	}

	/**
	 * @return the biasItemReg
	 */
	public double getBiasItemReg() {
		return biasItemReg;
	}

	/**
	 * @param biasItemReg the biasItemReg to set
	 */
	public void setBiasItemReg(double biasItemReg) {
		this.biasItemReg = biasItemReg;
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
	 * @return the globalAverage
	 */
	public double getGlobalAverage() {
		return globalAverage;
	}

	/**
	 * @param globalAverage the globalAverage to set
	 */
	public void setGlobalAverage(double globalAverage) {
		this.globalAverage = globalAverage;
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
	 * @return the optimization
	 */
	public String getOptimization() {
		return optimization;
	}

	/**
	 * @param optimization the optimization to set
	 */
	public void setOptimization(String optimization) {
		this.optimization = optimization;
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
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
