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

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;


/**
 * This interface specifies what must be implemented by a client in order to tokenize
 * text in a custom way.
 *
 * Note that unless you need more advanced tokenization than simple "split on a list of
 * characters", then you should probably just extend {@link AbstractTokenizer}.
 *
 * @author Thomas Joiner
 */
public interface Tokenizer {
	public Iterable<WordToken> tokenize(Document document, int offset, int length) throws BadLocationException;
}
