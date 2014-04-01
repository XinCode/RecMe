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
 * Abstract class of an item. All advanced item types will extends this abstract class.
 * 
 * @author Xin Liu
 * */

package ch.epfl.lsir.xin.model;

public abstract class AbstractItem {

	/**
	 * ID (string) of this item
	 * */
	private String ID = null;
	
	/**
	 * Index of this item in the item list
	 * */
	private int index = -1;

	/**
	 * constructor:
	 * @param: ID of this item
	 * */
	protected AbstractItem( String itemID )
	{
		this.ID = itemID;
	}
	
	/**
	 * constructor:
	 * @param: ID of this item
	 * @param: index of this item in the item list
	 * */
	protected AbstractItem( String itemID , int index )
	{
		this.ID = itemID;
		this.index = index;
	}
	
	/**
	 * @return the iD
	 */
	public String getID() {
		return ID;
	}

	/**
	 * @param iD the iD to set
	 */
	public void setID(String iD) {
		ID = iD;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
}
