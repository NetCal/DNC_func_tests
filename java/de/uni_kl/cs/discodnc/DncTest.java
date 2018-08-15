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

package de.uni_kl.cs.discodnc;

import de.uni_kl.cs.discodnc.nc.Analysis;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig;
import de.uni_kl.cs.discodnc.nc.Analysis.Analyses;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.ArrivalBoundMethod;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.Multiplexing;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.MuxDiscipline;
import de.uni_kl.cs.discodnc.nc.AnalysisResults;
import de.uni_kl.cs.discodnc.nc.analyses.PmooAnalysis;
import de.uni_kl.cs.discodnc.nc.analyses.SeparateFlowAnalysis;
import de.uni_kl.cs.discodnc.nc.analyses.TotalFlowAnalysis;
import de.uni_kl.cs.discodnc.nc.bounds.Bound;
import de.uni_kl.cs.discodnc.network.Flow;
import de.uni_kl.cs.discodnc.network.Network;
import de.uni_kl.cs.discodnc.network.NetworkFactory;
import de.uni_kl.cs.discodnc.network.Server;
import de.uni_kl.cs.discodnc.numbers.Num;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class DncTest {
	protected NetworkFactory network_factory;
	protected DncTestConfig test_config;
	protected DncTestResults expected_results;

	@SuppressWarnings("unused")
	private DncTest() {
	}

	protected DncTest(NetworkFactory network_factory, DncTestResults expected_results) {
		this.network_factory = network_factory;
		this.expected_results = expected_results;
	}

	protected void initializeTest(DncTestConfig test_config) {
		this.test_config = test_config;
		printSetting();

		if (test_config.enable_checks) {
			Calculator.getInstance().enableAllChecks();
		} else {
			Calculator.getInstance().disableAllChecks();
		}

		Calculator.getInstance().setCurveBackend(test_config.getCurveBackend());
		Calculator.getInstance().setNumBackend(test_config.getNumBackend());

		// reinitialize the network and the expected bounds
		network_factory.reinitializeCurves();
		expected_results.initialize();
	}

	public void printSetting() {
		if (test_config.console_output) {
			System.out.println("--------------------------------------------------------------");
			System.out.println();
			System.out.println("Number representation:\t" + test_config.getNumBackend().toString());
			System.out.println("Curve representation:\t" + test_config.getCurveBackend().toString());
			System.out.println("Arrival Boundings:\t" + test_config.arrivalBoundMethods().toString());
			System.out.println("Remove duplicate ABs:\t" + Boolean.toString(test_config.removeDuplicateArrivalBounds()));
		}
	}

	public void setMux(Set<Server> servers) {
		if (!test_config.define_multiplexing_globally) {

			test_config.setMultiplexingDiscipline(MuxDiscipline.SERVER_LOCAL);
			for (Server s : servers) {
				s.setMultiplexingDiscipline(test_config.multiplexing);
			}

		} else {
			// Enforce potential test failure by defining the server-local multiplexing differently.
			Multiplexing mux_local;
			MuxDiscipline mux_global;

			if (test_config.multiplexing == Multiplexing.ARBITRARY) {
				mux_global = MuxDiscipline.GLOBAL_ARBITRARY;
				mux_local = Multiplexing.FIFO;
			} else {
				mux_global = MuxDiscipline.GLOBAL_FIFO;
				mux_local = Multiplexing.ARBITRARY;
			}

			test_config.setMultiplexingDiscipline(mux_global);
			for (Server s : servers) {
				s.setMultiplexingDiscipline(mux_local);
			}
		}
	}

	private void runAnalysis(Analysis analysis, Flow flow_of_interest) {
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

		// The alias holds the original flow ID, independent of the order flows are added to the network under analysis.
		Integer foiID_from_alias = Integer.valueOf(flow_of_interest.getAlias().substring(1));
		
		AnalysisResults bounds = expected_results.getBounds(foiID_from_alias, Analyses.TFA, test_config.arrivalBoundMethods(), test_config.multiplexing);
		
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

		// The alias holds the original flow ID, independent of the order flows are added to the network under analysis.
		Integer foiID_from_alias = Integer.valueOf(flow_of_interest.getAlias().substring(1));
		
		AnalysisResults bounds = expected_results.getBounds(foiID_from_alias, Analyses.SFA, test_config.arrivalBoundMethods(), test_config.multiplexing);
		
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

		// The alias holds the original flow ID, independent of the order flows are added to the network under analysis.
		Integer foiID_from_alias = Integer.valueOf(flow_of_interest.getAlias().substring(1));
		
		AnalysisResults bounds = expected_results.getBounds(foiID_from_alias, Analyses.PMOO, test_config.arrivalBoundMethods(), test_config.multiplexing);
		
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

	protected void runSinkTreePMOOtest(Network sink_tree, Flow flow_of_interest) {
		Num num_factory = Num.getFactory(Calculator.getInstance().getNumBackend());
		
		Num backlog_bound_TBRL = null;
		Num backlog_bound_TBRL_CONV = null;
		Num backlog_bound_TBRL_CONV_TBRL_DECONV = null;
		Num backlog_bound_TBRL_HOMO = null;

		try {
			backlog_bound_TBRL = num_factory.create(Bound.backlogPmooSinkTreeTbRl(sink_tree,
					flow_of_interest.getSink(), ArrivalBoundMethod.PMOO_SINKTREE_TBRL));
			backlog_bound_TBRL_CONV = num_factory.create(Bound.backlogPmooSinkTreeTbRl(sink_tree,
					flow_of_interest.getSink(), ArrivalBoundMethod.PMOO_SINKTREE_TBRL_CONV));
			backlog_bound_TBRL_CONV_TBRL_DECONV = num_factory.create(Bound.backlogPmooSinkTreeTbRl(sink_tree,
					flow_of_interest.getSink(), ArrivalBoundMethod.PMOO_SINKTREE_TBRL_CONV_TBRL_DECONV));
			backlog_bound_TBRL_HOMO = num_factory.create(Bound.backlogPmooSinkTreeTbRl(sink_tree,
					flow_of_interest.getSink(), ArrivalBoundMethod.PMOO_SINKTREE_TBRL_HOMO));
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

			System.out.println("backlog bound TBRL                  : " + backlog_bound_TBRL.toString());
			System.out.println("backlog bound TBRL CONV             : " + backlog_bound_TBRL_CONV.toString());
			System.out
					.println("backlog bound TBRL CONV TBRL DECONV : " + backlog_bound_TBRL_CONV_TBRL_DECONV.toString());
			System.out.println("backlog bound RBRL HOMO             : " + backlog_bound_TBRL_HOMO.toString());
			System.out.println();
		}

		// The alias holds the original flow ID, independent of the order flows are added to the network under analysis.
		Integer foiID_from_alias = Integer.valueOf(flow_of_interest.getAlias().substring(1));

		AnalysisResults bounds = expected_results.getBounds(foiID_from_alias, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY);

		Num epsilon = expected_results.getEpsilon(foiID_from_alias, Analyses.PMOO, 
				test_config.arrivalBoundMethods(), test_config.multiplexing, test_config.getNumBackend());
		
		// AssertEquals fails with a delta of zero.
		if(epsilon.eqZero()) {
			assertEquals(
				bounds.getBacklogBound(), 
				backlog_bound_TBRL,
				"PMOO backlog TBRL");
			assertEquals(
				bounds.getBacklogBound(), 
				backlog_bound_TBRL_CONV, 
				"PMOO backlog TBRL CONV");
			assertEquals(
				bounds.getBacklogBound(), 
				backlog_bound_TBRL_CONV_TBRL_DECONV, 
				"PMOO backlog TBRL CONV TBRL DECONV");
			assertEquals(
				bounds.getBacklogBound(), 
				backlog_bound_TBRL_HOMO, 
				"PMOO backlog RBRL HOMO");
		} else {
			assertEquals(
				bounds.getBacklogBound().doubleValue(), 
				backlog_bound_TBRL.doubleValue(),
				epsilon.doubleValue(), 
				"PMOO backlog TBRL");
			assertEquals(
				bounds.getBacklogBound().doubleValue(), 
				backlog_bound_TBRL_CONV.doubleValue(), 
				epsilon.doubleValue(), 
				"PMOO backlog TBRL CONV");
			assertEquals(
				bounds.getBacklogBound().doubleValue(), 
				backlog_bound_TBRL_CONV_TBRL_DECONV.doubleValue(), 
				epsilon.doubleValue(), 
				"PMOO backlog TBRL CONV TBRL DECONV");
			assertEquals(
				bounds.getBacklogBound().doubleValue(), 
				backlog_bound_TBRL_HOMO.doubleValue(), 
				epsilon.doubleValue(), 
				"PMOO backlog RBRL HOMO");
		}
	}
}
