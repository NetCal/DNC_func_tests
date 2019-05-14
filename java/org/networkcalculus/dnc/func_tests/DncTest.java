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

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.AnalysisConfig.ArrivalBoundMethod;
import org.networkcalculus.dnc.AnalysisConfig.Multiplexing;
import org.networkcalculus.dnc.AnalysisConfig.MultiplexingEnforcement;
import org.networkcalculus.dnc.feedforward.ArrivalBoundDispatch;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.func_tests.DncTestConfig;
import org.networkcalculus.dnc.func_tests.DncTestMethodSources;
import org.networkcalculus.dnc.func_tests.DncTestResults;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.network.server_graph.ServerGraphFactory;
import org.networkcalculus.dnc.sinktree.Backlog_SinkTree;
import org.networkcalculus.dnc.tandem.TandemAnalysis;
import org.networkcalculus.dnc.tandem.TandemAnalysisResults;
import org.networkcalculus.dnc.tandem.TandemAnalysis.Analyses;
import org.networkcalculus.dnc.tandem.analyses.PmooAnalysis;
import org.networkcalculus.dnc.tandem.analyses.SeparateFlowAnalysis;
import org.networkcalculus.dnc.tandem.analyses.TotalFlowAnalysis;
import org.networkcalculus.num.Num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class DncTest {
	protected ServerGraphFactory sg_factory;
	protected DncTestConfig test_config;
	protected DncTestResults expected_results;

	@SuppressWarnings("unused")
	private DncTest() {
	}

	protected DncTest(ServerGraphFactory sg_factory, DncTestResults expected_results) {
		this.sg_factory = sg_factory;
		this.expected_results = expected_results;
	}

	protected void initializeTest(DncTestConfig test_config) {
		this.test_config = test_config;
		printSetting();

		Calculator.getInstance().setCurveBackend(test_config.getCurveBackend());
		Calculator.getInstance().setNumBackend(test_config.getNumBackend());
		ArrivalBoundDispatch.clearAllCaches();
		
		// reinitialize the server graph and the expected bounds
		sg_factory.reinitializeCurves();
		expected_results.initialize();
	}

	public void printSetting() {
		if (test_config.console_output) {
			System.out.println("--------------------------------------------------------------");
			System.out.println();
			System.out.println("Number representation:\t" + test_config.getNumBackend().toString());
			System.out.println("Curve representation:\t" + test_config.getCurveBackend().toString());
			System.out.println("Arrival Boundings:\t" + test_config.arrivalBoundMethods().toString());
			System.out.println("Convolve alterantive ABs:\t" + Boolean.toString(test_config.convolveAlternativeArrivalBounds()));
		}
	}

	public void setMux(Set<Server> servers) {
		if (!test_config.define_multiplexing_globally) {

			test_config.enforceMultiplexing(MultiplexingEnforcement.SERVER_LOCAL);
			for (Server s : servers) {
				s.setMultiplexing(test_config.multiplexing);
			}

		} else {
			// Enforce potential test failure by defining the server-local multiplexing differently.
			Multiplexing mux_local;
			MultiplexingEnforcement mux_global;

			if (test_config.multiplexing == Multiplexing.ARBITRARY) {
				mux_global = MultiplexingEnforcement.GLOBAL_ARBITRARY;
				mux_local = Multiplexing.FIFO;
			} else {
				mux_global = MultiplexingEnforcement.GLOBAL_FIFO;
				mux_local = Multiplexing.ARBITRARY;
			}

			test_config.enforceMultiplexing(mux_global);
			for (Server s : servers) {
				s.setMultiplexing(mux_local);
			}
		}
	}

	private void runAnalysis(TandemAnalysis analysis, Flow flow_of_interest) {
		try {
			analysis.performAnalysis(flow_of_interest);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Analysis failed");
		}
	}

	protected void runTFAtest(TotalFlowAnalysis tfa, Flow flow_of_interest) {
		runAnalysis(tfa, flow_of_interest);

		if (test_config.fullConsoleOutput()) {
			System.out.println("Analysis:\t\tTotal Flow Analysis (TFA)");
			System.out.println("Multiplexing:\t\tFIFO");

			System.out.println("Flow of interest:\t" + flow_of_interest.toString());
			System.out.println();

			System.out.println("--- Results: ---");
			System.out.println("delay bound     : " + tfa.getDelayBound());
			System.out.println("     per server : " + tfa.getServerDelayBoundMapString());
			System.out.println("backlog bound   : " + tfa.getBacklogBound());
			System.out.println("     per server : " + tfa.getServerBacklogBoundMapString());
			System.out.println("alpha per server: " + tfa.getServerAlphasMapString());
			System.out.println();
		}

		// The alias holds the original flow ID, independent of the order flows are added to the server graph under analysis.
		Integer foiID_from_alias = Integer.valueOf(flow_of_interest.getAlias().substring(1));
		
		TandemAnalysisResults bounds = expected_results.getBounds(foiID_from_alias, Analyses.TFA, test_config.arrivalBoundMethods(), test_config.multiplexing);
		
		Num epsilon = expected_results.getEpsilon(foiID_from_alias, Analyses.TFA, 
				test_config.arrivalBoundMethods(), test_config.multiplexing, test_config.getNumBackend());
		
		// AssertEquals fails with a delta of zero.
		if(epsilon.eqZero()) {
			assertEquals(
				bounds.getDelayBound(), 
				tfa.getDelayBound(), 
				"TFA delay");
			assertEquals(
				bounds.getBacklogBound(), 
				tfa.getBacklogBound(), 
				"TFA backlog");
		} else {
			assertEquals(
				bounds.getDelayBound().doubleValue(), 
				tfa.getDelayBound().doubleValue(), 
				epsilon.doubleValue(), 
				"TFA delay");
			assertEquals(
				bounds.getBacklogBound().doubleValue(), 
				tfa.getBacklogBound().doubleValue(), 
				epsilon.doubleValue(), 
				"TFA backlog");
		}
	}

	protected void runSFAtest(SeparateFlowAnalysis sfa, Flow flow_of_interest) {
		runAnalysis(sfa, flow_of_interest);

		if (test_config.fullConsoleOutput()) {
			System.out.println("Analysis:\t\tSeparate Flow Analysis (SFA)");
			System.out.println("Multiplexing:\t\tFIFO");

			System.out.println("Flow of interest:\t" + flow_of_interest.toString());
			System.out.println();

			System.out.println("--- Results: ---");
			System.out.println("e2e SFA SCs     : " + sfa.getLeftOverServiceCurves());
			System.out.println("     per server : " + sfa.getServerLeftOverBetasMapString());
			System.out.println("xtx per server  : " + sfa.getServerAlphasMapString());
			System.out.println("delay bound     : " + sfa.getDelayBound());
			System.out.println("backlog bound   : " + sfa.getBacklogBound());
			System.out.println();
		}

		// The alias holds the original flow ID, independent of the order flows are added to the server graph under analysis.
		Integer foiID_from_alias = Integer.valueOf(flow_of_interest.getAlias().substring(1));
		
		TandemAnalysisResults bounds = expected_results.getBounds(foiID_from_alias, Analyses.SFA, test_config.arrivalBoundMethods(), test_config.multiplexing);
		
		Num epsilon = expected_results.getEpsilon(foiID_from_alias, Analyses.SFA, 
				test_config.arrivalBoundMethods(), test_config.multiplexing, test_config.getNumBackend());
		
		// AssertEquals fails with a delta of zero.
		if(epsilon.eqZero()) {
			assertEquals(
				bounds.getDelayBound(), 
				sfa.getDelayBound(), 
				"SFA delay");
			assertEquals(
				bounds.getBacklogBound(), 
				sfa.getBacklogBound(), 
				"SFA backlog");
		} else {
			assertEquals(
				bounds.getDelayBound().doubleValue(), 
				sfa.getDelayBound().doubleValue(), 
				epsilon.doubleValue(), 
				"SFA delay");
			assertEquals(
				bounds.getBacklogBound().doubleValue(), 
				sfa.getBacklogBound().doubleValue(), 
				epsilon.doubleValue(), 
				"SFA backlog");
		}
	}

	protected void runPMOOtest(PmooAnalysis pmoo, Flow flow_of_interest) {
		if(test_config.multiplexing == AnalysisConfig.Multiplexing.FIFO) {
			assertTrue( true, "PMOO FIFO test skipped");
			return;
		}
		
		runAnalysis(pmoo, flow_of_interest);

		if (test_config.fullConsoleOutput()) {
			System.out.println("Analysis:\t\tPay Multiplexing Only Once (PMOO)");
			System.out.println("Multiplexing:\t\tArbitrary");

			System.out.println("Flow of interest:\t" + flow_of_interest.toString());
			System.out.println();

			System.out.println("--- Results: ---");
			System.out.println("e2e PMOO SCs    : " + pmoo.getLeftOverServiceCurves());
			System.out.println("xtx per server  : " + pmoo.getServerAlphasMapString());
			System.out.println("delay bound     : " + pmoo.getDelayBound());
			System.out.println("backlog bound   : " + pmoo.getBacklogBound());
			System.out.println();
		}

		// The alias holds the original flow ID, independent of the order flows are added to the server graph under analysis.
		Integer foiID_from_alias = Integer.valueOf(flow_of_interest.getAlias().substring(1));
		
		TandemAnalysisResults bounds = expected_results.getBounds(foiID_from_alias, Analyses.PMOO, test_config.arrivalBoundMethods(), test_config.multiplexing);
		
		Num epsilon = expected_results.getEpsilon(foiID_from_alias, Analyses.PMOO, 
				test_config.arrivalBoundMethods(), test_config.multiplexing, test_config.getNumBackend());
		
		// AssertEquals fails with a delta of zero.
		if(epsilon.eqZero()) {
			assertEquals(
				bounds.getDelayBound(), 
				pmoo.getDelayBound(),
				"PMOO delay");
			assertEquals(
				bounds.getBacklogBound(), 
				pmoo.getBacklogBound(), 
				"PMOO backlog");
		} else {
			assertEquals(
				bounds.getDelayBound().doubleValue(), 
				pmoo.getDelayBound().doubleValue(),
				epsilon.doubleValue(), 
				"PMOO delay");
			assertEquals(
				bounds.getBacklogBound().doubleValue(), 
				pmoo.getBacklogBound().doubleValue(), 
				epsilon.doubleValue(), 
				"PMOO backlog");
		}
	}

	protected void runSinkTreePMOOtest(ServerGraph sink_tree, Flow flow_of_interest) {
		Num num_factory = Num.getFactory(Calculator.getInstance().getNumBackend());
		
		Num backlog_bound_AFFINE_CONV = null;
		Num backlog_bound_AFFINE_DIRECT = null;
		Num backlog_bound_AFFINE_HOMO = null;

		try {
			backlog_bound_AFFINE_CONV = num_factory.create(
					Backlog_SinkTree.derivePmooSinkTreeAffine(
							sink_tree, flow_of_interest.getSink(), ArrivalBoundMethod.SINKTREE_AFFINE_MINPLUS));
			backlog_bound_AFFINE_DIRECT = num_factory.create(
					Backlog_SinkTree.derivePmooSinkTreeAffine(
							sink_tree, flow_of_interest.getSink(), ArrivalBoundMethod.SINKTREE_AFFINE_DIRECT));
			backlog_bound_AFFINE_HOMO = num_factory.create(
					Backlog_SinkTree.derivePmooSinkTreeAffine(
							sink_tree, flow_of_interest.getSink(), ArrivalBoundMethod.SINKTREE_AFFINE_HOMO));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Analysis failed");
		}

		if (test_config.fullConsoleOutput()) {
			System.out.println("Analysis:\t\tTree Backlog Bound Analysis");
			System.out.println("Multiplexing:\t\tArbitrary");

			System.out.println("Flow of interest:\t" + flow_of_interest.toString());
			System.out.println();

			System.out.println("--- Result: ---");

			System.out.println("backlog bound AFFINE CONV               : " + backlog_bound_AFFINE_CONV.toString());
			System.out.println("backlog bound AFFINE DIRECT             : " + backlog_bound_AFFINE_DIRECT.toString());
			System.out.println("backlog bound AFFINE HOMO               : " + backlog_bound_AFFINE_HOMO.toString());
			System.out.println();
		}

		// The alias holds the original flow ID, independent of the order flows are added to the server graph under analysis.
		Integer foiID_from_alias = Integer.valueOf(flow_of_interest.getAlias().substring(1));

		TandemAnalysisResults bounds = expected_results.getBounds(foiID_from_alias, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY);

		Num epsilon = expected_results.getEpsilon(foiID_from_alias, Analyses.PMOO, 
				test_config.arrivalBoundMethods(), test_config.multiplexing, test_config.getNumBackend());
		
		// AssertEquals fails with a delta of zero.
		if(epsilon.eqZero()) {
			assertEquals(
				bounds.getBacklogBound(), 
				backlog_bound_AFFINE_CONV, 
				"PMOO backlog AFFINE MINPLUS");
			assertEquals(
				bounds.getBacklogBound(), 
				backlog_bound_AFFINE_DIRECT,
				"PMOO backlog AFFINE DIRECT");
			assertEquals(
				bounds.getBacklogBound(), 
				backlog_bound_AFFINE_HOMO, 
				"PMOO backlog AFFINE HOMO");
		} else {
			assertEquals(
				bounds.getBacklogBound().doubleValue(), 
				backlog_bound_AFFINE_CONV.doubleValue(), 
				epsilon.doubleValue(), 
				"PMOO backlog AFFINE MINPLUS");
			assertEquals(
				bounds.getBacklogBound().doubleValue(), 
				backlog_bound_AFFINE_DIRECT.doubleValue(),
				epsilon.doubleValue(), 
				"PMOO backlog AFFINE DIRECT");
			assertEquals(
				bounds.getBacklogBound().doubleValue(), 
				backlog_bound_AFFINE_HOMO.doubleValue(), 
				epsilon.doubleValue(), 
				"PMOO backlog AFFINE HOMO");
		}
	}
}
