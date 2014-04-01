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
 * This class implements a result unit, which records the user id, item id and the prediction
 * 
 * @author Xin Liu
 * */

package ch.epfl.lsir.xin.evaluation;

public class ResultUnit implements Comparable {

	/**
	 * id of the user
	 * */
	private String userID = null;
	
	/**
	 * index of the user
	 * */
	private int userIndex = -1;
	
	/**
	 * id of the item
	 * */
	private String itemID = null;
	
	/**
	 * index of the item
	 * */
	private int itemIndex = -1;
	
	/**
	 * predicted value
	 * */
	private double prediciton = Double.NaN;
	
	/**
	 * constructor
	 * @param: id of the user
	 * @param: id of the item
	 * @param: prediction value 
	 * */
	public ResultUnit( String userID , String itemID , double prediction )
	{
		this.userID = userID;
		this.itemID = itemID;
		this.prediciton = prediction;
	}
	
	/**
	 * constructor
	 * @param: id of the user
	 * @param: id of the item
	 * @param: prediction value 
	 * */
	public ResultUnit( int userIndex , int itemIndex , double prediction )
	{
		this.userIndex = userIndex;
		this.itemIndex = itemIndex;
		this.prediciton = prediction;
	}

	@Override
	public int compareTo(Object unit) {
		// TODO Auto-generated method stub
		ResultUnit u = (ResultUnit)unit;
		if( this.prediciton > u.prediciton )
		{
			return 1;
		}else if( this.prediciton < u.prediciton )
		{
			return -1;
		}else
		{
			return 0;
		}
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

	/**
	 * @return the prediciton
	 */
	public double getPrediciton() {
		return prediciton;
	}

	/**
	 * @param prediciton the prediciton to set
	 */
	public void setPrediciton(double prediciton) {
		this.prediciton = prediciton;
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
	
	
}
