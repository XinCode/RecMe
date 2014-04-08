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
 *This is the interface for matrix which will be implemented by other advanced matrices
 *@author Xin Liu
 * */
package ch.epfl.lsir.xin.datatype;


public interface IMatrix<T> {

	/** 
	   * Get the value at (i,j)
	   * @param x the row ID
	   * @param y the column ID
	   * @return the value at (i,j)
	   */
	  T get(int x, int y);

	  /** 
	   * Set the value at (i,j)
	   * @param x the row ID
	   * @param y the column ID
	   * @param value the value
	   */
	  void set(int x, int y, T value);
	  
	  /** 
	   * Get the number of rows of the matrix.
	   * @return the number of rows of the matrix
	   */
	  int numberOfRows();

	  /** 
	   * Get the number of columns of the matrix.
	   * @return rhe number of columns of the matrix
	   */
	  int numberOfColumns();

	  /**
	   * Create a matrix with a given number of rows and columns.
	   * @param num_rows the number of rows
	   * @param num_columns the number of columns
	   * @return a matrix with num_rows rows and num_column columns
	   */
	  IMatrix<T> createMatrix(int num_rows, int num_columns);
}
