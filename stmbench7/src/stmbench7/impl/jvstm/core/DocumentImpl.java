package stmbench7.impl.jvstm.core;

import jvstm.VBox;
import stmbench7.core.CompositePart;
import stmbench7.core.Document;

public class DocumentImpl implements Document {

	private final VBox<Integer> id;
	private final VBox<String> title;
	private final VBox<String> text;
	private final VBox<CompositePart> part;

	public DocumentImpl(int id, String title, String text) {
		this.id = new VBox<Integer>(id);
		this.title = new VBox<String>(title);
		this.text = new VBox<String>(text);
		this.part = new VBox<CompositePart>();
	}

	public DocumentImpl(DocumentImpl source) {
		//TODO: really needed???
//		this.title = source.title;
//		this.id = source.id;
//		this.text = source.text;
//		this.part = source.part;
		throw new Error("DocumentImpl(DocumentImpl source) not implemented");
	}

	public void setPart(CompositePart part) {
		this.part.put(part);
	}

	public CompositePart getCompositePart() {
		return part.get();
	}

	public int getDocumentId() {
		return id.get();
	}

	public String getTitle() {
		return title.get();
	}

	public void nullOperation() {
	}

	public int searchText(char symbol) {
		int occurences = 0;
		String t = text.get();

		for(int i = 0; i < t.length(); i++)
			if(t.charAt(i) == symbol) occurences++;

		return occurences;
	}

	public int replaceText(String from, String to) {
		String t = text.get();
		if(! t.startsWith(from))
			return 0;

		text.put(t.replaceFirst(from, to));
		return 1;
	}

	public boolean textBeginsWith(String prefix) {
		return text.get().startsWith(prefix);
	}

	public String getText() {
		return text.get();
	}


}
