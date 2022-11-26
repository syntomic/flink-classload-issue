# Pyflink ClassLoad Issue

- Pyflink `stream_execution_environment.add_classpaths` add remote jars error, raise `java.lang.ClassNotFoundException`
    - [Demo](add/src/main/python/com/syntomic/issues/add/add.py)

- While Flink in java whit similar implements can work
    - [Demo](add/src/main/java/com/syntomic/issues/add/Add.java)


- I guess there is something bug in `JavaGateway`