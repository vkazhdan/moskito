package net.anotheria.moskito.central.storages;

import net.anotheria.moskito.central.StatStorage;
import net.anotheria.moskito.central.StatStorageException;
import net.anotheria.moskito.core.producers.IStatsSnapshot;
import net.anotheria.moskito.core.producers.SnapshotArchiverRegistry;
import org.configureme.annotations.AbortedConfiguration;
import org.configureme.annotations.AfterInitialConfiguration;
import org.configureme.annotations.AfterReConfiguration;
import org.configureme.annotations.ConfigureMe;

import java.util.Collection;
import java.util.Date;

/**
 * Remote implementation of the StatStorage
 * that is executed with RMI distributeme
 *
 * @author imercuriev
 *         Date: Mar 16, 2010
 *         Time: 10:44:33 AM
 */
@ConfigureMe(allfields=true, name="moskitoStorage")
public class RemoteStatStorage implements StatStorage {

    private String storageClassName;
    private String storageParams;
    private StatStorage localStorage;

    public String getStorageClassName() {
        return storageClassName;
    }

    public void setStorageClassName(String storageClassName) {
        this.storageClassName = storageClassName;
    }

    public String getStorageParams() {
        return storageParams;
    }

    public void setStorageParams(String storageParams) {
        this.storageParams = storageParams;
    }

    @AfterInitialConfiguration
    public void callAfterInitialConfigurationOnly() {
        try {
            localStorage = (StatStorage) SnapshotArchiverRegistry.INSTANCE.createStorage(storageClassName, storageParams);
        } catch (Exception e) {
            System.err.println("Failed to create local storage");
            e.printStackTrace();
        }
    }

    @AfterReConfiguration
    public void callAfterReConfigurationOnly() {
        callAfterInitialConfigurationOnly();
    }

    @AbortedConfiguration
    public void callIfAborted(){
        System.err.println("Configuration has been aborted. Please try again ...");
    }

    public void store(Collection<IStatsSnapshot> snapshots, Date when, String host, String interval) throws StatStorageException {
        localStorage.store(snapshots, when, host, interval);
    }

    public IStatsSnapshot queryLastSnapshotByDate(Date when, String host, String statName, String interval) throws StatStorageException {
        return localStorage.queryLastSnapshotByDate(when, host, statName, interval);
    }
}
