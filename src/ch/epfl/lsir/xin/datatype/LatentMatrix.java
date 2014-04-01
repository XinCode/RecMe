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
 * This class implements low rank matrix for users and item
 * 
 * @author Xin Liu
 * */

package ch.epfl.lsir.xin.datatype;

import java.util.Random;

public class LatentMatrix implements IMatrix<Double> {

	/**
	 * number of users or items
	 * */
	private int rows = -1;
	
	/**
	 * number of latent factors
	 * */
	private int columns = -1;
	
	/**
	 * content of this matrix
	 * */
	private double[][] values = null;
	
	/**
	 * indicator for initialization of latent factor
	 * */
	private int initialization = -1;
	
	/**
	 * constructor
	 * @param: row of the matrix
	 * @param: column of the matrix
	 * @param: indicator for how latent factors are initialized 
	 * */
	public LatentMatrix( int rows , int columns , int indicator )
	{
		this.rows = rows;
		this.columns = columns;
		this.initialization = indicator;
		this.values = new double[this.rows][this.columns];
	}
	
	/* constructor
	 * @param: row of the matrix
	 * @param: column of the matrix
	 * */
	public LatentMatrix( int rows , int columns )
	{
		this.rows = rows;
		this.columns = columns;
		this.values = new double[this.rows][this.columns];
	}
	
	/**
	 * this function initializes the latnet factors
	 * */
	public void valueInitialization()
	{
		//0 for all NaN; 1 for all 0; 2 for random[0,1]
		if( this.initialization == 0 )
		{
			for( int i = 0 ; i < this.rows ; i++ )
			{
				for( int j = 0 ; j < this.columns ; j++ )
				{
					this.values[i][j] = Double.NaN;
				}
			}
		}else if( this.initialization == 1 )
		{
			for( int i = 0 ; i < this.rows ; i++ )
			{
				for( int j = 0 ; j < this.columns ; j++ )
				{
					this.values[i][j] = 0;
				}
			}
		}else if( this.initialization == 2 )
		{
			for( int i = 0 ; i < this.rows ; i++ )
			{
				for( int j = 0 ; j < this.columns ; j++ )
				{
					this.values[i][j] = new Random().nextDouble();
				}
			}
		}else{
			System.out.println("Error in latent factor initialization indicator setting.");
		}
	}
	
	/**
	 * This function sets a specific row
	 * @param: index of the row to be set
	 * @param: the new values of the row
	 * */
	public void setRowValue( int rowIndex , double[] row )
	{
		this.values[rowIndex] = row;
	}
	
	/**
	 * This function clones and returns the matrix itself
	 * @return: the cloned matrix 
	 * */
	public LatentMatrix clone()
	{
		LatentMatrix matrix = new LatentMatrix( this.rows , this.columns );
		for( int i = 0 ; i < this.rows ; i++ )
		{
			for( int j = 0 ; j < this.columns ; j++ )
			{
				matrix.set(i, j, this.get(i, j));
			}
		}
		matrix.setInitialization(this.initialization);
		return matrix;
	}
	
	/**
	 * This function returns the vector of the specific row
	 * @param: index of the row
	 * @return: returned vector of the specific row
	 * */
	public double[] getRowValues( int index )
	{
//		System.out.println(values[index].length + " ,,, ");
		return this.values[index];
	}
	
	
	@Override
	public Double get(int x, int y) {
		// TODO Auto-generated method stub
		return this.values[x][y];
	}

	@Override
	public void set(int x, int y, Double value) {
		// TODO Auto-generated method stub
		this.values[x][y] = value;
	}

	@Override
	public int numberOfRows() {
		// TODO Auto-generated method stub
		return this.rows;
	}

	@Override
	public int numberOfColumns() {
		// TODO Auto-generated method stub
		return this.columns;
	}

	@Override
	public IMatrix<Double> createMatrix(int num_rows, int num_columns) {
		// TODO Auto-generated method stub
		return new LatentMatrix( num_rows , num_columns );
	}

	/**
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @param rows the rows to set
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * @return the columns
	 */
	public int getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(int columns) {
		this.columns = columns;
	}

	/**
	 * @return the values
	 */
	public double[][] getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(double[][] values) {
		this.values = values;
	}

	/**
	 * @return the initialization
	 */
	public int getInitialization() {
		return initialization;
	}

	/**
	 * @param initialization the initialization to set
	 */
	public void setInitialization(int initialization) {
		this.initialization = initialization;
	}

	
}
