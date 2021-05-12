package it.polito.tdp.extflightdelays.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	private SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	private Map<Integer, Airport> idMap; // id degli Airport è un int
	
	public Model() {
		dao = new ExtFlightDelaysDAO();
		idMap = new HashMap<Integer, Airport>();
		dao.loadAllAirports(idMap);
	}
	
	// tutte le volte che lo user clicca su Analizza aeroporti, creo da 0 un nuovo grafo
	// lo metto qui e non nel costruttore del Model per questo motivo
	public void creaGrafo(int minCompagnie) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		// dobbiamo filtrare gli aeroporti per prendere quelli che ci servono
		// il filtro sui vertici è dato dal numero minimo di compagnie
		// AGGIUNGO VERTICI FILTRATI
		Graphs.addAllVertices(grafo, dao.getVertici(idMap, minCompagnie));
		
		// AGGIUNGO GLI ARCHI
		for (Rotta r : dao.getRotte(idMap)) {
			// devo controllare se mi interessa considerare la rotta nella mappa, a seconda del mio input
			if (this.grafo.containsVertex(r.getA1()) && this.grafo.containsVertex(r.getA2())) {
				// ritorna l'arco indipendentemente dall'ordine con cui passo: NON ORIENTATO
				DefaultWeightedEdge e = this.grafo.getEdge(r.getA1(), r.getA2());
				if (e == null) {
					Graphs.addEdgeWithVertices(grafo, r.getA1(), r.getA2(), r.getnVoli());
				} else {
					// anche se sono int, metto double perché setEdgeWeight vuole parametri di tipo double
					double pesoVecchio = this.grafo.getEdgeWeight(e);
					double pesoNuovo = pesoVecchio + r.getnVoli();
					grafo.setEdgeWeight(e, pesoNuovo);
				}
			}
		}		
		System.out.println("# Vertici: " + grafo.vertexSet().size());
		System.out.println("# Archi: " + grafo.edgeSet().size());
	}

	public Set<Airport> getVertici() {
		return this.grafo.vertexSet();
	}

	public Set<DefaultWeightedEdge> getNArchi() {
		// TODO Auto-generated method stub
		return this.grafo.edgeSet();
	}

}
