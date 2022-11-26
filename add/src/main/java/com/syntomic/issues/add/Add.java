package com.syntomic.issues.add;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.shaded.guava30.com.google.common.collect.ObjectArrays;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class Add {

	public static void main(String[] args) throws Exception {

        String jarUrl = "https://raw.githubusercontent.com/syntomic/pyflink-classload-issue/main/jars/udf.jar";

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(addClasspaths(jarUrl));
        final StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        tableEnv.executeSql("CREATE TABLE DemoTable (input String) WITH ('connector' = 'source-v1')");
        tableEnv.executeSql("CREATE TEMPORARY FUNCTION IF NOT EXISTS chat AS 'com.syntomic.issues.added.AddedUDF' LANGUAGE JAVA");
        tableEnv.executeSql("SELECT chat(input) FROM DemoTable").print();
	}

    /**
     * Adds a list of URLs that are added to the classpath of each user code classloader of the
     * program. Paths must specify a protocol (e.g. file://) and be accessible on all nodes
     * 
     * @param jarUrls Classpaths that will be added.
     * @throws Exception
     */
    private static Configuration addClasspaths(String... jarUrls) throws Exception {
        addJarsToContextClassLoader(jarUrls);

        Configuration conf = new Configuration();
        conf.set(PipelineOptions.CLASSPATHS, Arrays.asList(jarUrls));

        return conf;
    }

    /**
     * Add jars to Python gateway server for local compilation and local execution (i.e. minicluster).
     * There are many component in Flink which won't be added to classpath by default. e.g. Kafka
     * connector, JDBC connector, CSV format etc. This utility function can be used to hot load the
     * jars.
     * @param jarUrls The list of jar urls.
     */
    private static void addJarsToContextClassLoader(String[] jarUrls) throws Exception {

        URL[] extraUrls =
                    Stream.of(jarUrls)
                            .map(
                                    url -> {
                                        try {
                                            return new URL(url);
                                        } catch (MalformedURLException e) {
                                            throw new IllegalArgumentException(e);
                                        }
                                    })
                            .toArray(URL[]::new);

        URLClassLoader contextClassLoader =
                (URLClassLoader) Thread.currentThread().getContextClassLoader();

        URL[] existingUrls = contextClassLoader.getURLs();

        // cluster run
        if ("org.apache.flink.runtime.execution.librarycache.FlinkUserCodeClassLoaders$SafetyNetWrapperClassLoader"
                .equals(contextClassLoader.getClass().getName())) {
            Method ensureInner = contextClassLoader.getClass().getDeclaredMethod("ensureInner");
            ensureInner.setAccessible(true);
            contextClassLoader = (URLClassLoader) ensureInner.invoke(contextClassLoader);

            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);

            for (URL url : extraUrls) {
                addURL.invoke(contextClassLoader, url);
            }
        } else {
            // local run on oracle jave 8: sun.misc.Launcher$AppClassLoader
            URLClassLoader newClassLoader =
                    new URLClassLoader(
                            ObjectArrays.concat(existingUrls, extraUrls, URL.class),
                            contextClassLoader);

            Thread.currentThread().setContextClassLoader(newClassLoader);
        }
    }
}
