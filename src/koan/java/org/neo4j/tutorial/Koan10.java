package org.neo4j.tutorial;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.neo4j.helpers.collection.IteratorUtil.asIterable;
import static org.neo4j.tutorial.matchers.ContainsOnlySpecificTitles.containsOnlyTitles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.cypher.commands.Query;
import org.neo4j.cypher.parser.CypherParser;
import org.neo4j.graphdb.Node;

/**
 * In this Koan we use the Cypher graph pattern matching language to investigate
 * the history of the Dalek props.
 */
public class Koan10 {
	private static EmbeddedDoctorWhoUniverse universe;

	@BeforeClass
	public static void createDatabase() throws Exception {
		universe = new EmbeddedDoctorWhoUniverse();
	}

	@AfterClass
	public static void closeTheDatabase() {
		universe.stop();
	}

	@Test
	public void shouldFindAllTheEpisodesInWhichTheDaleksAppeared() {
		CypherParser parser = new CypherParser();
		ExecutionEngine engine = new ExecutionEngine(universe.getDatabase());
		String cql = null;

		// YOUR CODE GOES HERE
		// SNIPPET_START

		cql = "START daleks=(Species,species,\"Dalek\") MATCH (daleks)-[:APPEARED_IN]->(episode) RETURN episode";

		// SNIPPET_END

		Query query = parser.parse(cql);
		ExecutionResult result = engine.execute(query);
		Iterator<Node> episodes = result.javaColumnAs("episode");

		assertThat(asIterable(episodes),
				containsOnlyTitles(
						"The Pandorica Opens",
						"Victory of the Daleks",
						"Journey's End",
						"The Stolen Earth",
						"Evolution of the Daleks",
						"Daleks in Manhattan",
						"Doomsday",
						"Army of Ghosts",
						"The Parting of the Ways",
						"Bad Wolf",
						"Dalek",
						"Remembrance of the Daleks",
						"Revelation of the Daleks",
						"Resurrection of the Daleks",
						"Destiny of the Daleks",
						"Genesis of the Daleks",
						"Death to the Daleks",
						"Planet of the Daleks",
						"The Evil of the Daleks",
						"The Power of the Daleks",
						"The Daleks' Master Plan",
						"The Chase",
						"The Dalek Invasion of Earth",
						"The Daleks"));

	}

	@Test
	public void shouldFindTheTop3HardestWorkingPropPartsInShowbiz() {
		CypherParser parser = new CypherParser();
		ExecutionEngine engine = new ExecutionEngine(universe.getDatabase());
		String cql = null;

		// YOUR CODE GOES HERE
		// SNIPPET_START

		cql = "START daleks=(Species,species,\"Dalek\")"
				+ " MATCH (daleks)-[:APPEARED_IN]->(episode)"
				+ "<-[:USED_IN]-(props)"
				+ "<-[:MEMBER_OF]-(prop)"
				+ "-[:COMPOSED_OF]->(part)"
				+ "-[:ORIGINAL_PROP]->(originalprop)"
				+ " RETURN originalprop.prop, part.part, COUNT(*)"
				+ " ORDER BY COUNT(*) DESC"
				+ " LIMIT 3";

		// SNIPPET_END

		Query query = parser.parse(cql);
		ExecutionResult result = engine.execute(query);
		Iterator<Map<String, Object>> stats = result.javaIterator();

		Collection<PropPartUsageInfo> expectedPropPartStats = new ArrayList<PropPartUsageInfo>();
		expectedPropPartStats.add(new PropPartUsageInfo("Dalek 1", "shoulder", 11));
		expectedPropPartStats.add(new PropPartUsageInfo("Dalek 5", "skirt", 11));
		expectedPropPartStats.add(new PropPartUsageInfo("Dalek 6", "shoulder", 11));
		Iterator<PropPartUsageInfo> expectedStats = expectedPropPartStats.iterator();

		while (stats.hasNext()) {
			Map<String, Object> stat = stats.next();
			PropPartUsageInfo expectedStat = expectedStats.next();
			assertEquals(expectedStat.getOriginalProp(), stat.get("originalprop.prop"));
			assertEquals(expectedStat.getPart(), stat.get("part.part"));
			assertEquals(expectedStat.getCount(), stat.get("count(*)"));
		}

	}

	private class PropPartUsageInfo {
		private final String originalProp;
		private final String part;
		private final int count;

		public PropPartUsageInfo(String originalProp, String part, int count) {
			super();
			this.originalProp = originalProp;
			this.part = part;
			this.count = count;
		}

		public String getOriginalProp() {
			return originalProp;
		}

		public String getPart() {
			return part;
		}

		public int getCount() {
			return count;
		}
	}
}
