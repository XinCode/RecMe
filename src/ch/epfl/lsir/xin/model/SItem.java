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
 * This class implements a abstract item recording the similarity information with another item.
 * This class is typically used in memory based collaborative filtering where items' similarity
 * are explicitly measured. 
 * 
 * @author Xin Liu
 * */

package ch.epfl.lsir.xin.model;


public class SItem implements Comparable
{
	private int itemIndex = -1;
	
	private double rating = -1;
	
	private double similarity = -1;
	
	public SItem( int index , double rating , double similarity )
	{
		this.itemIndex = index;
		this.rating = rating;
		this.similarity = similarity;
	}


	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		SItem item = (SItem)arg0;
		if( this.similarity > item.similarity )
		{
			return 1;
		}else if( this.similarity < item.similarity )
		{
			return -1;
		}else{
			return 0;
		}
	}


	/**
	 * @return the itemIndex
	 */
	public int getItemIndex() {
		return itemIndex;
	}


	/**
	 * @param itemIndex the itemIndex to set
	 */
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}


	/**
	 * @return the rating
	 */
	public double getRating() {
		return rating;
	}


	/**
	 * @param rating the rating to set
	 */
	public void setRating(double rating) {
		this.rating = rating;
	}


	/**
	 * @return the similarity
	 */
	public double getSimilarity() {
		return similarity;
	}


	/**
	 * @param similarity the similarity to set
	 */
	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
	
	
}
