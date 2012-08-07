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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlascopco.literumilo.tokenizer.WordToken;

/**
 * This {@link Action} will replace a given word with its (presumably) correctly
 * spelled alternative.
 *
 * @author Thomas Joiner
 */
class ReplaceWordAction extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = -7748554128503131378L;
	private static final Logger log = LoggerFactory.getLogger(ReplaceWordAction.class);

	/**
	 * The document to carry the replacement out on.
	 */
	private final Document document;
	/**
	 * The token (with associated location information) to replace.
	 */
	private final WordToken token;
	/**
	 * The text to replace the token with.
	 */
	private final String replacement;

	/**
	 * The action takes a document to do the replacement on, the token (with the position information)
	 * and the word to replace it with.
	 *
	 * @param document the document to perform the replacement on
	 * @param token the token to replace
	 * @param replacement the word to replace the token with
	 */
	public ReplaceWordAction(Document document, WordToken token, String replacement) {
		super(replacement);
		this.document = document;
		this.token = token;
		this.replacement = replacement;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		try {
			this.document.remove(this.token.getStartOffset().getOffset(), this.token.getLength());
			this.document.insertString(this.token.getStartOffset().getOffset(), this.replacement, null);
		} catch (BadLocationException e) {
			log.error("An error occurred when replacing a word.", e);
		}
	}

}
