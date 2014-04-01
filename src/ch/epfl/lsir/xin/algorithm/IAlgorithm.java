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


package ch.epfl.lsir.xin.algorithm;

public interface IAlgorithm {

	/**
	 * This function saves the model
	 * @param: the file where the model is saved
	 * */
	public void saveModel( String file );
	
	
	/**
	 * This function reads the model from a local file
	 * @param: the file there the model is read from
	 * */
	public void readModel( String file );
	
	/**
	 * This function builds the model
	 * */
	public void build();
	
}
