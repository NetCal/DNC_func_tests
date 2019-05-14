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

public class TA_2S_1SC_1F_1AC_1P_Results extends DncTestResults {

	protected TA_2S_1SC_1F_1AC_1P_Results() {
	}

	protected void initialize() {
		super.clear();

		Num num_factory = Num.getFactory(Calculator.getInstance().getNumBackend());
		
		for( Set<ArrivalBoundMethod> ab_set : DncTestMethodSources.ab_sets ) {
			// --------------------------------------------------------------------------------------------------------------
		    // TFA
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(30), num_factory.create(125));
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(30), num_factory.create(125));

			// --------------------------------------------------------------------------------------------------------------
		    // SFA
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(22.5), num_factory.create(125));
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(22.5), num_factory.create(125));

			// --------------------------------------------------------------------------------------------------------------
		    // PMOO
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(22.5), num_factory.create(125));
		}

		// --------------------------------------------------------------------------------------------------------------
	    // Sink tree
	    // --------------------------------------------------------------------------------------------------------------
		addBounds(0, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(125));
	}
}