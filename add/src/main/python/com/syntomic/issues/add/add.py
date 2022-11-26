from pyflink.datastream import StreamExecutionEnvironment
from pyflink.table import StreamTableEnvironment

def main():

    env = StreamExecutionEnvironment.get_execution_environment()
    env.add_classpaths("https://raw.githubusercontent.com/syntomic/pyflink-classload-issue/main/jars/udf.jar")
    table_env = StreamTableEnvironment.create(env)

    table_env.execute_sql("CREATE TABLE DemoTable (input String) WITH ('connector' = 'source-v1')")
    table_env.executeSql("CREATE TEMPORARY FUNCTION IF NOT EXISTS chat AS 'com.syntomic.issues.added.AddedUDF' LANGUAGE JAVA");
    table_env.execute_sql("SELECT chat(input) FROM DemoTable").print()

if __name__ == "__main__":
    main()
