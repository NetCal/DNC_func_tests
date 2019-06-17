/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2017 - 2018 Steffen Bondorf
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.networkcalculus.dnc.AnalysisConfig.ArrivalBoundMethod;
import org.networkcalculus.dnc.AnalysisConfig.Multiplexing;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.tandem.TandemAnalysisResults;
import org.networkcalculus.dnc.tandem.TandemAnalysis.Analyses;
import org.networkcalculus.num.Num;
import org.networkcalculus.num.NumBackend;

public abstract class DncTestResults {
	private Map<Integer, Map<Analyses, Map<Set<ArrivalBoundMethod>, Map<Multiplexing, Set<TandemAnalysisResults>>>>> results_map;
	private Map<Integer, Map<Analyses, Map<Set<ArrivalBoundMethod>, Map<Multiplexing, Map<NumBackend, Num>>>>> epsilon_map;
	
	public DncTestResults() {
		results_map = new HashMap<Integer, Map<Analyses, Map<Set<ArrivalBoundMethod>, Map<Multiplexing, Set<TandemAnalysisResults>>>>>();
		epsilon_map = new HashMap<Integer, Map<Analyses, Map<Set<ArrivalBoundMethod>, Map<Multiplexing, Map<NumBackend, Num>>>>>();
	}

	protected void clear() {
		results_map.clear();
		epsilon_map.clear();
	}

	protected abstract void initialize();

	// TODO Some batch mode would be nice in order not to query the maps for every analysis of the same flow.
	protected void addBounds(Integer flowId, Analyses analysis, Set<ArrivalBoundMethod> ab_set, Multiplexing mux, Num delay, Num backlog) {
		TandemAnalysisResults expected_results = new TandemAnalysisResults(delay, backlog, null);

		Map<Analyses, Map<Set<ArrivalBoundMethod>, Map<Multiplexing, Set<TandemAnalysisResults>>>> foi_maps = results_map.get(flowId);
		if(foi_maps == null) {
			foi_maps = new HashMap<Analyses, Map<Set<ArrivalBoundMethod>, Map<Multiplexing, Set<TandemAnalysisResults>>>>();
			results_map.put(flowId, foi_maps);
		}
		
		Map<Set<ArrivalBoundMethod>, Map<Multiplexing, Set<TandemAnalysisResults>>> foi_analysis_maps = foi_maps.get(analysis);
		if(foi_analysis_maps == null) {
			foi_analysis_maps =  new HashMap<Set<ArrivalBoundMethod>, Map<Multiplexing, Set<TandemAnalysisResults>>>();
			foi_maps.put(analysis, foi_analysis_maps);
		}
		
		Map<Multiplexing, Set<TandemAnalysisResults>> foi_analysis_ab_maps = foi_analysis_maps.get(ab_set);
		if(foi_analysis_ab_maps == null) {
			foi_analysis_ab_maps = new HashMap<Multiplexing, Set<TandemAnalysisResults>>();
			foi_analysis_maps.put(ab_set, foi_analysis_ab_maps);
		}
		
		Set<TandemAnalysisResults> existing_results = foi_analysis_ab_maps.get(mux);
		if(existing_results == null) {
			existing_results = new HashSet<TandemAnalysisResults>();
			foi_analysis_ab_maps.put(mux, existing_results);
		}

		existing_results.add(expected_results);
	}

	public TandemAnalysisResults getBounds(Integer flowId, Analyses analysis, Set<ArrivalBoundMethod> ab_set, Multiplexing mux) {
		Map<Analyses, Map<Set<ArrivalBoundMethod>, Map<Multiplexing, Set<TandemAnalysisResults>>>> foi_maps = results_map.get(flowId);
		if(foi_maps == null || foi_maps.isEmpty()) {
			throw new RuntimeException("No DNC test results found! The results file may be corrupted.");
		}
		
		Map<Set<ArrivalBoundMethod>, Map<Multiplexing, Set<TandemAnalysisResults>>> foi_analysis_maps = foi_maps.get(analysis);
		if(foi_analysis_maps == null || foi_analysis_maps.isEmpty()) {
			throw new RuntimeException("No DNC test results found! The results file may be corrupted.");
		}
		
		Map<Multiplexing, Set<TandemAnalysisResults>> foi_analysis_ab_maps = new HashMap<Multiplexing, Set<TandemAnalysisResults>>(); 
		for(Map.Entry<Set<ArrivalBoundMethod>, Map<Multiplexing, Set<TandemAnalysisResults>>> abs_to_map : foi_analysis_maps.entrySet()) {
			if( abs_to_map.getKey().size() == ab_set.size()
					&& abs_to_map.getKey().containsAll(ab_set)) {
				foi_analysis_ab_maps = abs_to_map.getValue();
				break;
			}
		}
		if(foi_analysis_ab_maps.isEmpty()) {
			throw new RuntimeException("No DNC test results found! The results file may be corrupted.");
		}
		
		Set<TandemAnalysisResults> existing_results = foi_analysis_ab_maps.get(mux);
		if(existing_results == null || existing_results.isEmpty()) {
			throw new RuntimeException("No DNC test results found! The results file may be corrupted.");
		}
		
		if(existing_results.size() == 1) {
			return existing_results.iterator().next();
		} else {
			System.out.println( existing_results.toString() );
			throw new RuntimeException("Ambiguous DNC test results! The results file may be corrupted.");
		}
	}
	
