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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import org.networkcalculus.dnc.AlgDncBackend;
import org.networkcalculus.dnc.AlgDncBackend_DNC_Affine;
import org.networkcalculus.dnc.AlgDncBackend_DNC_ConPwAffine;
import org.networkcalculus.dnc.AlgDncBackend_MPARTC_DISCO_Affine;
import org.networkcalculus.dnc.AlgDncBackend_MPARTC_DISCO_PwAffine;
import org.networkcalculus.dnc.AlgDncBackend_MPARTC_PwAffine;
import org.networkcalculus.dnc.AnalysisConfig.ArrivalBoundMethod;
import org.networkcalculus.dnc.AnalysisConfig.Multiplexing;
import org.networkcalculus.dnc.algebra.MinPlus;
import org.networkcalculus.dnc.algebra.disco.MinPlus_Disco_Affine;
import org.networkcalculus.dnc.algebra.disco.MinPlus_Disco_ConPwAffine;
import org.networkcalculus.dnc.bounds.BoundingCurves;
import org.networkcalculus.dnc.bounds.Bounds;
import org.networkcalculus.dnc.bounds.disco.BoundingCurves_Disco_ConPwAffine;
import org.networkcalculus.dnc.bounds.disco.Bounds_Disco_PwAffine;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.LinearSegment;
import org.networkcalculus.dnc.curves.disco.LinearSegment_Disco;
import org.networkcalculus.dnc.curves.disco.pw_affine.Curve_Disco_PwAffine;
import org.networkcalculus.dnc.func_tests.AlgDncBackend_DNC_PwAffineC_Affine;
import org.networkcalculus.dnc.func_tests.DncTestConfig;
import org.networkcalculus.num.NumBackend;

public class DncTestMethodSources {

	protected static Set<Set<ArrivalBoundMethod>> ab_sets = instantiateABsets();
	protected static Set<ArrivalBoundMethod> single_1, single_2, single_3, pair_1, pair_2, pair_3, triplet, sinktree;

	private static Set<Set<ArrivalBoundMethod>> instantiateABsets() {
		single_1 = new HashSet<ArrivalBoundMethod>();
		single_1.add(ArrivalBoundMethod.AGGR_PBOO_CONCATENATION);

		single_2 = new HashSet<ArrivalBoundMethod>();
		single_2.add(ArrivalBoundMethod.AGGR_PBOO_PER_SERVER);

		single_3 = new HashSet<ArrivalBoundMethod>();
		single_3.add(ArrivalBoundMethod.AGGR_PMOO);

		pair_1 = new HashSet<ArrivalBoundMethod>();
		pair_1.add(ArrivalBoundMethod.AGGR_PBOO_PER_SERVER);
		pair_1.add(ArrivalBoundMethod.AGGR_PBOO_CONCATENATION);

		pair_2 = new HashSet<ArrivalBoundMethod>();
		pair_2.add(ArrivalBoundMethod.AGGR_PBOO_PER_SERVER);
		pair_2.add(ArrivalBoundMethod.AGGR_PMOO);

		pair_3 = new HashSet<ArrivalBoundMethod>();
		pair_3.add(ArrivalBoundMethod.AGGR_PBOO_CONCATENATION);
		pair_3.add(ArrivalBoundMethod.AGGR_PMOO);

		triplet = new HashSet<ArrivalBoundMethod>();
		triplet.add(ArrivalBoundMethod.AGGR_PBOO_PER_SERVER);
		triplet.add(ArrivalBoundMethod.AGGR_PBOO_CONCATENATION);
		triplet.add(ArrivalBoundMethod.AGGR_PMOO);
		
		Set<Set<ArrivalBoundMethod>> ab_sets = new HashSet<Set<ArrivalBoundMethod>>();
		ab_sets.add(single_1);
		ab_sets.add(single_2);
		ab_sets.add(single_3);
		ab_sets.add(pair_1);
		ab_sets.add(pair_2);
		ab_sets.add(pair_3);
		ab_sets.add(triplet);

		// sink tree bounds are not added to ab_sets as the tests treat them differently. 
		sinktree = new HashSet<ArrivalBoundMethod>();
		sinktree.add(ArrivalBoundMethod.SINKTREE_AFFINE_MINPLUS);
		sinktree.add(ArrivalBoundMethod.SINKTREE_AFFINE_DIRECT);
		sinktree.add(ArrivalBoundMethod.SINKTREE_AFFINE_HOMO);
		
		return ab_sets;
	}

