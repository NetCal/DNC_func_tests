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

import org.networkcalculus.dnc.AlgDncBackend;
import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.num.NumBackend;

public class DncTestConfig extends AnalysisConfig {
	// Functional test specific parameters
	protected boolean define_multiplexing_globally;
	protected AnalysisConfig.Multiplexing multiplexing;
	protected boolean console_output = false;

	// Calculator configuration
	protected NumBackend num_backend;
	protected AlgDncBackend curve_backend;

	@SuppressWarnings("unused")
	private DncTestConfig() {
	}

	public DncTestConfig(Set<ArrivalBoundMethod> arrival_bound_methods, 
			boolean arrival_bounds_caching, boolean convolve_alternative_arrival_bounds,
			AnalysisConfig.Multiplexing multiplexing,
			boolean define_multiplexing_globally, NumBackend numbers, AlgDncBackend curves ) {

		super(AnalysisConfig.MultiplexingEnforcement.GLOBAL_ARBITRARY, // Not used, no influence yet.
				MaxScEnforcement.GLOBALLY_OFF, // Not used, no influence yet.
				MaxScEnforcement.GLOBALLY_OFF, // Not used, no influence yet.
				arrival_bound_methods, 
				convolve_alternative_arrival_bounds, arrival_bounds_caching, false);

		this.multiplexing = multiplexing;
		this.define_multiplexing_globally = define_multiplexing_globally;

		// Will not work. Num implementation and curve implementation need to be stored
		// locally as the singleton pattern will cause overwriting this setting in
		// CalculatorConfig.

		num_backend = numbers;
		curve_backend = curves;
	}

	public boolean fullConsoleOutput() { // false == Exceptions only
		return console_output;
	}

	protected NumBackend getNumBackend() {
		return num_backend;
	}

	protected AlgDncBackend getCurveBackend() {
		return curve_backend;
	}

	@Override
	public String toString() {
		// AB, convolve alternative ABs, mux, global mux def, numbers, curves
		StringBuffer func_test_str = new StringBuffer();

		func_test_str.append(arrivalBoundMethods().toString());
		
		if (convolveAlternativeArrivalBounds()) {
			func_test_str.append(", " + "conv ABs");
		}
		
		if (useArrivalBoundsCache()) {
			func_test_str.append(", " + "AB cache");
		}

		func_test_str.append(", " + multiplexing.toString());

		if (define_multiplexing_globally) {
			func_test_str.append(", " + "MUX global");
		}

		func_test_str.append(", NUM_" + num_backend.toString());

		// Also prints the min-plus implementation used by this config.
		func_test_str.append(", C_" + curve_backend.toString());

		return func_test_str.toString();
	}
}
