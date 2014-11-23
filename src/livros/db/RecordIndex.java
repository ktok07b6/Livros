package livros.db;

public class RecordIndex implements Comparable
{
	public int recid;
	public int index;
	public int chunkid;

	public RecordIndex(int recid, int index, int chunkid) {
		this.recid = recid;
		this.index = index;
		this.chunkid = chunkid;
	}

	public RecordIndex(Record r) {
		this(r.id(), r.index(), r.chunkid());
	}

	public String toString() {
		return "[" + recid + "](" + index + "," + chunkid + ")";
	}

	public int compareTo(Object o) {
		RecordIndex other = (RecordIndex)o;
		return recid - other.recid;
	}
}
