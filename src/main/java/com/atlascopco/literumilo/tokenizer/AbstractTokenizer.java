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
package com.atlascopco.literumilo.tokenizer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is an abstract implementation of the {@link Tokenizer} class.  This is based on a non-contextual
 * list of characters that are considered to be word-break characters.  Implementers of this class
 * just need to implement {@link #isWordBreakCharacter(char)} and the implementation will handle
 * all the rest.
 *
 * @author Thomas Joiner
 */
public abstract class AbstractTokenizer implements Tokenizer {

	private static final Logger log = LoggerFactory.getLogger(AbstractTokenizer.class);

	@Override
	public Iterable<WordToken> tokenize(final Document document, final int offset, final int length) throws BadLocationException {
		return new TokenizingIterable(document, offset, length);
	}

	/**
	 * This class provides an iterable that will produce {@link Iterator}s that will lazily
	 * tokenize the string.  Take note that each iterator will independently tokenize the
	 * string so it would be best to only call it once.
	 *
	 * @author Thomas Joiner
	 */
	private final class TokenizingIterable implements Iterable<WordToken> {

		private final Document document;
		private final int offset;
		private final int length;
		private final Segment segment;
		private final int firstCharacterOffset;
		private final int lastCharacterOffset;

		/**
		 * Creates a {@link TokenizingIterable}.  The text from {@code offset} to {@code offset+length}
		 * will be tokenized.
		 * @param document the document to tokenize the text of
		 * @param offset the offset to start the tokenization from
		 * @param length the length of the text to tokenize
		 * @throws BadLocationException if unable to retrieve the text from the document.
		 */
		public TokenizingIterable(final Document document, final int offset, final int length) throws BadLocationException {
			this.document = document;
			this.offset = offset;
			this.length = length;
			this.segment = new Segment();
			this.document.getText(0, this.document.getLength(), this.segment);
			this.firstCharacterOffset = this.findFirstCharacter(this.segment);
			this.lastCharacterOffset = this.findLastCharacter(this.segment);

			if ( log.isDebugEnabled() ) {
				log.debug("Tokenizing: \"{}\"", new String(this.segment.array, this.segment.offset, this.segment.count));
			}
		}


		/**
		 * This method finds the first character that should be tokenized by the
		 * tokenizer.
		 *
		 * @param text the {@link Segment} that contains the text to tokenize
		 * @return the index from which one should start
		 */
		private int findFirstCharacter(Segment text) {
			for ( int i = this.offset; i > 0; i--) {
				if (AbstractTokenizer.this.isWordBreakCharacter(text.charAt(i))) {
					return i + 1;
				}
			}

			return 0;
		}

		/**
		 * This method finds the last character that should be tokenized by the
		 * tokenizer.
		 *
		 * @param text the {@link Segment} that contains the text to tokenize
		 * @return the index one should stop at
		 */
		private int findLastCharacter(Segment text) {
			int documentLength = text.count;

			if ( text.count == 0 ) {
				return 0;
			}

			for ( int i = this.offset+this.length-1; i < documentLength; i++ ) {
				if (AbstractTokenizer.this.isWordBreakCharacter(text.charAt(i))) {
					return i-1;
				}
			}

			return text.count-1;
		}


		@Override
		public Iterator<WordToken> iterator() {
			return new Iterator<WordToken>() {

				private WordToken nextToken;
				private int currentIndex = TokenizingIterable.this.firstCharacterOffset;

				@Override
				public boolean hasNext() {
					if ( this.nextToken != null ) {
						return true;
					} else {
						this.nextToken = this.nextToken();
						return this.nextToken != null;
					}
				}

				@Override
				public WordToken next() {
					if ( this.nextToken == null && !this.hasNext() ) {
						throw new NoSuchElementException();
					}

					WordToken token = this.nextToken;
					this.nextToken = null;

					return token;
				}

				private WordToken nextToken() {
					if ( TokenizingIterable.this.segment.length() == 0 ) {
						return null;
					}

					try {
						int offset=-1, length=0;
						StringBuilder currentToken = new StringBuilder();

						for (int i = this.currentIndex; i <= TokenizingIterable.this.lastCharacterOffset; i++) {
							if ( !AbstractTokenizer.this.isWordBreakCharacter(TokenizingIterable.this.segment.charAt(i)) ) {
								if ( offset == -1) {
									offset = i;
								}
								length++;
								currentToken.append(TokenizingIterable.this.segment.charAt(i));
							} else {
								if ( length > 0 ) {
									WordToken wordToken = new WordToken();
									wordToken.setStartOffset(TokenizingIterable.this.document.createPosition(offset));
									wordToken.setEndOffset(TokenizingIterable.this.document.createPosition(i-1));
									wordToken.setToken(currentToken.toString());

									this.currentIndex = i+1;

									log.debug("Token found: {}", wordToken);

									return wordToken;
								}
							}
						}

						// In case there is a token at the end without a word-break character
						// after it
						if ( offset != -1 && length > 0 ) {
							WordToken wordToken = new WordToken();
							wordToken.setStartOffset(TokenizingIterable.this.document.createPosition(offset));
							wordToken.setEndOffset(TokenizingIterable.this.document.createPosition(TokenizingIterable.this.lastCharacterOffset));
							wordToken.setToken(currentToken.toString());

							this.currentIndex = TokenizingIterable.this.lastCharacterOffset+1;

							log.debug("Token found: {}", wordToken);

							return wordToken;
						}
					} catch (BadLocationException e) {
						log.error("An error occurred in TokenizingIterator.", e);
					}
					return null;
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException("Mutating actions not allowed on Document.");
				}
			};
		}


	}

	/**
	 * This method should be implemented by sub-classes in order to tell them what characters are considered
	 * parts of words.
	 * @param character the character to test
	 * @return true if the character is not considered a part of a word
	 */
	public abstract boolean isWordBreakCharacter(char character);

}
