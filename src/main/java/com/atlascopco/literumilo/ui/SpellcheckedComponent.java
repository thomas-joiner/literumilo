/*******************************************************************************
 * Copyright 2012 Atlas Copco Drilling Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.atlascopco.literumilo.ui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlascopco.literumilo.spellchecker.Spellchecker;
import com.atlascopco.literumilo.spellchecker.Spellchecker.DictionaryChangeListener;
import com.atlascopco.literumilo.tokenizer.Tokenizer;
import com.atlascopco.literumilo.tokenizer.WordToken;

public class SpellcheckedComponent implements DocumentListener, DictionaryChangeListener {
	/**
	 * This is the cutoff for an update to be considered "small".  If an update is considered small
	 * then it will be executed synchronously.  If it is not considered small, then it will be executed
	 * asynchronously setting the component uneditable in the mean time. (cutoff arbitrarily chosen)
	 */
	private static final int SMALL_UPDATE_CUTOFF = 1024;

	private static final Logger log = LoggerFactory.getLogger(SpellcheckedComponent.class);

	private final List<WordToken> highlights = new ArrayList<WordToken>();
	private final JTextComponent component;
	private JPopupMenu popupMenu;

	private Spellchecker spellchecker;

	private Tokenizer tokenizer;

	/**
	 * This field indicates whether or not the menus have been/should be registered.
	 */
	private final boolean registerMenus;
	/**
	 * This method indicates whether or not the initialization has taken place.
	 */
	private boolean initialized = false;

	private JPopupMenu oldPopupMenu;
	private InputTracker inputTracker;

	private ErrorMarker errorMarker;

	/**
	 * This will instantiate the listeners for the component necessary in order to spell-check it.
	 *
	 * In order to actually begin spell-checking, one must call the {@link #initialize()} method.
	 *
	 * @param component the component to activate spell-checking for.
	 * @param spellchecker the {@link Spellchecker} with which to perform spell-checking
	 * @param tokenizer the {@link Tokenizer} with which to tokenize the text
	 * @param registerMenus true if context menus should be managed by the {@link SpellcheckedComponent}
	 * @see #initialize()
	 */
	public SpellcheckedComponent(JTextComponent component, Spellchecker spellchecker, Tokenizer tokenizer, boolean registerMenus) {
		this.component = component;
		this.spellchecker = spellchecker;
		this.tokenizer = tokenizer;
		this.registerMenus = registerMenus;
	}

	/**
	 * This method must be called after instantiation to actually start the spell-checking.
	 */
	public void initialize() {
		// Perform in a method rather than in the constructor in order to avoid leaking
		// a reference to a not-fully-constructed object.
		if (!this.initialized) {
			this.component.getDocument().addDocumentListener(this);
			this.spellchecker.addDictionaryChangeListener(this);
			if (this.registerMenus) {
				this.registerMenus();
			}

			this.initialized = true;
		}
	}

	/**
	 * This method should be called if the user wants to remove spell-checking from a component.
	 */
	public void remove() {
		if ( !this.initialized ) {
			log.warn("Calling remove on a SpellcheckedComponent that hasn't been initialized.");
		} else {
			Highlighter highlighter = this.component.getHighlighter();
			for (WordToken token : this.highlights) {
				highlighter.removeHighlight(token);
			}

			this.component.getDocument().removeDocumentListener(this);
			this.spellchecker.removeDictionaryChangeListener(this);

			if ( this.registerMenus ) {
				this.component.setComponentPopupMenu(this.oldPopupMenu);
				this.component.removeKeyListener(this.inputTracker);
				this.component.removeMouseListener(this.inputTracker);
				this.component.removeMouseMotionListener(this.inputTracker);
			}

			this.initialized = false;
		}
	}

	/**
	 * This method performs the work necessary in order to register the context
	 *  menus.
	 */
	private void registerMenus() {
		this.popupMenu = new JPopupMenu("spellcheck");

		final InputTracker inputTracker = new InputTracker();
		this.component.addKeyListener(inputTracker);
		this.component.addMouseListener(inputTracker);
		this.component.addMouseMotionListener(inputTracker);

		this.popupMenu.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				Point triggerPosition = SpellcheckedComponent.this.component.getMousePosition();

				if (inputTracker.isKeyboardTrigger()
						|| triggerPosition == null) {
					triggerPosition = SpellcheckedComponent.this.component.getCaret().getMagicCaretPosition();
				}

				if (triggerPosition == null) {
					return;
				}

				List<Action> spellcheckSuggestions = SpellcheckedComponent.this.getSpellcheckSuggestions(triggerPosition);

				for (Action action : spellcheckSuggestions) {
					SpellcheckedComponent.this.popupMenu.add(action);
				}

				if (spellcheckSuggestions.size() > 0) {
					SpellcheckedComponent.this.popupMenu.addSeparator();
				}

				JMenu languageMenu = new JMenu("Languages");

				for (JMenuItem language : SpellcheckedComponent.this.getLanguageSelectors()) {
					languageMenu.add(language);
				}

				SpellcheckedComponent.this.popupMenu.add(languageMenu);
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				SpellcheckedComponent.this.popupMenu.removeAll();
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				SpellcheckedComponent.this.popupMenu.removeAll();
			}
		});

		this.oldPopupMenu = this.component.getComponentPopupMenu();

		this.component.setComponentPopupMenu(this.popupMenu);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		long start = -1;
		if ( log.isDebugEnabled() ) {
			start = System.nanoTime();
		}
		Highlighter highlighter = this.component.getHighlighter();

		for (Iterator<WordToken> wordIterator = this.highlights.iterator(); wordIterator.hasNext();) {
			WordToken word = wordIterator.next();

			// If the edit affected the word, then we need to remove the highlight and re-tokenize that portion
			if ( word.hasChanged() || word.getEndOffset().getOffset() == e.getOffset() - 1 || word.getEndOffset().getOffset() == e.getOffset() || word.getStartOffset().getOffset() == e.getOffset() ) {
				highlighter.removeHighlight(word.getHighlightTag());
				wordIterator.remove();
			}
		}

		if ( log.isDebugEnabled() ) {
			long end = System.nanoTime();
			log.debug("removeUpdate: {} ms", TimeUnit.MILLISECONDS.convert(end - start, TimeUnit.NANOSECONDS));
		}
		int offset = bound(0, e.getOffset()-1, e.getDocument().getLength()-1);
		int length = bound(0, 2, e.getDocument().getLength()-offset);
		this.errorMarker = this.createErrorMarker(offset, length, this.component.getDocument());//new ErrorMarker(offset, length, this.component, this.highlights, this.spellchecker, this.tokenizer);

		this.errorMarker.doInForeground();
		this.errorMarker = null;
	}

	/**
	 * This method makes sure that a value is within the given bounds, if it isn't, then
	 * the bound that it exceeded will be returned instead.
	 * @param min the minimum bound (inclusive)
	 * @param val the value
	 * @param max the maximum bound (inclusive)
	 * @return the value as described
	 */
	private static int bound(int min, int val, int max) {
		if ( val < min ) {
			return min;
		} else if ( val > max ) {
			return max;
		} else {
			return val;
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		long start = -1;
		if ( log.isDebugEnabled() ) {
			start = System.nanoTime();
		}
		Highlighter highlighter = this.component.getHighlighter();

		for (Iterator<WordToken> wordIterator = this.highlights.iterator(); wordIterator.hasNext();) {
			WordToken word = wordIterator.next();

			// If the edit affected the word, then we need to remove the highlight and re-tokenize that portion
			if ( word.hasChanged() || word.getStartOffset().getOffset() == e.getOffset()+e.getLength() || word.getEndOffset().getOffset() == e.getOffset() - 1) {
				highlighter.removeHighlight(word.getHighlightTag());
				wordIterator.remove();
			}
		}

		if ( log.isDebugEnabled() ) {
			long end = System.nanoTime();
			log.debug("insertUpdate: {} ms", TimeUnit.MILLISECONDS.convert(end - start, TimeUnit.NANOSECONDS));
		}
		int offset = bound(0, e.getOffset()-1, e.getDocument().getLength()-1);
		int length = bound(0, e.getLength()+2, e.getDocument().getLength()-offset);

		this.errorMarker = this.createErrorMarker(offset, length, this.component.getDocument());//new ErrorMarker(offset, length, this.component, this.highlights, this.spellchecker, this.tokenizer);

		if ( this.errorMarker.getLength() > SMALL_UPDATE_CUTOFF ) {
			this.component.setEditable(false);
			this.errorMarker.setCallback(new Runnable() {
				@Override
				public void run() {
					SpellcheckedComponent.this.component.setEditable(true);
				}
			});
			this.errorMarker.execute();
		} else {
			this.errorMarker.doInForeground();
			this.errorMarker = null;
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// Changed updates are fired when text attributes change.  We don't care
		// about the way the text looks, just the text itself.
	}

	/**
	 * This method takes care of creating the {@link ErrorMarker} to mark the errors.
	 *
	 * If the previous {@link ErrorMarker} is still running, this method will take care
	 * of canceling it and adjusting the offset and length of the {@link ErrorMarker} to
	 * match.
	 *
	 * @param offset the offset to start the marking at
	 * @param length the length to mark
	 * @param document the document to do this to
	 * @return the created {@link ErrorMarker}, ready to be executed
	 */
	private ErrorMarker createErrorMarker(int offset, int length, Document document) {
		if ( this.errorMarker != null && !this.errorMarker.isDone() ) {
			this.errorMarker.cancel(false);

			List<WordToken> list = this.errorMarker.getProcessedItems();
			Highlighter highlighter = this.component.getHighlighter();

			// Remove all the highlights that were added by the canceled
			// ErrorMarker, otherwise they will be added again by the new one.
			for (WordToken wordToken : list) {
				highlighter.removeHighlight(wordToken.getHighlightTag());
				this.highlights.remove(wordToken);
			}

			// Adjust the offset and length
			if ( offset < this.errorMarker.getOffset() ) {
				length = (this.errorMarker.getOffset()-offset)+length;
			} else {
				length = (offset-this.errorMarker.getOffset())+length;
				offset = this.errorMarker.getOffset();
			}
		}

		// Make sure to bound the offset and length by the limits of the document
		int realOffset = bound(0, offset, document.getLength());
		int realLength = bound(0, length, document.getLength()-realOffset);

		return new ErrorMarker(realOffset, realLength, this.component, this.highlights, this.spellchecker, this.tokenizer);
	}

	/**
	 * This method will create the {@link Action}s to be used as menu items to
	 * correct the spelling for a misspelled term.  If the word beneath the provided
	 * {@link Point} is not misspelled, then the returned list will be empty.
	 *
	 * @param target the {@link Point} where the menu was triggered.
	 * @return the {@link Action}s that will cause the misspelled word to be replaced
	 */
	public List<Action> getSpellcheckSuggestions(Point target) {
		List<Action> spellcheckActions = new ArrayList<Action>();

		int offset = this.component.viewToModel(target);

		WordToken belowMouse = this.getTokenByOffset(offset);

		if (belowMouse != null) {
			List<String> suggestions = belowMouse.getSuggestions();

			if ( suggestions == null ) {
				suggestions = this.spellchecker.suggest(belowMouse.getToken());

				// cache the suggestions since depending on the word, it can take
				// hunspell quite a while to get back to us
				belowMouse.setSuggestions(suggestions);
			}

			// Create an action for each of the suggestions to perform the replacement
			for (String suggestion : suggestions) {
				spellcheckActions.add(new ReplaceWordAction(this.component.getDocument(), belowMouse, suggestion));
			}
		}

		return spellcheckActions;
	}

	/**
	 * This method will retrieve a given {@link WordToken} from the list of highlights given its
	 * offset.
	 *
	 * @param offset the offset to find
	 * @return the {@link WordToken} that matches, or null if none found
	 */
	private WordToken getTokenByOffset(int offset) {
		for (WordToken token : this.highlights) {
			if ( offset >= token.getStartOffset().getOffset() && offset <= token.getEndOffset().getOffset() ) {
				return token;
			}
		}

		return null;
	}

	/**
	 * This method will return a list of {@link JMenuItem}s that will list the available
	 * languages and allow switching between them.
	 *
	 * @return a {@link JMenuItem} for each possible language
	 */
	public List<JMenuItem> getLanguageSelectors() {
		List<JMenuItem> languageItems = new ArrayList<JMenuItem>();
		ButtonGroup languageGroup = new ButtonGroup();

		Locale currentLocale = this.spellchecker.getCurrentDictionary();
		for ( Locale language : this.spellchecker.getAvailableDictionaries() ) {
			JCheckBoxMenuItem languageItem = new JCheckBoxMenuItem();
			languageItem.setAction(new LanguageSelector(this.spellchecker, language));
			if (currentLocale.equals(language)) {
				languageItem.setSelected(true);
			}
			languageGroup.add(languageItem);
			languageItems.add(languageItem);
		}

		return languageItems;
	}

	@Override
	public void dictionaryChanged() {
		Highlighter highlighter = this.component.getHighlighter();

		// Clear all previous highlights
		for (WordToken wordToken : this.highlights) {
			highlighter.removeHighlight(wordToken.getHighlightTag());
		}

		this.highlights.clear();

		int documentLength = this.component.getDocument().getLength();

		// Perform an error marking on the whole document using the new
		// dictionary.
		this.errorMarker = new ErrorMarker(0, documentLength, this.component, this.highlights, this.spellchecker, this.tokenizer);
		if ( documentLength < SMALL_UPDATE_CUTOFF ) {
			this.errorMarker.doInForeground();
		} else {
			this.component.setEditable(false);
			this.errorMarker.setCallback(new Runnable() {
				@Override
				public void run() {
					SpellcheckedComponent.this.component.setEditable(true);
				}
			});
			this.errorMarker.execute();
		}
	}

	public Spellchecker getSpellchecker() {
		return this.spellchecker;
	}

	/**
	 * This method will set the {@link Spellchecker} that this component is using.  This results in
	 * an immediate refresh of the spell-checking.
	 * @param spellchecker the new {@link Spellchecker}
	 */
	public void setSpellchecker(Spellchecker spellchecker) {
		this.spellchecker = spellchecker;

		this.refreshSpellchecking();
	}

	public Tokenizer getTokenizer() {
		return this.tokenizer;
	}

	/**
	 * This method will set the {@link Tokenizer} that this component is using.  This results in
	 * an immediate refresh of the spell-checking.
	 * @param tokenizer the new {@link Tokenizer}
	 */
	public void setTokenizer(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;

		this.refreshSpellchecking();
	}

	/**
	 * This method is executed when the component should discard all previous state and
	 * perform spell-checking again.
	 */
	private void refreshSpellchecking() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SpellcheckedComponent.this.dictionaryChanged();
			}
		});
	}
}
