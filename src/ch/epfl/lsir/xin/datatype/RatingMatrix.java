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
 * This class implements the the data structure that stores the input ratings
 * @author Xin Liu
 * */
package ch.epfl.lsir.xin.datatype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RatingMatrix implements IMatrix<Double> {

	/**
	 * value matrix 
	 * */
	//private double[][] ratingMatrix = null;
	private ArrayList<HashMap<Integer , Double>> ratingMatrix = null;
	
	/**
	 * number of rows
	 * */
	private int row = -1;
	
	/**
	 * number of column
	 * */
	private int column = -1;
	
	/**
	 * Average rating of this matrix
	 * */
	private double averageRating = Double.NaN;
	
	/**
	 * This vector stores the mean rating for all users
	 * */
	private ArrayList<Double> usersMean = null;
	
	/**
	 * This vector stores the mean rating for all items
	 * */
	private ArrayList<Double> itemsMean = null;
	
	/**
	 * This stores the index of items that users have rated
	 * */
	private ArrayList<ArrayList<Integer>> ratedItems = null;
	
	/**
	 * constructor
	 * @param: row of the matrix
	 * @param: colume of the matrix
	 * */
	public RatingMatrix( int row , int column )
	{
		this.row = row;
		this.column = column;
		this.ratingMatrix = new ArrayList<HashMap<Integer , Double>>();
		for( int i = 0 ; i < row ; i++ )
		{
			this.ratingMatrix.add(new HashMap<Integer , Double>());
		}
		this.usersMean = new ArrayList<Double>();
		this.itemsMean = new ArrayList<Double>();
		this.ratedItems = new ArrayList<ArrayList<Integer>>();
	}
	
	/**
	 * this function calculates the mean rating of a given user
	 * @param: the index of a given user
	 * @return: the average ratings of the given user
	 * */
	public double getUserMean( int userIndex )
	{
		double mean = 0;
		int count = 0;
		for( Map.Entry<Integer, Double> entry : this.ratingMatrix.get(userIndex).entrySet() )
		{
			mean += entry.getValue();
			count++;
		}
		
		if( count != 0 )
		{
			return mean/count;
		}else{
			return Double.NaN;
		}
	}
	
	/**
	 * This function calculates the mean rating of a given item
	 * @param: the index of a given item
	 * @return: the average ratings of the given item
	 * */
	public double getItemMean( int itemIndex )
	{
		double mean = 0;
		int count = 0;
		for( int i = 0 ; i < this.ratingMatrix.size() ; i++ )
		{
			Double value = this.ratingMatrix.get(i).get(itemIndex);
			if( value != null )
			{
				mean = mean + value.doubleValue();
				count++;
			}
		}
		if( count != 0 )
		{
			return mean/count;
		}else{
			return Double.NaN;
		}
	}
	
	/**
	 * This function calculates the mean rating for all users
	 * */
	public void calculateUsersMean()
	{
		for( int i = 0 ; i < this.row ; i++ )
		{
			this.usersMean.add(this.getUserMean(i));
		}
	}
	
	/**
	 * This function calculates the mean rating for all items
	 * */
	public void calculateItemsMean()
	{
		for( int j = 0 ; j < this.column ; j++ )
		{
			this.itemsMean.add(this.getItemMean(j));
		}
	}
	
	/**
	 * This function returns the number of ratings of a given user
	 * @param: the index of a given user
	 * @return: the number of ratings of the given user
	 * */
	public int getUserRatingNumber( int userIndex )
	{
		return this.ratingMatrix.get(userIndex).size();
	}
	
	/**
	 * This function returns the number of ratings of a given item
	 * @param: the index of a given item
	 * @return: the number of ratings of the given item
	 * */
	public int getItemRatingNumber( int itemIndex )
	{
		int count = 0;
		for( int i = 0 ; i < this.ratingMatrix.size() ; i++ )
		{
			Double value = this.ratingMatrix.get(i).get(itemIndex);
			if( value != null )
			{
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * this function initializes the variable ratedItems
	 * */
	public void calculateRatedItemIndex()
	{
		for( int i = 0 ; i < this.row ; i++ )
		{
			ArrayList<Integer> array = getRatedItemIndex(i);
			this.ratedItems.add(array);
		}
	}
	
	/**
	 * this function returns the index of items that a given user has rated
	 * @param: index of a user
	 * @return: array of index of items that the user has rated
	 * */
	public ArrayList<Integer> getRatedItemIndex( int userIndex )
	{
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		for( Integer index : this.ratingMatrix.get(userIndex).keySet() )
		{
			indexList.add(index.intValue());
		}
		
		return indexList;
	}

	/**
	 * This function calculates the global average of the rating matrix
	 * @return: the calculated global average
	 * */
	public double calculateGlobalAverage()
	{
		double mean = 0;
		int count = 0;
		for( int i = 0 ; i < this.ratingMatrix.size() ; i++ )
		{
			for( Map.Entry<Integer, Double> entry : this.ratingMatrix.get(i).entrySet() )
			{
				double value = entry.getValue();
				mean += value;
				count++;
			}
		}
		
		if( count == 0 )
		{
			return Double.NaN;
		}else{
			this.averageRating = mean/count;
			return mean/count;
		}
	}
	
	/**
	 * This function returns the total number of ratings
	 * @return: total number of ratings
	 * */
	public int getTotalRatingNumber()
	{
		int number = 0;
		for( int i = 0 ; i < this.ratingMatrix.size() ; i++ )
		{
			number += this.ratingMatrix.get(i).size();
		}
		
		return number;
	}
	
	/**
	 * This function returns all entries with non-NaN value
	 * @return: a list of entries with valid value
	 * */
	public ArrayList<MatrixEntry2D> getValidEntries()
	{
		ArrayList<MatrixEntry2D> entries = new ArrayList<MatrixEntry2D>();
		for( int i = 0 ; i < this.row ; i++ )
		{
			for( Map.Entry<Integer, Double> entry : this.ratingMatrix.get(i).entrySet() )
			{
				MatrixEntry2D element = new MatrixEntry2D(i , entry.getKey() , entry.getValue());
				entries.add(element);
			}
		}
		
		return entries;
	}
	

	@Override
	public Double get(int x, int y) {
		// TODO Auto-generated method stub
		
		Double value =  this.ratingMatrix.get(x).get(y);
		if( value == null )
		{
			return Double.NaN;
		}else{
			return value.doubleValue();
		}
	}

	@Override
	public void set(int x, int y, Double value) {
		// TODO Auto-generated method stub
		
		this.ratingMatrix.get(x).put(y, value);
	}

	@Override
	public int numberOfRows() {
		// TODO Auto-generated method stub
		return this.row;
	}

	@Override
	public int numberOfColumns() {
		// TODO Auto-generated method stub
		return this.column;
	}

	@Override
	public IMatrix<Double> createMatrix(int num_rows, int num_columns) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the ratingMatrix
	 */
	public ArrayList<HashMap<Integer , Double>> getRatingMatrix() {
		return ratingMatrix;
	}

	/**
	 * @param ratingMatrix the ratingMatrix to set
	 */
	public void setRatingMatrix(ArrayList<HashMap<Integer , Double>> ratingMatrix) {
		this.ratingMatrix = ratingMatrix;
	}

	/**
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @param row the row to set
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * @return the column
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * @param column the column to set
	 */
	public void setColumn(int column) {
		this.column = column;
	}

	/**
	 * @return the averageRating
	 */
	public double getAverageRating() {
		return averageRating;
	}

	/**
	 * @param averageRating the averageRating to set
	 */
	public void setAverageRating(double averageRating) {
		this.averageRating = averageRating;
	}

	/**
	 * @return the usersMean
	 */
	public ArrayList<Double> getUsersMean() {
		return usersMean;
	}

	/**
	 * @param usersMean the usersMean to set
	 */
	public void setUsersMean(ArrayList<Double> usersMean) {
		this.usersMean = usersMean;
	}

	/**
	 * @return the itemsMean
	 */
	public ArrayList<Double> getItemsMean() {
		return itemsMean;
	}

	/**
	 * @param itemsMean the itemsMean to set
	 */
	public void setItemsMean(ArrayList<Double> itemsMean) {
		this.itemsMean = itemsMean;
	}

	/**
	 * @return the ratedItems
	 */
	public ArrayList<ArrayList<Integer>> getRatedItems() {
		return ratedItems;
	}

	/**
	 * @param ratedItems the ratedItems to set
	 */
	public void setRatedItems(ArrayList<ArrayList<Integer>> ratedItems) {
		this.ratedItems = ratedItems;
	}
	
	
}
