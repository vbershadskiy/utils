package com.vbersh.dbs;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.config.processlistener.ProcessListenerBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.IStreamProcessor;
import de.flapdoodle.embed.process.io.NullProcessor;
import de.flapdoodle.embed.process.runtime.Network;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo
 * http://docs.mongodb.org/ecosystem/drivers/java/
 * http://docs.mongodb.org/manual/reference/sql-comparison/
 * http://www.unityjdbc.com/mongojdbc/mongosqltranslate.php
 * http://docs.mongodb.org/manual/core/index-multikey/
 *
 */
public class MongoSample {

    public static void main(String[] args) throws Exception {

        // 1. start mongo
        IStreamProcessor stream = new NullProcessor();
        MongodStarter runtime = MongodStarter.getInstance(new RuntimeConfigBuilder()
                .defaults(Command.MongoD)
                .processOutput(new ProcessOutput(stream, stream, stream))
                .artifactStore(new ArtifactStoreBuilder()
                        .defaults(Command.MongoD)
                        .executableNaming(new UUIDTempNaming())
                        .build())
                .build());
        MongodExecutable mongodExecutable = runtime.prepare(new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .processListener(new ProcessListenerBuilder()
                        .copyFilesIntoDbDirBeforeStarFrom(new File("db/mongodb"))
                        .copyDbFilesBeforeStopInto(new File("db/mongodb"))
                        .build())
                .net(new Net(27017, Network.localhostIsIPv6()))
                .build());

        MongodProcess mongod = mongodExecutable.start();

        // 2. connect client to mongo
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);

            // 3. run query

            DB db = mongoClient.getDB("sampledb");

            // db.createCollection("digits")
            DBCollection store = db.getCollection("digits");

            System.out.println(store.getCount());

            testArrayIndex(store);

/*            for (String number : numbers) {
                Map<String, Object> data = new HashMap<>();
                data.put("name", number);
                store.save(new BasicDBObject(data));
            }*/

            System.out.println(store.getCount());

            // db.digits.remove( { } )
            store.remove(new BasicDBObject());
        } finally {
            // 4, stop mongo
            if (mongod != null) {
                mongod.stop();
            }
            if (mongodExecutable != null) {
                mongodExecutable.stop();
            }
        }

    }

    private static void testArrayIndex(DBCollection store) {
        // db.digits.ensureIndex( { names: 1 } )
        store.createIndex(new BasicDBObject("names", 1));

        // db.digits.save({'names': ['one']})
        store.save(new BasicDBObject("names", Arrays.asList("one")));

        // db.digits.save({'names': ['one','two']})
        store.save(new BasicDBObject("names", Arrays.asList("one", "two")));

        // db.digits.save({'names': ['one','two','three']})
        store.save(new BasicDBObject("names", Arrays.asList("one", "two", "three")));

        // db.digits.find({'names': 'one','two'})
        // matches the document that has exactly "one","two", no less, no more, and in order
        DBCursor cursor = store.find(new BasicDBObject("names", new String[]{"one", "two"}));

        while(cursor.hasNext() ) {
            DBObject o = cursor.next();
            for(String key : o.keySet() ) {
                System.out.println(o.get(key));
            }
        }
    }

}
