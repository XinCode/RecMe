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
 * This class implements various similarity calculation methods
 * 
 * @author Xin Liu
 * */

package ch.epfl.lsir.xin.util;

import java.util.List;

public class SimilarityCalculator {

	/**
	 * This function calculates the similarity between two given vectors using Pearson Correlation Coefficient
	 * @param: input vector 1
	 * @param: input vector 2
	 * @return: The calculated PCC value (-1 to 1)
	 * */
	public static double getSimilarityPCC( List<Double> vector1 , List<Double> vector2 , int shrinkage )
	{
		if( vector1 == null || vector2 == null || vector1.size() < 2 || 
				vector2.size() < 2 || vector1.size() != vector2.size() )
		{
			return 0;
		}
		
		double mean1 = 0;
		double mean2 = 0;
		for( int i = 0 ; i < vector1.size() ; i++ )
		{
			mean1 = mean1 + vector1.get(i);
			mean2 = mean2 + vector2.get(i);
		}
		mean1 = mean1/vector1.size();
		mean2 = mean2/vector2.size();
		
		double covariance = 0;
		double variance1 = 0;
		double variance2 = 0;
		for( int i = 0 ; i < vector1.size() ; i++ )
		{
			covariance = covariance + (vector1.get(i) - mean1) * (vector2.get(i) - mean2);
			variance1 = variance1 + Math.pow((vector1.get(i) - mean1), 2);
			variance2 = variance2 + Math.pow((vector2.get(i) - mean2), 2);
		}

		if( variance1 == 0 || variance2 == 0 )
		{
			return 0;
		}
		
		double pcc = covariance/(Math.sqrt(variance1) * Math.sqrt(variance2));
		double shrinkedPCC = (double)vector1.size()/(vector1.size() + shrinkage) * pcc;
		return shrinkedPCC;
	}
	
	/**
	 * This function calculates the cosine similarity between two given vectors
	 * @param: input vector 1
	 * @param: input vector 2
	 * @return: The calculated cosine similarity.
	 * */
	public static double getSimilarityCosine( List<Double> vector1 , List<Double> vector2 , int shrinkage )
	{
		if( vector1 == null || vector2 == null || vector1.size() < 2 || 
				vector2.size() < 2 || vector1.size() != vector2.size() )
		{
			return 0;
		}
		
		double vv = 0;
		double v1 = 0;
		double v2 = 0;
		for( int i = 0 ; i < vector1.size() ; i++ )
		{
			vv = vv + vector1.get(i) * vector2.get(i);
			v1 = v1 + vector1.get(i) * vector1.get(i);
			v2 = v2 + vector2.get(i) * vector2.get(i);
		}
		
		if( v1 == 0 || v2 == 0 )
		{
			return 0;
		}
		
		double cosine = vv / (Math.sqrt(v1) * Math.sqrt(v2));
		double shrinkedCosine = (double)vector1.size()/(vector1.size() + shrinkage) * cosine;
		return shrinkedCosine;		
	}
}
