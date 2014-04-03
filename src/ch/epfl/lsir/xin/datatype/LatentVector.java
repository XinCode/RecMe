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
 * This class implements the vector of latent factors for user and item
 * 
 * @author Xin Liu
 * */

package ch.epfl.lsir.xin.datatype;

public class LatentVector {

	/**
	 * id
	 * */
	private String ID = null;
	
	/**
	 * index
	 * */
	private int index = -1;
	
	/**
	 * latent factor vector
	 * */
	private double[] factors = null;
	
	
	/**
	 * constructor
	 * @param: id of user or item
	 * @param: index of user or item
	 * @param: latent factors
	 * */
	public LatentVector( String id , int index , double[] factors )
	{
		this.ID = id;
		this.index = index;
		this.factors = factors;
	}


	public String getID() {
		return ID;
	}


	public void setID(String iD) {
		ID = iD;
	}


	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}


	public double[] getFactors() {
		return factors;
	}


	public void setFactors(double[] factors) {
		this.factors = factors;
	}
	
	
}
