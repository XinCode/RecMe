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
 * This class implements a abstract user recording the similarity information with an other user.
 * This class is typically used in memory based collaborative filtering where users' similarity
 * are explicitly measured. 
 * 
 * @author Xin Liu
 * */

package ch.epfl.lsir.xin.model;

public class SUser implements Comparable
{
	private int userIndex = -1;
	
	private double rating = -1;
	
	private double similarity = -1;
	
	public SUser( int index , double rating , double similarity )
	{
		this.userIndex = index;
		this.rating = rating;
		this.similarity = similarity;
	}


	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		SUser user = (SUser)arg0;
		if( this.similarity > user.similarity )
		{
			return 1;
		}else if( this.similarity < user.similarity )
		{
			return -1;
		}else{
			return 0;
		}
	}


	/**
	 * @return the userIndex
	 */
	public int getUserIndex() {
		return userIndex;
	}


	/**
	 * @param userIndex the userIndex to set
	 */
	public void setUserIndex(int userIndex) {
		this.userIndex = userIndex;
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
