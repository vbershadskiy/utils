package com.vbersh.dbs;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

public class Neo4jSample {

    enum RelTypes implements RelationshipType  {
        CONTAINED_IN, KNOWS
    }

    public static void main(String[] args) throws ClassNotFoundException {

        GraphDatabaseService graphDb = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder( "db/neo4jsample.db" )
                .setConfig( GraphDatabaseSettings.nodestore_mapped_memory_size, "10M" )
                .setConfig(GraphDatabaseSettings.string_block_size, "60")
                .setConfig(GraphDatabaseSettings.array_block_size, "300")
                .newGraphDatabase();

        registerShutdownHook(graphDb);

        /**
         * All operations have to be performed in a transaction. This is a conscious design decision,
         * since we believe transaction demarcation to be an important part of working with a real
         * enterprise database.
         */
        try (Transaction tx = graphDb.beginTx()) {
            Node firstNode = graphDb.createNode();
            firstNode.setProperty( "message", "Hello, " );
            Node secondNode = graphDb.createNode();
            secondNode.setProperty( "message", "World!" );

            Relationship relationship = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
            relationship.setProperty( "message", "brave Neo4j " );

            for (Node node : GlobalGraphOperations.at(graphDb).getAllNodes()) {
                System.out.println(node.getProperty("message"));
                for(Relationship rel : node.getRelationships()) {
                    System.out.println(rel.getProperty("message"));
                    rel.delete();
                }
                node.delete();
            }
            
            tx.success();
        } 
    }

    private static void registerShutdownHook( final GraphDatabaseService graphDb ) {
        Runtime.getRuntime().addShutdownHook( new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        } );
    }
}
