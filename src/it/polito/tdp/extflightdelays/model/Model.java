package it.polito.tdp.extflightdelays.model;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	Map<Integer, Airport> aIdMap;
	
	ExtFlightDelaysDAO dao;
	
	public Model() {
		grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		aIdMap = new HashMap<Integer, Airport>();
		dao = new ExtFlightDelaysDAO();
	}
	
	public void creaGrafo() {
		dao.loadAllAirports(aIdMap);
		
		Graphs.addAllVertices(grafo, aIdMap.values());
		System.out.println(grafo.vertexSet().size());
		
		
	}

}
