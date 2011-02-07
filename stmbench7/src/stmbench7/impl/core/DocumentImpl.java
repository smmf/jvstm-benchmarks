package stmbench7.impl.core;

import stmbench7.core.CompositePart;
import stmbench7.core.Document;


/**
 * STMBench7 benchmark Document (see the specification).
 * Default implementation.
 */
public class DocumentImpl implements Document {
    
    private final int id;
    private String title;
    private String text;
    private CompositePart part;

    public DocumentImpl(int id, String title, String text) {
    	this.id = id;
    	this.title = title;
    	this.text = text;
    }

    public DocumentImpl(DocumentImpl source) {
    	this.title = source.title;
    	this.id = source.id;
    	this.text = source.text;
    	this.part = source.part;
    }
    
    public void setPart(CompositePart part) {
    	this.part = part;
    }

    public CompositePart getCompositePart() {
    	return part;
    }

    public int getDocumentId() {
    	return id;
    }

    public String getTitle() {
    	return title;
    }

    public void nullOperation() {
    }

    public int searchText(char symbol) {
    	int occurences = 0;

    	for(int i = 0; i < text.length(); i++)
    		if(text.charAt(i) == symbol) occurences++;

    	return occurences;
    }

    public int replaceText(String from, String to) {
    	if(! text.startsWith(from)) return 0;

    	text = text.replaceFirst(from, to);
    	return 1;
    }

	public boolean textBeginsWith(String prefix) {
		return text.startsWith(prefix);
	}

	public String getText() {
		return text;
	}
}
