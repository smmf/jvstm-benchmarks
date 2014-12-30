package stmbench7.impl.jvstm.core;

import jvstm.VBox;
import stmbench7.core.Manual;
import stmbench7.core.Module;
import stmbench7.core.RuntimeError;

public class ManualImpl implements Manual {

	private final int id;
	private final String title;
	private final VBox<String> text;
	private final VBox<Module> module;

	public ManualImpl(int id, String title, String text) {
		this.id = id;
		this.title = title;
		this.text = new VBox<String>(text);
		this.module = new VBox<Module>();
	}

	public ManualImpl(ManualImpl source) {
		throw new Error("ManualImpl(ManualImpl source) not implemented");
	}

	public void setModule(Module module) {
		this.module.put(module);
	}

	public int countOccurences(char ch) {
		int position = 0, count = 0, newPosition, textLen = text.get().length();

		do {
			newPosition = text.get().indexOf(ch, position);
			if(newPosition == -1) break;

			position = newPosition + 1;
			count++;
		} while(position < textLen);

		return count;
	}

	public int checkFirstLastCharTheSame() {
		String t = text.get();
		if(t.charAt(0) == t.charAt(t.length() - 1)) return 1;
		return 0;
	}

	public boolean startsWith(char ch) {
		return (text.get().charAt(0) == ch);
	}

	public int replaceChar(char from, char to) {
		text.put(text.get().replace(from, to));
		return countOccurences(to);
	}

	public int getId() {
		return id;
	}

	public Module getModule() {
		return module.get();
	}

	public String getText() {
		return text.get();
	}

	public String getTitle() {
		return title;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Manual)) return false;
		return ((Manual) obj).getId() == id;
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

	@Override
	public String toString() {
		return getClass().getName() + ": id=" + id + ", title=" + title + ", text=" +
		text.get().substring(0, 10) + " (...) " + text.get().substring(text.get().length() - 10, text.get().length());
	}
}
