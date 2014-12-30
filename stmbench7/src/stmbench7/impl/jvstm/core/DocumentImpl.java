package stmbench7.impl.jvstm.core;

import jvstm.VBox;
import stmbench7.core.CompositePart;
import stmbench7.core.Document;
import stmbench7.core.RuntimeError;

public class DocumentImpl implements Document {

	private final int id;
	private final String title;
	private final VBox<String> text;
	private final VBox<CompositePart> part;

	public DocumentImpl(int id, String title, String text) {
		this.id = id;
		this.title = title;
		this.text = new VBox<String>(text);
		this.part = new VBox<CompositePart>();
	}

	public DocumentImpl(DocumentImpl source) {
		throw new Error("DocumentImpl(DocumentImpl source) not implemented");
	}

	public void setPart(CompositePart part) {
		this.part.put(part);
	}

	public CompositePart getCompositePart() {
		return part.get();
	}

	public int getDocumentId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void nullOperation() {
	    // Intentionally empty
	}

	public int searchText(char symbol) {
		int occurences = 0;
		String t = text.get();

		for(int i = 0; i < t.length(); i++) {
			if(t.charAt(i) == symbol) occurences++;
		}

		return occurences;
	}

	public int replaceText(String from, String to) {
		String t = text.get();
		if(!t.startsWith(from)) return 0;

		text.put(t.replaceFirst(from, to));
		return 1;
	}

	public boolean textBeginsWith(String prefix) {
		return text.get().startsWith(prefix);
	}

	public String getText() {
		return text.get();
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Document)) return false;
		return ((Document) obj).getDocumentId() == id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch(CloneNotSupportedException e) {
			throw new RuntimeError(e);
		}
	}
}
