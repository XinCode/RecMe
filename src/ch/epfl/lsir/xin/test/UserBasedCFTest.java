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

package ch.epfl.lsir.xin.test;

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

import ch.epfl.lsir.xin.algorithm.core.UserBasedCF;
import ch.epfl.lsir.xin.datatype.RatingMatrix;
import ch.epfl.lsir.xin.evaluation.RankResultGenerator;
import ch.epfl.lsir.xin.evaluation.ResultUnit;
import ch.epfl.lsir.xin.io.DataLoaderFile;
import ch.epfl.lsir.xin.model.DataSetNumeric;
import ch.epfl.lsir.xin.model.NumericRating;

public class UserBasedCFTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		PrintWriter logger = new PrintWriter(".//results//UserBasedCF");
		PropertiesConfiguration config = new PropertiesConfiguration();
		config.setFile(new File(".//conf//UserBasedCF.properties"));
		try {
			config.load();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) +" Read rating data...");
		DataLoaderFile loader = new DataLoaderFile(".//data//MoveLens100k.txt");
		loader.readSimple();
		DataSetNumeric dataset = loader.getDataset();
		System.out.println("Number of ratings: " + dataset.getRatings().size() + 
				" Number of users: " + dataset.getUserIDs().size() + " Number of items: "
				+ dataset.getItemIDs().size());
		logger.println("Number of ratings: " + dataset.getRatings().size() + 
				", Number of users: " + dataset.getUserIDs().size() + ", Number of items: "
				+ dataset.getItemIDs().size() );
		logger.flush();
		
		double totalMAE = 0;
		double totalRMSE = 0;
		double totalPrecision = 0;
		double totalRecall = 0;
		double totalMAP = 0;
		double totalNDCG = 0;
		double totalMRR = 0;
		double totalAUC = 0;
		int F = 5;
		logger.println(F + "- folder cross validation.");
		ArrayList<ArrayList<NumericRating>> folders = new ArrayList<ArrayList<NumericRating>>();
		for( int i = 0 ; i < F ; i++ )
		{
			folders.add(new ArrayList<NumericRating>());
		}
		while( dataset.getRatings().size() > 0 )
		{
			int index = new Random().nextInt(dataset.getRatings().size());
			int r = new Random().nextInt(F);
			folders.get(r).add(dataset.getRatings().get(index));
			dataset.getRatings().remove(index);
		}
		
		for( int folder = 1 ; folder <= F ; folder++ )
		{
			logger.println("Folder: " + folder ); 
			System.out.println("Folder: " + folder);
			ArrayList<NumericRating> trainRatings = new ArrayList<NumericRating>();
			ArrayList<NumericRating> testRatings = new ArrayList<NumericRating>();
			for( int i = 0 ; i < folders.size() ; i++ )
			{
				if( i == folder - 1 )//test data
				{
					testRatings.addAll(folders.get(i));
				}else{//training data
					trainRatings.addAll(folders.get(i));
				}
			}
			
			//create rating matrix
			HashMap<String , Integer> userIDIndexMapping = new HashMap<String , Integer>();
			HashMap<String , Integer> itemIDIndexMapping = new HashMap<String , Integer>();
			for( int i = 0 ; i < dataset.getUserIDs().size() ; i++ )
			{
				userIDIndexMapping.put(dataset.getUserIDs().get(i), i);
			}
			for( int i = 0 ; i < dataset.getItemIDs().size() ; i++ )
			{
				itemIDIndexMapping.put(dataset.getItemIDs().get(i) , i);
			}
			RatingMatrix trainRatingMatrix = new RatingMatrix( dataset.getUserIDs().size() , 
					dataset.getItemIDs().size());
			for( int i = 0 ; i < trainRatings.size() ; i++ )
			{
				trainRatingMatrix.set(userIDIndexMapping.get(trainRatings.get(i).getUserID()), 
						itemIDIndexMapping.get(trainRatings.get(i).getItemID()), trainRatings.get(i).getValue());
			}
			trainRatingMatrix.calculateGlobalAverage();
			trainRatingMatrix.calculateUsersMean();
			RatingMatrix testRatingMatrix = new RatingMatrix( dataset.getUserIDs().size() , 
					dataset.getItemIDs().size() );
			for( int i = 0 ; i < testRatings.size() ; i++ )
			{
				if( testRatings.get(i).getValue() < 5 )
					continue;
				testRatingMatrix.set(userIDIndexMapping.get(testRatings.get(i).getUserID()), 
						itemIDIndexMapping.get(testRatings.get(i).getItemID()), testRatings.get(i).getValue() );
			}
			logger.println("Initialize a user based collaborative filtering recommendation model.");
			UserBasedCF algo = new UserBasedCF( trainRatingMatrix , false , 
					".//localModels//" + config.getString("NAME"));
			algo.setLogger(logger);
			algo.build();//if read local model, no need to build the model
			algo.saveModel(".//localModels//" + config.getString("NAME"));
			logger.println("Save the model.");
			System.out.println(trainRatings.size() + " vs. " + testRatings.size());
			logger.flush();
			
			//rating prediction accuracy
			double RMSE = 0;
			double MAE = 0;
			double precision = 0;
			double recall = 0;
			double map = 0;
			double ndcg = 0;
			double mrr = 0;
			double auc = 0;
			int count = 0;
			for( int i = 0 ; i < testRatings.size() ; i++ )
			{
				NumericRating rating = testRatings.get(i);
				double prediction = algo.predict(userIDIndexMapping.get(rating.getUserID()), 
						itemIDIndexMapping.get(rating.getItemID()) , false);

				if( Double.isNaN(prediction) )
				{
					System.out.println("no prediction");
					continue;
				}
				if( prediction > algo.getMaxRating() )
					prediction = algo.getMaxRating();
				if( prediction < algo.getMinRating() )
					prediction = algo.getMinRating();
				MAE = MAE + Math.abs(rating.getValue() - prediction);
				RMSE = RMSE + Math.pow((rating.getValue() - prediction) , 2);
				count++;
			}
			MAE = MAE/count;
			RMSE = Math.sqrt(RMSE/count);
			totalMAE = totalMAE + MAE;
			totalRMSE = totalRMSE + RMSE;
			System.out.println("Folder --- MAE: " + MAE + " RMSE: " + RMSE);
			logger.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + 
					" Folder --- MAE: " + MAE + " RMSE: " + RMSE);
			logger.flush();
			//ranking accuracy
			if( algo.getTopN() > 0 )
			{
				HashMap<Integer , ArrayList<ResultUnit>> results = new HashMap<Integer , ArrayList<ResultUnit>>();
				for( int i = 0 ; i < testRatingMatrix.getRow() ; i++ )
				{
					ArrayList<ResultUnit> rec = algo.getRecommendationList(i);
					if( rec == null )
						continue;
					int total = testRatingMatrix.getUserRatingNumber(i);
					if( total == 0 )//this user is ignored
						continue;
					results.put(i, rec);
//					for( Map.Entry<Integer, Double> entry : testRatingMatrix.getRatingMatrix().get(i).entrySet() )
//					{
//						System.out.print( entry.getKey() + "(" + entry.getValue() + ") , ");
//					}
//					System.out.println();
//					for( int j = 0 ; j < rec.size() ; j++ )
//					{
//						System.out.print(rec.get(j).getItemIndex() + "(" + rec.get(j).getPrediciton() +
//								") , ");
//					}
//					System.out.println("**********");
				}
				RankResultGenerator generator = new RankResultGenerator(results , algo.getTopN() , testRatingMatrix);
				precision = generator.getPrecisionN();
				totalPrecision = totalPrecision + precision;
				recall = generator.getRecallN();
				totalRecall = totalRecall + recall;
				map = generator.getMAPN();
				totalMAP = totalMAP + map;
				ndcg = generator.getNDCGN();
				totalNDCG = totalNDCG + ndcg;
				mrr = generator.getMRRN();
				totalMRR = totalMRR + mrr;
				auc = generator.getAUC();
				totalAUC = totalAUC + auc;
				System.out.println("Folder --- precision: " + precision + " recall: " + 
				recall + " map: " + map + " ndcg: " + ndcg + " mrr: " + mrr + " auc: " + auc);
				logger.println("Folder --- precision: " + precision + " recall: " + 
						recall + " map: " + map + " ndcg: " + ndcg + " mrr: " + 
						mrr + " auc: " + auc);
			}			
		}
		
		System.out.println("MAE: " + totalMAE/F + " RMSE: " + totalRMSE/F);
		System.out.println("Precision@N: " + totalPrecision/F);
		System.out.println("Recall@N: " + totalRecall/F);
		System.out.println("MAP@N: " + totalMAP/F);
		System.out.println("MRR@N: " + totalMRR/F);
		System.out.println("NDCG@N: " + totalNDCG/F);
		System.out.println("AUC@N: " + totalAUC/F);
		// MovieLens100k
		//MAE: 0.7343907480119425 RMSE: 0.9405808357192891 (MovieLens 100K, shrinkage 25, neighbor size 60, PCC)
		//MAE: 0.7522376630596646 RMSE: 0.9520931265724659 (MovieLens 100K, no shrinkage , neighbor size 40, COSINE)
		/**
		 * Precision@N: 0.09775251511582342
			Recall@N: 0.19148039452057336
			MAP@N: 0.1426564541136997
			MRR@N: 0.30048667156091896
			NDCG@N: 0.3470226035028776
			AUC@N: 0.6447526748573156
		 * */
		logger.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n" + 
				"MAE: " + totalMAE/F + " RMSE: " + totalRMSE/F + "\n" + 
				"Precision@N: " + totalPrecision/F + "\n" + 
				"Recall@N: " + totalRecall/F + "\n" + 
				"MAP@N: " + totalMAP/F + "\n" +
				"MRR@N: " + totalMRR/F + "\n" + 
				"NDCG@N: " + totalNDCG/F + "\n" + 
				"AUC@N: " + totalAUC/F );
		logger.flush();
		logger.close();

	}

}
