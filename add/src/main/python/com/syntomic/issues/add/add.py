from pyflink.datastream import StreamExecutionEnvironment
from pyflink.table import StreamTableEnvironment

def main():

    env = StreamExecutionEnvironment.get_execution_environment()
    env.add_classpaths("https://raw.githubusercontent.com/syntomic/pyflink-classload-issue/main/jars/udf.jar")
    table_env = StreamTableEnvironment.create(env)

    table_env.execute_sql("CREATE TABLE DemoTable (input String) WITH ('connector' = 'custom-source')")
    table_env.execute_sql("SELECT UPPER(input) FROM DemoTable").print()

if __name__ == "__main__":
    main()
