package neo4j;

import org.neo4j.graphdb.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Carla Urrea Blázquez on 06/06/2018.
 *
 * QueryExecutor.java
 */
public class QueryExecutor {
	private static QueryExecutor instance;
	private GraphDatabaseService graphDatabaseService;

	public static QueryExecutor getInstace() {
		if (instance == null) instance = new QueryExecutor();
		return instance;
	}

	private QueryExecutor() {
		graphDatabaseService = GraphDatabase.getInstance().getDataBaseGraphService();
	}

	public List<ResultEntity> processQuery(String query) {
		try (Transaction q = graphDatabaseService.beginTx();
			 Result result = graphDatabaseService.execute(query)) {

//			System.out.println(result.resultAsString());

			List<ResultEntity> list = new ArrayList<>();

			System.out.println("HAS NEXT: " + result.hasNext());
			System.out.println("Columnas: " + result.columns().size());

			while (result.hasNext()) {
				Map<String, Object> next = result.next();

				Node node = (Node)next.get("n");
				if (node != null) {
					ResultNode resultNode = new ResultNode();

					Iterable<String> properties = node.getPropertyKeys();
					Iterable<Label> labels = node.getLabels();

					for (String propertyKey : properties) resultNode.addProperty(propertyKey, node.getProperty(propertyKey));

					for (Label label : labels) resultNode.addLabel(label.name());

					list.add(resultNode);
				} else {
					// Is Relation
					System.out.println("Is relation??");
					Relationship relationship = (Relationship) next.get("r");
					if (relationship != null) {
						ResultRelation resultRelation = new ResultRelation();

						Iterable<String> properties = relationship.getPropertyKeys();

						for (String propertyKey : properties) {
							resultRelation.addProperty(propertyKey, relationship.getProperty(propertyKey));
						}

						resultRelation.setStartNodeId(relationship.getStartNodeId());
						resultRelation.setEndNodeId(relationship.getEndNodeId());

						list.add(resultRelation);
					}
				}
			}

			// Important to avoid unwanted behaviour, such as leaking transactions
			result.close();

			return list;
		}
	}
}
