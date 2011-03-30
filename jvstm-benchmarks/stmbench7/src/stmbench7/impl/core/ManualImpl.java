package stmbench7.impl.core;

import stmbench7.core.Manual;
import stmbench7.core.Module;


/**
 * STMBench7 benchmark Manual (see the specification).
 * Default implementation.
 */
public class ManualImpl implements Manual {

    private final int id;
    private String title;
    private String text;
    private Module module;

    public ManualImpl(int id, String title, String text) {
    	this.id = id;
    	this.title = title;
    	this.text = text;
    }

    public ManualImpl(ManualImpl source) {
    	this.title = source.title;
    	this.id = source.id;
    	this.text = source.text;
    	this.module = source.module;
    }
    
    public void setModule(Module module) {
    	this.module = module;
    }

    public int countOccurences(char ch) {
    	int position = 0, count = 0, newPosition, textLen = text.length();

    	do {
    		newPosition = text.indexOf(ch, position);
    		if(newPosition == -1) break;

    		position = newPosition + 1;
    		count++;
    	}
    	while(position < textLen);

    	return count;
    }

    public int checkFirstLastCharTheSame() {
    	if(text.charAt(0) == text.charAt(text.length() - 1)) return 1;
    	return 0;
    }
    
    public boolean startsWith(char ch) {
    	return (text.charAt(0) == ch);
    }

    public int replaceChar(char from, char to) {
    	text = text.replace(from, to);
    	return countOccurences(to);
    }

	public int getId() {
		return id;
	}

	public Module getModule() {
		return module;
	}

	public String getText() {
		return text;
	}

	public String getTitle() {
		return title;
	}
}
