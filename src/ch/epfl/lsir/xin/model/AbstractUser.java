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
 * Abstract class of a user. All advanced user types will extends this abstract class.
 * 
 * @author Xin Liu
 * */

package ch.epfl.lsir.xin.model;

public abstract class AbstractUser {

	/**
	 * ID (string) of this user
	 * */
	private String ID = null;
	
	/**
	 * Index of this user in the user list
	 * */
	private int index = -1;

	/**
	 * constructor
	 * @param: id of the user
	 * */
	protected AbstractUser( String userID )
	{
		this.ID = userID;
	}
	
	/**
	 * constructor
	 * @param: id of the user
	 * @param: index of this user int he user list
	 * */
	protected AbstractUser( String userID , int userIndex )
	{
		this.ID = userID;
		this.index = userIndex;
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
