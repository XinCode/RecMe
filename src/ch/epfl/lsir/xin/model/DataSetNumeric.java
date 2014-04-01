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
 * Dataset where the ratings are numeric.
 * 
 * @author Xin Liu
 * */

package ch.epfl.lsir.xin.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.epfl.lsir.xin.datatype.RatingMatrix;

public class DataSetNumeric extends AbstractDataSet {

	/**
	 * ratings
	 * */
	private ArrayList<NumericRating> ratings = null;
	
	/**
	 * list of user id 
	 * */
	private List<String> userIDs = null;
	
	/**
	 * list of item id
	 * */
	private List<String> itemIDs = null;
	
	/**
	 * user ID and index mapping
	 * */
	private HashMap<String , Integer> userIDMapping = null;
	
	/**
	 * item ID and index mapping
	 * */
	private HashMap<String , Integer> itemIDMapping = null;
	
	/**
	 * trust/social relationship matrix
	 * rating here indicates relationship
	 * */
	private RatingMatrix relationships = null;
	
	/**
	 * maximum value of the rating
	 * */
	private double maxRating = Double.NaN;
	
	/**
	 * minimum value of the rating
	 * */
	private double minRating = Double.NaN;
	
	/**
	 * constructor
	 * @param: ratings of this dataset
	 * */
	public DataSetNumeric( ArrayList<NumericRating> ratings )
	{
		super( ratings );
		this.ratings = ratings;
		this.userIDs = new ArrayList<String>();
		this.itemIDs = new ArrayList<String>();
		this.userIDMapping = new HashMap<String , Integer>();
		this.itemIDMapping = new HashMap<String , Integer>();
	}

	@Override
	public void createUserItemIDMapping() {
		// TODO Auto-generated method stub
		int userIndex = 0;
		int itemIndex = 0;
		for( int i = 0 ; i < this.ratings.size() ; i++ )
		{
			String userId = this.ratings.get(i).getUserID();
			String itemId = this.ratings.get(i).getItemID();
			//process user ids
			if( !this.userIDMapping.containsKey(userId) )
			{
				this.userIDMapping.put(userId, userIndex);
				userIndex++;
			}
			//process item ids
			if( !this.itemIDMapping.containsKey(itemId) )
			{
				this.itemIDMapping.put(itemId, itemIndex);
				itemIndex++;
			}
		}
		for( String entry : this.userIDMapping.keySet() )
		{
			this.userIDs.add(entry);
		}
		for( String entry : this.itemIDMapping.keySet() )
		{
			this.itemIDs.add(entry);
		}
	}
	

	/**
	 * @return the ratings
	 */
	public List<NumericRating> getRatings() {
		return ratings;
	}

	/**
	 * @param ratings the ratings to set
	 */
	public void setRatings(ArrayList<NumericRating> ratings) {
		this.ratings = ratings;
	}

	/**
	 * @return the userIDs
	 */
	public List<String> getUserIDs() {
		return userIDs;
	}

	/**
	 * @param userIDs the userIDs to set
	 */
	public void setUserIDs(List<String> userIDs) {
		this.userIDs = userIDs;
	}

	/**
	 * @return the itemIDs
	 */
	public List<String> getItemIDs() {
		return itemIDs;
	}

	/**
	 * @param itemIDs the itemIDs to set
	 */
	public void setItemIDs(List<String> itemIDs) {
		this.itemIDs = itemIDs;
	}

	/**
	 * @return the userIDMapping
	 */
	public HashMap<String, Integer> getUserIDMapping() {
		return userIDMapping;
	}

	/**
	 * @param userIDMapping the userIDMapping to set
	 */
	public void setUserIDMapping(HashMap<String, Integer> userIDMapping) {
		this.userIDMapping = userIDMapping;
	}

	/**
	 * @return the itemIDMapping
	 */
	public HashMap<String, Integer> getItemIDMapping() {
		return itemIDMapping;
	}

	/**
	 * @param itemIDMapping the itemIDMapping to set
	 */
	public void setItemIDMapping(HashMap<String, Integer> itemIDMapping) {
		this.itemIDMapping = itemIDMapping;
	}

	/**
	 * @return the maxRating
	 */
	public double getMaxRating() {
		return maxRating;
	}

	/**
	 * @param maxRating the maxRating to set
	 */
	public void setMaxRating(double maxRating) {
		this.maxRating = maxRating;
	}

	/**
	 * @return the minRating
	 */
	public double getMinRating() {
		return minRating;
	}

	/**
	 * @param minRating the minRating to set
	 */
	public void setMinRating(double minRating) {
		this.minRating = minRating;
	}

	/**
	 * @return the relationships
	 */
	public RatingMatrix getRelationships() {
		return relationships;
	}

	/**
	 * @param relationships the relationships to set
	 */
	public void setRelationships(RatingMatrix relationships) {
		this.relationships = relationships;
	}
	
	
}
