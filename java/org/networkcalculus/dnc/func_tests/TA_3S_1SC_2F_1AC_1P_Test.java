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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.networkcalculus.dnc.func_tests.DncTest;
import org.networkcalculus.dnc.func_tests.DncTestConfig;
import org.networkcalculus.dnc.func_tests.TA_3S_1SC_2F_1AC_1P_Results;
import org.networkcalculus.dnc.func_tests.TA_3S_1SC_2F_1AC_1P_ServerGraph;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.tandem.analyses.PmooAnalysis;
import org.networkcalculus.dnc.tandem.analyses.SeparateFlowAnalysis;
import org.networkcalculus.dnc.tandem.analyses.TotalFlowAnalysis;

public class TA_3S_1SC_2F_1AC_1P_Test extends DncTest {
	private static TA_3S_1SC_2F_1AC_1P_ServerGraph network_factory = new TA_3S_1SC_2F_1AC_1P_ServerGraph();
	private static ServerGraph network = network_factory.getServerGraph();
	private Flow f0, f1;

	private TA_3S_1SC_2F_1AC_1P_Test() throws Exception {
		super(network_factory, new TA_3S_1SC_2F_1AC_1P_Results());
		f0 = network.getFlow(0);
		f1 = network.getFlow(1);
	}

	// --------------------Flow 0--------------------
	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("org.networkcalculus.dnc.func_tests.DncTestMethodSources#provideAllArguments")
	public void f0_tfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runTFAtest(new TotalFlowAnalysis(network, test_config), f0);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("org.networkcalculus.dnc.func_tests.DncTestMethodSources#provideAllArguments")
	public void f0_sfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runSFAtest(new SeparateFlowAnalysis(network, test_config), f0);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("org.networkcalculus.dnc.func_tests.DncTestMethodSources#provideArbPmooArguments")
	public void f0_pmoo_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runPMOOtest(new PmooAnalysis(network, test_config), f0);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("org.networkcalculus.dnc.func_tests.DncTestMethodSources#provideSinkTreeArguments")
	public void f0_sinktree_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runSinkTreePMOOtest(network, f0); // tests all PMOO Sink Tree variants
	}

	// --------------------Flow 1--------------------
	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("org.networkcalculus.dnc.func_tests.DncTestMethodSources#provideAllArguments")
	public void f1_tfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runTFAtest(new TotalFlowAnalysis(network, test_config), f1);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("org.networkcalculus.dnc.func_tests.DncTestMethodSources#provideAllArguments")
	public void f1_sfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runSFAtest(new SeparateFlowAnalysis(network, test_config), f1);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("org.networkcalculus.dnc.func_tests.DncTestMethodSources#provideArbPmooArguments")
	public void f1_pmoo_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runPMOOtest(new PmooAnalysis(network, test_config), f1);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("org.networkcalculus.dnc.func_tests.DncTestMethodSources#provideSinkTreeArguments")
	public void f1_sinktree_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runSinkTreePMOOtest(network, f1); // tests all PMOO Sink Tree variants
	}
}