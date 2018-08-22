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
import de.uni_kl.cs.discodnc.server_graph.Flow;
import de.uni_kl.cs.discodnc.server_graph.ServerGraph;
import de.uni_kl.cs.discodnc.server_graph.ServerGraphFactory;
import de.uni_kl.cs.discodnc.server_graph.Server;

public class S_1SC_1F_1AC_ServerGraph implements ServerGraphFactory {
	private final int sc_R = 10;
	private final int sc_T = 10;
	private final int ac_r = 5;
	private final int ac_b = 25;
	
	private Server s0;
	
	private ServiceCurve service_curve = Curve.getFactory().createRateLatency(sc_R, sc_T);
	private ArrivalCurve arrival_curve = Curve.getFactory().createTokenBucket(ac_r, ac_b);
	
	private ServerGraph network;

	public S_1SC_1F_1AC_ServerGraph() {
		network = createNetwork();
	}

	public ServerGraph getNetwork() {
		return network;
	}

	public ServerGraph createNetwork() {
		network = new ServerGraph();

		s0 = network.addServer(service_curve);
		s0.setUseGamma(false);
		s0.setUseExtraGamma(false);

		try {
			network.addFlow("f0", arrival_curve, s0);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return network;
	}

	public void reinitializeCurves() {
		service_curve = Curve.getFactory().createRateLatency(sc_R, sc_T);
		for (Server server : network.getServers()) {
			server.setServiceCurve(service_curve);
		}

		arrival_curve = Curve.getFactory().createTokenBucket(ac_r, ac_b);
		for (Flow flow : network.getFlows()) {
			flow.setArrivalCurve(arrival_curve);
		}
	}
}
