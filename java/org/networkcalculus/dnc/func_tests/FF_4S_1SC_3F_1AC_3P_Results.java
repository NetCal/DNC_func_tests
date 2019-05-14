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

public class FF_4S_1SC_3F_1AC_3P_Results extends DncTestResults {

	protected FF_4S_1SC_3F_1AC_3P_Results() {
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
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(6425, 36), num_factory.create(6125, 9));
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(5985, 64), num_factory.create(9425, 16));
			
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(11975, 90), num_factory.create(6125, 9));
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(3965, 64), num_factory.create(9425, 16));
			
			addBounds(2, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(685, 6), num_factory.create(1475, 3));
			addBounds(2, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(885, 16), num_factory.create(1825, 4));
			
			/*
			 * Observed test failures:
			 * 
			 * Real Double: Epsilon set to ignore
			 * 		TFA delay ==> expected <114.16666666666667> but was <114.16666666666666>
			 * 		TFA backlog ==> expected <680.5555555555555> but was <680.5555555555557>
			 * 		TFA delay ==> expected <133.05555555555554> but was <133.05555555555557>
			 * 
			 * Real Single: Epsilon set to ignore
			 * 		TFA delay ==> expected <114.166664> but was <114.16667>
			 * 		TFA delay ==> expected <114.16666412353516> but was <114.16667175292969>
			 * 		TFA backlog ==> expected <491.6666564941406> but was <491.66668701171875>
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		TFA delay ==> expected <6425 / 36> but was <31397165370936889 / 175921860444160>
			 * 		TFA delay ==> expected <6425 / 36> but was <3924645671367111 / 21990232555520>
			 * 		TFA delay ==> expected <2395 / 18> but was <11703690437882311 / 87960930222080>
			 * 		TFA delay ==> expected <685 / 6> but was <10042206200354133 / 87960930222080>
			 */
			real_double_epsilon = new RealDoublePrecision(Double.parseDouble("6e-14"));
			addEpsilon(2, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(2, Analyses.TFA, ab_set, Multiplexing.FIFO, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_double_epsilon = new RealDoublePrecision(Double.parseDouble("2e-13"));
			addEpsilon(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(0, Analyses.TFA, ab_set, Multiplexing.FIFO, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);

			addEpsilon(1, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(1, Analyses.TFA, ab_set, Multiplexing.FIFO, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_single_epsilon = new RealSinglePrecision(Float.parseFloat("3.25e-5"));
			addEpsilon(2, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(2, Analyses.TFA, ab_set, Multiplexing.FIFO, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			addEpsilon(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(0, Analyses.TFA, ab_set, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			addEpsilon(1, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.TFA, ab_set, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			addEpsilon(2, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(2, Analyses.TFA, ab_set, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);

			// --------------------------------------------------------------------------------------------------------------
		    // SFA
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(875, 9), num_factory.create(4525, 9));
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(1795, 24), num_factory.create(3125, 8));
			
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(2095, 27), num_factory.create(10925, 27));
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(10715, 192), num_factory.create(18925, 64));
			
			addBounds(2, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(65), num_factory.create(1025, 3));
			addBounds(2, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(295, 6), num_factory.create(525, 2));

			/*
			 * Observed test failures:
			 * 
			 * Real Double: Epsilon set to ignore
			 * 		SFA delay ==> expected <341.6666666666667> but was <341.66666666666663>
			 * 
			 * Real Single: Epsilon set to ignore
			 * 		SFA delay ==> expected <77.59259> but was <77.5926>
			 * 		SFA delay ==> expected <341.66666> but was <341.6667>
			 * 		SFA backlog ==> expected <404.629638671875> but was <404.62957763671875>
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		SFA delay ==> expected <875 / 9> but was <25655271314773333 / 263882790666240>
			 * 		SFA delay ==> expected <875 / 9> but was <1710351420984889 / 17592186044416>
			 * 		SFA delay ==> expected <2095 / 27> but was <5118837467090489 / 65970697666560>
			 * 		SFA delay ==> expected <2095 / 27> but was <5460093298229855 / 70368744177664>
			 * 		SFA delay ==> expected <1025 / 3> but was <3005331782587733 / 8796093022208>
			 */
			real_double_epsilon = new RealDoublePrecision(Double.parseDouble("7e-14"));
			addEpsilon(2, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(2, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_single_epsilon = new RealSinglePrecision(Float.parseFloat("6.25e-5"));
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);
			
			real_single_epsilon = new RealSinglePrecision(Float.parseFloat("4e-5"));
			addEpsilon(2, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(2, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			addEpsilon(2, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(2, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);

			// --------------------------------------------------------------------------------------------------------------
		    // PMOO
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(875, 9), num_factory.create(4525, 9));
			addBounds(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(2095, 27), num_factory.create(10925, 27));
			addBounds(2, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(65), num_factory.create(1025, 3));
			
			/*
			 * Observed test failures:
			 * 
			 * Real Double: Epsilon set to ignore
			 * 		PMOO delay ==> expected <97.22222222222223> but was <97.22222222222221>
			 * 		PMOO backlog ==> expected <502.77777777777777> but was <502.7777777777777>
			 * 		PMOO delay ==> expected <341.6666666666667> but was <341.66666666666663>
			 * 
			 * Real Single: Epsilon set to ignore
			 * 		PMOO delay ==> expected <341.66666> but was <341.6667>
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		PMOO delay ==> expected <875 / 9> but was <1710351420984889 / 17592186044416>
			 * 		PMOO delay ==> expected <2095 / 27> but was <5118837467090489 / 65970697666560>
			 * 		PMOO delay ==> expected <2095 / 27> but was <5460093298229855 / 70368744177664>
			 * 		PMOO delay ==> expected <1025 / 3> but was <6010663565175467 / 17592186044416>
			 */
			real_double_epsilon = new RealDoublePrecision(Double.parseDouble("7e-14"));
			addEpsilon(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(2, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_single_epsilon = new RealSinglePrecision(Float.parseFloat("4e-5"));
			addEpsilon(2, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			addEpsilon(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(2, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
		}
	}
}