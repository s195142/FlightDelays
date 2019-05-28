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
		aIdMap = new HashMap<Integer, Airport>();
		visita = new HashMap<Airport, Airport>();
	}
	
	public void creaGrafo(int distanzaMedia) {
		ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
		dao.loadAllAirports(aIdMap);
		
		//aggiungo vertici
		Graphs.addAllVertices(grafo, aIdMap.values());
		
		for(Rotta rotta : dao.getRotte(aIdMap, distanzaMedia)) {
			//controllo se esiste gia' un arco tra i due. indipendentemente da sorgente e dest
			//se esiste aggiorno il peso
			DefaultWeightedEdge edge = grafo.getEdge(rotta.getSource(), rotta.getDest());
			if(edge==null) {
				Graphs.addEdge(grafo, rotta.getSource(), rotta.getDest(), rotta.getAvg());
			}else {
				double peso = grafo.getEdgeWeight(edge);
				double newPeso = (peso + rotta.getAvg())/2;
				System.out.println("Aggiornare peso. Peso vecchio: "+peso+", peso nuovo: "+newPeso);
				grafo.setEdgeWeight(edge, newPeso);
				
			}
			
		}
		
		System.out.println("Grafo creato!");
		System.out.println("Vertici: "+grafo.vertexSet().size());
		System.out.println("Archi: "+grafo.edgeSet().size());
	}
	
	public Boolean testConnessione(Integer a1, Integer a2) {
		Set<Airport> visitati = new HashSet<Airport>();
		Airport part = aIdMap.get(a1);
		Airport dest = aIdMap.get(a2);
		System.out.println("Testo connessione tra "+part+" e "+dest);
		BreadthFirstIterator<Airport, DefaultWeightedEdge> it = new BreadthFirstIterator<>(this.grafo, part);
		
		while(it.hasNext()) {
			visitati.add(it.next());
		}
		
		if(visitati.contains(dest)) {
			return true;
		}else {
			return false;
		}

	}
	
	public List<Airport> trovaPercorso(Integer a1, Integer a2){
		List<Airport> percorso = new ArrayList<Airport>();
		Airport part = aIdMap.get(a1);
		Airport dest = aIdMap.get(a2);
		System.out.println("Cerco percorso tra "+part+" e "+dest);
		BreadthFirstIterator<Airport, DefaultWeightedEdge> it = new BreadthFirstIterator<>(this.grafo, part);

		visita.put(part, null);
		
		it.addTraversalListener(new TraversalListener<Airport, DefaultWeightedEdge>(){

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultWeightedEdge> ev) {
				Airport sorgente = grafo.getEdgeSource(ev.getEdge());
				Airport destinazione = grafo.getEdgeTarget(ev.getEdge());
				
				if(!visita.containsKey(destinazione) && visita.containsKey(sorgente)) {
					visita.put(destinazione, sorgente);
					
				}else if(!visita.containsKey(sorgente) && visita.containsKey(destinazione)){
					visita.put(sorgente, destinazione);
				}
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Airport> arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Airport> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		while(it.hasNext()) {
			it.next();
		}
		
		
		if(!visita.containsKey(part) || !visita.containsKey(dest)) {
			return null;
		}
		
		Airport step = dest;
		while(!step.equals(part)) {
			percorso.add(step);
			step = visita.get(step);
		}
		
		return percorso; 
	}
	

}
