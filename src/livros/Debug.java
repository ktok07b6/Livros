package livros;

import java.util.Stack;

public class Debug
{
	public static void assertTrue(boolean b) {
		if (!b) {
			System.out.println("ASSERTION FAILED");
			new Throwable().printStackTrace();
			System.exit(0);
		}
	}

	public static void hexDump(byte[] bytes) {
		hexDump(bytes, 0);
	}

	public static void hexDump(byte[] bytes, int startAddress) {
		StringBuilder builder = new StringBuilder();
		StringBuilder chars = new StringBuilder();
		int address = startAddress;
		builder.append("\n");
		builder.append((address/256 < 0x10 ? "0":"") + Integer.toHexString(address/256));
		builder.append((address%256 < 0x10 ? "0":"") + Integer.toHexString(address%256) + ": ");

		int i = 0;
		for (; i < bytes.length; i++) {
			byte b = bytes[i];
			builder.append((0 <= b && b < 0x10 ? "0":"") + 
						   (b < 0 ? Integer.toHexString(b).substring(6, 8) : Integer.toHexString(b)) + " ");
			chars.append(((b >=32 && b <= 126) ? (char)(b):'.'));
			if ((i % 8) == 7) {
				builder.append(chars.toString());
				chars = new StringBuilder();
				builder.append("\n");
				address++;

				builder.append((address/256 < 0x10 ? "0":"") + Integer.toHexString(address/256));
				builder.append((address%256 < 0x10 ? "0":"") + Integer.toHexString(address%256) + ": ");
				continue;
			}
			address++;
		}
		for (int j = 0; j < (i % 8); j++) {
			builder.append("   ");
		}
		builder.append(chars.toString());
		System.out.println(builder.toString());
	}

	private static String getCallerName() {
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		assertTrue(ste.length > 3);
		return ste[3].getClassName() + "#" + ste[3].getMethodName();
	}

	static Stack mProfileStack = new Stack();
	public static void startProfile() { 
		if (Livros.PROFILE) {
			String name = getCallerName();
			long startTime = System.nanoTime();
			mProfileStack.push(new Object[]{name, new Long(startTime)});
		}
	}

	public static void endProfile() { 
		if (Livros.PROFILE) {
			try {
				Object[] info = (Object[])mProfileStack.pop();
				String name = (String)info[0];
				assertTrue(getCallerName().equals(name));
				long startTime = ((Long)info[1]).longValue();
				long endTime = System.nanoTime();
				System.out.println("PROFILE: " + name + " " + (endTime-startTime)/1000 + "us");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void printMemoryInfo() {
		long free = Runtime.getRuntime().freeMemory() / (1024*1024);
		long total = Runtime.getRuntime().totalMemory() / (1024*1024);
		long max = Runtime.getRuntime().maxMemory() / (1024*1024);
		long used = total - free;
		double ratio = (used * 100 / (double)total);
		System.out.println("/ total:" + total + "MB / free:" + free + "MB / used:" + used + "MB / max:" + max + "MB");

		// Place this code just before the end of the program
		/*
		try {
			String memoryUsage = new String();
			List pools = ManagementFactory.getMemoryPoolMXBeans();
			for (int i = 0; i < pools.size(); i++) {
				MemoryPoolMXBean pool = (MemoryPoolMXBean)pools.get(i);
				MemoryUsage peak = pool.getPeakUsage();
				memoryUsage += String.format("Peak %s memory used: %,d%n", pool.getName(),peak.getUsed());
				memoryUsage += String.format("Peak %s memory reserved: %,d%n", pool.getName(), peak.getCommitted());
			}

			// we print the result in the console
			System.out.println(memoryUsage);
 
		} catch (Throwable t) {
			System.err.println("Exception in agent: " + t);
		}
		*/
	}


}
