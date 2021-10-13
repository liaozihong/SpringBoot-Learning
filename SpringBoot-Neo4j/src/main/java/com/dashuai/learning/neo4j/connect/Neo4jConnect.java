package com.dashuai.learning.neo4j.connect;

import org.neo4j.driver.*;

import static org.neo4j.driver.Values.parameters;

public class Neo4jConnect implements AutoCloseable {
    private final Driver driver;

    public Neo4jConnect( String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    public void printGreeting( final String message )
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    Result result = tx.run( "CREATE (a:JAVA) " +
                                    "SET a.message = $message " +
                                    "RETURN a.message + ', from node ' + id(a)",
                            parameters( "message", message ) );
                    return result.single().get( 0 ).asString();
                }
            } );
            System.out.println( greeting );
        }
    }

    public static void main( String... args ) throws Exception
    {
        try ( Neo4jConnect greeter = new Neo4jConnect( "bolt://localhost:7687", "neo4j", "123456" ) )
        {
            greeter.printGreeting( "hello, world neo4j" );
        }
    }
}
