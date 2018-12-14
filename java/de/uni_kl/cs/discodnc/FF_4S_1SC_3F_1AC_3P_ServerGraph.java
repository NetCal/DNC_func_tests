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

import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.network.server_graph.Flow;
import de.uni_kl.cs.discodnc.network.server_graph.Server;
import de.uni_kl.cs.discodnc.network.server_graph.ServerGraph;
import de.uni_kl.cs.discodnc.network.server_graph.ServerGraphFactory;
import de.uni_kl.cs.discodnc.network.server_graph.Turn;

import java.util.LinkedList;
import java.util.List;

public class FF_4S_1SC_3F_1AC_3P_ServerGraph implements ServerGraphFactory {
	private final int sc_R = 20;
	private final int sc_T = 20;
	private final int ac_r = 5;
	private final int ac_b = 25;
	
	private Server s0, s1, s2, s3;
	private Turn t_s0_s1, t_s1_s3;
	
	private ServiceCurve service_curve = Curve.getFactory().createRateLatency(sc_R, sc_T);
	private ArrivalCurve arrival_curve = Curve.getFactory().createTokenBucket(ac_r, ac_b);
	
	private ServerGraph server_graph;

	public FF_4S_1SC_3F_1AC_3P_ServerGraph() {
		server_graph = createServerGraph();
	}

	public ServerGraph getServerGraph() {
		return server_graph;
	}

	public ServerGraph createServerGraph() {
		server_graph = new ServerGraph();

		s0 = server_graph.addServer(service_curve);
		s1 = server_graph.addServer(service_curve);
		s2 = server_graph.addServer(service_curve);
		s3 = server_graph.addServer(service_curve);

		try {
			t_s0_s1 = server_graph.addTurn(s0, s1);
			server_graph.addTurn(s0, s3);
			t_s1_s3 = server_graph.addTurn(s1, s3);
			server_graph.addTurn(s2, s0);
			server_graph.addTurn(s2, s1);
			server_graph.addTurn(s2, s3);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		List<Turn> f0_path = new LinkedList<Turn>();
		f0_path.add(t_s0_s1);
		f0_path.add(t_s1_s3);

		try {
			server_graph.addFlow(arrival_curve, f0_path); // f0
			server_graph.addFlow(arrival_curve, s2, s3);  // f1
			server_graph.addFlow(arrival_curve, s2, s1);  // f1
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return server_graph;
	}

	public void reinitializeCurves() {
		service_curve = Curve.getFactory().createRateLatency(sc_R, sc_T);
		for (Server server : server_graph.getServers()) {
			server.setServiceCurve(service_curve);
		}

		arrival_curve = Curve.getFactory().createTokenBucket(ac_r, ac_b);
		for (Flow flow : server_graph.getFlows()) {
			flow.setArrivalCurve(arrival_curve);
		}
	}
}