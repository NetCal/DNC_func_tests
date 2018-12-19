/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2013 - 2018 Steffen Bondorf
 * Copyright (C) 2017+ The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://discodnc.cs.uni-kl.de
 *
 *
 * The Disco Deterministic Network Calculator (DiscoDNC) is free software;
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

package de.uni_kl.cs.discodnc.func_tests;

import de.uni_kl.cs.discodnc.network.server_graph.Flow;
import de.uni_kl.cs.discodnc.network.server_graph.ServerGraph;
import de.uni_kl.cs.discodnc.tandem.analyses.PmooAnalysis;
import de.uni_kl.cs.discodnc.tandem.analyses.SeparateFlowAnalysis;
import de.uni_kl.cs.discodnc.tandem.analyses.TotalFlowAnalysis;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class TR_7S_1SC_3F_1AC_3P_Test extends DncTest {
	private static TR_7S_1SC_3F_1AC_3P_ServerGraph network_factory = new TR_7S_1SC_3F_1AC_3P_ServerGraph();
	private static ServerGraph network = network_factory.getServerGraph();
	private Flow f0, f1, f2;

	private TR_7S_1SC_3F_1AC_3P_Test() throws Exception {
		super(network_factory, new TR_7S_1SC_3F_1AC_3P_Results());
		f0 = network.getFlow(0);
		f1 = network.getFlow(1);
		f2 = network.getFlow(2);
	}

	// --------------------Flow 0--------------------
	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.func_tests.DncTestMethodSources#provideAllArguments")
	public void f0_tfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runTFAtest(new TotalFlowAnalysis(network, test_config), f0);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.func_tests.DncTestMethodSources#provideAllArguments")
	public void f0_sfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runSFAtest(new SeparateFlowAnalysis(network, test_config), f0);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.func_tests.DncTestMethodSources#provideArbArguments")
	public void f0_pmoo_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runPMOOtest(new PmooAnalysis(network, test_config), f0);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.func_tests.DncTestMethodSources#provideSinkTreeArguments")
	public void f0_sinktree_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runSinkTreePMOOtest(network, f0); // tests all PMOO Sink Tree variants
	}

	// --------------------Flow 1--------------------
	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.func_tests.DncTestMethodSources#provideAllArguments")
	public void f1_tfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runTFAtest(new TotalFlowAnalysis(network, test_config), f1);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.func_tests.DncTestMethodSources#provideAllArguments")
	public void f1_sfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runSFAtest(new SeparateFlowAnalysis(network, test_config), f1);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.func_tests.DncTestMethodSources#provideArbArguments")
	public void f1_pmoo_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runPMOOtest(new PmooAnalysis(network, test_config), f1);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.func_tests.DncTestMethodSources#provideSinkTreeArguments")
	public void f1_sinktree_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runSinkTreePMOOtest(network, f1); // tests all PMOO Sink Tree variants
	}

	// --------------------Flow 2--------------------
	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.func_tests.DncTestMethodSources#provideAllArguments")
	public void f2_tfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runTFAtest(new TotalFlowAnalysis(network, test_config), f2);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.func_tests.DncTestMethodSources#provideAllArguments")
	public void f2_sfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runSFAtest(new SeparateFlowAnalysis(network, test_config), f2);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.func_tests.DncTestMethodSources#provideArbArguments")
	public void f2_pmoo_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runPMOOtest(new PmooAnalysis(network, test_config), f2);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.func_tests.DncTestMethodSources#provideSinkTreeArguments")
	public void f2_sinktree_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runSinkTreePMOOtest(network, f2); // tests all PMOO Sink Tree variants
	}
}