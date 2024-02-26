package planemqx.instrumentation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

import com.opencsv.CSVWriter;

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
		long compilTime = -1L;

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
			compilTime = compilBean.getTotalCompilationTime();
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
		 
		 //Saving data to csv
		 //FIXME: save results to csv
		 /*
		 String[] header = {"time", "load", "totalLoadedClass", "totalUnloadedClass", "currentLoadedClass", "compileTime", "totalStartedThreadCount", "peakThreadCount", 
				 "currentThreadCount", "cpuTime", "userTime", "initHeapMemory", "usedHeapMemory", "commitedHeapMemory", "maxHeapMemory", "buffsMemoryUsed", 
				 "buffsTotalCapacity", "gcCount", "gcTime"};
		 File file = new File("results/responsetimes.csv");
		 FileWriter outputfile;
		try {
			outputfile = new FileWriter(file, true);
			CSVWriter writer = new CSVWriter(outputfile);
			String[] data = {Long.toString(time), Double.toString(load), Long.toString(totalLoadedClass), Long.toString(totalUnloadedClass), Long.toString(currentLoadedClass),
					Long.toString(compilTime), Long.toString(totalStartedThreadCount), Long.toString(peakThreadCount), Long.toString(currentThreadCount), 
					Long.toString(cpuTime), Long.toString(userTime), Long.toString(initHeapMemory), Long.toString(usedHeapMemory), Long.toString(committedHeapMemory), 
					Long.toString(buffsMemoryUsed), Long.toString(buffsTotalCapacity), Long.toString(gcCount), Long.toString(gcTime)};
			writer.writeNext(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
}
