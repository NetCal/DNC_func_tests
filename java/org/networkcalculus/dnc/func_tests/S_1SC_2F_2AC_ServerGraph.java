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

import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.network.server_graph.ServerGraphFactory;

public class S_1SC_2F_2AC_ServerGraph implements ServerGraphFactory {
	private final int sc_R = 10;
	private final int sc_T = 10;
	private final int ac_r_0 = 4;
	private final int ac_b_0 = 10;
	private final int ac_r_1 = 5;
	private final int ac_b_1 = 25;

	private Server s0;
	private Flow f0, f1;

	private ServiceCurve service_curve = Curve.getFactory().createRateLatency(sc_R, sc_T);
	private ArrivalCurve arrival_curve_0 = Curve.getFactory().createTokenBucket(ac_r_0, ac_b_0);
	private ArrivalCurve arrival_curve_1 = Curve.getFactory().createTokenBucket(ac_r_1, ac_b_1);

	private ServerGraph server_graph;

	public S_1SC_2F_2AC_ServerGraph() {
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
			f0 = server_graph.addFlow("f0", arrival_curve_0, s0);
			f1 = server_graph.addFlow("f1", arrival_curve_1, s0);
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

		arrival_curve_0 = Curve.getFactory().createTokenBucket(ac_r_0, ac_b_0);
		f0.setArrivalCurve(arrival_curve_0);

		arrival_curve_1 = Curve.getFactory().createTokenBucket(ac_r_1, ac_b_1);
		f1.setArrivalCurve(arrival_curve_1);
	}
}
