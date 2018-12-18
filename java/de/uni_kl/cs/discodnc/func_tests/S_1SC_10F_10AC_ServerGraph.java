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

import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.network.server_graph.Flow;
import de.uni_kl.cs.discodnc.network.server_graph.Server;
import de.uni_kl.cs.discodnc.network.server_graph.ServerGraph;
import de.uni_kl.cs.discodnc.network.server_graph.ServerGraphFactory;

public class S_1SC_10F_10AC_ServerGraph implements ServerGraphFactory {
	private final int sc_R = 10;
	private final int sc_T = 10;
	
	private Server s0;
	private Flow[] flows = new Flow[10];
	
	private ServiceCurve service_curve = Curve.getFactory().createRateLatency(sc_R, sc_T);
	private ArrivalCurve[] arrival_curves = new ArrivalCurve[10];
	
	private ServerGraph server_graph;

	public S_1SC_10F_10AC_ServerGraph() {
		server_graph = createServerGraph();
	}

	public ServerGraph getServerGraph() {
		return server_graph;
	}

	public ServerGraph createServerGraph() {
		server_graph = new ServerGraph();

		s0 = server_graph.addServer(service_curve);
		s0.useMaxSC(false);
		s0.useMaxScRate(false);

		try {
			for (int i = 1; i <= 10; i++) {
				arrival_curves[i - 1] = Curve.getFactory().createTokenBucket(i * 0.1, i);
				flows[i - 1] = server_graph.addFlow( "f" + Integer.toString(i-1) , arrival_curves[i - 1], s0);
			}
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

		for (int i = 1; i <= 10; i++) {
			arrival_curves[i - 1] = Curve.getFactory().createTokenBucket(i * 0.1, i);
			flows[i - 1].setArrivalCurve(arrival_curves[i - 1]);
		}
	}
}
