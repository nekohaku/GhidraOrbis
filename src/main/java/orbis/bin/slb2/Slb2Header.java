package orbis.bin.slb2;

import java.io.IOException;
import java.util.*;

import ghidra.app.util.bin.BinaryReader;
import ghidra.program.model.data.*;

public final class Slb2Header extends Slb2Structure {

	static final String MAGIC = "SLB2";
	private static final int MAX_ENTRIES = 10;
	public static final Structure dataType = getDataType();

	private final long version;
	private final long flags;
	private final long fileCount;
	private final long blockCount;
	private final Slb2Entry[] entries;

	public Slb2Header(BinaryReader reader) throws IOException {
		if (!reader.readNextAsciiString(4).equals(MAGIC)) {
			throw new IOException("Invalid SLB2 Container");
		}
		this.version = reader.readNextUnsignedInt();
		this.flags = reader.readNextUnsignedInt();
		this.fileCount = reader.readNextUnsignedInt();
		if (fileCount > MAX_ENTRIES) {
			String count = Long.toString(fileCount);
			throw new IOException("SLB2 Container has too many entries: " + count);
		}
		this.blockCount = reader.readNextUnsignedInt();
		advanceReader(reader, Integer.BYTES * 3);
		this.entries = new Slb2Entry[(int) fileCount];
		for (int i = 0; i < fileCount; i++) {
			entries[i] = new Slb2Entry(reader);
		}
	}

	/**
	 * @return the version
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * @return the flags
	 */
	public long getFlags() {
		return flags;
	}

	/**
	 * @return the fileCount
	 */
	public long getFileCount() {
		return fileCount;
	}

	/**
	 * @return the blockCount
	 */
	public long getBlockCount() {
		return blockCount;
	}

	/**
	 * @return the entries
	 */
	public List<Slb2Entry> getEntries() {
		return List.of(entries);
	}

	private static Structure getDataType() {
		Structure struct = new StructureDataType(PATH, "slb2_header", 0);
		DataType magic = new ArrayDataType(CharDataType.dataType, 4, 1);
		struct.add(magic, "magic", null);
		struct.add(DWordDataType.dataType, "version", null);
		struct.add(DWordDataType.dataType, "flags", null);
		struct.add(DWordDataType.dataType, "file_count", null);
		struct.add(DWordDataType.dataType, "block_count", null);
		struct.setFlexibleArrayComponent(Slb2Entry.dataType, "entry_list", null);
		struct.setInternallyAligned(true);
		struct.setToMachineAlignment();
		return struct;
	}
}
