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
 * Abstract class for dataset. All advanced dataset class must extend this abstract class.
 * 
 * @author Xin Liu
 * */

package ch.epfl.lsir.xin.model;

import java.util.List;

public abstract class AbstractDataSet {

	/**
	 * list of ratings
	 * */
	private List ratings = null;
	
	/**
	 * constructor
	 * */
	public AbstractDataSet( List ratings )
	{
		this.ratings = ratings;
	}
	
	/**
	 * this function builds the mapping between ID and index (for user and item)  
	 * */
	public abstract void createUserItemIDMapping();

	/**
	 * @return the ratings
	 */
	public List getRatings() {
		return ratings;
	}

	/**
	 * @param ratings the ratings to set
	 */
	public void setRatings(List ratings) {
		this.ratings = ratings;
	}
	
	
}
