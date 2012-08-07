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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlascopco.literumilo.spellchecker.Spellchecker;
import com.atlascopco.literumilo.tokenizer.Tokenizer;
import com.atlascopco.literumilo.tokenizer.WordToken;
import com.atlascopco.literumilo.ui.painters.SquigglyUnderlineHighlightPainter;

final class ErrorMarker extends SwingWorker<List<WordToken>, WordToken> {

	private static final Logger log = LoggerFactory.getLogger(ErrorMarker.class);

	private final JTextComponent component;
	private final List<WordToken> highlights;
	private Runnable callback;
	private final Spellchecker spellchecker;
	private final Tokenizer tokenizer;

	private final int offset;
	private final int length;

	private final List<WordToken> processedItems = new ArrayList<WordToken>();

	public ErrorMarker(int offset, int length, JTextComponent component, List<WordToken> highlights, Spellchecker spellchecker, Tokenizer tokenizer) {
		if ( offset < 0 || (offset >= component.getDocument().getLength() && offset != 0) || offset+length > component.getDocument().getLength()) {
			throw new IllegalArgumentException("Invalid offset and length Event{offset="+offset+", length="+length+"}, Document {length="+component.getDocument().getLength()+"}");
		}
		this.offset = offset;
		this.length = length;
		this.component = component;
		this.highlights = highlights;
		this.spellchecker = spellchecker;
		this.tokenizer = tokenizer;
	}

	/**
	 * This method will execute the task in the foreground.  Note that this will
	 * only work properly if called on the EDT.
	 *
	 * @return the result from {@link #doInBackground()}
	 */
	public List<WordToken> doInForeground() {
		List<WordToken> doInBackground = this.doInBackground();

		this.done();

		return doInBackground;
	}

	@Override
	protected List<WordToken> doInBackground() {
		long start = -1;
		if ( log.isDebugEnabled() ) {
			start = System.nanoTime();
		}

		List<WordToken> tokens = new ArrayList<WordToken>();

		try {
			for (WordToken word : this.tokenizer.tokenize(this.component.getDocument(), this.offset, this.length)) {
				if ( this.isCancelled() ) {
					return tokens;
				}

				if (this.spellchecker.misspelled(word.getToken())) {
					tokens.add(word);

					// If we are running on the EDT, then we don't want the normal
					// processes to happen, because we are running it in the foreground.
					if ( !SwingUtilities.isEventDispatchThread() ) {
						this.publish(word);
					}
				}
			}

			// If we are running on the EDT, then we need to manually force it
			// to process the tokens, because we are running it in the
			// foreground.
			if ( SwingUtilities.isEventDispatchThread() ) {
				this.process(tokens);
			}
		} catch (BadLocationException e) {
			log.error("An error occurred when tokenizing.", e);
		}

		if ( log.isDebugEnabled() ) {
			long end = System.nanoTime();
			log.debug("markErrors: {} ms", TimeUnit.MILLISECONDS.convert(end - start, TimeUnit.NANOSECONDS));
		}

		return tokens;
	}

	@Override
	protected void process(List<WordToken> chunks) {
		// Don't perform any processing if it has already been cancelled.
		if ( this.isCancelled() ) {
			return;
		}

		super.process(chunks);

		for (WordToken wordToken : chunks) {
			try {
				Highlighter highlighter = this.component.getHighlighter();
				Object tag = highlighter.addHighlight(wordToken.getStartOffset().getOffset(),
								wordToken.getEndOffset().getOffset()+1,
								new SquigglyUnderlineHighlightPainter(Color.RED));
				wordToken.setHighlightTag(tag);
				this.processedItems.add(wordToken);
				this.highlights.add(wordToken);
			} catch (BadLocationException e) {
				log.error("An error occurred when adding highlight.", e);
			}
		}
	}

	@Override
	protected void done() {
		super.done();
		if ( this.callback != null ) {
			this.callback.run();
		}
	}

	/**
	 * Adds a callback to be executed after the task has completed.  This callback
	 * will be executed on the EDT.
	 * @param callback the callback to execute after the task has finished
	 */
	public void setCallback(Runnable callback) {
		this.callback = callback;
	}

	public int getOffset() {
		return this.offset;
	}

	public int getLength() {
		return this.length;
	}

	public List<WordToken> getProcessedItems() {
		return this.processedItems;
	}

}
