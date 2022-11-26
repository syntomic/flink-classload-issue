from pyflink.datastream import StreamExecutionEnvironment
from pyflink.table import StreamTableEnvironment

def main():

    env = StreamExecutionEnvironment.get_execution_environment()
    env.add_classpaths("https://raw.githubusercontent.com/syntomic/pyflink-classload-issue/master/jars/udf.jars")
    table_env = StreamTableEnvironment.create(env)


    table_env.execute_sql("CREATE TEMPORARY FUNCTION IF NOT EXISTS chat AS 'com.syntomic.issues.added.Added' LANGUAGE JAVA")
    table_env.execute_sql("SELECT chat(input) FROM (VALUES 'Nice to meet you.', 'Today is cold?') AS DemoTable(input)").print()


if __name__ == "__main__":
    main()
