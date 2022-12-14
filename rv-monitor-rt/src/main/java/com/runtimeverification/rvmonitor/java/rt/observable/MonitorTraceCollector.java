package com.runtimeverification.rvmonitor.java.rt.observable;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractIndexingTree;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractMonitor;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractPartitionedMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IDisableHolder;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IIndexingTreeValue;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitor;
import com.runtimeverification.rvmonitor.java.rt.util.TraceDB;

public class MonitorTraceCollector implements IInternalBehaviorObserver {

    protected final PrintWriter writer;

    protected final TraceDB traceDB;

    protected final Set<String> monitors;

    public MonitorTraceCollector(PrintWriter writer, String dbPath) {
        this.writer = writer;
        this.traceDB = new TraceDB(dbPath);
        this.monitors = new HashSet<>();
        traceDB.createTable();
    }

    @Override
    public void onMonitorTransitioned(AbstractMonitor monitor) {
        insertOrUpdate(monitor.getClass().getSimpleName() + "#" + monitor.monitorid, monitor.trace.toString(), monitor.trace.size());
    }

    private void insertOrUpdate(String monitorID, String trace, int length) {
        if (monitors.add(monitorID)) {
            traceDB.put(monitorID, trace, length);
        } else {
            traceDB.update(monitorID, trace, length);
        }
    }

    @Override
    public <TMonitor extends IMonitor> void onMonitorTransitioned(AbstractMonitorSet<TMonitor> set) {
        for (int i = 0; i < set.getSize(); ++i) {
            // AbstractMonitor is the only parent of all monitor types and it implements IMonitor
            AbstractMonitor monitor = (AbstractMonitor) set.get(i);
            insertOrUpdate(monitor.getClass().getSimpleName() + "#" + monitor.monitorid, monitor.trace.toString(), monitor.trace.size());
        }
    }

    @Override
    public <TMonitor extends IMonitor> void onMonitorTransitioned(AbstractPartitionedMonitorSet<TMonitor> set) {
        for (AbstractPartitionedMonitorSet<TMonitor>.MonitorIterator i = set.monitorIterator(true); i.moveNext(); ) {
            // AbstractMonitor is the only parent of all monitor types and it implements IMonitor
            AbstractMonitor monitor = (AbstractMonitor) i.getMonitor();
            insertOrUpdate(monitor.getClass().getSimpleName() + "#" + monitor.monitorid, monitor.trace.toString(), monitor.trace.size());
        }
    }

    @Override
    public void onCompleted() {

    }

    // TODO: We do not use any of the following methods; what's the runtime cost of keeping them?
    @Override
    public void onEventMethodEnter(String evtname, Object... args) {

    }

    @Override
    public void onIndexingTreeCacheHit(String cachename, Object cachevalue) {

    }

    @Override
    public void onIndexingTreeCacheMissed(String cachename) {

    }

    @Override
    public void onIndexingTreeCacheUpdated(String cachename, Object cachevalue) {

    }

    @Override
    public <TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> void onIndexingTreeLookup(AbstractIndexingTree<TWeakRef, TValue> tree, LookupPurpose purpose, Object retrieved, Object... keys) {

    }

    @Override
    public <TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> void onTimeCheck(AbstractIndexingTree<TWeakRef, TValue> tree, IDisableHolder source, IDisableHolder candidate, boolean definable, Object... keys) {

    }

    @Override
    public <TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> void onIndexingTreeNodeInserted(AbstractIndexingTree<TWeakRef, TValue> tree, Object inserted, Object... keys) {

    }

    @Override
    public void onNewMonitorCreated(AbstractMonitor created) {

    }

    @Override
    public void onMonitorCloned(AbstractMonitor existing, AbstractMonitor created) {

    }

    @Override
    public void onDisableFieldUpdated(IDisableHolder affected) {

    }

    @Override
    public void onEventMethodLeave() {

    }
}
