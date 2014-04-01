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
 * Abstract class of a rating. All advanced rating types will extends this abstract class.
 * 
 * @author Xin Liu
 * */

package ch.epfl.lsir.xin.model;

public abstract class AbstractRating {

	/**
	 * ID of the user who assigned this rating
	 * */
	private String userID = null;
	
	/**
	 * ID of the item to be assigned
	 * */
	private String itemID = null;
	
	/**
	 * constructor:
	 * @param: ID of this user
	 * @param: ID of this item
	 * */
	protected AbstractRating( String userID , String itemID )
	{
		this.userID = userID;
		this.itemID = itemID;
	}
	
	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}

	/**
	 * @return the itemID
	 */
	public String getItemID() {
		return itemID;
	}

	/**
	 * @param itemID the itemID to set
	 */
	public void setItemID(String itemID) {
		this.itemID = itemID;
	}
	
}
