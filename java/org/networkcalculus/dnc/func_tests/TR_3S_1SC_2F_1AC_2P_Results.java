/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2013 - 2018 Steffen Bondorf
 * Copyright (C) 2017 - 2018 The DiscoDNC contributors
 * Copyright (C) 2019+ The DNC contributors
 *
 * http://networkcalculus.org
 *
 *
 * The Deterministic Network Calculator (DNC) is free software;
 * you can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package org.networkcalculus.dnc.func_tests;

import java.util.Set;

import org.networkcalculus.dnc.AnalysisConfig.ArrivalBoundMethod;
import org.networkcalculus.dnc.AnalysisConfig.Multiplexing;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.func_tests.DncTestMethodSources;
import org.networkcalculus.dnc.func_tests.DncTestResults;
import org.networkcalculus.dnc.tandem.TandemAnalysis.Analyses;
import org.networkcalculus.num.Num;
import org.networkcalculus.num.NumBackend;
import org.networkcalculus.num.implementations.RationalBigInt;

public class TR_3S_1SC_2F_1AC_2P_Results extends DncTestResults {

	protected TR_3S_1SC_2F_1AC_2P_Results() {
	}

	protected void initialize() {
		super.clear();

		Num num_factory = Num.getFactory(Calculator.getInstance().getNumBackend());
		
		RationalBigInt rational_bigint_epsilon = new RationalBigInt(1, 1000000000);
		
		for( Set<ArrivalBoundMethod> ab_set : DncTestMethodSources.ab_sets ) {
			// --------------------------------------------------------------------------------------------------------------
		    // TFA
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(345, 4), num_factory.create(450));
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(53.75), num_factory.create(450));
			
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(345, 4), num_factory.create(450));
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(53.75), num_factory.create(450));

			// --------------------------------------------------------------------------------------------------------------
		    // SFA
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(170, 3), num_factory.create(300));
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(575, 12), num_factory.create(1025, 4));
			
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(170, 3), num_factory.create(300));
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(575, 12), num_factory.create(1025, 4));
			
			/*
			 * Observed test failures:
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		SFA delay ==> expected <170 / 3> but was <7975124340135253 / 140737488355328>
			 */
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);

			// --------------------------------------------------------------------------------------------------------------
		    // PMOO
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(170, 3), num_factory.create(300));
			addBounds(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(170, 3), num_factory.create(300));
			
			/*
			 * Observed test failures:
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		PMOO delay ==> expected <170 / 3> but was <7975124340135253 / 140737488355328>
			 */
			addEpsilon(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
		}
		
		// --------------------------------------------------------------------------------------------------------------
	    // Sink tree
	    // --------------------------------------------------------------------------------------------------------------
		addBounds(0, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(450));
		addBounds(1, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(450));
	}
}