	protected void addEpsilon(Integer flowId, Analyses analysis, Set<ArrivalBoundMethod> ab_set, Multiplexing mux, NumBackend num_rep, Num epsilon) {
		Map<Analyses, Map<Set<ArrivalBoundMethod>, Map<Multiplexing, Map<NumBackend, Num>>>> foi_maps = epsilon_map.get(flowId);
		if(foi_maps == null) {
			foi_maps = new HashMap<Analyses, Map<Set<ArrivalBoundMethod>, Map<Multiplexing, Map<NumBackend, Num>>>>();
			epsilon_map.put(flowId, foi_maps);
		}
		
		Map<Set<ArrivalBoundMethod>, Map<Multiplexing, Map<NumBackend, Num>>> foi_analysis_maps = foi_maps.get(analysis);
		if(foi_analysis_maps == null) {
			foi_analysis_maps =  new HashMap<Set<ArrivalBoundMethod>, Map<Multiplexing, Map<NumBackend, Num>>>();
			foi_maps.put(analysis, foi_analysis_maps);
		}
		
		Map<Multiplexing, Map<NumBackend, Num>> foi_analysis_ab_maps = foi_analysis_maps.get(ab_set);
		if(foi_analysis_ab_maps == null) {
			foi_analysis_ab_maps = new HashMap<Multiplexing, Map<NumBackend, Num>>();
			foi_analysis_maps.put(ab_set, foi_analysis_ab_maps);
		}
		
		Map<NumBackend, Num> existing_results = foi_analysis_ab_maps.get(mux);
		if(existing_results == null) {
			existing_results = new HashMap<NumBackend, Num>();
			foi_analysis_ab_maps.put(mux, existing_results);
		}

		existing_results.put(num_rep,epsilon);
	}

	public Num getEpsilon(Integer flowId, Analyses analysis, Set<ArrivalBoundMethod> ab_set, Multiplexing mux, NumBackend num_rep) {
		Map<Analyses, Map<Set<ArrivalBoundMethod>, Map<Multiplexing, Map<NumBackend, Num>>>> foi_maps = epsilon_map.get(flowId);
		if(foi_maps == null || foi_maps.isEmpty()) {
			return Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
		}
		
		Map<Set<ArrivalBoundMethod>, Map<Multiplexing, Map<NumBackend, Num>>> foi_analysis_maps = foi_maps.get(analysis);
		if(foi_analysis_maps == null || foi_analysis_maps.isEmpty()) {
			return Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
		}
		
		Map<Multiplexing, Map<NumBackend, Num>> foi_analysis_ab_maps = new HashMap<Multiplexing, Map<NumBackend, Num>>(); 
		for(Map.Entry<Set<ArrivalBoundMethod>, Map<Multiplexing, Map<NumBackend, Num>>> abs_to_map : foi_analysis_maps.entrySet()) {
			if(abs_to_map.getKey().size() == ab_set.size()
					&& abs_to_map.getKey().containsAll(ab_set)) {
				foi_analysis_ab_maps = abs_to_map.getValue();
				break;
			}
		}
		// Ignore Spotbugs' "Nullcheck" as it did not recognize the potential assignment in the loop before.
		// Also note that the same method is used in getBounds but Spotbugs did not complain.
		if(foi_analysis_ab_maps.isEmpty()) {
			return Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
		}
		
		Map<NumBackend, Num> existing_epsilons = foi_analysis_ab_maps.get(mux);
		if(foi_analysis_ab_maps == null || foi_analysis_ab_maps.isEmpty()) {
			return Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
		}
		
		return existing_epsilons.getOrDefault(num_rep, Num.getFactory(Calculator.getInstance().getNumBackend()).createZero());
	}

	@Override
	public String toString() {
		StringBuffer exp_results_str = new StringBuffer();
		String analysis_str, ab_str, mux_str; 
		
		for( Map.Entry<Integer, Map<Analyses, Map<Set<ArrivalBoundMethod>, Map<Multiplexing, Set<TandemAnalysisResults>>>>> foi_map_entry : results_map.entrySet() ) {
			exp_results_str.append("flow Id: " + foi_map_entry.getKey().toString());
			exp_results_str.append("\n");
			
			for( Map.Entry<Analyses, Map<Set<ArrivalBoundMethod>, Map<Multiplexing, Set<TandemAnalysisResults>>>> analysis_map_entry : foi_map_entry.getValue().entrySet() ) {
				analysis_str = "\t" + "Analysis: " + analysis_map_entry.getKey().toString();
				
				for( Map.Entry<Set<ArrivalBoundMethod>, Map<Multiplexing, Set<TandemAnalysisResults>>> ab_map_entry : analysis_map_entry.getValue().entrySet() ) {
					ab_str = "; " + "Arrival Boundings: " + ab_map_entry.getKey().toString();
					
					for( Map.Entry<Multiplexing, Set<TandemAnalysisResults>> mux_map_entry : ab_map_entry.getValue().entrySet() ) {
						mux_str = "; " + "Multiplexing: " + mux_map_entry.getKey().toString();
						
						for( TandemAnalysisResults stored_results : mux_map_entry.getValue() ) {
							exp_results_str.append( analysis_str );
							exp_results_str.append( ab_str );
							exp_results_str.append( mux_str );
							exp_results_str.append( "; " );
							exp_results_str.append(stored_results.toString());
							exp_results_str.append("\n");
						}
					}
				}
			}
		}

		return exp_results_str.toString();
	}
}
