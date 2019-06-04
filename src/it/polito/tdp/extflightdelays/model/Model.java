package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	Map<Integer, Airport> aIdMap;
	Map<Airport, Airport> visita;
	
	public Model() {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		aIdMap = new HashMap<>();
		visita = new HashMap<>();
	}
	
	public void creaGrafo(int distanzaMedia) {
		// i vertici sono tutti gli aeroporti. nel dao ho già un metodo che mi restituisce tutti gli aeroporti
		// devo strutturarlo in modo da utilizzare l'idmap.
		ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
		dao.loadAllAirports(aIdMap); // così popolo la mappa.
		
		// Aggiungo i vertici
		Graphs.addAllVertices(grafo, aIdMap.values());
		
		for(Rotta rotta : dao.getRotte(aIdMap, distanzaMedia)) {
			
			// controllo se esiste già un arco
			// se esiste aggiorno il peso
			DefaultWeightedEdge edge = grafo.getEdge(rotta.getSource(), rotta.getDestination());
			if(edge == null) {
				Graphs.addEdge(grafo, rotta.getSource(), rotta.getDestination(), rotta.getAvg());
			}else {
				double peso = grafo.getEdgeWeight(edge);
				double newpeso = (peso + rotta.getAvg())/2;
				System.out.println("Aggiornare peso! Peso vecchio: "+peso+", peso nuovo: "+newpeso);

				grafo.setEdgeWeight(edge, newpeso);
			}
			
		}
		
		System.out.println("Grafo creato! Vertici: "+grafo.vertexSet().size()+" Archi: "+grafo.edgeSet().size());
	}
	
	public boolean testConnessione(Integer a1, Integer a2) {
		Set<Airport> visitati = new HashSet<>();
		Airport partenza = aIdMap.get(a1);
		Airport destinazione = aIdMap.get(a2);
		System.out.println("Test connessione tra "+partenza+" e "+destinazione);
		BreadthFirstIterator<Airport, DefaultWeightedEdge> it = new BreadthFirstIterator<>(this.grafo, partenza);
		
		while(it.hasNext()) {
			visitati.add(it.next());
		}
		
		if(visitati.contains(destinazione)) {
			return true;
		}else {
			return false;
		}
	}
	
	
	public List<Airport> trovaPercorso(Integer a1, Integer a2){
		List<Airport> percorso = new ArrayList<Airport>();
		Airport partenza = aIdMap.get(a1);
		Airport destinazione = aIdMap.get(a2);
		System.out.println("Cerco percorso tra "+partenza+" e "+destinazione);
		
		BreadthFirstIterator<Airport, DefaultWeightedEdge> it = new BreadthFirstIterator<>(this.grafo, partenza);
		
		visita.put(partenza, null);
		it.addTraversalListener(new TraversalListener<Airport, DefaultWeightedEdge>() {
			
			@Override
			public void vertexTraversed(VertexTraversalEvent<Airport> arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void vertexFinished(VertexTraversalEvent<Airport> arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultWeightedEdge> ev) {
				Airport sorgente = grafo.getEdgeSource(ev.getEdge());
				Airport dest = grafo.getEdgeTarget(ev.getEdge());
				
				if(!visita.containsKey(dest) && visita.containsKey(sorgente)) {
					visita.put(dest, sorgente);
				}else if(!visita.containsKey(sorgente) && visita.containsKey(dest)){
					visita.put(sorgente, dest);
				}
			}
			
			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		while(it.hasNext()) {
			it.next();
		}
		
		if(!visita.containsKey(partenza) || !visita.containsKey(destinazione)) {
			return null; // gli aeroporti non sono collegati
		}
		
		Airport step = destinazione;
		while(!step.equals(partenza)) {
			percorso.add(step);
			step = visita.get(step);
		}
		percorso.add(step); //ultimo nodo, sorgente
		return percorso;
		
		
	}
	
	

}
