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
import java.util.Random;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import ch.epfl.lsir.xin.algorithm.baseline.ItemAverage;
import ch.epfl.lsir.xin.algorithm.baseline.UserAverage;
import ch.epfl.lsir.xin.datatype.RatingMatrix;
import ch.epfl.lsir.xin.io.DataLoaderFile;
import ch.epfl.lsir.xin.model.DataSetNumeric;
import ch.epfl.lsir.xin.model.NumericRating;
import ch.epfl.lsir.xin.util.TrainTestSplitter;

public class ItemAverageTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		PrintWriter logger = new PrintWriter(".//results//ItemAverage");
		PropertiesConfiguration config = new PropertiesConfiguration();
		config.setFile(new File(".//conf//ItemAverage.properties"));
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
				+ dataset.getItemIDs().size());
		logger.flush();
		
		double totalMAE = 0;
		double totalRMSE = 0;
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
			logger.println("Folder: " + folder);
			logger.flush();
			System.out.println("Folder: " + folder);
			ArrayList<NumericRating> trainRatings = new ArrayList<NumericRating>();
			ArrayList<NumericRating> testRatings = new ArrayList<NumericRating>();
			for( int i = 0 ; i < folders.size() ; i++ )
			{
				if( i == folder-1 )//test data
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
			RatingMatrix testRatingMatrix = new RatingMatrix( dataset.getUserIDs().size() , 
					dataset.getItemIDs().size() );
			for( int i = 0 ; i < testRatings.size() ; i++ )
			{
				testRatingMatrix.set(userIDIndexMapping.get(testRatings.get(i).getUserID()), 
						itemIDIndexMapping.get(testRatings.get(i).getItemID()), testRatings.get(i).getValue() );
			}
			System.out.println("Training: " + trainRatingMatrix.getTotalRatingNumber() + " vs Test: "
					+ testRatingMatrix.getTotalRatingNumber() );
			
			logger.println("Initialize a recommendation model based on item average method.");
			ItemAverage algo = new ItemAverage( trainRatingMatrix );
			algo.setLogger(logger);
			algo.build();
			algo.saveModel(".//localModels//" + config.getString("NAME"));
			logger.println("Save the model.");
			logger.flush();
			System.out.println(trainRatings.size() + " vs. " + testRatings.size());
			
			double RMSE = 0;
			double MAE = 0;
			int count = 0;
			for( int i = 0 ; i < testRatings.size() ; i++ )
			{
				NumericRating rating = testRatings.get(i);
				double prediction = algo.predict(userIDIndexMapping.get(rating.getUserID()), 
						itemIDIndexMapping.get(rating.getItemID()));
				if( Double.isNaN(prediction) )
				{
					System.out.println("no prediction");
					continue;
				}
				MAE = MAE + Math.abs(rating.getValue() - prediction);
				RMSE = RMSE + Math.pow((rating.getValue() - prediction) , 2);
				count++;
			}
			MAE = MAE/count;
			RMSE = Math.sqrt(RMSE/count);
			
			logger.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + 
					" MAE: " + MAE + " RMSE: " + RMSE);
			logger.flush();
//			System.out.println("MAE: " + MAE + " RMSE: " + RMSE);
			totalMAE = totalMAE + MAE;
			totalRMSE = totalRMSE + RMSE;
		}
		
		System.out.println("MAE: " + totalMAE/F + " RMSE: " + totalRMSE/F);
		logger.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + 
				" Final results: MAE: " + totalMAE/F + " RMSE: " + totalRMSE/F);
		logger.flush();
		//MAE: 0.8173633324758338 RMSE: 1.0251973503888645 (MovieLens 100K)
	
	}

}
