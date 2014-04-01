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
 * This class implements the entry of a 2D matrix
 * 
 * @author Xin Liu
 */


package ch.epfl.lsir.xin.datatype;

public class MatrixEntry2D {

	/**
	 * row index of this entry
	 * */
	private int rowIndex = -1;
	
	/**
	 * column index of this entry
	 * */
	private int columnIndex = -1;
	
	/**
	 * value of this entry
	 * */
	private double value = Double.NaN;
	
	/**
	 * constructor
	 * @param: row index of this entry
	 * @param: column index of this entry
	 * @param: value of this entry
	 * */
	public MatrixEntry2D( int r , int c , double v )
	{
		this.rowIndex = r;
		this.columnIndex = c;
		this.value = v;
	}

	/**
	 * @return the rowIndex
	 */
	public int getRowIndex() {
		return rowIndex;
	}

	/**
	 * @param rowIndex the rowIndex to set
	 */
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	/**
	 * @return the columnIndex
	 */
	public int getColumnIndex() {
		return columnIndex;
	}

	/**
	 * @param columnIndex the columnIndex to set
	 */
	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
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
