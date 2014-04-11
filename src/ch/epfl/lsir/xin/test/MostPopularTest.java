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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import ch.epfl.lsir.xin.algorithm.baseline.MostPopular;
import ch.epfl.lsir.xin.datatype.RatingMatrix;
import ch.epfl.lsir.xin.evaluation.RankResultGenerator;
import ch.epfl.lsir.xin.evaluation.ResultUnit;
import ch.epfl.lsir.xin.io.DataLoaderFile;
import ch.epfl.lsir.xin.model.DataSetNumeric;
import ch.epfl.lsir.xin.model.NumericRating;
import ch.epfl.lsir.xin.model.User;
import ch.epfl.lsir.xin.util.TrainTestSplitter;

public class MostPopularTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		PrintWriter logger = new PrintWriter(".//results//MostPopular");
		PropertiesConfiguration config = new PropertiesConfiguration();
		config.setFile(new File(".//conf//MostPopular.properties"));
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
		
		TrainTestSplitter splitter = new TrainTestSplitter(dataset);
		splitter.splitFraction(config.getDouble("TRAIN_FRACTION"));
		ArrayList<NumericRating> trainRatings = splitter.getTrain();
		ArrayList<NumericRating> testRatings = splitter.getTest();
		
		HashMap<String , Integer> userIDIndexMapping = new HashMap<String , Integer>();
		HashMap<String , Integer> itemIDIndexMapping = new HashMap<String , Integer>();
		//create rating matrix
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
		RatingMatrix testRatingMatrix = new RatingMatrix( dataset.getUserIDs().size() , 
				dataset.getItemIDs().size() );
		for( int i = 0 ; i < testRatings.size() ; i++ )
		{
			//only consider 5-star rating in the test set
			if( testRatings.get(i).getValue() < 5 )
				continue;
			testRatingMatrix.set(userIDIndexMapping.get(testRatings.get(i).getUserID()), 
					itemIDIndexMapping.get(testRatings.get(i).getItemID()), testRatings.get(i).getValue() );
		}
		System.out.println("Training: " + trainRatingMatrix.getTotalRatingNumber() + " vs Test: "
				+ testRatingMatrix.getTotalRatingNumber() );
		
		logger.println("Initialize a most popular based recommendation model.");
		MostPopular algo = new MostPopular( trainRatingMatrix );
		algo.setLogger(logger);
		algo.build();
		algo.saveModel(".//localModels//" + config.getString("NAME"));
		logger.println("Save the model.");
		logger.flush();
		
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
		}

		RankResultGenerator generator = new RankResultGenerator(results , algo.getTopN() , testRatingMatrix);
		System.out.println("Precision@N: " + generator.getPrecisionN());
		System.out.println("Recall@N: " + generator.getRecallN());
		System.out.println("MAP@N: " + generator.getMAPN());
		System.out.println("MRR@N: " + generator.getMRRN());
		System.out.println("NDCG@N: " + generator.getNDCGN());
		System.out.println("AUC@N: " + generator.getAUC());
		logger.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n" + 
				"Precision@N: " + generator.getPrecisionN() + "\n" + 
				"Recall@N: " + generator.getRecallN() + "\n" + 
				"MAP@N: " + generator.getMAPN() + "\n" +
				"MRR@N: " + generator.getMRRN() + "\n" + 
				"NDCG@N: " + generator.getNDCGN() + "\n" + 
				"AUC@N: " + generator.getAUC() );
		logger.flush();
		logger.close();
	}
	/** MovieLens100k
	 Precision@N: 0.07347715736040637
	Recall@N: 0.16619890977893928
	MAP@N: 0.11575719120135364
	MRR@N: 0.2342055434695027
	NDCG@N: 0.28388440883176297
	AUC@N: 0.6151238439113035
	 * */

}
