# Flink ClassLoad Issue

- Followed Pyflink's(1.16) Method `stream_execution_environment.add_classpaths` to implements add remote jars(http link) to classpath in Flink(Java) trigger a weird bug, both in Flink and PyFlink:
```
+----+--------------------------------+
| op |                         EXPR$0 |
/* 1 */public final class GeneratedCastExecutor$7 implements org.apache.flink.table.data.utils.CastExecutor {
/* 2 */
/* 3 */public GeneratedCastExecutor$7() {
/* 4 */}
/* 5 */@Override public Object cast(Object _myInputObj) throws org.apache.flink.table.api.TableException {
/* 6 */org.apache.flink.table.data.binary.BinaryStringData _myInput = ((org.apache.flink.table.data.binary.BinaryStringData)(_myInputObj));
/* 7 */boolean _myInputIsNull = _myInputObj == null;
/* 8 */return _myInput;
/* 9 */}
/* 10 */}

Exception in thread "main" org.apache.flink.util.FlinkRuntimeException: Cast executor cannot be instantiated. This is a bug. Please file an issue. Code:
public final class GeneratedCastExecutor$7 implements org.apache.flink.table.data.utils.CastExecutor {

public GeneratedCastExecutor$7() {
}
@Override public Object cast(Object _myInputObj) throws org.apache.flink.table.api.TableException {
org.apache.flink.table.data.binary.BinaryStringData _myInput = ((org.apache.flink.table.data.binary.BinaryStringData)(_myInputObj));
boolean _myInputIsNull = _myInputObj == null;
return _myInput;
}
}
        at org.apache.flink.table.planner.functions.casting.AbstractCodeGeneratorCastRule.create(AbstractCodeGeneratorCastRule.java:167)
        at org.apache.flink.table.planner.functions.casting.CastRuleProvider.create(CastRuleProvider.java:141)
        at org.apache.flink.table.planner.functions.casting.RowDataToStringConverterImpl.init(RowDataToStringConverterImpl.java:61)
        at org.apache.flink.table.planner.functions.casting.RowDataToStringConverterImpl.convert(RowDataToStringConverterImpl.java:82)
        at org.apache.flink.table.utils.print.TableauStyle.rowFieldsToString(TableauStyle.java:167)
        at org.apache.flink.table.utils.print.TableauStyle.print(TableauStyle.java:148)
        at org.apache.flink.table.api.internal.TableResultImpl.print(TableResultImpl.java:153)
        at com.syntomic.issues.add.Add.main(Add.java:26)
Caused by: org.apache.flink.util.FlinkRuntimeException: org.apache.flink.api.common.InvalidProgramException: Table program cannot be compiled. This is a bug. Please file an issue.
        at org.apache.flink.table.runtime.generated.CompileUtils.compile(CompileUtils.java:94)
        at org.apache.flink.table.planner.functions.casting.AbstractCodeGeneratorCastRule.create(AbstractCodeGeneratorCastRule.java:160)
        ... 7 more
Caused by: org.apache.flink.shaded.guava30.com.google.common.util.concurrent.UncheckedExecutionException: org.apache.flink.api.common.InvalidProgramException: Table program cannot be compiled. This is a bug. Please file an issue.
        at org.apache.flink.shaded.guava30.com.google.common.cache.LocalCache$Segment.get(LocalCache.java:2051)
        at org.apache.flink.shaded.guava30.com.google.common.cache.LocalCache.get(LocalCache.java:3962)
        at org.apache.flink.shaded.guava30.com.google.common.cache.LocalCache$LocalManualCache.get(LocalCache.java:4859)
        at org.apache.flink.table.runtime.generated.CompileUtils.compile(CompileUtils.java:92)
        ... 8 more
Caused by: org.apache.flink.api.common.InvalidProgramException: Table program cannot be compiled. This is a bug. Please file an issue.
        at org.apache.flink.table.runtime.generated.CompileUtils.doCompile(CompileUtils.java:107)
        at org.apache.flink.table.runtime.generated.CompileUtils.lambda$compile$0(CompileUtils.java:92)
        at org.apache.flink.shaded.guava30.com.google.common.cache.LocalCache$LocalManualCache$1.load(LocalCache.java:4864)
        at org.apache.flink.shaded.guava30.com.google.common.cache.LocalCache$LoadingValueReference.loadFuture(LocalCache.java:3529)
        at org.apache.flink.shaded.guava30.com.google.common.cache.LocalCache$Segment.loadSync(LocalCache.java:2278)
        at org.apache.flink.shaded.guava30.com.google.common.cache.LocalCache$Segment.lockedGetOrLoad(LocalCache.java:2155)
        at org.apache.flink.shaded.guava30.com.google.common.cache.LocalCache$Segment.get(LocalCache.java:2045)
        ... 11 more
Caused by: org.codehaus.janino.InternalCompilerException: Compiling "GeneratedCastExecutor$7": zip file closed
        at org.codehaus.janino.UnitCompiler.compileUnit(UnitCompiler.java:382)
        at org.codehaus.janino.SimpleCompiler.cook(SimpleCompiler.java:237)
        at org.codehaus.janino.SimpleCompiler.compileToClassLoader(SimpleCompiler.java:465)
        at org.codehaus.janino.SimpleCompiler.cook(SimpleCompiler.java:216)
        at org.codehaus.janino.SimpleCompiler.cook(SimpleCompiler.java:207)
        at org.codehaus.commons.compiler.Cookable.cook(Cookable.java:80)
        at org.codehaus.commons.compiler.Cookable.cook(Cookable.java:75)
        at org.apache.flink.table.runtime.generated.CompileUtils.doCompile(CompileUtils.java:104)
        ... 17 more
Caused by: java.lang.IllegalStateException: zip file closed
        at java.util.zip.ZipFile.ensureOpen(ZipFile.java:687)
        at java.util.zip.ZipFile.getEntry(ZipFile.java:316)
        at java.util.jar.JarFile.getEntry(JarFile.java:261)
        at sun.net.www.protocol.jar.URLJarFile.getEntry(URLJarFile.java:128)
        at java.util.jar.JarFile.getJarEntry(JarFile.java:244)
        at sun.misc.URLClassPath$JarLoader.getResource(URLClassPath.java:1070)
        at sun.misc.URLClassPath.getResource(URLClassPath.java:250)
        at java.net.URLClassLoader$1.run(URLClassLoader.java:366)
        at java.net.URLClassLoader$1.run(URLClassLoader.java:363)
        at java.security.AccessController.doPrivileged(Native Method)
        at java.net.URLClassLoader.findClass(URLClassLoader.java:362)
        at java.lang.ClassLoader.loadClass(ClassLoader.java:418)
        at java.lang.ClassLoader.loadClass(ClassLoader.java:405)
        at org.apache.flink.util.FlinkUserCodeClassLoader.loadClassWithoutExceptionHandling(FlinkUserCodeClassLoader.java:67)
        at org.apache.flink.util.ChildFirstClassLoader.loadClassWithoutExceptionHandling(ChildFirstClassLoader.java:74)
        at org.apache.flink.util.FlinkUserCodeClassLoader.loadClass(FlinkUserCodeClassLoader.java:51)
        at java.lang.ClassLoader.loadClass(ClassLoader.java:351)
        at org.apache.flink.util.FlinkUserCodeClassLoaders$SafetyNetWrapperClassLoader.loadClass(FlinkUserCodeClassLoaders.java:192)
        at java.lang.Class.forName0(Native Method)
        at java.lang.Class.forName(Class.java:348)
        at org.codehaus.janino.ClassLoaderIClassLoader.findIClass(ClassLoaderIClassLoader.java:89)
        at org.codehaus.janino.IClassLoader.loadIClass(IClassLoader.java:312)
        at org.codehaus.janino.UnitCompiler.findTypeByName(UnitCompiler.java:8556)
        at org.codehaus.janino.UnitCompiler.getReferenceType(UnitCompiler.java:6749)
        at org.codehaus.janino.UnitCompiler.getReferenceType(UnitCompiler.java:6594)
        at org.codehaus.janino.UnitCompiler.getType2(UnitCompiler.java:6573)
        at org.codehaus.janino.UnitCompiler.access$13900(UnitCompiler.java:215)
        at org.codehaus.janino.UnitCompiler$22$1.visitReferenceType(UnitCompiler.java:6481)
        at org.codehaus.janino.UnitCompiler$22$1.visitReferenceType(UnitCompiler.java:6476)
        at org.codehaus.janino.Java$ReferenceType.accept(Java.java:3928)
        at org.codehaus.janino.UnitCompiler$22.visitType(UnitCompiler.java:6476)
        at org.codehaus.janino.UnitCompiler$22.visitType(UnitCompiler.java:6469)
        at org.codehaus.janino.Java$ReferenceType.accept(Java.java:3927)
        at org.codehaus.janino.UnitCompiler.getType(UnitCompiler.java:6469)
        at org.codehaus.janino.UnitCompiler.getReturnType(UnitCompiler.java:10211)
        at org.codehaus.janino.UnitCompiler.access$18600(UnitCompiler.java:215)
        at org.codehaus.janino.UnitCompiler$36.getReturnType(UnitCompiler.java:10432)
        at org.codehaus.janino.IClass$IMethod.getDescriptor2(IClass.java:1226)
        at org.codehaus.janino.IClass$IInvocable.getDescriptor(IClass.java:983)
        at org.codehaus.janino.IClass.getIMethods(IClass.java:248)
        at org.codehaus.janino.IClass.getIMethods(IClass.java:237)
        at org.codehaus.janino.UnitCompiler.compile2(UnitCompiler.java:492)
        at org.codehaus.janino.UnitCompiler.compile2(UnitCompiler.java:432)
        at org.codehaus.janino.UnitCompiler.access$400(UnitCompiler.java:215)
        at org.codehaus.janino.UnitCompiler$2.visitPackageMemberClassDeclaration(UnitCompiler.java:411)
        at org.codehaus.janino.UnitCompiler$2.visitPackageMemberClassDeclaration(UnitCompiler.java:406)
        at org.codehaus.janino.Java$PackageMemberClassDeclaration.accept(Java.java:1414)
        at org.codehaus.janino.UnitCompiler.compile(UnitCompiler.java:406)
        at org.codehaus.janino.UnitCompiler.compileUnit(UnitCompiler.java:378)
        ... 24 more
```

- See Demos:
    - [Pyflink Demo](add/src/main/python/com/syntomic/issues/add/add.py)
    - [Flink Demo](add/src/main/java/com/syntomic/issues/add/Add.java)


- But when change to flink-1.14 or pyflink-1.14 it works
    - I think this is caused by source boundness(`source-v1` is bounded), when change to `source-v2`(not bounded), it can works both in 1.14 and 1.16