	public static Stream<Arguments> provideAllArguments() {
		return Stream.concat(provideArbArguments(), provideFifoArguments());
	}
	
	protected static Stream<Arguments> provideArbArguments() {
		return createParameters(Collections.singleton(Multiplexing.ARBITRARY), ab_sets).stream().map(Arguments::of);
	}
	
	protected static Stream<Arguments> provideFifoArguments() {
		Set<Set<ArrivalBoundMethod>> ab_sets_fifo = new HashSet<Set<ArrivalBoundMethod>>();
		ab_sets_fifo.add(single_1);
		ab_sets_fifo.add(single_2);
		ab_sets_fifo.add(pair_1);
		
		return createParameters(Collections.singleton(Multiplexing.FIFO), ab_sets_fifo).stream().map(Arguments::of);
	}
	
	protected static Stream<Arguments> provideSinkTreeArguments() {
		Set<Set<ArrivalBoundMethod>> ab_sets_sinktree = new HashSet<Set<ArrivalBoundMethod>>();
		ab_sets_sinktree.add(sinktree);
		
		return createParameters(Collections.singleton(Multiplexing.ARBITRARY), ab_sets_sinktree).stream().map(Arguments::of);
	}

	private static Set<DncTestConfig> createParameters(Set<Multiplexing> mux_disciplines, Set<Set<ArrivalBoundMethod>> ab_sets) {
		Set<DncTestConfig> test_configurations = new HashSet<DncTestConfig>();

		Set<NumBackend> nums = new HashSet<NumBackend>();
		nums.add(NumBackend.REAL_DOUBLE_PRECISION);
		nums.add(NumBackend.REAL_SINGLE_PRECISION);
		nums.add(NumBackend.RATIONAL_INTEGER);
		nums.add(NumBackend.RATIONAL_BIGINTEGER);

		Set<AlgDncBackend> curves = new HashSet<AlgDncBackend>();
		curves.add(AlgDncBackend_DNC_ConPwAffine.DISCO_CONPWAFFINE);
		curves.add(AlgDncBackend_DNC_Affine.DISCO_AFFINE);

		curves.add(AlgDncBackend_DNC_PwAffineC_Affine.DISCO_PWAFFINEC_AFFINEMP);
		
		curves.add(AlgDncBackend_MPARTC_PwAffine.MPARTC_PWAFFINE);
		curves.add(AlgDncBackend_MPARTC_DISCO_Affine.MPARTC_PWAFFINEC_DISCO_AFFINEMP);
		curves.add(AlgDncBackend_MPARTC_DISCO_PwAffine.MPARTC_PWAFFINEC_DISCO_CONPWAFFINEMP);

		// Parameter configurations for single arrival bounding tests:
		// 		AB, convolve alternative ABs, global mux def, number class to use, curve class to use.
		for (AlgDncBackend curve : curves) {
			for (NumBackend num : nums) {
				for (Set<ArrivalBoundMethod> ab : ab_sets) {
					for (Multiplexing mux : mux_disciplines) {
						test_configurations.add(new DncTestConfig(ab, false, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(ab, true, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(ab, false, mux, true, num, curve));
						test_configurations.add(new DncTestConfig(ab, true, mux, true, num, curve));
					}
				}
			}
		}

		return test_configurations;
	}
}

enum AlgDncBackend_DNC_PwAffineC_Affine implements AlgDncBackend {
	DISCO_PWAFFINEC_AFFINEMP;

	@Override
	public MinPlus getMinPlus() {
		return MinPlus_Disco_Affine.MINPLUS_DISCO_AFFINE;
	}

	@Override
	public BoundingCurves getBoundingCurves() {
		return BoundingCurves_Disco_ConPwAffine.BOUNDINGCURVES_DISCO_CONPWAFFINE;
	}

	@Override
	public Bounds getBounds() {
		return Bounds_Disco_PwAffine.BOUNDS_DISCO_PWAFFINE;
	}

	@Override
	public Curve getCurveFactory() {
		return Curve_Disco_PwAffine.getFactory();
	}

	@Override
	public LinearSegment.Builder getLinearSegmentFactory() {
		return LinearSegment_Disco.getBuilder();
	}

    @Override
    public String toString() {
    	return assembleString(this.name(), MinPlus_Disco_ConPwAffine.MINPLUS_DISCO_CONPWAFFINE.name());
    }
}
