package instrumentation;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.List;

public class Instrument implements Runnable {
	
	// this code should better be inserted into the main method
	static {
		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
		if (threadBean.isCurrentThreadCpuTimeSupported()) {
			threadBean.setThreadCpuTimeEnabled(true);
		}
	}

	@Override
	public void run() {
		long time = System.currentTimeMillis();
		// host CPU load
		OperatingSystemMXBean osbean = ManagementFactory.getOperatingSystemMXBean();
		double load = osbean.getSystemLoadAverage();
		// JVM class loading
		ClassLoadingMXBean clBean = ManagementFactory.getClassLoadingMXBean();
		long totalLoadedClass = clBean.getTotalLoadedClassCount();
		long totalUnloadedClass = clBean.getUnloadedClassCount();
		long currentLoadedClass = clBean.getLoadedClassCount();
		// JVM JIT compilation
		CompilationMXBean compilBean = ManagementFactory.getCompilationMXBean();
		if (compilBean.isCompilationTimeMonitoringSupported()) {
			long compilTime = compilBean.getTotalCompilationTime();
		}
		// JVM thread
		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
		long totalStartedThreadCount = threadBean.getTotalStartedThreadCount();
		long peakThreadCount = threadBean.getPeakThreadCount();
		long currentThreadCount = threadBean.getThreadCount();
		if (threadBean.isThreadCpuTimeEnabled()) {
			Arrays.asList(threadBean.getAllThreadIds()).stream().forEach(thId -> {
				long cpuTime = threadBean.getCurrentThreadCpuTime();
				long userTime = threadBean.getCurrentThreadUserTime();
			});
		}
		// JVM memory : heap
		MemoryMXBean mBean = ManagementFactory.getMemoryMXBean();
		MemoryUsage heapMemory = mBean.getHeapMemoryUsage();
		long initHeapMemory = heapMemory.getInit();
		long usedHeapMemory = heapMemory.getUsed();
		long committedHeapMemory = heapMemory.getCommitted();
		long maxHeapMemory = heapMemory.getMax();
		// JVM buffers
		List<BufferPoolMXBean> bufferPoolBeans = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
		long buffsMemoryUsed = bufferPoolBeans.stream().map(buf -> buf.getMemoryUsed()).reduce(0L, Long::sum);
		long buffsTotalCapacity = bufferPoolBeans.stream().map(buf -> buf.getTotalCapacity()).reduce(0L, Long::sum);
		// JVM GC
		 List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
		 long gcCount = gcBeans.stream().map(gc -> gc.getCollectionCount()).reduce(0L, Long::sum);
		 long gcTime = gcBeans.stream().map(gc -> gc.getCollectionTime()).reduce(0L, Long::sum);
	}
}
