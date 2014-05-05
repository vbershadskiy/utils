package com.vbersh.dbs;

import com.mongodb.*;
import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.*;
import de.flapdoodle.embed.mongo.config.processlistener.ProcessListenerBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.extract.ITempNaming;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.IStreamProcessor;
import de.flapdoodle.embed.process.io.NullProcessor;
import de.flapdoodle.embed.process.io.directories.FixedPath;
import de.flapdoodle.embed.process.io.directories.IDirectory;
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

            DB db = mongoClient.getDB("sampledb");

            DBCollection store = db.getCollection("Fruits");

            // 3. run query
            System.out.println(store.getCount());

            List<String> numbers = Arrays.asList("one", "two", "three", "four", "five", "six", "seven");

            for (String number : numbers) {
                Map<String, Object> data = new HashMap<>();
                data.put("name", number);
                store.save(new BasicDBObject(data));
            }

            System.out.println(store.getCount());
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


}
