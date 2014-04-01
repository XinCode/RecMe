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

package ch.epfl.lsir.xin.model;

import java.util.ArrayList;

public class Item extends AbstractItem {

	/**
	 * rating list of this item
	 * */
	private ArrayList ratings = null;
	
	/**
	 * training data
	 * */
	private ArrayList trainRatings = null;
	
	/**
	 * testing data
	 * */
	private ArrayList testRatings = null;
	
	/**
	 * constructor
	 * @param: id of this item
	 * @param: index of this item
	 * */
	public Item( String itemID , int index )
	{
		super( itemID , index );
		this.ratings = new ArrayList();
		this.trainRatings = new ArrayList();
		this.testRatings = new ArrayList();
	}
	
	/**
	 * add a rating
	 * */
	public void addRating( AbstractRating rating )
	{
		this.ratings.add(rating);
	}
	
	/**
	 * add a training data
	 * @param: the rating to be added
	 * */
	public void addTrainingRating( AbstractRating rating )
	{
		this.trainRatings.add(rating);
	}
	
	/**
	 * add a test data
	 * @param: the rating to be added
	 * */
	public void addTestRating( AbstractRating rating )
	{
		this.testRatings.add(rating);
	}
	

	/**
	 * this function generates the mean rating of this user (training data + test data)
	 * @return: the mean of all ratings
	 * */
	public double getMeanRatingAll()
	{
		double sum = 0;
		int count = 0;
		for( int i = 0 ; i < this.ratings.size() ; i++ )
		{
			sum = sum + ((NumericRating)this.ratings.get(i)).getValue();
			count++;
		}
		if( count < 1 )
		{
			return Double.NaN;
		}else{
			return (double)sum/count;
		}
	}
	
	/**
	 * this function generates the mean rating of this user (training data)
	 * @return: the mean of training ratings
	 * */
	public double getMeanRatingTrain()
	{
		double sum = 0;
		int count = 0;
		for( int i = 0 ; i < this.trainRatings.size() ; i++ )
		{
			sum = sum + ((NumericRating)this.trainRatings.get(i)).getValue();
			count++;
		}
		if( count < 1 )
		{
			return Double.NaN;
		}else{
			return (double)sum/count;
		}
	}
	
	/**
	 * @return the ratings
	 */
	public ArrayList getRatings() {
		return ratings;
	}

	/**
	 * @param ratings the ratings to set
	 */
	public void setRatings(ArrayList ratings) {
		this.ratings = ratings;
	}

	/**
	 * @return the trainRatings
	 */
	public ArrayList getTrainRatings() {
		return trainRatings;
	}

	/**
	 * @param trainRatings the trainRatings to set
	 */
	public void setTrainRatings(ArrayList trainRatings) {
		this.trainRatings = trainRatings;
	}

	/**
	 * @return the testRatings
	 */
	public ArrayList getTestRatings() {
		return testRatings;
	}

	/**
	 * @param testRatings the testRatings to set
	 */
	public void setTestRatings(ArrayList testRatings) {
		this.testRatings = testRatings;
	}
	
	
}
