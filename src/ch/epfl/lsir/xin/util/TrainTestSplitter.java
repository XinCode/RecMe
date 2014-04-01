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
 * This class splits the input dataset into training data and test data based on 
 * different criteria.
 * 
 *  @author Xin Liu
 * */

package ch.epfl.lsir.xin.util;

import java.util.ArrayList;
import java.util.Random;

import ch.epfl.lsir.xin.model.AbstractDataSet;
import ch.epfl.lsir.xin.model.AbstractRating;

public class TrainTestSplitter {

	/**
	 * input dataset
	 * */
	private AbstractDataSet dataset = null;
	
	/**
	 * the output train data
	 * */
	private ArrayList train = null;
	
	/**
	 * the output test data
	 * */
	private ArrayList test = null;
	
	/**
	 * constructor
	 * @param: the given input dataset
	 * */
	public TrainTestSplitter( AbstractDataSet dataset )
	{
		this.dataset = dataset;
		this.train = new ArrayList();
		this.test = new ArrayList();
	}
	
	/**
	 * This function splits the data into train and test based on the given fraction.
	 * @param: the fraction for train data
	 * */	
	public void splitFraction( double fraction )
	{
//		for( int i = 0 ; i < this.dataset.getRatings().size() ; i++ )
//		{
//			int r = new Random().nextInt(100)+1;
//			if( r <= fraction * 100 )
//			{
//				this.train.add(this.dataset.getRatings().get(i));
//			}else{
//				this.test.add(this.dataset.getRatings().get(i));
//			}
//		}
		ArrayList ratings = new ArrayList(this.dataset.getRatings());
		while( ratings.size() > 0 )
		{
			int index = new Random().nextInt(ratings.size());
			AbstractRating rating = (AbstractRating)ratings.get( index );
			int r = new Random().nextInt(100)+1;
			if( r <= fraction * 100 )
			{
				this.train.add(rating);
			}else{
				this.test.add(rating);
			}
			ratings.remove(index);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the dataset
	 */
	public AbstractDataSet getDataset() {
		return dataset;
	}

	/**
	 * @param dataset the dataset to set
	 */
	public void setDataset(AbstractDataSet dataset) {
		this.dataset = dataset;
	}

	/**
	 * @return the train
	 */
	public ArrayList getTrain() {
		return train;
	}

	/**
	 * @param train the train to set
	 */
	public void setTrain(ArrayList train) {
		this.train = train;
	}

	/**
	 * @return the test
	 */
	public ArrayList getTest() {
		return test;
	}

	/**
	 * @param test the test to set
	 */
	public void setTest(ArrayList test) {
		this.test = test;
	}

}
