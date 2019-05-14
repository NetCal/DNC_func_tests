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
 * Foundation, Inc., 51 Franklin Street, test_network.fifth Floor, Boston, MA 02110-1301 USA.
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
import org.networkcalculus.num.implementations.RealDoublePrecision;
import org.networkcalculus.num.implementations.RealSinglePrecision;

public class S_1SC_2F_2AC_Results extends DncTestResults {

	protected S_1SC_2F_2AC_Results() {
	}
	
	protected void initialize() {
		super.clear();

		Num num_factory = Num.getFactory(Calculator.getInstance().getNumBackend());
		
		RealDoublePrecision real_double_epsilon;
		RealSinglePrecision real_single_epsilon;
		RationalBigInt rational_bigint_epsilon = new RationalBigInt(1, 1000000000);
		
		for( Set<ArrivalBoundMethod> ab_set : DncTestMethodSources.ab_sets ) {
			// --------------------------------------------------------------------------------------------------------------
		    // TFA
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(135), num_factory.create(125));
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(13.5), num_factory.create(125));
			
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(135), num_factory.create(125));
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(13.5), num_factory.create(125));

			// --------------------------------------------------------------------------------------------------------------
		    // SFA
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(27), num_factory.create(110));
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(29, 2), num_factory.create(60));
			
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(22.5), num_factory.create(350, 3));
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(91, 6), num_factory.create(80));

			/*
			 * Observed test failures:
			 * 
			 * Real Double: Epsilon set to ignore
			 * 		SFA backlog ==> expected <116.66666666666667> but was <116.66666666666666>
			 * 
			 * Real Single: Epsilon set to ignore
			 * 		SFA backlog ==> expected <116.666664> but was <116.66667>
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		SFA backlog ==> expected <350 / 3> but was <4104843410363733 / 35184372088832>
			 */
			real_double_epsilon = new RealDoublePrecision(Double.parseDouble("1e-13"));
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_single_epsilon = new RealSinglePrecision(Float.parseFloat("6e-5"));
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			// --------------------------------------------------------------------------------------------------------------
		    // PMOO
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(27), num_factory.create(110));
			addBounds(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(22.5), num_factory.create(350, 3));

			/*
			 * Observed test failures:
			 * 
			 * Real Double: Epsilon set to ignore
			 * 		PMOO backlog ==> expected <22.5> but was <22.500000000000004>
			 * 		PMOO backlog ==> expected <116.66666666666667> but was <116.66666666666669>
			 * 
			 * Real Single: Epsilon set to ignore
			 * 		PMOO delay ==> expected <22.5> but was <22.499998>
			 * 		PMOO backlog ==> expected <116.66666412353516> but was <116.66665649414062>
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		PMOO backlog ==> expected <350 / 3> but was <4104843410363733 / 35184372088832>
			 */
			real_double_epsilon = new RealDoublePrecision(Double.parseDouble("2e-14"));
			addEpsilon(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_single_epsilon = new RealSinglePrecision(Float.parseFloat("8e-5"));
			addEpsilon(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			addEpsilon(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
		}

		// --------------------------------------------------------------------------------------------------------------
	    // Sink tree
	    // --------------------------------------------------------------------------------------------------------------
		addBounds(0, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(125));
		addBounds(1, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(125));
	}
}