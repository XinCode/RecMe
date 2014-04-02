RecMe is a lightweight recommendation algorithm library developed using Java 1.7 + Eclipse.

The purpose of this project is to provide implementation of the state-of-the-art recommendation algorithms, mainly for self use (e.g., performance comparison with my proposed methods). Kindly send me a message if you want some recommendation algorithms to be included in the library.

Current implementations:

Baseline: GlobalAverage, UserAverage, ItemAverage, MostPopular
Core: ItemCF, UserCF, PMF, SVD++, SocialReg (MF + social regularization)

Structure of the project:

(1) conf folder: configuration of each algorithm's parameters
(2) data folder: input dataset (e.g., MovieLens-100k)
(3) lib folder: third party libs
(4) localModels folder: saved model (e.g., learned user/item's latent factors)
(5) results folder: results of each algorithm + log information
(6) src folder: source code

Usage:

Check the package "ch.epfl.lsir.xin.test" where we show how each algorithm is used to provide recommendation.

The correctness of the implemented algorithms is (in part) validated by comparing with MyMediaLite (http://mymedialite.net/) using MovieLens100k dataset. Note that rating prediction accuracy of algorithms has been thoroughly tested but the ranking performance is not fully studied yet (an important follow-up task ;).


GPL License

Copyright (C) 2014  Xin Liu

RecMe: a lightweight recommendation algorithm library

RecMe is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
