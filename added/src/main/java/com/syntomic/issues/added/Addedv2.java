package com.syntomic.issues.added;

import java.util.Collections;
import java.util.Set;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.connector.source.ScanTableSource;
import org.apache.flink.table.connector.source.SourceFunctionProvider;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.StringData;
import org.apache.flink.table.factories.DynamicTableSourceFactory;

public class Addedv2 implements DynamicTableSourceFactory {

    @Override
    public String factoryIdentifier() {
        return "custom-source";
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        return Collections.emptySet();
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        return Collections.emptySet();
    }

    @Override
    public DynamicTableSource createDynamicTableSource(Context context) {
        return new CustomFileSource();
    }
    

    private static class CustomFileSource implements ScanTableSource {

        @Override
        public DynamicTableSource copy() {
            return new CustomFileSource();
        }

        @Override
        public String asSummaryString() {
            return  "Custom Table Source";
        }

        @Override
        public ChangelogMode getChangelogMode() {
            return ChangelogMode.insertOnly();
        }
    
        @Override
        public ScanRuntimeProvider getScanRuntimeProvider(ScanContext runtimeProviderContext) {
    
            final SourceFunction<RowData> sourceFunction =
                    new CustomFileTableSourceFunction();
    
            return SourceFunctionProvider.of(sourceFunction, false);
        }
        
    }

    private static class CustomFileTableSourceFunction extends RichSourceFunction<RowData> {

        @Override
        public void run(SourceContext<RowData> ctx) throws Exception {
            ctx.collect(GenericRowData.of(StringData.fromString("Nice to meet you!")));
        }

        @Override
        public void cancel() {
        }
        
    }
}
