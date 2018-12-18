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

package de.uni_kl.cs.discodnc.func_test;

import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.network.server_graph.Flow;
import de.uni_kl.cs.discodnc.network.server_graph.Server;
import de.uni_kl.cs.discodnc.network.server_graph.ServerGraph;
import de.uni_kl.cs.discodnc.network.server_graph.ServerGraphFactory;

public class TA_2S_2SC_2F_1AC_1P_ServerGraph implements ServerGraphFactory {
	private final int sc_R_0 = 10;
	private final int sc_T_0 = 10;
	private final int sc_R_1 = 6;
	private final int sc_T_1 = 6;
	private final double ac_r = 2.5;
	private final double ac_b = 12.5;
	
	private Server s0, s1;
	
	private ServiceCurve service_curve_0 = Curve.getFactory().createRateLatency(sc_R_0, sc_T_0);
	private ServiceCurve service_curve_1 = Curve.getFactory().createRateLatency(sc_R_1, sc_T_1);
	private ArrivalCurve arrival_curve = Curve.getFactory().createTokenBucket(ac_r, ac_b);
	
	private ServerGraph server_graph;

	public TA_2S_2SC_2F_1AC_1P_ServerGraph() {
		server_graph = createServerGraph();
	}

	public ServerGraph getServerGraph() {
		return server_graph;
	}

	public ServerGraph createServerGraph() {
		server_graph = new ServerGraph();

		s0 = server_graph.addServer(service_curve_0);
		s0.useMaxSC(false);
		s0.useMaxScRate(false);

		s1 = server_graph.addServer(service_curve_1);
		s1.useMaxSC(false);
		s1.useMaxScRate(false);

		try {
			server_graph.addTurn(s0, s1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		try {
			server_graph.addFlow("f0", arrival_curve, s0, s1);
			server_graph.addFlow("f1", arrival_curve, s0, s1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return server_graph;
	}

	public void reinitializeCurves() {
		service_curve_0 = Curve.getFactory().createRateLatency(sc_R_0, sc_T_0);
		s0.setServiceCurve(service_curve_0);

		service_curve_1 = Curve.getFactory().createRateLatency(sc_R_1, sc_T_1);
		s1.setServiceCurve(service_curve_1);

		arrival_curve = Curve.getFactory().createTokenBucket(ac_r, ac_b);
		for (Flow flow : server_graph.getFlows()) {
			flow.setArrivalCurve(arrival_curve);
		}
	}
}