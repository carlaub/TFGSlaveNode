package neo4j;

import org.neo4j.graphdb.*;

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

	public ResultQuery processQuery(String query) {
		try (Transaction q = graphDatabaseService.beginTx();
			 Result result = graphDatabaseService.execute(query)) {

			List<String> columnNames = result.columns();
			int columnsCount = columnNames.size();
			ResultQuery resultQuery = new ResultQuery((String[])result.columns().toArray());

			while (result.hasNext()) {
				Map<String, Object> next = result.next();

				for (int i = 0; i < columnsCount; i++) {
					Object o = next.get(resultQuery.getColumnsName()[i]);

					if (o instanceof Node) {
						Node node = (Node) o;

						ResultNode resultNode = new ResultNode();

						Iterable<String> properties = node.getPropertyKeys();
						Iterable<Label> labels = node.getLabels();

						for (String propertyKey : properties)
							resultNode.addProperty(propertyKey, node.getProperty(propertyKey));

						for (Label label : labels) resultNode.addLabel(label.name());

						resultQuery.addEntity(i, resultNode);


					} else if (o instanceof Relationship) {
						// Is Relation
						System.out.println("Is relation??");
						Relationship relationship = (Relationship) o;
						ResultRelation resultRelation = new ResultRelation();

						Iterable<String> properties = relationship.getPropertyKeys();

						for (String propertyKey : properties) {
							resultRelation.addProperty(propertyKey, relationship.getProperty(propertyKey));
						}

						resultRelation.setStartNodeId(relationship.getStartNodeId());
						resultRelation.setEndNodeId(relationship.getEndNodeId());

						resultQuery.addEntity(i, resultRelation);

					}
				}
			}

			// Important to avoid unwanted behaviour, such as leaking transactions
			result.close();

			return resultQuery;
		}
	}
}
