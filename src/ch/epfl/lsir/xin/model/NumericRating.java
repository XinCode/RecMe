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
 * This class extends the abstract rating to implement a rating with numeric value 
 * (typically used in the application domain where the rating is in x-point scale)
 * 
 * @author Xin Liu
 * */
package ch.epfl.lsir.xin.model;

public class NumericRating extends AbstractRating {

	/**
	 * Value of this rating
	 * */
	private double value = -1;
	
	/**
	 * Constructor:
	 * @param: ID of the user
	 * @param: ID of the item
	 * @param: numeric value of this rating
	 * */
	public NumericRating( String userID , String itemID , double value )
	{
		super( userID , itemID );
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}
	
	
}
