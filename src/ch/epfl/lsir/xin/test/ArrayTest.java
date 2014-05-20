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

package ch.epfl.lsir.xin.test;

import java.util.HashMap;

import Jama.CholeskyDecomposition;
import Jama.Matrix;

public class ArrayTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		System.out.println(Integer.MAX_VALUE - 5);
//		double[][] a = new double[401][40163];
//		for( int i = 0 ; i < a.length ; i++ )
//		{
//			for( int j = 0 ; j < a[i].length ; j++ )
//			{
//				a[i][j] = 100;
//			}
//		}
		
//		HashMap<Integer , Double> map = new HashMap<Integer , Double>();
//		map.put(1, 4.3);
//		map.put(2, 2.2);
//		Double v = map.get(2);
//		System.out.println(v.doubleValue());
//		System.out.println(map.get(4) == null);
//		System.out.println(map.size());
		
		Matrix matrix = new Matrix(4,4);
		matrix.set(0, 0, 1); matrix.set(0, 1, 2); matrix.set(0, 2, 3); matrix.set(0, 3, 4);
		matrix.set(1, 0, 2); matrix.set(1, 1, 5); matrix.set(1, 2, 9);matrix.set(1, 3, 10);
		matrix.set(2, 0, 3); matrix.set(2, 1, 9); matrix.set(2, 2, 22);matrix.set(2, 3, 20);
		matrix.set(3, 0, 4); matrix.set(3, 1, 10); matrix.set(3, 2, 20);matrix.set(3, 3, 37);
		CholeskyDecomposition cd = matrix.chol();
		if( !cd.isSPD() )
		{
			throw new RuntimeException("The covariance Matrix is not SDP...");
		}else
		{
			Matrix L = cd.getL().transpose();
			Matrix invL = L.inverse();
//			Matrix inv = invL.times(invL.transpose());
			Matrix inv = invL.times(L.transpose().inverse());
//			Matrix inv = L.transpose().inverse().times(L.inverse());
			for( int i = 0 ; i < inv.getRowDimension() ; i++ )
			{
				for( int j = 0 ; j < inv.getColumnDimension() ; j++ )
				{
					System.out.print(inv.get(i, j) + "  ");
				}
				System.out.println();
			}
			
			Matrix A = inv.times(matrix);
//			Matrix A = matrix.times(inv);
			for( int i = 0 ; i < inv.getRowDimension() ; i++ )
			{
				for( int j = 0 ; j < inv.getColumnDimension() ; j++ )
				{
					System.out.print(A.get(i, j) + "  ");
				}
				System.out.println();
			}
		}
		
	}

}
